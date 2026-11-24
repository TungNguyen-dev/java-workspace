package tungnn.tutor.java.csv;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CsvUtilsTest {

    record Item(String id, String name, int count) {}

    @TempDir
    Path dir;

    @Test
    void roundTripPreservesWhitespaceOnWrite() {
        Path file = dir.resolve("items.csv");
        var items = List.of(new Item("1", " a ", 2), new Item("2", "b", 3));

        CsvUtils.writeList(file, items, Item.class, CsvFormatUtils.createFormat(b -> b.setTrim(false)));

        assertThat(CsvUtils.readAsList(file, Item.class, CsvFormatUtils.createFormat(b -> b.setTrim(false))))
                .isEqualTo(items);
    }

    @Test
    void writeWithDefaultFormatDoesNotTrimStoredValues() throws IOException {
        Path file = dir.resolve("items.csv");

        CsvUtils.writeList(file, List.of(new Item("1", " a ", 2)), Item.class);

        assertThat(Files.readString(file)).contains(" a ");
    }

    @Test
    void readMatchesHeaderCaseInsensitively() throws IOException {
        Path file = dir.resolve("items.csv");
        Files.writeString(file, "ID,NAME,COUNT\n1,x,5\n");

        assertThat(CsvUtils.readAsList(file, Item.class)).containsExactly(new Item("1", "x", 5));
    }

    @Test
    void appendWritesHeaderOnlyOnce() throws IOException {
        Path file = dir.resolve("log.csv");
        var headers = List.of("a", "b");

        for (String value : List.of("1", "2")) {
            CsvUtils.appendStream(
                    file, Stream.of(value), headers, v -> new Object[] {v, v}, CsvFormatUtils.defaultFormat());
        }

        assertThat(Files.readAllLines(file)).containsExactly("a,b", "1,1", "2,2");
    }

    @Test
    void appendAddsLineBreakWhenFileHasNoTrailingNewline() throws IOException {
        Path file = dir.resolve("log.csv");
        Files.writeString(file, "a,b\nfoo,bar");

        CsvUtils.appendStream(
                file, Stream.of("x"), List.of("a", "b"), v -> new Object[] {v, v}, CsvFormatUtils.defaultFormat());

        assertThat(Files.readAllLines(file)).containsExactly("a,b", "foo,bar", "x,x");
    }

    @Test
    void honoursCustomCharset() {
        Path file = dir.resolve("utf16.csv");
        Charset charset = Charset.forName("UTF-16LE");
        var items = List.of(new Item("1", "xin chào", 1));

        CsvUtils.writeStream(file, items.stream(), Item.class, CsvFormatUtils.defaultFormat(), charset);

        try (var stream = CsvUtils.readAsStream(file, Item.class, CsvFormatUtils.defaultFormat(), charset)) {
            assertThat(stream.toList()).isEqualTo(items);
        }
    }

    @Test
    void rejectsNonRecordClass() {
        Path file = dir.resolve("x.csv");

        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class, () -> CsvUtils.readAsList(file, String.class));
    }
}
