package tungnn.tutor.java.csv.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import tungnn.tutor.java.core.lib.reflection.PropertyReflectionUtil;
import tungnn.tutor.java.core.lib.reflection.ReflectionUtil;

public final class CsvUtils {

  private static final Map<Class<?>, Function<String, Object>> CONVERTERS =
      Map.of(
          String.class, s -> s,
          int.class, Integer::parseInt,
          Integer.class, Integer::valueOf,
          long.class, Long::parseLong,
          Long.class, Long::valueOf,
          double.class, Double::parseDouble,
          Double.class, Double::valueOf,
          boolean.class, Boolean::parseBoolean,
          Boolean.class, Boolean::valueOf);

  // ==========================================
  // CSV CONFIG RECORD
  // ==========================================

  private CsvUtils() {
    throw new UnsupportedOperationException("cannot instantiate utility class");
  }

  // ==========================================
  // READ METHODS
  // ==========================================

  public static <E> Stream<E> readAsStream(Path path, Class<E> clazz, CsvConfig csvConfig) {

    if (!clazz.isRecord()) {
      throw new IllegalArgumentException("Class is not record: " + clazz);
    }

    var clazzProperties = PropertyReflectionUtil.resolveProperties(clazz);

    BufferedReader bufferedReader = null;
    CSVParser csvParser = null;

    try {
      bufferedReader = Files.newBufferedReader(path, csvConfig.charset());
      csvParser = csvConfig.csvFormat().parse(bufferedReader);

      // Capture references for safe closure within the stream's onClose hook
      final var finalReader = bufferedReader;
      final var finalParser = csvParser;

      return csvParser.stream()
          .map(csvRecord -> mapRecordToEntity(csvRecord, clazz, clazzProperties))
          .onClose(() -> closeResources(finalParser, finalReader));

    } catch (Exception e) {
      // Ensure all allocated resources are freed if stream initialization fails
      closeResources(csvParser, bufferedReader);

      if (e instanceof IOException ioe) {
        throw new UncheckedIOException("Failed to read CSV as stream: " + path, ioe);
      }
      throw new RuntimeException("Failed to read CSV as stream: " + path, e);
    }
  }

  public static <E> Stream<E> readAsStream(Path path, Class<E> clazz) {
    return readAsStream(path, clazz, CsvConfig.DEFAULT);
  }

  public static <E> List<E> readAsList(Path path, Class<E> clazz) {
    return readAsStream(path, clazz).toList();
  }

  // ==========================================
  // WRITE METHODS
  // ==========================================

  public static <E> void writeStream(
      Path path, Stream<E> data, Class<E> clazz, CsvConfig csvConfig) {

    if (!clazz.isRecord()) {
      throw new IllegalArgumentException("Class is not record: " + clazz);
    }

    var clazzProperties = PropertyReflectionUtil.resolveProperties(clazz);
    var clazzPropertiesSize = clazzProperties.size();
    var clazzAccessors = resolveAccessors(clazzProperties);

    // 1. Ensure the incoming stream is properly closed when done
    try (Stream<E> stream = data;
        BufferedWriter bufferedWriter = Files.newBufferedWriter(path, csvConfig.charset());
        CSVPrinter csvPrinter = csvConfig.csvFormat().print(bufferedWriter)) {

      // Print headers
      Object[] headers =
          clazzProperties.stream().map(p -> p.field().getName()).toArray(Object[]::new);
      csvPrinter.printRecord(headers);

      // Reuse a single values array to reduce GC allocations
      Object[] values = new Object[clazzPropertiesSize];

      // 2. Iterate using standard stream iterator to avoid checked exception wrapping in lambdas
      Iterator<E> iterator = stream.iterator();
      while (iterator.hasNext()) {
        E element = iterator.next();
        for (int i = 0; i < clazzPropertiesSize; i++) {
          values[i] = clazzAccessors[i].apply(element);
        }
        csvPrinter.printRecord(values);
      }

    } catch (IOException e) {
      throw new UncheckedIOException("Failed to write CSV stream to path: " + path, e);
    }
  }

  public static <E> void writeStream(Path path, Stream<E> data, Class<E> clazz) {
    writeStream(path, data, clazz, CsvConfig.DEFAULT);
  }

  public static <E> void writeList(Path path, List<E> data, Class<E> clazz) {
    writeStream(path, data.stream(), clazz);
  }

  // ==========================================
  // OPTIMIZED HELPER & CONVERSION LOGIC
  // ==========================================

  private static <E> E mapRecordToEntity(
      CSVRecord csvRecord,
      Class<E> clazz,
      List<PropertyReflectionUtil.PropertyMetadata> clazzProperties) {

    Object[] args = new Object[clazzProperties.size()];
    for (int i = 0; i < clazzProperties.size(); i++) {
      var prop = clazzProperties.get(i);
      args[i] = readValue(csvRecord, prop.field().getName(), prop.field().getType());
    }
    return ReflectionUtil.newInstance(clazz, args);
  }

  @SuppressWarnings("unchecked")
  private static <E> Function<E, Object>[] resolveAccessors(
      List<PropertyReflectionUtil.PropertyMetadata> properties) {
    Function<E, Object>[] accessors = new Function[properties.size()];

    for (int i = 0; i < properties.size(); i++) {
      var prop = properties.get(i);

      if (prop.readable() && prop.accessor() != null) {
        var accessorMethod = prop.accessor();
        if (!accessorMethod.canAccess(null)) {
          accessorMethod.setAccessible(true);
        }
        accessors[i] =
            e -> {
              try {
                return accessorMethod.invoke(e);
              } catch (Exception ex) {
                throw new RuntimeException(
                    "Error invoking accessor: " + accessorMethod.getName(), ex);
              }
            };
      } else {
        var field = prop.field();
        if (!field.canAccess(null)) {
          field.setAccessible(true);
        }
        accessors[i] =
            e -> {
              try {
                return field.get(e);
              } catch (Exception ex) {
                throw new RuntimeException("Error accessing field: " + field.getName(), ex);
              }
            };
      }
    }
    return accessors;
  }

  private static Object readValue(CSVRecord record, String name, Class<?> type) {
    String raw = findMappedValue(record, name);

    if (raw == null) {
      throw new IllegalArgumentException(
          String.format("Missing column '%s' at record line %d", name, record.getRecordNumber()));
    }
    return convert(raw, type);
  }

  private static String findMappedValue(CSVRecord record, String name) {
    if (record.isMapped(name)) return record.get(name);

    String lower = name.toLowerCase(Locale.ROOT);
    if (record.isMapped(lower)) return record.get(lower);

    String upper = name.toUpperCase(Locale.ROOT);
    if (record.isMapped(upper)) return record.get(upper);

    return null;
  }

  private static Object convert(String value, Class<?> type) {
    if (value == null || value.isBlank()) {
      if (type.isPrimitive()) {
        throw new IllegalArgumentException(
            "Cannot map empty/null string to primitive type: " + type.getName());
      }
      return null;
    }

    var converter = CONVERTERS.get(type);
    if (converter != null) {
      return converter.apply(value);
    }

    throw new UnsupportedOperationException("Unsupported type conversion for: " + type.getName());
  }

  private static void closeResources(AutoCloseable... closeables) {
    for (var closeable : closeables) {
      if (closeable != null) {
        try {
          closeable.close();
        } catch (Exception _) {
        }
      }
    }
  }

  public record CsvConfig(Charset charset, CSVFormat csvFormat) {

    /** Default configuration for reading and writing CSV files. */
    public static final CsvConfig DEFAULT =
        of(
            StandardCharsets.UTF_8,
            CSVFormat.DEFAULT
                .builder()
                // Automatically uses the first record as column headers for name-based mapping
                .setHeader()
                // Treats header matching as case-insensitive to handle variations in column casing
                .setIgnoreHeaderCase(true)
                // Skips the header row so it is not processed as a data record in the stream
                .setSkipHeaderRecord(true)
                // Ignores blank lines to prevent processing empty records
                .setIgnoreEmptyLines(true)
                // Trims leading and trailing whitespaces from header names and field values
                .setTrim(true)
                .build());

    public static CsvConfig of(Charset charset, CSVFormat csvFormat) {
      return new CsvConfig(charset, csvFormat);
    }
  }
}
