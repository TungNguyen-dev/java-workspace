package tungnn.tutor.java.tool.todo.core.model;

import java.util.regex.Pattern;

public enum DateField {
    DUE("📅", "📆", "🗓"),
    SCHEDULED("⏳", "⌛"),
    START("🛫"),
    CREATED("➕"),
    DONE("✅"),
    CANCELLED("❌");

    private final String emoji;
    private final Pattern pattern;

    DateField(String... emojis) {
        this.emoji = emojis[0];
        this.pattern = Pattern.compile("(?:" + String.join("|", emojis) + ")\\uFE0F?\\s*(\\d{4}-\\d{2}-\\d{2})");
    }

    public String emoji() {
        return emoji;
    }

    public Pattern pattern() {
        return pattern;
    }
}
