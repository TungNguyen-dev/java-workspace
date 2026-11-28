package tungnn.tutor.java.mail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AppleMailUtilsTest {

    @TempDir
    Path dir;

    static Path writeEmlx(Path file, String rfc822) throws Exception {
        var length = rfc822.getBytes(StandardCharsets.UTF_8).length;
        var plist = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><plist version=\"1.0\"><dict/></plist>";
        Files.createDirectories(file.getParent());
        Files.writeString(file, length + "\n" + rfc822 + plist, StandardCharsets.UTF_8);
        return file;
    }

    static String mail(String from, String subject, String messageId, String date, String body) {
        return "From: " + from + "\r\nTo: me@example.com\r\nSubject: " + subject + "\r\nMessage-ID: " + messageId
                + "\r\nDate: " + date + "\r\nContent-Type: text/plain; charset=UTF-8\r\n\r\n" + body + "\r\n";
    }

    @Test
    void shouldReadMessageAndIgnoreTrailingPlist() throws Exception {
        var file = writeEmlx(
                dir.resolve("1.emlx"),
                mail(
                        "Bank <bank@example.com>",
                        "Balance",
                        "<1@x>",
                        "Sat, 28 Nov 2026 10:00:00 +0700",
                        "Debit 500.000"));

        var message = AppleMailUtils.readEmlx(file);

        assertThat(message.getSubject()).isEqualTo("Balance");
        assertThat(MailUtils.getContentAsPlainText(message))
                .contains("Debit 500.000")
                .doesNotContain("plist");
    }

    @Test
    void shouldListOnlyRequestedMailboxes() throws Exception {
        var inbox = writeEmlx(
                dir.resolve("V10/a/INBOX.mbox/u/Data/Messages/1.emlx"),
                mail("a@x", "s", "<1>", "Sat, 28 Nov 2026 10:00:00 +0700", "b"));
        writeEmlx(
                dir.resolve("V10/a/Sent Messages.mbox/u/Data/Messages/2.emlx"),
                mail("a@x", "s", "<2>", "Sat, 28 Nov 2026 10:00:00 +0700", "b"));
        Files.writeString(dir.resolve("V10/a/INBOX.mbox/u/Data/Messages/3.txt"), "ignored");

        assertThat(AppleMailUtils.listEmlx(dir, List.of("inbox"), null)).containsExactly(inbox);
        assertThat(AppleMailUtils.listEmlx(dir, List.of(), null)).hasSize(2);
    }

    @Test
    void shouldSkipFilesNotModifiedSinceCursor() throws Exception {
        writeEmlx(dir.resolve("INBOX.mbox/1.emlx"), mail("a@x", "s", "<1>", "Sat, 28 Nov 2026 10:00:00 +0700", "b"));

        assertThat(AppleMailUtils.listEmlx(dir, List.of(), Instant.parse("2100-01-01T00:00:00Z")))
                .isEmpty();
    }

    @Test
    void shouldPickTheHighestVersionDirectoryUnlessOverridden() {
        assertThat(AppleMailUtils.resolveMailRoot(dir.toString())).isEqualTo(dir);
        assertThatThrownBy(() -> AppleMailUtils.readEmlx(dir.resolve("missing.emlx")))
                .isInstanceOf(IllegalStateException.class);
    }
}
