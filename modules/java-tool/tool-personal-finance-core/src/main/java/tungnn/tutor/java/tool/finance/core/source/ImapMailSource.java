package tungnn.tutor.java.tool.finance.core.source;

import jakarta.mail.internet.MimeMessage;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import tungnn.tutor.java.mail.ImapMailUtils;
import tungnn.tutor.java.tool.finance.core.model.RawDocument;
import tungnn.tutor.java.tool.finance.utils.MailDocumentUtils;

public final class ImapMailSource implements FinanceSource {

    private final String id;
    private final Function<Instant, List<MimeMessage>> fetcher;

    public ImapMailSource(String id, Function<Instant, List<MimeMessage>> fetcher) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.fetcher = Objects.requireNonNull(fetcher, "fetcher must not be null");
    }

    public static ImapMailSource imap(
            String id, String host, String user, String password, String folder, List<String> senders) {
        return new ImapMailSource(id, since -> ImapMailUtils.fetch(host, user, password, folder, senders, since));
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public Stream<RawDocument> fetch(SyncCursor cursor) {
        return fetcher.apply(cursor.lastSyncedAt()).stream().map(message -> MailDocumentUtils.toDocument(id, message));
    }
}
