package tungnn.tutor.java.core.lib.multithread;

public final class ThreadUtils {

    private ThreadUtils() {}

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            /*
             * Restore interrupted status: when InterruptedException is thrown, Java automatically clears the thread's "interrupted" flag (sets it to false).
             * Calling interrupt() here restores the interrupted flag (interrupted flag = true)
             * so higher-level code (or the caller of this method) can tell the thread was interrupted and handle it properly (e.g. stop a loop, release resources,...).
             */
            Thread.currentThread().interrupt();

            throw new RuntimeException(e);
        }
    }
}
