package tungnn.tutor.java.obsidian.report;

import java.util.List;
import java.util.Map;

public record VaultStats(
        int totalNotes,
        Map<String, Integer> notesPerFolder,
        Map<String, Integer> tagCounts,
        List<String> noFrontmatter) {}
