package tungnn.tutor.java.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class MailUtil {

  public static MimeMessage readMail(Path mailPath) throws IOException, MessagingException {
    var props = new Properties();
    var session = Session.getDefaultInstance(props, null);

    try (var is = Files.newInputStream(mailPath)) {
      return new MimeMessage(session, is);
    }
  }

  public static String getContentAsPlainText(Part part) throws Exception {

    if (part.isMimeType("text/plain")) {
      return (String) part.getContent();
    }

    if (part.isMimeType("text/html")) {
      var html = (String) part.getContent();
      return stripHtml(html);
    }

    if (part.isMimeType("multipart/alternative")) {
      var multipart = (Multipart) part.getContent();
      String htmlFallback = null;

      for (var i = 0; i < multipart.getCount(); i++) {
        var bodyPart = multipart.getBodyPart(i);

        if (bodyPart.isMimeType("text/plain")) {
          return (String) bodyPart.getContent();
        }

        if (bodyPart.isMimeType("text/html")) {
          htmlFallback = stripHtml((String) bodyPart.getContent());
        }
      }

      return htmlFallback != null ? htmlFallback : "";
    }

    if (part.isMimeType("multipart/*")) {
      var multipart = (Multipart) part.getContent();
      var result = new StringBuilder();

      for (var i = 0; i < multipart.getCount(); i++) {
        var bodyPart = multipart.getBodyPart(i);

        // Skip attachment (robust hơn)
        var disposition = bodyPart.getDisposition();
        if (Part.ATTACHMENT.equalsIgnoreCase(disposition) || bodyPart.getFileName() != null) {
          continue;
        }

        result.append(getContentAsPlainText(bodyPart));
      }

      return result.toString();
    }

    return "";
  }

  public static String stripHtml(String html) {
    return html.replaceAll("<[^>]*>", "") // remove tags
        .replace("&nbsp;", " ")
        .trim();
  }
}
