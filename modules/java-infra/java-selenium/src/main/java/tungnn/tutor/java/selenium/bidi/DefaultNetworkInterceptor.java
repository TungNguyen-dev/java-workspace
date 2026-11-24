package tungnn.tutor.java.selenium.bidi;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import org.openqa.selenium.UsernameAndPassword;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.BiDiException;
import org.openqa.selenium.bidi.HasBiDi;
import org.openqa.selenium.bidi.module.Network;
import org.openqa.selenium.bidi.network.AddInterceptParameters;
import org.openqa.selenium.bidi.network.BeforeRequestSent;
import org.openqa.selenium.bidi.network.InterceptPhase;
import org.openqa.selenium.bidi.network.ResponseDetails;

public class DefaultNetworkInterceptor implements NetworkInterceptor {
    private final Network network;
    private final List<String> interceptIds = new CopyOnWriteArrayList<>();

    public DefaultNetworkInterceptor(WebDriver driver) {
        if (!(driver instanceof HasBiDi)) {
            throw new IllegalArgumentException("WebDriver does not support BiDi: " + driver);
        }
        try {
            this.network = new Network(driver);
        } catch (BiDiException e) {
            throw new IllegalArgumentException(
                    "WebDriver has BiDi disabled. Create it with BiDi, e.g."
                            + " ChromeDriverUtils.createDriver(profile, true)",
                    e);
        }
    }

    @Override
    public Network getNetwork() {
        return network;
    }

    @Override
    public void addAuthenticationHandler(String username, String password) {
        var credentials = new UsernameAndPassword(username, password);
        addNetworkIntercept(InterceptPhase.AUTH_REQUIRED);
        network.onAuthRequired(
                response -> network.continueWithAuth(response.getRequest().getRequestId(), credentials));
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
        String interceptId = network.addIntercept(new AddInterceptParameters(phase));
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
        RuntimeException failure = null;
        for (String id : List.copyOf(interceptIds)) {
            try {
                removeIntercept(id);
            } catch (RuntimeException e) {
                interceptIds.remove(id);
                if (failure == null) {
                    failure = e;
                } else {
                    failure.addSuppressed(e);
                }
            }
        }
        if (failure != null) {
            throw failure;
        }
    }

    @Override
    public void close() {
        try {
            clearAllIntercepts();
        } finally {
            network.close();
        }
    }
}
