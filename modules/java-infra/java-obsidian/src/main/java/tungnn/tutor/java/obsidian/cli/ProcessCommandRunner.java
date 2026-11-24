package tungnn.tutor.java.obsidian.cli;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ProcessCommandRunner implements CommandRunner {

    private final Duration timeout;

    public ProcessCommandRunner(Duration timeout) {
        this.timeout = timeout;
    }

    @Override
    public CommandResult run(List<String> command) {
        try {
            var process = new ProcessBuilder(command).start();
            process.getOutputStream().close();

            var stdout = CompletableFuture.supplyAsync(() -> read(process.getInputStream()));
            var stderr = CompletableFuture.supplyAsync(() -> read(process.getErrorStream()));

            if (!process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS)) {
                process.destroyForcibly();
                throw new ObsidianCliException("Obsidian CLI timed out after " + timeout + ": " + command);
            }
            return new CommandResult(process.exitValue(), stdout.join(), stderr.join());
        } catch (IOException e) {
            throw new ObsidianCliException("Failed to start Obsidian CLI: " + command.getFirst(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ObsidianCliException("Interrupted while running Obsidian CLI", e);
        }
    }

    private static String read(java.io.InputStream in) {
        try (in) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new java.io.UncheckedIOException(e);
        }
    }
}
