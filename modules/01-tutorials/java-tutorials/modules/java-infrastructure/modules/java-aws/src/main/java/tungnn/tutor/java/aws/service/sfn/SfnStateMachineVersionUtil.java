package tungnn.tutor.java.aws.service.sfn;

import java.util.List;
import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.model.*;

public final class SfnStateMachineVersionUtil {

  private SfnStateMachineVersionUtil() {}

  public static PublishStateMachineVersionResponse publishStateMachineVersion(
      SfnClient client, PublishStateMachineVersionRequest request) {
    return client.publishStateMachineVersion(request);
  }

  public static DeleteStateMachineVersionResponse deleteStateMachineVersion(
      SfnClient client, DeleteStateMachineVersionRequest request) {
    return client.deleteStateMachineVersion(request);
  }

  public static List<StateMachineVersionListItem> listStateMachineVersions(
      SfnClient client, ListStateMachineVersionsRequest request) {
    return List.copyOf(client.listStateMachineVersions(request).stateMachineVersions());
  }
}
