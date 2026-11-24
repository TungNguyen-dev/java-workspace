package tungnn.tutor.java.docker.support;

import com.github.dockerjava.api.async.ResultCallbackTemplate;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public final class DockerAwait {
    private DockerAwait() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static <C extends ResultCallbackTemplate<?, ?>> C await(C callback, Duration timeout, String operation) {
        try {
            if (!callback.awaitCompletion(timeout.toMillis(), TimeUnit.MILLISECONDS)) {
                closeQuietly(callback);
                throw new DockerOperationException(operation + " timed out after " + timeout);
            }
            return callback;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            closeQuietly(callback);
            throw new DockerOperationException(operation + " was interrupted", e);
        }
    }

    public static void closeQuietly(AutoCloseable closeable) {
        try {
            closeable.close();
        } catch (Exception e) {
            // best effort: the original timeout or interrupt is the failure worth reporting
        }
    }
}
