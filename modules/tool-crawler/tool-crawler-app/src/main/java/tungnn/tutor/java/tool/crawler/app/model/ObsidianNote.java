package tungnn.tutor.java.tool.crawler.app.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record ObsidianNote(
        String title, String content, String url, List<String> references, String author, String email) {

    private static final String TEMPLATE = """
        ---
        id: %s
        title: "%s"
        author: %s
        email: %s
        date: %s
        tags:
        ---
        # %s

        %s

        ---

        ## Reference

        - [%s](%s)

        """;

    public String toMarkdown() {
        var now = LocalDateTime.now();
        var id = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        var date = now.toLocalDate().format(DateTimeFormatter.ISO_DATE);

        // Prevent a title containing double quotes from breaking the YAML frontmatter
        var yamlTitle = title.replace("\"", "\\\"");

        var sb = new StringBuilder();
        sb.append(String.format(TEMPLATE, id, yamlTitle, author, email, date, title, content, title, url));

        if (references != null && !references.isEmpty()) {
            for (var ref : references) {
                sb.append("- ").append(ref).append(System.lineSeparator());
            }
            sb.append(System.lineSeparator()).append("---").append(System.lineSeparator());
        }

        return sb.toString();
    }
}
