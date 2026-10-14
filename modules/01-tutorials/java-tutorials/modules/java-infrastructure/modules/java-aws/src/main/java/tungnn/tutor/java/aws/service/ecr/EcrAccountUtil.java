package tungnn.tutor.java.aws.service.ecr;

import software.amazon.awssdk.services.ecr.EcrClient;
import software.amazon.awssdk.services.ecr.model.*;

public final class EcrAccountUtil {

  private EcrAccountUtil() {}

  public static GetAccountSettingResponse getAccountSetting(
      EcrClient ecrClient, GetAccountSettingRequest request) {
    return ecrClient.getAccountSetting(request);
  }

  public static PutAccountSettingResponse putAccountSetting(
      EcrClient ecrClient, PutAccountSettingRequest request) {
    return ecrClient.putAccountSetting(request);
  }
}
