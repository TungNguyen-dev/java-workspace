package tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text.memory;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tungnn.tutor.java.csv.utils.CsvUtils;
import tungnn.tutor.java.tool.doctrans.core.domain.text.LanguageCode;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationMemory;

public class CsvFileTextTranslationMemory implements TextTranslationMemory {

  private static final Logger LOGGER = LoggerFactory.getLogger(CsvFileTextTranslationMemory.class);
  private static final String FILE_NAME_PREFIX = "text-translation-memory_";
  private static final String FILE_EXTENSION = ".csv";
  private static final String COLUMN_SOURCE = "source";
  private static final String COLUMN_TRANSLATION = "translation";
  private static final CSVFormat READ_FORMAT = CsvUtils.CsvConfig.DEFAULT.csvFormat();
  private static final CSVFormat WRITE_FORMAT = CsvUtils.CsvConfig.DEFAULT.csvFormat();
  private final Path memoryDir;
  private final Map<LanguageCode, LanguagePartition> partitions = new ConcurrentHashMap<>();

  public CsvFileTextTranslationMemory(Path memoryDir) {
    this.memoryDir = Objects.requireNonNull(memoryDir, "memoryDir must not be null");
    try {
      Files.createDirectories(memoryDir);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to create memory directory: " + memoryDir, e);
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

  public Path memoryFileOf(LanguageCode targetLanguage) {
    return memoryDir.resolve(FILE_NAME_PREFIX + targetLanguage.name() + FILE_EXTENSION);
  }

  private LanguagePartition partitionOf(LanguageCode targetLanguage) {
    return partitions.computeIfAbsent(targetLanguage, this::load);
  }

  private LanguagePartition load(LanguageCode targetLanguage) {
    var file = memoryFileOf(targetLanguage);
    var entries = new ConcurrentHashMap<String, String>();
    if (Files.notExists(file)) {
      return new LanguagePartition(file, entries);
    }
    try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);
        CSVParser parser = READ_FORMAT.parse(reader)) {
      for (CSVRecord csvRecord : parser) {
        if (!csvRecord.isConsistent()) {
          continue;
        }
        entries.put(csvRecord.get(COLUMN_SOURCE), csvRecord.get(COLUMN_TRANSLATION));
      }
    } catch (IOException | RuntimeException e) {
    }
    return new LanguagePartition(file, entries);
  }

  private void append(Path file, String sourceText, String translation) {
    try {
      boolean writeHeader = Files.notExists(file) || Files.size(file) == 0;
      try (Writer writer =
              Files.newBufferedWriter(
                  file,
                  StandardCharsets.UTF_8,
                  StandardOpenOption.CREATE,
                  StandardOpenOption.APPEND);
          CSVPrinter printer = new CSVPrinter(writer, WRITE_FORMAT)) {
        if (writeHeader) {
          printer.printRecord(COLUMN_SOURCE, COLUMN_TRANSLATION);
        }
        printer.printRecord(sourceText, translation);
      }
    } catch (IOException | RuntimeException e) {
    }
  }

  private record LanguagePartition(Path file, Map<String, String> entries) {}
}
