// SesEmailUtil.java
package tungnn.tutor.java.aws.service.ses.v1;

import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

public final class SesEmailUtil {

  private SesEmailUtil() {}

  public static SendBounceResponse sendBounce(SesClient client, SendBounceRequest request) {
    return client.sendBounce(request);
  }

  public static SendBulkTemplatedEmailResponse sendBulkTemplatedEmail(
      SesClient client, SendBulkTemplatedEmailRequest request) {
    return client.sendBulkTemplatedEmail(request);
  }

  public static SendCustomVerificationEmailResponse sendCustomVerificationEmail(
      SesClient client, SendCustomVerificationEmailRequest request) {
    return client.sendCustomVerificationEmail(request);
  }

  public static SendEmailResponse sendEmail(SesClient client, SendEmailRequest request) {
    return client.sendEmail(request);
  }

  public static SendRawEmailResponse sendRawEmail(SesClient client, SendRawEmailRequest request) {
    return client.sendRawEmail(request);
  }

  public static SendTemplatedEmailResponse sendTemplatedEmail(
      SesClient client, SendTemplatedEmailRequest request) {
    return client.sendTemplatedEmail(request);
  }
}
