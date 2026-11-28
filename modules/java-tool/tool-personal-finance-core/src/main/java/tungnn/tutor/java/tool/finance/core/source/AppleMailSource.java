package tungnn.tutor.java.tool.finance.core.source;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;
import tungnn.tutor.java.mail.AppleMailUtils;
import tungnn.tutor.java.tool.finance.core.model.RawDocument;
import tungnn.tutor.java.tool.finance.utils.MailDocumentUtils;

public final class AppleMailSource implements FinanceSource {

    private final String id;
    private final Path root;
    private final List<String> mailboxes;
    private final List<String> senders;

    public AppleMailSource(String id, Path root, List<String> mailboxes, List<String> senders) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.root = Objects.requireNonNull(root, "root must not be null");
        this.mailboxes = List.copyOf(mailboxes);
        Objects.requireNonNull(senders, "senders must not be null");
        if (senders.isEmpty()) {
            // Without a sender filter every personal mail would be sent to the LLM.
            throw new IllegalArgumentException("Apple Mail source " + id + " needs at least one sender");
        }
        this.senders = senders.stream().map(s -> s.toLowerCase(Locale.ROOT)).toList();
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public Stream<RawDocument> fetch(SyncCursor cursor) {
        var since = cursor.lastSyncedAt();
        var seen = new HashSet<String>();
        // Files are pre-filtered by mtime, which only moves forward after a mail was written or changed.
        return AppleMailUtils.listEmlx(root, mailboxes, since).stream()
                .map(AppleMailUtils::readEmlx)
                .filter(this::fromWantedSender)
                .filter(message -> !MailDocumentUtils.receivedAt(message).isBefore(since))
                .filter(message -> seen.add(MailDocumentUtils.originOf(message)))
                .map(message -> MailDocumentUtils.toDocument(id, message));
    }

    private boolean fromWantedSender(MimeMessage message) {
        try {
            var from = message.getFrom();
            if (from == null) {
                return false;
            }
            for (var address : from) {
                var text = (address instanceof InternetAddress internet ? internet.getAddress() : address.toString())
                        .toLowerCase(Locale.ROOT);
                if (senders.stream().anyMatch(text::contains)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            throw new IllegalStateException("Cannot read the sender of a message in source " + id, e);
        }
    }
}
