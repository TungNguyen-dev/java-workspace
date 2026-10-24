package tungnn.tutor.java.core.lib.multithread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Function;

public final class ConcurrentUtils {

  private ConcurrentUtils() {
    throw new UnsupportedOperationException("Cannot instantiate a utility class");
  }

  public static <R> List<R> executeConcurrently(int count, Callable<R> task) {
    Objects.requireNonNull(task, "task must not be null");
    if (count < 0) {
      throw new IllegalArgumentException("count must not be negative");
    }
    return runAll(Collections.nCopies(count, task), null);
  }

  public static <R> List<R> executeConcurrently(int count, Callable<R> task, int maxConcurrency) {
    Objects.requireNonNull(task, "task must not be null");
    if (count < 0) {
      throw new IllegalArgumentException("count must not be negative");
    }
    return runAll(Collections.nCopies(count, task), newSemaphore(maxConcurrency));
  }

  public static <A, R> List<R> executeConcurrently(List<A> args, Function<A, R> task) {
    return executeConcurrently(args, task, null);
  }

  public static <A, R> List<R> executeConcurrently(
      List<A> args, Function<A, R> task, int maxConcurrency) {
    return executeConcurrently(args, task, newSemaphore(maxConcurrency));
  }

  private static <A, R> List<R> executeConcurrently(
      List<A> args, Function<A, R> task, Semaphore semaphore) {
    Objects.requireNonNull(args, "args must not be null");
    Objects.requireNonNull(task, "task must not be null");

    if (args.isEmpty()) {
      return List.of();
    }

    // Defensive copy to guarantee thread-safety and prevent external list mutations
    var safeArgs = List.copyOf(args);
    var tasks = safeArgs.stream().map(arg -> (Callable<R>) () -> task.apply(arg)).toList();

    return runAll(tasks, semaphore);
  }

  private static <R> List<R> runAll(List<? extends Callable<R>> tasks, Semaphore semaphore) {
    if (tasks.isEmpty()) {
      return List.of();
    }

    // try-with-resources implicitly awaits termination of all virtual threads
    try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
      var futures =
          tasks.stream()
              .map(task -> limited(task, semaphore))
              .map(task -> CompletableFuture.supplyAsync(() -> call(task), executor))
              .toList();

      return joinAllResults(futures);
    }
  }

  private static Semaphore newSemaphore(int maxConcurrency) {
    if (maxConcurrency <= 0) {
      throw new IllegalArgumentException("maxConcurrency must be greater than 0");
    }
    // fair = true: các task lấy permit theo thứ tự chờ (FIFO) nên gần giống thứ tự submit
    return new Semaphore(maxConcurrency, true);
  }

  private static <R> Callable<R> limited(Callable<R> task, Semaphore semaphore) {
    if (semaphore == null) {
      return task;
    }
    return () -> {
      semaphore.acquire();
      try {
        return task.call();
      } finally {
        semaphore.release();
      }
    };
  }

  private static <R> R call(Callable<R> task) {
    try {
      return task.call();
    } catch (Exception e) {
      // Wrap checked exceptions in CompletionException to propagate through CompletableFuture
      // stages safely
      throw new CompletionException(e);
    }
  }

  private static <R> List<R> joinAllResults(List<CompletableFuture<R>> futures) {
    var results = new ArrayList<R>(futures.size());
    Throwable firstFailure = null;

    // Await all tasks and preserve insertion order
    for (var future : futures) {
      try {
        results.add(future.join());
      } catch (CompletionException e) {
        // Record the first encountered failure for propagation after all tasks complete
        if (firstFailure == null) {
          firstFailure = (e.getCause() != null) ? e.getCause() : e;
        }
      } catch (CancellationException e) {
        if (firstFailure == null) {
          firstFailure = e;
        }
      }
    }

    // Unwrap and rethrow the cause to preserve original exception types
    if (firstFailure != null) {
      if (firstFailure instanceof RuntimeException re) {
        throw re;
      }
      if (firstFailure instanceof Error err) {
        throw err;
      }
      throw new CompletionException(firstFailure);
    }

    return Collections.unmodifiableList(results);
  }
}
