// SesAccountUtil.java
package tungnn.tutor.java.aws.service.ses.v1;

import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

public final class SesAccountUtil {

  private SesAccountUtil() {}

  public static GetAccountSendingEnabledResponse getAccountSendingEnabled(
      SesClient client, GetAccountSendingEnabledRequest request) {
    return client.getAccountSendingEnabled(request);
  }

  public static GetSendQuotaResponse getSendQuota(SesClient client, GetSendQuotaRequest request) {
    return client.getSendQuota(request);
  }

  public static GetSendStatisticsResponse getSendStatistics(
      SesClient client, GetSendStatisticsRequest request) {
    return client.getSendStatistics(request);
  }

  public static UpdateAccountSendingEnabledResponse updateAccountSendingEnabled(
      SesClient client, UpdateAccountSendingEnabledRequest request) {
    return client.updateAccountSendingEnabled(request);
  }
}
