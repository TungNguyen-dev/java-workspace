package tungnn.tutor.java.tool.doctrans.core.cache;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tungnn.tutor.java.csv.CsvFormatUtils;
import tungnn.tutor.java.csv.CsvUtils;
import tungnn.tutor.java.tool.doctrans.core.text.LanguageCode;

public class CsvFileTranslationCache implements TranslationCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvFileTranslationCache.class);
    private static final String FILE_NAME_PREFIX = "text-translation-memory_";
    private static final String FILE_EXTENSION = ".csv";
    private static final String COLUMN_SOURCE = "source";
    private static final String COLUMN_TRANSLATION = "translation";
    private static final List<String> HEADERS = List.of(COLUMN_SOURCE, COLUMN_TRANSLATION);

    private static final CSVFormat CSV_FORMAT = CsvFormatUtils.createFormat(b -> b.setTrim(false));

    private final Path cacheDir;
    private final Map<LanguageCode, LanguagePartition> partitions = new ConcurrentHashMap<>();

    public CsvFileTranslationCache(Path cacheDir) {
        this.cacheDir = Objects.requireNonNull(cacheDir, "cacheDir must not be null");
        try {
            Files.createDirectories(cacheDir);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to create cache directory: " + cacheDir, e);
        }
    }

    @Override
    public Optional<String> find(LanguageCode targetLanguage, String sourceText) {
        if (targetLanguage == null || sourceText == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(partitionOf(targetLanguage).entries().get(sourceText));
    }

    @Override
    public void save(LanguageCode targetLanguage, String sourceText, String translation) {
        if (targetLanguage == null || sourceText == null || translation == null) {
            return;
        }
        var partition = partitionOf(targetLanguage);
        synchronized (partition) {
            if (translation.equals(partition.entries().get(sourceText))) {
                return;
            }
            partition.entries().put(sourceText, translation);
            append(partition.file(), sourceText, translation);
        }
    }

    public Path cacheFileOf(LanguageCode targetLanguage) {
        return cacheDir.resolve(FILE_NAME_PREFIX + targetLanguage.name() + FILE_EXTENSION);
    }

    private LanguagePartition partitionOf(LanguageCode targetLanguage) {
        return partitions.computeIfAbsent(targetLanguage, this::load);
    }

    private LanguagePartition load(LanguageCode targetLanguage) {
        var file = cacheFileOf(targetLanguage);
        var entries = new ConcurrentHashMap<String, String>();
        if (Files.notExists(file)) {
            return new LanguagePartition(file, entries);
        }
        try (var records = CsvUtils.readAsStream(file, Function.identity(), CSV_FORMAT)) {
            records.filter(CSVRecord::isConsistent)
                    .forEach(r -> entries.put(r.get(COLUMN_SOURCE), r.get(COLUMN_TRANSLATION)));
        } catch (RuntimeException e) {
            LOGGER.warn("Failed to load translation cache from {}", file, e);
        }
        return new LanguagePartition(file, entries);
    }

    private void append(Path file, String sourceText, String translation) {
        try {
            CsvUtils.appendStream(
                    file,
                    Stream.of(new Entry(sourceText, translation)),
                    HEADERS,
                    e -> new Object[] {e.source(), e.translation()},
                    CSV_FORMAT);
        } catch (RuntimeException e) {
            LOGGER.warn("Failed to append translation cache entry to {}", file, e);
        }
    }

    private record Entry(String source, String translation) {}

    private record LanguagePartition(Path file, Map<String, String> entries) {}
}
