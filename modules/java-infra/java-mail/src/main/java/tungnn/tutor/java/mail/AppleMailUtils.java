package tungnn.tutor.java.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Pattern;

public final class AppleMailUtils {

    private static final Pattern VERSION_DIRECTORY = Pattern.compile("V(\\d+)");
    private static final String MAILBOX_SUFFIX = ".mbox";

    private AppleMailUtils() {
        throw new UnsupportedOperationException("cannot instantiate utility class");
    }

    public static Path resolveMailRoot(String override) {
        if (override != null && !override.isBlank()) {
            return Path.of(override.strip());
        }
        var library = Path.of(System.getProperty("user.home"), "Library", "Mail");
        try (var children = Files.list(library)) {
            return children.filter(path -> VERSION_DIRECTORY
                            .matcher(path.getFileName().toString())
                            .matches())
                    .max(Comparator.comparingInt(AppleMailUtils::version))
                    .orElseThrow(() -> new IllegalStateException("No V<n> directory found in " + library));
        } catch (AccessDeniedException e) {
            throw denied(library, e);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot list " + library, e);
        }
    }

    public static List<Path> listEmlx(Path root, List<String> mailboxes, Instant modifiedAfter) {
        var wanted = mailboxes == null
                ? List.<String>of()
                : mailboxes.stream().map(name -> name.toLowerCase(Locale.ROOT)).toList();
        try (var files = Files.walk(root)) {
            return files.filter(AppleMailUtils::isEmlx)
                    .filter(file -> wanted.isEmpty() || inMailbox(root, file, wanted))
                    .filter(file -> modifiedAfter == null || modifiedAfter(file, modifiedAfter))
                    .sorted()
                    .toList();
        } catch (AccessDeniedException e) {
            throw denied(root, e);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot scan " + root, e);
        }
    }

    public static MimeMessage readEmlx(Path file) {
        try {
            var bytes = Files.readAllBytes(file);
            var lineEnd = 0;
            while (lineEnd < bytes.length && bytes[lineEnd] != '\n') {
                lineEnd++;
            }
            var declared = Integer.parseInt(new String(bytes, 0, lineEnd).strip());
            var start = lineEnd + 1;
            // Ignore the trailing plist; trust the declared length unless the file was truncated.
            var length = Math.min(declared, bytes.length - start);
            var session = Session.getInstance(new Properties());
            return new MimeMessage(session, new ByteArrayInputStream(bytes, start, length));
        } catch (AccessDeniedException e) {
            throw denied(file, e);
        } catch (IOException | MessagingException | NumberFormatException e) {
            throw new IllegalStateException("Cannot read emlx file " + file, e);
        }
    }

    private static boolean isEmlx(Path file) {
        var name = file.getFileName().toString();
        return Files.isRegularFile(file) && name.endsWith(".emlx");
    }

    private static boolean inMailbox(Path root, Path file, List<String> wanted) {
        for (var part : root.relativize(file)) {
            var name = part.toString().toLowerCase(Locale.ROOT);
            if (name.endsWith(MAILBOX_SUFFIX)
                    && wanted.contains(name.substring(0, name.length() - MAILBOX_SUFFIX.length()))) {
                return true;
            }
        }
        return false;
    }

    private static boolean modifiedAfter(Path file, Instant since) {
        try {
            return !Files.getLastModifiedTime(file).toInstant().isBefore(since);
        } catch (IOException e) {
            return true;
        }
    }

    private static int version(Path path) {
        var matcher = VERSION_DIRECTORY.matcher(path.getFileName().toString());
        return matcher.matches() ? Integer.parseInt(matcher.group(1)) : -1;
    }

    private static IllegalStateException denied(Path path, AccessDeniedException cause) {
        return new IllegalStateException(
                "Access denied to " + path + ". Grant Full Disk Access to the terminal/IDE running this tool "
                        + "(System Settings > Privacy & Security > Full Disk Access).",
                cause);
    }
}
