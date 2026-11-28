package tungnn.tutor.java.tool.meddoc.core.glossary;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public record Glossary(Map<String, String> terms) {

    public static final Glossary EMPTY = new Glossary(Map.of());

    public Glossary {
        terms = Map.copyOf(terms);
    }

    public static Glossary parse(String answer) {
        var terms = new LinkedHashMap<String, String>();
        if (answer == null) {
            return EMPTY;
        }
        for (var line : answer.split("\\R")) {
            var cells = line.strip().replaceAll("^\\|\\s*|\\s*\\|$", "").split("\\s*\\|\\s*");
            if (cells.length < 2) {
                continue;
            }
            var source = cells[0].strip();
            var target = cells[1].strip();
            if (source.isEmpty() || source.equals("中文") || target.isEmpty() || source.startsWith("-") || !source.codePoints().anyMatch(Character::isIdeographic)) {
                continue;
            }
            terms.putIfAbsent(source, target);
        }
        return new Glossary(terms);
    }

    public boolean isEmpty() {
        return terms.isEmpty();
    }

    public String promptSectionFor(String text) {
        var matching = terms.entrySet().stream()
                .filter(e -> text.contains(e.getKey()))
                .map(e -> "- " + e.getKey() + " → " + e.getValue())
                .collect(Collectors.joining("\n"));
        if (matching.isEmpty()) {
            return "";
        }
        return "\n\n# Bảng thuật ngữ bắt buộc\n\nVới các thuật ngữ sau xuất hiện trong đoạn, dùng đúng cách dịch đã cho:\n\n"
                + matching
                + "\n";
    }

    public String render() {
        var sb = new StringBuilder("# Glossary\n\n| Source | Translation |\n| --- | --- |\n");
        terms.forEach((source, target) -> sb.append("| %s | %s |%n".formatted(source, target)));
        return sb.toString();
    }
}
