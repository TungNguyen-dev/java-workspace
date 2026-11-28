package tungnn.tutor.java.tool.meddoc.core.glossary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import org.apache.commons.csv.CSVRecord;
import tungnn.tutor.java.csv.CsvFormatUtils;

public final class GlossaryFormatUtils {

    private static final Pattern SEPARATOR_CELL = Pattern.compile(":?-{3,}:?");
    private static final List<String> HEADER_NAMES = List.of("source", "中文", "原文");

    private GlossaryFormatUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static Glossary parse(String fileName, Reader reader) throws IOException {
        var name = fileName.toLowerCase(Locale.ROOT);
        if (name.endsWith(".csv")) {
            return fromCsv(fileName, reader, ',');
        }
        if (name.endsWith(".tsv")) {
            return fromCsv(fileName, reader, '\t');
        }
        if (name.endsWith(".md")) {
            return fromMarkdown(reader);
        }
        throw new IllegalArgumentException("Unsupported glossary file '" + fileName + "'. Use .csv, .tsv or .md");
    }

    private static Glossary fromCsv(String fileName, Reader reader, char delimiter) throws IOException {
        var format = CsvFormatUtils.createFormat(builder -> builder.setDelimiter(delimiter));
        var entries = new ArrayList<GlossaryEntry>();
        try (var parser = format.parse(reader)) {
            var header = parser.getHeaderMap();
            if (header == null || !header.containsKey("source") || !header.containsKey("target")) {
                throw new IllegalArgumentException(
                        "Glossary file " + fileName + " needs the header: source,target[,context]");
            }
            for (CSVRecord csvRecord : parser) {
                var source = cell(csvRecord, "source");
                var target = cell(csvRecord, "target");
                if (source != null && target != null) {
                    entries.add(new GlossaryEntry(source, target, cell(csvRecord, "context")));
                }
            }
        }
        return new Glossary(entries);
    }

    private static String cell(CSVRecord csvRecord, String column) {
        if (!csvRecord.isMapped(column) || !csvRecord.isSet(column)) {
            return null;
        }
        var value = csvRecord.get(column);
        return value == null || value.isBlank() ? null : value;
    }

    private static Glossary fromMarkdown(Reader reader) throws IOException {
        var entries = new ArrayList<GlossaryEntry>();
        var lines = new BufferedReader(reader).lines().toList();
        for (var line : lines) {
            var stripped = line.strip();
            if (!stripped.startsWith("|")) {
                continue;
            }
            var cells = stripped.replaceAll("^\\|\\s*|\\s*\\|$", "").split("\\s*(?<!\\\\)\\|\\s*", -1);
            if (cells.length < 2 || isHeaderOrSeparator(cells[0])) {
                continue;
            }
            var source = cells[0].strip();
            var target = cells[1].strip().replace("\\|", "|");
            if (source.isEmpty() || target.isEmpty()) {
                continue;
            }
            var context = cells.length > 2 ? cells[2].strip().replace("\\|", "|") : null;
            entries.add(new GlossaryEntry(source, target, context));
        }
        return new Glossary(entries);
    }

    private static boolean isHeaderOrSeparator(String firstCell) {
        var value = firstCell.strip();
        return SEPARATOR_CELL.matcher(value).matches() || HEADER_NAMES.contains(value.toLowerCase(Locale.ROOT));
    }
}
