package tungnn.tutor.java.tool.hub.plugin.samples;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import tungnn.tutor.java.tool.hub.api.ToolException;

final class JsonReformatter {

    private static final String INDENT = "  ";

    private JsonReformatter() {}

    static String reformat(String json, boolean pretty) throws ToolException {
        Objects.requireNonNull(json, "JSON must not be null");
        if (json.isBlank()) {
            throw new ToolException("JSON is empty");
        }

        StringBuilder out = new StringBuilder(json.length() + 16);
        Deque<Character> stack = new ArrayDeque<>();
        boolean inString = false;
        int n = json.length();

        for (int i = 0; i < n; i++) {
            char c = json.charAt(i);

            if (inString) {
                out.append(c);
                if (c == '\\' && i + 1 < n) {
                    out.append(json.charAt(++i));
                } else if (c == '"') {
                    inString = false;
                }
                continue;
            }

            switch (c) {
                case '"' -> {
                    inString = true;
                    out.append(c);
                }
                case '{', '[' -> {
                    stack.push(c);
                    out.append(c);
                    int next = nextNonWs(json, i + 1);
                    char close = c == '{' ? '}' : ']';
                    if (next < n && json.charAt(next) == close) {
                        out.append(close);
                        stack.pop();
                        i = next;
                    } else if (pretty) {
                        newline(out, stack.size());
                    }
                }
                case '}', ']' -> {
                    char open = c == '}' ? '{' : '[';
                    if (stack.isEmpty() || stack.pop() != open) {
                        throw new ToolException("Invalid JSON: '" + c + "' does not match at position " + i);
                    }
                    if (pretty) {
                        newline(out, stack.size());
                    }
                    out.append(c);
                }
                case ',' -> {
                    out.append(c);
                    if (pretty) {
                        newline(out, stack.size());
                    }
                }
                case ':' -> out.append(pretty ? ": " : ":");
                case ' ', '\t', '\n', '\r' -> {
                    // Skip whitespace outside of strings
                }
                default -> out.append(c);
            }
        }

        if (inString) {
            throw new ToolException("Invalid JSON: unterminated string");
        }
        if (!stack.isEmpty()) {
            throw new ToolException("Invalid JSON: missing '" + (stack.peek() == '{' ? '}' : ']') + "'");
        }
        if (out.isEmpty()) {
            throw new ToolException("JSON is empty");
        }
        return out.toString();
    }

    private static int nextNonWs(String s, int from) {
        int i = from;
        while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
            i++;
        }
        return i;
    }

    private static void newline(StringBuilder out, int depth) {
        out.append('\n').append(INDENT.repeat(depth));
    }
}
