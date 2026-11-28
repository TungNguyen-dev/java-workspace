package tungnn.tutor.java.tool.finance.utils;

import jakarta.mail.internet.MimeMessage;
import java.time.Instant;
import tungnn.tutor.java.mail.MailUtils;
import tungnn.tutor.java.tool.finance.core.model.RawDocument;
import tungnn.tutor.java.tool.finance.core.model.SourceKind;

public final class MailDocumentUtils {

    private MailDocumentUtils() {
        throw new UnsupportedOperationException("cannot instantiate utility class");
    }

    public static String originOf(MimeMessage message) {
        try {
            return message.getMessageID() != null ? message.getMessageID() : String.valueOf(message.getSubject());
        } catch (Exception e) {
            throw new IllegalStateException("Cannot read the identity of a message", e);
        }
    }

    public static Instant receivedAt(MimeMessage message) {
        try {
            var date = message.getReceivedDate() != null ? message.getReceivedDate() : message.getSentDate();
            return date != null ? date.toInstant() : Instant.now();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot read the date of a message", e);
        }
    }

    public static RawDocument toDocument(String sourceId, MimeMessage message) {
        try {
            return new RawDocument(
                    sourceId,
                    SourceKind.EMAIL,
                    originOf(message),
                    MediaTypes.EML,
                    MailUtils.getContentAsPlainText(message),
                    null,
                    receivedAt(message));
        } catch (Exception e) {
            throw new IllegalStateException("Cannot read a message of source " + sourceId, e);
        }
    }
}
