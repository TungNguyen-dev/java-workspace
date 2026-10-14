// SesV2AccountUtil.java
package tungnn.tutor.java.aws.service.ses.v2;

import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

public final class SesV2AccountUtil {

  private SesV2AccountUtil() {}

  public static GetAccountResponse getAccount(SesV2Client client, GetAccountRequest request) {
    return client.getAccount(request);
  }

  public static PutAccountDedicatedIpWarmupAttributesResponse putAccountDedicatedIpWarmupAttributes(
      SesV2Client client, PutAccountDedicatedIpWarmupAttributesRequest request) {
    return client.putAccountDedicatedIpWarmupAttributes(request);
  }

  public static PutAccountDetailsResponse putAccountDetails(
      SesV2Client client, PutAccountDetailsRequest request) {
    return client.putAccountDetails(request);
  }

  public static PutAccountSendingAttributesResponse putAccountSendingAttributes(
      SesV2Client client, PutAccountSendingAttributesRequest request) {
    return client.putAccountSendingAttributes(request);
  }

  public static PutAccountSuppressionAttributesResponse putAccountSuppressionAttributes(
      SesV2Client client, PutAccountSuppressionAttributesRequest request) {
    return client.putAccountSuppressionAttributes(request);
  }

  public static PutAccountVdmAttributesResponse putAccountVdmAttributes(
      SesV2Client client, PutAccountVdmAttributesRequest request) {
    return client.putAccountVdmAttributes(request);
  }
}
