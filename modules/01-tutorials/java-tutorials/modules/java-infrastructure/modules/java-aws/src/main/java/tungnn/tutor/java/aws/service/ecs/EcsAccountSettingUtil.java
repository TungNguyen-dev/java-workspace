package tungnn.tutor.java.aws.service.ecs;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.*;

public final class EcsAccountSettingUtil {

  private EcsAccountSettingUtil() {}

  public static PutAccountSettingResponse putAccountSetting(
      EcsClient client, PutAccountSettingRequest request) {
    return client.putAccountSetting(request);
  }

  public static PutAccountSettingDefaultResponse putAccountSettingDefault(
      EcsClient client, PutAccountSettingDefaultRequest request) {
    return client.putAccountSettingDefault(request);
  }

  public static DeleteAccountSettingResponse deleteAccountSetting(
      EcsClient client, DeleteAccountSettingRequest request) {
    return client.deleteAccountSetting(request);
  }

  public static List<Setting> listAccountSettings(
      EcsClient client, ListAccountSettingsRequest request) {
    return client.listAccountSettingsPaginator(request).stream()
        .flatMap(r -> r.settings().stream())
        .collect(Collectors.toList());
  }
}
