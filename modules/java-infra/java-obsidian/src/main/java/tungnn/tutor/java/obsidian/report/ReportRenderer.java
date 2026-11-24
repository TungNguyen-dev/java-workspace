package tungnn.tutor.java.obsidian.report;

import java.util.List;

public final class ReportRenderer {

    private ReportRenderer() {}

    public static String render(VaultStats stats) {
        var sb = new StringBuilder("# Vault Statistics\n\n");
        sb.append("Total notes: **").append(stats.totalNotes()).append("**\n\n");
        sb.append("## Notes per folder\n\n| Folder | Notes |\n| --- | --- |\n");
        stats.notesPerFolder()
                .forEach((k, v) ->
                        sb.append("| ").append(k).append(" | ").append(v).append(" |\n"));
        sb.append("\n## Tags\n\n| Tag | Count |\n| --- | --- |\n");
        stats.tagCounts()
                .forEach((k, v) ->
                        sb.append("| ").append(k).append(" | ").append(v).append(" |\n"));
        sb.append("\n## Notes without frontmatter\n\n");
        appendLinks(sb, stats.noFrontmatter());
        return sb.toString();
    }

    public static String render(LinkHealthReport report) {
        var sb = new StringBuilder("# Link Health\n\n## Unresolved links\n\n");
        if (report.unresolved().isEmpty()) {
            sb.append("None.\n");
        }
        report.unresolved().forEach((target, sources) -> {
            sb.append("- `").append(target).append("` referenced by ");
            sb.append(String.join(
                    ", ", sources.stream().map(s -> "[[" + stripExt(s) + "]]").toList()));
            sb.append('\n');
        });
        sb.append("\n## Orphans\n\n");
        appendLinks(sb, report.orphans());
        sb.append("\n## Dead ends\n\n");
        appendLinks(sb, report.deadends());
        return sb.toString();
    }

    public static String render(PeriodicReport report) {
        var title = report.period() == PeriodicReport.Period.DAILY
                ? "Daily report " + report.from()
                : "Weekly report " + report.from() + " to " + report.to();
        var sb = new StringBuilder("# ").append(title).append("\n\n## Created\n\n");
        appendLinks(sb, report.createdNotes());
        sb.append("\n## Modified\n\n");
        appendLinks(sb, report.modifiedNotes());
        return sb.toString();
    }

    public static String renderIndex(String folderName, List<String> notes) {
        var sb = new StringBuilder("# ").append(folderName).append("\n\n");
        appendLinks(sb, notes);
        return sb.toString();
    }

    private static void appendLinks(StringBuilder sb, List<String> paths) {
        if (paths.isEmpty()) {
            sb.append("None.\n");
            return;
        }
        paths.forEach(p -> sb.append("- [[").append(stripExt(p)).append("]]\n"));
    }

    private static String stripExt(String path) {
        return path.endsWith(".md") ? path.substring(0, path.length() - 3) : path;
    }
}
