package tungnn.tutor.java.tool.browsersetup.utils;

import java.util.Objects;
import tungnn.tutor.java.tool.browsersetup.core.WindowBounds;

public final class WindowLayoutUtils {

    private WindowLayoutUtils() {
        throw new UnsupportedOperationException("Cannot instantiate a utility class");
    }

    public static int columns(int count) {
        return (int) Math.ceil(Math.sqrt(count));
    }

    public static int rows(int count) {
        return (int) Math.ceil((double) count / columns(count));
    }

    public static WindowBounds grid(WindowBounds screen, int count, int index) {
        Objects.requireNonNull(screen, "screen must not be null");
        if (count <= 0) {
            throw new IllegalArgumentException("count must be positive: " + count);
        }
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException("index must be in [0, %d): %d".formatted(count, index));
        }

        var columns = columns(count);
        var width = screen.width() / columns;
        var height = screen.height() / rows(count);
        return new WindowBounds(
                screen.x() + (index % columns) * width, screen.y() + (index / columns) * height, width, height);
    }
}
