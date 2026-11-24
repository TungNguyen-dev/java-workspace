package tungnn.tutor.java.csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import tungnn.tutor.java.core.lib.reflection.PropertyReflectionUtils;
import tungnn.tutor.java.core.lib.reflection.ReflectionUtils;

public final class CsvUtils {

    private CsvUtils() {}

    public static <E> Stream<E> readAsStream(Path path, Class<E> clazz, CSVFormat format, Charset charset) {
        Objects.requireNonNull(clazz, "clazz must not be null");

        var properties = CsvRecordMapper.propertiesOf(clazz);
        return openStream(path, format, charset, csvRecord -> CsvRecordMapper.toEntity(csvRecord, clazz, properties));
    }

    public static <E> Stream<E> readAsStream(Path path, Class<E> clazz, CSVFormat format) {
        return readAsStream(path, clazz, format, StandardCharsets.UTF_8);
    }

    public static <E> Stream<E> readAsStream(Path path, Class<E> clazz) {
        return readAsStream(path, clazz, CsvFormatUtils.defaultFormat());
    }

    public static <E> Stream<E> readAsStream(
            Path path, Function<CSVRecord, E> mapper, CSVFormat format, Charset charset) {
        Objects.requireNonNull(mapper, "mapper must not be null");

        return openStream(path, format, charset, mapper);
    }

    public static <E> Stream<E> readAsStream(Path path, Function<CSVRecord, E> mapper, CSVFormat format) {
        return readAsStream(path, mapper, format, StandardCharsets.UTF_8);
    }

    public static <E> List<E> readAsList(Path path, Class<E> clazz, CSVFormat format) {
        try (Stream<E> stream = readAsStream(path, clazz, format)) {
            return stream.toList();
        }
    }

    public static <E> List<E> readAsList(Path path, Class<E> clazz) {
        return readAsList(path, clazz, CsvFormatUtils.defaultFormat());
    }

    public static <E> void writeStream(Path path, Stream<E> data, Class<E> clazz, CSVFormat format, Charset charset) {
        Objects.requireNonNull(clazz, "clazz must not be null");

        var properties = CsvRecordMapper.propertiesOf(clazz);
        write(
                path,
                data,
                CsvRecordMapper.headers(properties),
                CsvRecordMapper.toRowMapper(properties),
                format,
                charset,
                false);
    }

    public static <E> void writeStream(Path path, Stream<E> data, Class<E> clazz, CSVFormat format) {
        writeStream(path, data, clazz, format, StandardCharsets.UTF_8);
    }

    public static <E> void writeStream(Path path, Stream<E> data, Class<E> clazz) {
        writeStream(path, data, clazz, CsvFormatUtils.defaultFormat());
    }

    public static <E> void writeStream(
            Path path,
            Stream<E> data,
            List<String> headers,
            Function<E, Object[]> mapper,
            CSVFormat format,
            Charset charset) {
        write(path, data, headers, mapper, format, charset, false);
    }

    public static <E> void writeStream(
            Path path, Stream<E> data, List<String> headers, Function<E, Object[]> mapper, CSVFormat format) {
        writeStream(path, data, headers, mapper, format, StandardCharsets.UTF_8);
    }

    public static <E> void writeList(Path path, List<E> data, Class<E> clazz, CSVFormat format) {
        Objects.requireNonNull(data, "data must not be null");

        writeStream(path, data.stream(), clazz, format);
    }

    public static <E> void writeList(Path path, List<E> data, Class<E> clazz) {
        writeList(path, data, clazz, CsvFormatUtils.defaultFormat());
    }

    public static <E> void appendStream(
            Path path,
            Stream<E> data,
            List<String> headers,
            Function<E, Object[]> mapper,
            CSVFormat format,
            Charset charset) {
        write(path, data, headers, mapper, format, charset, true);
    }

    public static <E> void appendStream(
            Path path, Stream<E> data, List<String> headers, Function<E, Object[]> mapper, CSVFormat format) {
        appendStream(path, data, headers, mapper, format, StandardCharsets.UTF_8);
    }

    private static <E> Stream<E> openStream(
            Path path, CSVFormat format, Charset charset, Function<CSVRecord, E> mapper) {
        Objects.requireNonNull(path, "path must not be null");
        Objects.requireNonNull(format, "format must not be null");
        Objects.requireNonNull(charset, "charset must not be null");

        BufferedReader reader = null;
        CSVParser parser = null;

        try {
            reader = Files.newBufferedReader(path, charset);
            parser = format.parse(reader);

            // Capture references for safe closure within the stream's onClose hook
            final var finalReader = reader;
            final var finalParser = parser;

            return parser.stream().map(mapper).onClose(() -> closeResources(finalParser, finalReader));

        } catch (Exception e) {
            // Ensure all allocated resources are freed if stream initialization fails
            closeResources(parser, reader);

            if (e instanceof IOException ioe) {
                throw new UncheckedIOException("Failed to read CSV as stream: " + path, ioe);
            }
            throw new CsvException("Failed to read CSV as stream: " + path, e);
        }
    }

    private static <E> void write(
            Path path,
            Stream<E> data,
            List<String> headers,
            Function<E, Object[]> mapper,
            CSVFormat format,
            Charset charset,
            boolean append) {
        Objects.requireNonNull(path, "path must not be null");
        Objects.requireNonNull(data, "data must not be null");
        Objects.requireNonNull(mapper, "mapper must not be null");
        Objects.requireNonNull(format, "format must not be null");
        Objects.requireNonNull(charset, "charset must not be null");

        // Trimming is a read-side concern; applying it on write would silently alter the stored values
        CSVFormat printFormat = format.builder().setTrim(false).build();

        try {
            boolean appendToContent = append && Files.exists(path) && Files.size(path) > 0;
            boolean writeHeader = headers != null && !headers.isEmpty() && !appendToContent;
            // A file without a trailing line break would otherwise glue the first new record onto its last line
            boolean needsLineBreak = appendToContent && !endsWithLineBreak(path, charset);

            OpenOption[] options = append
                    ? new OpenOption[] {StandardOpenOption.CREATE, StandardOpenOption.APPEND}
                    : new OpenOption[] {
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE
                    };

            try (Stream<E> stream = data;
                    BufferedWriter writer = Files.newBufferedWriter(path, charset, options);
                    CSVPrinter printer = printFormat.print(writer)) {

                if (needsLineBreak) {
                    printer.println();
                }
                if (writeHeader) {
                    printer.printRecord(headers);
                }
                // Iterate with the stream iterator to avoid checked exception wrapping in lambdas
                Iterator<E> iterator = stream.iterator();
                while (iterator.hasNext()) {
                    printer.printRecord(mapper.apply(iterator.next()));
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write CSV stream to path: " + path, e);
        }
    }

    private static boolean endsWithLineBreak(Path path, Charset charset) throws IOException {
        try (SeekableByteChannel channel = Files.newByteChannel(path)) {
            long size = channel.size();
            for (String lineBreak : List.of("\n", "\r")) {
                ByteBuffer expected = charset.encode(lineBreak);
                int length = expected.remaining();
                if (size < length) {
                    continue;
                }
                ByteBuffer tail = ByteBuffer.allocate(length);
                channel.position(size - length);
                channel.read(tail);
                if (tail.flip().equals(expected)) {
                    return true;
                }
            }
            return false;
        }
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

    static final class CsvRecordMapper {

        private static final Map<Class<?>, Function<String, Object>> CONVERTERS = Map.of(
                String.class, s -> s,
                int.class, Integer::parseInt,
                Integer.class, Integer::valueOf,
                long.class, Long::parseLong,
                Long.class, Long::valueOf,
                double.class, Double::parseDouble,
                Double.class, Double::valueOf,
                boolean.class, Boolean::parseBoolean,
                Boolean.class, Boolean::valueOf);

        private CsvRecordMapper() {}

        static List<PropertyReflectionUtils.PropertyMetadata> propertiesOf(Class<?> clazz) {
            if (!clazz.isRecord()) {
                throw new IllegalArgumentException("Class is not record: " + clazz);
            }
            return PropertyReflectionUtils.resolveProperties(clazz);
        }

        static List<String> headers(List<PropertyReflectionUtils.PropertyMetadata> properties) {
            return properties.stream().map(p -> p.field().getName()).toList();
        }

        static <E> E toEntity(
                CSVRecord csvRecord, Class<E> clazz, List<PropertyReflectionUtils.PropertyMetadata> properties) {
            Object[] args = new Object[properties.size()];
            for (int i = 0; i < properties.size(); i++) {
                var prop = properties.get(i);
                args[i] = readValue(
                        csvRecord, prop.field().getName(), prop.field().getType());
            }
            return ReflectionUtils.newInstance(clazz, args);
        }

        @SuppressWarnings("unchecked")
        static <E> Function<E, Object[]> toRowMapper(List<PropertyReflectionUtils.PropertyMetadata> properties) {
            Function<E, Object>[] accessors = new Function[properties.size()];

            for (int i = 0; i < properties.size(); i++) {
                var prop = properties.get(i);

                if (prop.readable() && prop.accessor() != null) {
                    var method = prop.accessor();
                    accessors[i] = e -> {
                        try {
                            return method.invoke(e);
                        } catch (ReflectiveOperationException ex) {
                            throw new CsvException("Error invoking accessor: " + method.getName(), ex);
                        }
                    };
                } else if (prop.field() != null) {
                    var field = prop.field();
                    accessors[i] = e -> {
                        try {
                            return field.get(e);
                        } catch (IllegalAccessException ex) {
                            throw new CsvException("Error accessing field: " + field.getName(), ex);
                        }
                    };
                } else {
                    throw new IllegalStateException("No readable accessor or field for property: " + prop);
                }
            }

            // Reuse a single values array to reduce GC allocations; rows are printed one at a time
            Object[] values = new Object[accessors.length];
            return element -> {
                for (int i = 0; i < accessors.length; i++) {
                    values[i] = accessors[i].apply(element);
                }
                return values;
            };
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
    }
}
