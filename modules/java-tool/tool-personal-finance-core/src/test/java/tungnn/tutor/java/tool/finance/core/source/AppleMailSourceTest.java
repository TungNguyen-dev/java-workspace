package tungnn.tutor.java.tool.finance.core.source;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tungnn.tutor.java.tool.finance.core.model.RawDocument;
import tungnn.tutor.java.tool.finance.core.model.SourceKind;
import tungnn.tutor.java.tool.finance.utils.MediaTypes;

class AppleMailSourceTest {

    private static final String BANK = "alerts@bank.example.com";

    @TempDir
    Path dir;

    private void emlx(String relative, String from, String messageId, String date, String body) throws Exception {
        var rfc822 = "From: Bank <" + from + ">\r\nTo: me@example.com\r\nSubject: Alert\r\nMessage-ID: " + messageId
                + "\r\nDate: " + date + "\r\nContent-Type: text/plain; charset=UTF-8\r\n\r\n" + body + "\r\n";
        var length = rfc822.getBytes(StandardCharsets.UTF_8).length;
        var file = dir.resolve(relative);
        Files.createDirectories(file.getParent());
        Files.writeString(file, length + "\n" + rfc822 + "<plist/>", StandardCharsets.UTF_8);
    }

    private AppleMailSource source() {
        return new AppleMailSource("bank-mail", dir, List.of("INBOX"), List.of(BANK));
    }

    @Test
    void shouldReturnOnlyMailFromConfiguredSendersAndDeduplicateByMessageId() throws Exception {
        emlx(
                "V10/a/INBOX.mbox/u/Data/Messages/1.emlx",
                BANK,
                "<1@x>",
                "Sat, 28 Nov 2026 10:00:00 +0700",
                "Debit 500.000 VND");
        emlx(
                "V10/a/INBOX.mbox/u/Data/Messages/2.emlx",
                BANK,
                "<1@x>",
                "Sat, 28 Nov 2026 10:00:00 +0700",
                "Debit 500.000 VND");
        emlx(
                "V10/a/INBOX.mbox/u/Data/Messages/3.emlx",
                "friend@example.com",
                "<2@x>",
                "Sat, 28 Nov 2026 11:00:00 +0700",
                "Lunch?");
        emlx(
                "V10/a/Sent Messages.mbox/u/Data/Messages/4.emlx",
                BANK,
                "<3@x>",
                "Sat, 28 Nov 2026 12:00:00 +0700",
                "Other mailbox");

        var documents = source().fetch(SyncCursor.initial()).toList();

        assertThat(documents).hasSize(1);
        RawDocument document = documents.get(0);
        assertThat(document.sourceId()).isEqualTo("bank-mail");
        assertThat(document.kind()).isEqualTo(SourceKind.EMAIL);
        assertThat(document.mediaType()).isEqualTo(MediaTypes.EML);
        assertThat(document.origin()).isEqualTo("<1@x>");
        assertThat(document.text()).contains("Debit 500.000 VND");
    }

    @Test
    void shouldSkipMailOlderThanTheCursor() throws Exception {
        emlx("V10/a/INBOX.mbox/u/Data/Messages/1.emlx", BANK, "<1@x>", "Mon, 01 Jun 2020 10:00:00 +0700", "Old debit");

        var documents = source().fetch(new SyncCursor(Instant.parse("2025-01-01T00:00:00Z")))
                .toList();

        assertThat(documents).isEmpty();
    }

    @Test
    void shouldRefuseToReadEverythingWhenNoSenderIsConfigured() {
        assertThatThrownBy(() -> new AppleMailSource("bank-mail", dir, List.of(), List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
