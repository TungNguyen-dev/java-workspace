package tungnn.tutor.java.tool.browsersetup.utils;

import java.util.List;
import tungnn.tutor.java.tool.browsersetup.core.BrowserSetupPage;

public final class BrowserSetupPageUtils {

    private BrowserSetupPageUtils() {
        throw new UnsupportedOperationException("Cannot instantiate a utility class");
    }

    public static List<BrowserSetupPage> defaults() {
        return List.of(
                BrowserSetupPage.of("gemini", "https://gemini.google.com/app"),
                BrowserSetupPage.of("coursera", "https://www.coursera.org/"),
                BrowserSetupPage.of("youtube", "https://www.youtube.com/"));
    }
}
