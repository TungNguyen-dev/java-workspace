package tungnn.tutor.java.tool.todo.core.model;

import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;

public enum Priority {
    HIGHEST("🔺"),
    HIGH("⏫"),
    MEDIUM("🔼"),
    NONE(""),
    LOW("🔽"),
    LOWEST("⏬");

    public static final Pattern SYMBOL_PATTERN = Pattern.compile("(?:🔺|⏫|🔼|🔽|⏬)\\uFE0F?");

    private final String symbol;

    Priority(String symbol) {
        this.symbol = symbol;
    }

    public String symbol() {
        return symbol;
    }

    public static Priority fromSymbol(String text) {
        return Arrays.stream(values())
                .filter(p -> !p.symbol.isEmpty() && text.startsWith(p.symbol))
                .findFirst()
                .orElse(NONE);
    }

    public static Priority parse(String name) {
        try {
            return valueOf(name.strip().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Unknown priority: " + name + " (use highest, high, medium, low, lowest or none)", e);
        }
    }
}
