package tungnn.tutor.java.mail;

import jakarta.mail.Folder;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.search.FromStringTerm;
import jakarta.mail.search.OrTerm;
import jakarta.mail.search.ReceivedDateTerm;
import jakarta.mail.search.SearchTerm;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public final class ImapMailUtils {

    private ImapMailUtils() {
        throw new UnsupportedOperationException("cannot instantiate utility class");
    }

    public static List<MimeMessage> fetch(
            String host, String user, String password, String folderName, List<String> senders, Instant since) {
        var props = new Properties();
        props.put("mail.store.protocol", "imaps");
        var session = Session.getInstance(props);
        try (var store = session.getStore("imaps")) {
            store.connect(host, user, password);
            var folder = store.getFolder(folderName);
            folder.open(Folder.READ_ONLY);
            try {
                var messages = folder.search(buildTerm(senders, since));
                var detached = new ArrayList<MimeMessage>(messages.length);
                for (var message : messages) {
                    // Copy while the folder is still open; a lazily loaded message fails once the store is closed.
                    detached.add(new MimeMessage((MimeMessage) message));
                }
                return detached;
            } finally {
                folder.close(false);
            }
        } catch (MessagingException e) {
            throw new IllegalStateException("Unable to read mailbox " + folderName + " on " + host, e);
        }
    }

    private static SearchTerm buildTerm(List<String> senders, Instant since) {
        SearchTerm term = new ReceivedDateTerm(jakarta.mail.search.ComparisonTerm.GE, Date.from(since));
        if (senders != null && !senders.isEmpty()) {
            var fromTerms = senders.stream().map(FromStringTerm::new).toArray(SearchTerm[]::new);
            term = new jakarta.mail.search.AndTerm(term, new OrTerm(fromTerms));
        }
        return term;
    }
}
