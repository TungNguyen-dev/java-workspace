package tungnn.tutor.java.obsidian;

import java.time.LocalDate;
import java.util.List;
import tungnn.tutor.java.obsidian.note.Frontmatter;
import tungnn.tutor.java.obsidian.note.NoteDraft;
import tungnn.tutor.java.obsidian.note.NoteRef;
import tungnn.tutor.java.obsidian.report.PeriodicReport;

public final class ObsidianUtils {

    private static final class Holder {
        static final Obsidian DEFAULT = Obsidian.fromEnv();
    }

    private ObsidianUtils() {}

    public static String createNote(String path, String title, String body, String... tags) {
        return createNote(Holder.DEFAULT, path, title, body, tags);
    }

    public static String createNote(Obsidian obsidian, String path, String title, String body, String... tags) {
        var author = obsidian.config().author();
        var frontmatter = Frontmatter.standard(title, author, null, List.of(tags));
        obsidian.notes().create(new NoteDraft(path, title, frontmatter, body), false);
        return path;
    }

    public static String createNoteFromTemplate(String template, String path) {
        return createNoteFromTemplate(Holder.DEFAULT, template, path);
    }

    public static String createNoteFromTemplate(Obsidian obsidian, String template, String path) {
        obsidian.templates().createNoteFrom(template, path);
        return path;
    }

    public static void saveTemplate(String name, String content) {
        saveTemplate(Holder.DEFAULT, name, content);
    }

    public static void saveTemplate(Obsidian obsidian, String name, String content) {
        obsidian.templates().create(name, content);
    }

    public static void appendToDailyNote(String content) {
        appendToDailyNote(Holder.DEFAULT, content);
    }

    public static void appendToDailyNote(Obsidian obsidian, String content) {
        obsidian.notes().appendToDaily(content, null);
    }

    public static String readNote(String path) {
        return Holder.DEFAULT.notes().read(NoteRef.byPath(path));
    }

    public static List<String> search(String query, int limit) {
        return Holder.DEFAULT.vault().search(query, limit);
    }

    public static String publishVaultStats() {
        return publishVaultStats(Holder.DEFAULT);
    }

    public static String publishVaultStats(Obsidian obsidian) {
        return obsidian.publisher().publish(obsidian.reports().buildStats(), LocalDate.now());
    }

    public static String publishLinkHealth() {
        return publishLinkHealth(Holder.DEFAULT);
    }

    public static String publishLinkHealth(Obsidian obsidian) {
        return obsidian.publisher().publish(obsidian.reports().buildLinkHealth(), LocalDate.now());
    }

    public static String publishDailyReport() {
        return publishPeriodic(Holder.DEFAULT, PeriodicReport.Period.DAILY);
    }

    public static String publishWeeklyReport() {
        return publishPeriodic(Holder.DEFAULT, PeriodicReport.Period.WEEKLY);
    }

    public static String publishPeriodic(Obsidian obsidian, PeriodicReport.Period period) {
        return obsidian.publisher().publish(obsidian.reports().buildPeriodic(period, LocalDate.now()));
    }

    public static String generateIndex(String folder) {
        return generateIndex(Holder.DEFAULT, folder);
    }

    public static String generateIndex(Obsidian obsidian, String folder) {
        return obsidian.indexes().generate(folder);
    }
}
