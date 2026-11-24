package tungnn.tutor.java.obsidian.note;

public record NoteDraft(String path, String title, Frontmatter frontmatter, String body) {

    public String toMarkdown() {
        var sb = new StringBuilder();
        if (frontmatter != null) {
            sb.append(frontmatter.toYaml()).append('\n');
        }
        if (title != null && !title.isBlank()) {
            sb.append("# ").append(title).append("\n\n");
        }
        if (body != null) {
            sb.append(body);
            if (!body.endsWith("\n")) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }
}
