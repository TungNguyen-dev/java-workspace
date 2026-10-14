package tungnn.tutor.java.selenium.driver.bidi;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.HasBiDi;
import org.openqa.selenium.bidi.module.Network;
import org.openqa.selenium.bidi.network.AddInterceptParameters;
import org.openqa.selenium.bidi.network.BeforeRequestSent;
import org.openqa.selenium.bidi.network.InterceptPhase;
import org.openqa.selenium.bidi.network.ResponseDetails;

public class DefaultNetworkInterceptor implements NetworkInterceptor {
  private final Network network;
  private final List<String> interceptIds;

  public DefaultNetworkInterceptor(WebDriver driver) {
    if (!(driver instanceof HasBiDi)) {
      throw new IllegalArgumentException("Driver được cấp không hỗ trợ giao thức WebDriver BiDi!");
    }
    this.network = new Network(driver);
    this.interceptIds = new ArrayList<>();
  }

  @Override
  public Network getNetwork() {
    return network;
  }

  @Override
  public void addAuthenticationHandler(String username, String password) {
    network.onAuthRequired(
        authRequired -> {
          // Logic xử lý map credentials ngầm của Selenium BiDi
        });
  }

  @Override
  public void addRequestHandler(Consumer<BeforeRequestSent> requestCallback) {
    network.onBeforeRequestSent(requestCallback);
  }

  @Override
  public void addResponseHandler(Consumer<ResponseDetails> responseCallback) {
    network.onResponseStarted(responseCallback);
  }

  @Override
  public String addNetworkIntercept(InterceptPhase phase) {
    AddInterceptParameters params = new AddInterceptParameters(phase);
    String interceptId = network.addIntercept(params);
    interceptIds.add(interceptId);
    return interceptId;
  }

  @Override
  public void removeIntercept(String interceptId) {
    if (interceptId != null && !interceptId.isEmpty()) {
      network.removeIntercept(interceptId);
      interceptIds.remove(interceptId);
    }
  }

  @Override
  public void clearAllIntercepts() {
    List<String> targets = new ArrayList<>(interceptIds);
    for (String id : targets) {
      removeIntercept(id);
    }
  }

  @Override
  public void close() {
    clearAllIntercepts();
  }
}
