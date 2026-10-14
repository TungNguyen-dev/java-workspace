package tungnn.tutor.java.starter.infrastructure.obsidian;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record ObsidianNote(String title, String content, List<String> references) {

  private static final String TEMPLATE =
      """
      ---
      id: {{id}}
      title: {{title}}
      author: TungNN
      email: tungnn.hn@gmail.com
      date: {{date}}
      tags:
      - java
      - jls26
      ---
      ---

      # {{title}}

      {{content}}

      ---

      """;

  public String toMarkdown() {
    var now = LocalDateTime.now();
    var today = now.toLocalDate();

    var id = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(now);
    var date = today.format(DateTimeFormatter.ISO_DATE);

    var sb = new StringBuilder();

    sb.append(
        TEMPLATE
            .replace("{{id}}", id)
            .replace("{{date}}", date)
            .replace("{{title}}", title)
            .replace("{{content}}", content));

    if (!references.isEmpty()) {
      sb.append("## References").repeat(System.lineSeparator(), 2);

      for (String ref : references) {
        sb.append("- ").append(ref).append(System.lineSeparator());
      }

      sb.append(System.lineSeparator()).append("---").append(System.lineSeparator());
    }

    return sb.toString();
  }
}
