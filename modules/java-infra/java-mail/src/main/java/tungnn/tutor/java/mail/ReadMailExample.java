package tungnn.tutor.java.mail;

import jakarta.mail.Message;
import java.nio.file.Path;
import java.util.Arrays;

public class ReadMailExample {

  static void main(String[] args) {
    try {
      var mailPath = Path.of(args[0]);
      var mail = MailUtil.readMail(mailPath);

      System.out.println("From: " + Arrays.toString(mail.getFrom()));
      System.out.println("To  : " + Arrays.toString(mail.getRecipients(Message.RecipientType.TO)));
      System.out.println("CC  : " + Arrays.toString(mail.getRecipients(Message.RecipientType.CC)));
      System.out.println("BCC : " + Arrays.toString(mail.getRecipients(Message.RecipientType.BCC)));
      System.out.println("-".repeat(80));
      System.out.println("Subject      : " + mail.getSubject());
      System.out.println("Sender       : " + mail.getSender());
      System.out.println("Send Date    : " + mail.getSentDate());
      System.out.println("Receive Date : " + mail.getReceivedDate());
      System.out.println("-".repeat(80));
      System.out.println("Mail plaintext : \n" + MailUtil.getContentAsPlainText(mail));
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }
}
