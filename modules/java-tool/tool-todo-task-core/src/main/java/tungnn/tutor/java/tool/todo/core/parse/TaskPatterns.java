package tungnn.tutor.java.tool.todo.core.parse;

import java.util.regex.Pattern;

public final class TaskPatterns {

    public static final Pattern LINE =
            Pattern.compile("^(?<prefix>[\\s>]*)(?<bullet>[-*+]|\\d+[.)]) \\[(?<status>.)\\] (?<body>.*)$");

    public static final Pattern TAG = Pattern.compile("(?:^|\\s)#([\\p{L}\\p{N}_/-]+)");

    public static final Pattern TRAILING_TAGS = Pattern.compile("(?:\\s+#[\\p{L}\\p{N}_/-]+)+\\s*$");

    public static final Pattern FENCE = Pattern.compile("^[\\s>]*(```|~~~)");

    public static final Pattern OTHER_MARKER = Pattern.compile("🔁|🆔|⛔|🏁");

    private TaskPatterns() {
        throw new UnsupportedOperationException("cannot instantiate utility class");
    }
}
