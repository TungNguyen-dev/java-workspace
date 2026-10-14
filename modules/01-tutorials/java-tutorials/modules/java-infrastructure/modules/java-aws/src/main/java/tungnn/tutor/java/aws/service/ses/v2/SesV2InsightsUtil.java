// SesV2InsightsUtil.java
package tungnn.tutor.java.aws.service.ses.v2;

import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

public final class SesV2InsightsUtil {

  private SesV2InsightsUtil() {}

  public static BatchGetMetricDataResponse batchGetMetricData(
      SesV2Client client, BatchGetMetricDataRequest request) {
    return client.batchGetMetricData(request);
  }

  public static GetBlacklistReportsResponse getBlacklistReports(
      SesV2Client client, GetBlacklistReportsRequest request) {
    return client.getBlacklistReports(request);
  }

  public static GetEmailAddressInsightsResponse getEmailAddressInsights(
      SesV2Client client, GetEmailAddressInsightsRequest request) {
    return client.getEmailAddressInsights(request);
  }

  public static GetMessageInsightsResponse getMessageInsights(
      SesV2Client client, GetMessageInsightsRequest request) {
    return client.getMessageInsights(request);
  }
}
