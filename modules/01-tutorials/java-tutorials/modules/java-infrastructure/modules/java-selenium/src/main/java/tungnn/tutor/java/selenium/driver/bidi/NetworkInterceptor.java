package tungnn.tutor.java.selenium.driver.bidi;

import java.util.function.Consumer;
import org.openqa.selenium.bidi.module.Network; // Đảm bảo đúng module Network của BiDi
import org.openqa.selenium.bidi.network.BeforeRequestSent;
import org.openqa.selenium.bidi.network.InterceptPhase;
import org.openqa.selenium.bidi.network.ResponseDetails;

/** Interface định nghĩa các lõi tính năng Network Interaction sử dụng WebDriver BiDi. */
public interface NetworkInterceptor extends AutoCloseable {

  /**
   * Lấy ra đối tượng điều khiển Network gốc của Selenium BiDi.
   *
   * @return Đối tượng org.openqa.selenium.bidi.module.Network
   */
  Network getNetwork();

  /** Kích hoạt tính năng tự động điền thông tin xác thực. */
  void addAuthenticationHandler(String username, String password);

  /** Đăng ký một bộ lắng nghe sự kiện xử lý dữ liệu của Request. */
  void addRequestHandler(Consumer<BeforeRequestSent> requestCallback);

  /** Đăng ký một bộ lắng nghe sự kiện xử lý dữ liệu của Response. */
  void addResponseHandler(Consumer<ResponseDetails> responseCallback);

  /**
   * Phương thức mặc định (default method) tự động đăng ký bộ lắng nghe để in các thông tin chi tiết
   * có sẵn của Response ra Console mà không làm crash ứng dụng.
   */
  default void addSimpleResponsePrintHandler() {
    addResponseHandler(
        response -> {
          try {
            String url = response.getResponseData().getUrl();
            long status = response.getResponseData().getStatus();
            String mimeType = response.getResponseData().getMimeType();

            // BiDi chỉ cung cấp Headers và thông tin khái quát của Content (như Size)
            long headersCount = response.getResponseData().getHeaders().size();

            System.out.printf(
                "[Response Log] URL: %s%n" + "               Status: %d | Type: %s | Headers: %d%n",
                url, status, mimeType, headersCount);
          } catch (Exception e) {
            System.err.println("[Response Log] Lỗi khi đọc dữ liệu response: " + e.getMessage());
          }
        });
  }

  /** Khởi tạo một điểm đánh chặn mạng (Network Intercept) dựa theo vòng đời (Phase). */
  String addNetworkIntercept(InterceptPhase phase);

  /** Gỡ bỏ một điểm đánh chặn mạng (Network Intercept) cụ thể ra khỏi Session. */
  void removeIntercept(String interceptId);

  /** Dọn dẹp sạch sẽ toàn bộ các điểm đánh chặn (Intercepts) đã đăng ký. */
  void clearAllIntercepts();
}
