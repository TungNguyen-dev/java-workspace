package tungnn.tutor.java.tool.todo.core.parse;

import java.time.LocalDate;
import java.util.regex.Pattern;
import tungnn.tutor.java.tool.todo.core.model.DateField;
import tungnn.tutor.java.tool.todo.core.model.Priority;

public final class TaskLineEditor {

    private TaskLineEditor() {
        throw new UnsupportedOperationException("cannot instantiate utility class");
    }

    public static String setStatus(String line, char symbol) {
        var matcher = TaskPatterns.LINE.matcher(line);
        requireTask(matcher.matches());
        return line.substring(0, matcher.start("status")) + symbol + line.substring(matcher.end("status"));
    }

    public static String setDate(String line, DateField field, LocalDate value) {
        var matcher = field.pattern().matcher(line);
        if (matcher.find()) {
            if (value == null) {
                return removeRange(line, matcher.start(), matcher.end());
            }
            return line.substring(0, matcher.start(1)) + value + line.substring(matcher.end(1));
        }
        return value == null ? line : insertToken(line, field.emoji() + " " + value);
    }

    public static String setPriority(String line, Priority priority) {
        var matcher = Priority.SYMBOL_PATTERN.matcher(line);
        if (matcher.find()) {
            return priority == Priority.NONE
                    ? removeRange(line, matcher.start(), matcher.end())
                    : line.substring(0, matcher.start()) + priority.symbol() + line.substring(matcher.end());
        }
        return priority == Priority.NONE ? line : insertToken(line, priority.symbol());
    }

    public static String setDescription(String line, String text) {
        var matcher = TaskPatterns.LINE.matcher(line);
        requireTask(matcher.matches());
        var body = matcher.group("body");
        var end = descriptionEnd(body);
        var rest = body.substring(end).stripLeading();
        var newBody = text.strip() + (rest.isEmpty() ? "" : " " + rest);
        return line.substring(0, matcher.start("body")) + newBody;
    }

    public static String addTag(String line, String tag) {
        var clean = TaskLineFormatter.stripHash(tag);
        var existing = Pattern.compile("(?:^|\\s)#" + Pattern.quote(clean) + "(?![\\p{L}\\p{N}_/-])");
        return existing.matcher(line).find() ? line : line.stripTrailing() + " #" + clean;
    }

    public static String removeTag(String line, String tag) {
        var clean = TaskLineFormatter.stripHash(tag);
        return Pattern.compile("\\s+#" + Pattern.quote(clean) + "(?![\\p{L}\\p{N}_/-])")
                .matcher(line)
                .replaceAll("");
    }

    private static int descriptionEnd(String body) {
        var end = body.length();
        for (var field : DateField.values()) {
            var matcher = field.pattern().matcher(body);
            if (matcher.find()) {
                end = Math.min(end, matcher.start());
            }
        }
        var priority = Priority.SYMBOL_PATTERN.matcher(body);
        if (priority.find()) {
            end = Math.min(end, priority.start());
        }
        var other = TaskPatterns.OTHER_MARKER.matcher(body);
        if (other.find()) {
            end = Math.min(end, other.start());
        }
        var tags = TaskPatterns.TRAILING_TAGS.matcher(body);
        if (tags.find()) {
            end = Math.min(end, tags.start());
        }
        return end;
    }

    private static String insertToken(String line, String token) {
        var tags = TaskPatterns.TRAILING_TAGS.matcher(line);
        if (tags.find()) {
            return line.substring(0, tags.start()) + " " + token + line.substring(tags.start());
        }
        return line.stripTrailing() + " " + token;
    }

    private static String removeRange(String line, int start, int end) {
        var from = start;
        while (from > 0 && Character.isWhitespace(line.charAt(from - 1))) {
            from--;
        }
        return line.substring(0, from) + line.substring(end);
    }

    private static void requireTask(boolean isTask) {
        if (!isTask) {
            throw new IllegalArgumentException("Line is not a task");
        }
    }
}
