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

/** Theo dõi thư mục plugins: thả/sửa/xoá file *.zip thì tự load/reload/unload plugin. */
public class PluginWatcher implements Runnable, AutoCloseable {

  private static final Logger log = LoggerFactory.getLogger(PluginWatcher.class);

  private static final long DEBOUNCE_MS = 700;
  private static final long STABLE_CHECK_MS = 300;
  private static final int STABLE_MAX_RETRY = 20; // tối đa ~6s chờ file copy xong

  private final Path dir;
  private final PluginService service;

  private final ScheduledExecutorService worker =
      Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "plugin-watcher-worker");
        t.setDaemon(true);
        return t;
      });

  /** Mỗi file zip chỉ giữ 1 tác vụ đang chờ; sự kiện mới sẽ thay tác vụ cũ (debounce). */
  private final Map<Path, ScheduledFuture<?>> pending = new ConcurrentHashMap<>();

  private volatile boolean running = true;

  public PluginWatcher(Path dir, PluginService service) {
    this.dir = dir.toAbsolutePath().normalize();
    this.service = service;
  }

  private static boolean isPluginZip(Path file) {
    String name = file.getFileName().toString().toLowerCase();
    return name.endsWith(".zip") && !name.startsWith("."); // bỏ qua file tạm kiểu .xxx.zip
  }

  @Override
  public void run() {
    try (WatchService ws = dir.getFileSystem().newWatchService()) {
      dir.register(ws, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
      log.info("Đang theo dõi thư mục plugins: {}", dir);

      while (running && !Thread.currentThread().isInterrupted()) {
        WatchKey key = ws.take();

        for (WatchEvent<?> ev : key.pollEvents()) {
          if (ev.kind() == OVERFLOW) { // context() == null, bỏ qua
            log.warn("WatchService bị overflow, có thể đã bỏ lỡ sự kiện");
            continue;
          }
          Path file = dir.resolve((Path) ev.context());
          if (!isPluginZip(file)) continue;
          schedule(file, ev.kind());
        }

        if (!key.reset()) { // thư mục bị xoá/không còn truy cập được
          log.warn("Thư mục plugins không còn hợp lệ, dừng watcher: {}", dir);
          break;
        }
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (IOException e) {
      log.error("Không khởi tạo được WatchService cho {}", dir, e);
    } finally {
      worker.shutdownNow();
    }
  }

  private void schedule(Path file, WatchEvent.Kind<?> kind) {
    // Hủy tác vụ cũ của cùng file để gộp nhiều event (CREATE + nhiều MODIFY khi đang copy)
    pending.compute(
        file,
        (f, old) -> {
          if (old != null) old.cancel(false);
          return worker.schedule(() -> handle(file, kind), DEBOUNCE_MS, TimeUnit.MILLISECONDS);
        });
  }

  private void handle(Path file, WatchEvent.Kind<?> kind) {
    try {
      if (kind == ENTRY_DELETE || !Files.exists(file)) {
        onDeleted(file);
      } else {
        onCreatedOrModified(file);
      }
    } catch (Throwable t) {
      log.error("Lỗi xử lý plugin {} ({})", file.getFileName(), kind.name(), t);
    } finally {
      pending.remove(file);
    }
  }

  private void onDeleted(Path file) {
    String id = service.pluginIdOf(file);
    if (id == null) {
      log.debug("Bỏ qua xoá, không có plugin ứng với {}", file.getFileName());
      return;
    }
    log.info("Gỡ plugin '{}' ({} bị xoá)", id, file.getFileName());
    service.unloadOne(id);
  }

  private void onCreatedOrModified(Path file) throws InterruptedException {
    if (!waitUntilStable(file)) {
      log.warn("File {} chưa ổn định sau khi chờ, bỏ qua lần này", file.getFileName());
      return;
    }
    String id = service.pluginIdOf(file);
    if (id != null) {
      log.info("Reload plugin '{}' ({} thay đổi)", id, file.getFileName());
      service.unloadOne(id); // MODIFY = unload rồi load lại
    } else {
      log.info("Nạp plugin mới từ {}", file.getFileName());
    }
    service.loadOne(file);
  }

  /** Chờ đến khi kích thước file không đổi giữa hai lần kiểm tra (copy xong). */
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
      log.warn("Không đọc được kích thước {}", file, e);
    }
    return false;
  }

  @Override
  public void close() {
    running = false;
    worker.shutdownNow();
  }
}
