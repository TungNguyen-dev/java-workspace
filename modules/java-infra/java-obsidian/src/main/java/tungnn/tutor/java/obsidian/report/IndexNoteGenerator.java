package tungnn.tutor.java.obsidian.report;

import java.util.Comparator;
import java.util.regex.Pattern;
import tungnn.tutor.java.obsidian.vault.VaultService;

public class IndexNoteGenerator {

    private static final Pattern DIGITS = Pattern.compile("\\d+");

    private final VaultService vault;
    private final ReportPublisher publisher;

    public IndexNoteGenerator(VaultService vault, ReportPublisher publisher) {
        this.vault = vault;
        this.publisher = publisher;
    }

    public String generate(String folder) {
        var notes = vault.files(folder).stream()
                .filter(p -> p.endsWith(".md"))
                .filter(p -> !p.endsWith("/_Index.md"))
                .sorted(Comparator.comparing(IndexNoteGenerator::naturalKey))
                .toList();
        return publisher.publishIndex(folder, notes);
    }

    private static String naturalKey(String path) {
        return DIGITS.matcher(path).replaceAll(m -> "%010d".formatted(Long.parseLong(m.group())));
    }
}
