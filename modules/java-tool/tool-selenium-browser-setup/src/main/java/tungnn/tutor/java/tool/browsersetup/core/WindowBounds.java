package tungnn.tutor.java.tool.browsersetup.core;

public record WindowBounds(int x, int y, int width, int height) {

    public WindowBounds {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("width and height must be positive: %dx%d".formatted(width, height));
        }
    }
}
