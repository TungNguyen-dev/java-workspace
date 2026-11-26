package tungnn.tutor.java.tool.browsersetup.utils;

import java.awt.GraphicsEnvironment;
import java.util.Optional;
import tungnn.tutor.java.tool.browsersetup.core.WindowBounds;

public final class ScreenUtils {

    private ScreenUtils() {
        throw new UnsupportedOperationException("Cannot instantiate a utility class");
    }

    public static Optional<WindowBounds> availableBounds() {
        // Without this flag AWT adds a second Java icon to the macOS Dock while the tool runs.
        System.setProperty("apple.awt.UIElement", "true");
        try {
            var bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
            return Optional.of(new WindowBounds(bounds.x, bounds.y, bounds.width, bounds.height));
        } catch (RuntimeException | Error e) {
            return Optional.empty();
        }
    }
}
