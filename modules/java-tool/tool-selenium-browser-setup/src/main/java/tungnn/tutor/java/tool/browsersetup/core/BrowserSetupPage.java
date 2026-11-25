package tungnn.tutor.java.tool.browsersetup.core;

import java.util.Objects;

public record BrowserSetupPage(String name, String url, SetupAction action) {

    public BrowserSetupPage {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("url must not be blank");
        }
        Objects.requireNonNull(action, "action must not be null");
    }

    public static BrowserSetupPage of(String name, String url) {
        return new BrowserSetupPage(name, url, SetupAction.NO_OP);
    }

    public static BrowserSetupPage of(String name, String url, SetupAction action) {
        return new BrowserSetupPage(name, url, action);
    }
}
