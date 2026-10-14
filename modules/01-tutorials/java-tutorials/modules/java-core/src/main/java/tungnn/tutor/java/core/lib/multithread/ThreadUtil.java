package tungnn.tutor.java.core.lib.multithread;

public final class ThreadUtil {

  private ThreadUtil() {}

  public static void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      /*
       * Restore interrupted status: Khi InterruptedException xuất hiện, Java tự động xóa (clear) cờ "interrupted" của Thread về false.
       * Việc gọi interrupt() ở đây nhằm thiết lập lại cờ báo hiệu ngắt (interrupted flag = true)
       * để các đoạn code cấp cao hơn (hoặc caller của phương thức này) có thể biết luồng đã bị ngắt và xử lý đúng logic ngắt (ví dụ: dừng loop, dọn dẹp tài nguyên,...).
       */
      Thread.currentThread().interrupt();

      throw new RuntimeException(e);
    }
  }
}
