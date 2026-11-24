package tungnn.tutor.java.obsidian.report;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import tungnn.tutor.java.core.lib.io.filesystem.FileUtils;
import tungnn.tutor.java.obsidian.vault.VaultService;

public class ReportService {

    private final VaultService vault;

    public ReportService(VaultService vault) {
        this.vault = vault;
    }

    public VaultStats buildStats() {
        var root = vault.root();
        var notes = markdownFiles(root);
        var perFolder = new TreeMap<String, Integer>();
        var noFrontmatter = new ArrayList<String>();

        for (var note : notes) {
            var relative = root.relativize(note).toString();
            var slash = relative.indexOf('/');
            perFolder.merge(slash < 0 ? "(root)" : relative.substring(0, slash), 1, Integer::sum);
            if (!FileUtils.readString(note).stripLeading().startsWith("---")) {
                noFrontmatter.add(relative);
            }
        }

        var tags = new LinkedHashMap<String, Integer>();
        vault.tagCounts().entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(e -> tags.put(e.getKey(), e.getValue()));

        return new VaultStats(notes.size(), perFolder, tags, noFrontmatter);
    }

    public LinkHealthReport buildLinkHealth() {
        return new LinkHealthReport(vault.unresolved(), vault.orphans(), vault.deadends());
    }

    public PeriodicReport buildPeriodic(PeriodicReport.Period period, LocalDate date) {
        var from = period == PeriodicReport.Period.WEEKLY
                ? date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                : date;
        var to = period == PeriodicReport.Period.WEEKLY ? from.plusDays(6) : date;

        var root = vault.root();
        var created = new ArrayList<String>();
        var modified = new ArrayList<String>();

        for (var note : markdownFiles(root)) {
            var relative = root.relativize(note).toString();
            try {
                var attrs = Files.readAttributes(note, java.nio.file.attribute.BasicFileAttributes.class);
                var createdDay = toDate(attrs.creationTime());
                var modifiedDay = toDate(attrs.lastModifiedTime());
                if (within(createdDay, from, to)) {
                    created.add(relative);
                } else if (within(modifiedDay, from, to)) {
                    modified.add(relative);
                }
            } catch (java.io.IOException e) {
                throw new RuntimeException("Failed to read attributes: " + note, e);
            }
        }
        created.sort(Comparator.naturalOrder());
        modified.sort(Comparator.naturalOrder());
        return new PeriodicReport(period, from, to, created, modified);
    }

    private static boolean within(LocalDate day, LocalDate from, LocalDate to) {
        return !day.isBefore(from) && !day.isAfter(to);
    }

    private static LocalDate toDate(java.nio.file.attribute.FileTime time) {
        return time.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static java.util.List<Path> markdownFiles(Path root) {
        return FileUtils.walk(root).stream()
                .filter(Files::isRegularFile)
                .filter(p -> p.getFileName().toString().endsWith(".md"))
                .filter(p -> !root.relativize(p).startsWith(".obsidian"))
                .filter(p -> !root.relativize(p).startsWith(".trash"))
                .toList();
    }
}
