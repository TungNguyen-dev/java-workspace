package tungnn.tutor.java.obsidian.report;

import java.time.LocalDate;
import java.time.temporal.IsoFields;
import tungnn.tutor.java.obsidian.note.Frontmatter;
import tungnn.tutor.java.obsidian.note.NoteDraft;
import tungnn.tutor.java.obsidian.note.NoteService;

public class ReportPublisher {

    private final NoteService notes;
    private final String reportsFolder;

    public ReportPublisher(NoteService notes, String reportsFolder) {
        this.notes = notes;
        this.reportsFolder = reportsFolder;
    }

    public String publish(VaultStats stats, LocalDate date) {
        return write("Vault Stats " + date, ReportRenderer.render(stats));
    }

    public String publish(LinkHealthReport report, LocalDate date) {
        return write("Link Health " + date, ReportRenderer.render(report));
    }

    public String publish(PeriodicReport report) {
        if (report.period() == PeriodicReport.Period.DAILY) {
            notes.appendToDaily("\n## Report\n\n" + body(report), null);
            return "daily:" + report.from();
        }
        var week = "%d-W%02d"
                .formatted(
                        report.from().get(IsoFields.WEEK_BASED_YEAR),
                        report.from().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
        return write("Weekly " + week, ReportRenderer.render(report));
    }

    public String publishIndex(String folder, java.util.List<String> notePaths) {
        var path = folder + "/_Index.md";
        var name = folder.substring(folder.lastIndexOf('/') + 1);
        notes.create(new NoteDraft(path, null, null, ReportRenderer.renderIndex(name, notePaths)), true);
        return path;
    }

    private String write(String title, String markdown) {
        var path = reportsFolder + "/" + title + ".md";
        var frontmatter = Frontmatter.create()
                .put("title", title)
                .put("date", LocalDate.now().toString())
                .tag("report");
        notes.create(new NoteDraft(path, null, frontmatter, markdown), true);
        return path;
    }

    private static String body(PeriodicReport report) {
        var rendered = ReportRenderer.render(report);
        return rendered.substring(rendered.indexOf("## Created"));
    }
}
