package tungnn.tutor.java.csv;

import java.util.function.Consumer;
import org.apache.commons.csv.CSVFormat;

public final class CsvFormatUtils {

    private static final CSVFormat DEFAULT_FORMAT = defaultBuilder().build();

    private CsvFormatUtils() {}

    public static CSVFormat defaultFormat() {
        return DEFAULT_FORMAT;
    }

    public static CSVFormat createFormat(Consumer<CSVFormat.Builder> configurer) {
        CSVFormat.Builder builder = defaultBuilder();
        if (configurer != null) {
            configurer.accept(builder);
        }
        return builder.build();
    }

    private static CSVFormat.Builder defaultBuilder() {
        return CSVFormat.DEFAULT
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
                .setTrim(true);
    }
}
