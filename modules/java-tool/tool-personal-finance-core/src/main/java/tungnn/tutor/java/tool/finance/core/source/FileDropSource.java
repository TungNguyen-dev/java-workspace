package tungnn.tutor.java.tool.finance.core.source;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;
import tungnn.tutor.java.infra.document.ingestion.TikaContentExtractionUtils;
import tungnn.tutor.java.mail.MailUtils;
import tungnn.tutor.java.tool.finance.core.model.RawDocument;
import tungnn.tutor.java.tool.finance.core.model.SourceKind;
import tungnn.tutor.java.tool.finance.utils.MediaTypes;

public final class FileDropSource implements FinanceSource {

    private final String id;
    private final SourceKind kind;
    private final Path directory;

    public FileDropSource(String id, SourceKind kind, Path directory) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.kind = Objects.requireNonNull(kind, "kind must not be null");
        this.directory = Objects.requireNonNull(directory, "directory must not be null");
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public Stream<RawDocument> fetch(SyncCursor cursor) {
        if (!Files.isDirectory(directory)) {
            return Stream.empty();
        }
        try (var files = Files.list(directory)) {
            return files.filter(Files::isRegularFile)
                    .sorted()
                    .filter(file -> modifiedAfter(file, cursor))
                    .map(this::toDocument)
                    .filter(Objects::nonNull)
                    .toList()
                    .stream();
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot list " + directory, e);
        }
    }

    private static boolean modifiedAfter(Path file, SyncCursor cursor) {
        try {
            return Files.getLastModifiedTime(file).toInstant().isAfter(cursor.lastSyncedAt());
        } catch (IOException e) {
            return true;
        }
    }

    private RawDocument toDocument(Path file) {
        var name = file.getFileName().toString().toLowerCase(Locale.ROOT);
        var origin = file.toAbsolutePath().toString();
        try {
            if (name.endsWith(".csv")) {
                return new RawDocument(id, kind, origin, MediaTypes.CSV, null, null, Instant.now());
            }
            if (name.endsWith(".xlsx")) {
                return new RawDocument(id, kind, origin, MediaTypes.XLSX, null, null, Instant.now());
            }
            if (name.endsWith(".pdf")) {
                var text = TikaContentExtractionUtils.extractContent(file);
                return new RawDocument(id, kind, origin, MediaTypes.PDF, text, null, Instant.now());
            }
            if (name.endsWith(".eml")) {
                var mail = MailUtils.readMail(file);
                var text = MailUtils.getContentAsPlainText(mail);
                return new RawDocument(id, SourceKind.EMAIL, origin, MediaTypes.EML, text, null, Instant.now());
            }
            // TODO: support the file formats of specific wallets/banks once real exports are available (e.g. .xls, .txt)
            return null;
        } catch (Exception e) {
            throw new IllegalStateException("Cannot read " + file, e);
        }
    }
}
