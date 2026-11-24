package tungnn.tutor.java.tool.hub.core;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginWatcher implements Runnable, AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(PluginWatcher.class);
    private static final long DEBOUNCE_MS = 700;
    private static final long STABLE_CHECK_MS = 300;
    private static final int STABLE_MAX_RETRY = 20;
    private final Path dir;
    private final PluginService service;
    private final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "plugin-watcher-worker");
        t.setDaemon(true);
        return t;
    });
    private final Map<Path, Task> pending = new ConcurrentHashMap<>();
    private volatile boolean running = true;

    public PluginWatcher(Path dir, PluginService service) {
        this.dir = dir.toAbsolutePath().normalize();
        this.service = service;
    }

    private static boolean isPluginZip(Path file) {
        String name = file.getFileName().toString().toLowerCase();
        return name.endsWith(".zip") && !name.startsWith(".");
    }

    @Override
    public void run() {
        try (WatchService ws = dir.getFileSystem().newWatchService()) {
            dir.register(ws, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
            log.info("Watching plugins directory: {}", dir);
            while (running && !Thread.currentThread().isInterrupted()) {
                WatchKey key = ws.take();
                for (WatchEvent<?> ev : key.pollEvents()) {
                    if (ev.kind() == OVERFLOW) {
                        log.warn("WatchService overflowed, events may have been missed");
                        continue;
                    }
                    Path file = dir.resolve((Path) ev.context());
                    if (!isPluginZip(file)) continue;
                    schedule(file, ev.kind());
                }
                if (!key.reset()) {
                    log.warn("Plugins directory is no longer valid, stopping watcher: {}", dir);
                    break;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            log.error("Unable to create a WatchService for {}", dir, e);
        } finally {
            worker.shutdownNow();
        }
    }

    private void schedule(Path file, WatchEvent.Kind<?> kind) {
        pending.compute(file, (f, old) -> {
            if (old != null) old.future.cancel(false);
            Task task = new Task(file, kind);
            task.future = worker.schedule(task, DEBOUNCE_MS, TimeUnit.MILLISECONDS);
            return task;
        });
    }

    private final class Task implements Runnable {
        private final Path file;
        private final WatchEvent.Kind<?> kind;
        private volatile ScheduledFuture<?> future;

        private Task(Path file, WatchEvent.Kind<?> kind) {
            this.file = file;
            this.kind = kind;
        }

        @Override
        public void run() {
            try {
                if (kind == ENTRY_DELETE || !Files.exists(file)) {
                    onDeleted(file);
                } else {
                    onCreatedOrModified(file);
                }
            } catch (Throwable t) {
                log.error("Error handling plugin {} ({})", file.getFileName(), kind.name(), t);
            } finally {
                // only remove our own entry, a newer task for the same file must stay tracked
                pending.remove(file, this);
            }
        }
    }

    private void onDeleted(Path file) {
        log.info("Removing plugin of {} (file was deleted)", file.getFileName());
        service.unloadZip(file);
    }

    private void onCreatedOrModified(Path file) throws InterruptedException {
        if (!waitUntilStable(file)) {
            log.warn("File {} is still not stable after waiting, skipping this time", file.getFileName());
            return;
        }
        log.info("Loading plugin from {}", file.getFileName());
        service.loadOne(file);
    }

    private boolean waitUntilStable(Path file) throws InterruptedException {
        try {
            long last = -1;
            for (int i = 0; i < STABLE_MAX_RETRY; i++) {
                if (!Files.exists(file)) return false;
                long size = Files.size(file);
                if (size > 0 && size == last) return true;
                last = size;
                Thread.sleep(STABLE_CHECK_MS);
            }
        } catch (IOException e) {
            log.warn("Unable to read the size of {}", file, e);
        }
        return false;
    }

    @Override
    public void close() {
        running = false;
        worker.shutdownNow();
    }
}
