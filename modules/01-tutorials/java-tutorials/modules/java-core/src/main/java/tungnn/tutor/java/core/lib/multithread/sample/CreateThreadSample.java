package tungnn.tutor.java.core.lib.multithread.sample;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class CreateThreadSample {

  static void main(String[] args) throws ExecutionException, InterruptedException {
    createTaskUsingRunnable();
    createTaskByExtendingThread();
    createTaskUsingCallableAndFutureTask();
  }

  private static void createTaskUsingRunnable() {
    Runnable task =
        () -> System.out.println("Hello Runnable from: " + Thread.currentThread().getName());

    Thread thread = new Thread(task);
    thread.start();
  }

  private static void createTaskByExtendingThread() {
    class CustomThread extends Thread {
      @Override
      public void run() {
        System.out.println("Hello Extended Thread from: " + Thread.currentThread().getName());
      }
    }

    CustomThread thread = new CustomThread();
    thread.start();
  }

  private static void createTaskUsingCallableAndFutureTask()
      throws ExecutionException, InterruptedException {
    Callable<String> task = () -> "Hello Callable from: " + Thread.currentThread().getName();

    FutureTask<String> futureTask = new FutureTask<>(task);

    Thread thread = new Thread(futureTask);
    thread.start();

    String result = futureTask.get();
    System.out.println(result);
  }
}
