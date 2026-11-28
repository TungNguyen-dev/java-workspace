package tungnn.tutor.java.tool.finance.utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public final class PromptUtils {

    private PromptUtils() {
        throw new UnsupportedOperationException("cannot instantiate utility class");
    }

    public static String load(String name) {
        try (var in = PromptUtils.class.getResourceAsStream("/prompts/" + name)) {
            if (in == null) {
                throw new IllegalStateException("Prompt not found: " + name);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read prompt " + name, e);
        }
    }
}
