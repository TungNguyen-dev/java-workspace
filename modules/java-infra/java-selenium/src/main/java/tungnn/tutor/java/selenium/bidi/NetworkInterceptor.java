package tungnn.tutor.java.selenium.bidi;

import java.util.function.Consumer;
import org.openqa.selenium.bidi.module.Network; // Make sure this is the BiDi Network module
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

        // BiDi only exposes headers and general content info (such as size)
        long headersCount = response.getResponseData().getHeaders().size();

        System.out.printf(
            "[Response Log] URL: %s%n" + "               Status: %d | Type: %s | Headers: %d%n",
            url, status, mimeType, headersCount);
      } catch (Exception e) {
        System.err.println("[Response Log] Failed to read response data: " + e.getMessage());
      }
    });
  }

  String addNetworkIntercept(InterceptPhase phase);

  void removeIntercept(String interceptId);

  void clearAllIntercepts();
}
