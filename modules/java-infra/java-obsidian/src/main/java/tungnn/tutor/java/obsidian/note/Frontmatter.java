package tungnn.tutor.java.obsidian.note;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Frontmatter {

    private final Map<String, String> scalars = new LinkedHashMap<>();
    private final List<String> tags = new ArrayList<>();

    public static Frontmatter create() {
        return new Frontmatter();
    }

    public static Frontmatter standard(String title, String author, String email, List<String> tags) {
        var now = LocalDateTime.now();
        var fm = create().put(
                        "id", DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(now))
                .put("title", title)
                .put("author", author)
                .put("email", email)
                .put("date", LocalDate.from(now).toString());
        tags.forEach(fm::tag);
        return fm;
    }

    public Frontmatter put(String key, String value) {
        if (value != null && !value.isBlank()) {
            scalars.put(key, value);
        }
        return this;
    }

    public Frontmatter tag(String tag) {
        tags.add(tag);
        return this;
    }

    public String toYaml() {
        var sb = new StringBuilder("---\n");
        scalars.forEach((k, v) -> sb.append(k).append(": ").append(yamlValue(v)).append('\n'));
        if (!tags.isEmpty()) {
            sb.append("tags:\n");
            tags.forEach(t -> sb.append("  - ").append(t).append('\n'));
        }
        return sb.append("---\n").toString();
    }

    private static String yamlValue(String value) {
        var needsQuotes = value.contains(":") || value.contains("#") || value.contains("\"") || value.startsWith("-");
        return needsQuotes ? "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"" : value;
    }
}
