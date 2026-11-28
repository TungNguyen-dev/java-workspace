package tungnn.tutor.java.tool.meddoc.core.glossary;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public record Glossary(List<GlossaryEntry> entries) {

    public static final Glossary EMPTY = new Glossary(List.of());

    public Glossary {
        entries = List.copyOf(new LinkedHashSet<>(entries));
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public int size() {
        return entries.size();
    }

    public List<GlossaryEntry> matching(String text) {
        if (text == null || text.isEmpty() || entries.isEmpty()) {
            return List.of();
        }
        var bySource = entries.stream()
                .collect(Collectors.groupingBy(GlossaryEntry::source, LinkedHashMap::new, Collectors.toList()));
        var sources = new ArrayList<>(bySource.keySet());
        // Longest first, so "生石膏" claims its characters before "石膏" can match inside it
        sources.sort(Comparator.comparingInt(String::length).reversed());

        var covered = new boolean[text.length()];
        var firstPosition = new TreeMap<Integer, String>();
        for (var source : sources) {
            int first = -1;
            int from = 0;
            int at;
            while ((at = text.indexOf(source, from)) >= 0) {
                int end = at + source.length();
                if (isFree(covered, at, end)) {
                    java.util.Arrays.fill(covered, at, end, true);
                    if (first < 0) {
                        first = at;
                    }
                }
                from = at + 1;
            }
            if (first >= 0) {
                firstPosition.put(first, source);
            }
        }
        var matched = new ArrayList<GlossaryEntry>();
        firstPosition.values().forEach(source -> matched.addAll(bySource.get(source)));
        return matched;
    }

    public Glossary overriddenBy(Glossary other) {
        var overridden = other.entries.stream().map(GlossaryEntry::source).collect(Collectors.toSet());
        var merged = new ArrayList<>(entries.stream().filter(e -> !overridden.contains(e.source())).toList());
        merged.addAll(other.entries);
        return new Glossary(merged);
    }

    public Glossary restrictTo(String documentText) {
        return new Glossary(matching(documentText));
    }

    public String promptSectionFor(String text) {
        var matching = matching(text);
        if (matching.isEmpty()) {
            return "";
        }
        var bySource = new LinkedHashMap<String, List<GlossaryEntry>>();
        matching.forEach(e ->
                bySource.computeIfAbsent(e.source(), k -> new ArrayList<>()).add(e));
        boolean ambiguous = bySource.values().stream().anyMatch(list -> list.size() > 1);

        var lines = matching.stream().map(Glossary::promptLine).collect(Collectors.joining("\n"));
        var section = new StringBuilder("\n\n# Bảng thuật ngữ bắt buộc\n\n")
                .append("Với các thuật ngữ sau xuất hiện trong đoạn, dùng đúng cách dịch đã cho:\n\n")
                .append(lines)
                .append('\n');
        if (ambiguous) {
            section.append("\nMột số thuật ngữ có nhiều cách dịch: chọn cách hợp với ngữ cảnh của đoạn.\n");
        }
        return section.toString();
    }

    public String render() {
        var sb = new StringBuilder("# Glossary\n\n| Source | Translation | Context |\n| --- | --- | --- |\n");
        entries.forEach(e -> sb.append("| %s | %s | %s |%n"
                .formatted(cell(e.source()), cell(e.target()), e.hasContext() ? cell(e.context()) : "")));
        return sb.toString();
    }

    private static String promptLine(GlossaryEntry entry) {
        var line = "- " + entry.source() + " → " + entry.target();
        return entry.hasContext() ? line + " (ngữ cảnh: " + entry.context() + ")" : line;
    }

    private static String cell(String value) {
        return value.replace("|", "\\|");
    }

    private static boolean isFree(boolean[] covered, int from, int to) {
        for (int i = from; i < to; i++) {
            if (covered[i]) {
                return false;
            }
        }
        return true;
    }
}
