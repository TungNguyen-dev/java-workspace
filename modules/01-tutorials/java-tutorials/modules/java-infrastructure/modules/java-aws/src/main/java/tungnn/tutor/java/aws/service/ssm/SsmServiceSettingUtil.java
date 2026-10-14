package tungnn.tutor.java.aws.service.ssm;

import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.*;

public final class SsmServiceSettingUtil {

  private SsmServiceSettingUtil() {}

  public static GetServiceSettingResponse getServiceSetting(
      SsmClient client, GetServiceSettingRequest request) {
    return client.getServiceSetting(request);
  }

  public static ResetServiceSettingResponse resetServiceSetting(
      SsmClient client, ResetServiceSettingRequest request) {
    return client.resetServiceSetting(request);
  }

  public static UpdateServiceSettingResponse updateServiceSetting(
      SsmClient client, UpdateServiceSettingRequest request) {
    return client.updateServiceSetting(request);
  }
}
