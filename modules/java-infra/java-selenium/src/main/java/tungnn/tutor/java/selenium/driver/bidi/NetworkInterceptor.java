package tungnn.tutor.java.selenium.driver.bidi;

import java.util.function.Consumer;
import org.openqa.selenium.bidi.module.Network; // Đảm bảo đúng module Network của BiDi
import org.openqa.selenium.bidi.network.BeforeRequestSent;
import org.openqa.selenium.bidi.network.InterceptPhase;
import org.openqa.selenium.bidi.network.ResponseDetails;

public interface NetworkInterceptor extends AutoCloseable {

  Network getNetwork();

  void addAuthenticationHandler(String username, String password);

  void addRequestHandler(Consumer<BeforeRequestSent> requestCallback);

  void addResponseHandler(Consumer<ResponseDetails> responseCallback);

  default void addSimpleResponsePrintHandler() {
    addResponseHandler(response -> {
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

  String addNetworkIntercept(InterceptPhase phase);

  void removeIntercept(String interceptId);

  void clearAllIntercepts();
}
