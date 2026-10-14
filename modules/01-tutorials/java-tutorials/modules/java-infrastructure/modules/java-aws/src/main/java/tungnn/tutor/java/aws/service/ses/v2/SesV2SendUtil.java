// SesV2SendUtil.java
package tungnn.tutor.java.aws.service.ses.v2;

import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

public final class SesV2SendUtil {

  private SesV2SendUtil() {}

  public static SendBulkEmailResponse sendBulkEmail(
      SesV2Client client, SendBulkEmailRequest request) {
    return client.sendBulkEmail(request);
  }

  public static SendCustomVerificationEmailResponse sendCustomVerificationEmail(
      SesV2Client client, SendCustomVerificationEmailRequest request) {
    return client.sendCustomVerificationEmail(request);
  }

  public static SendEmailResponse sendEmail(SesV2Client client, SendEmailRequest request) {
    return client.sendEmail(request);
  }
}
