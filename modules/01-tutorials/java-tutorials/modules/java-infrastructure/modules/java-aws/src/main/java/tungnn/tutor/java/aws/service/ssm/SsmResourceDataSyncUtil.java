package tungnn.tutor.java.aws.service.ssm;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.*;

public final class SsmResourceDataSyncUtil {

  private SsmResourceDataSyncUtil() {}

  public static CreateResourceDataSyncResponse createResourceDataSync(
      SsmClient client, CreateResourceDataSyncRequest request) {
    return client.createResourceDataSync(request);
  }

  public static DeleteResourceDataSyncResponse deleteResourceDataSync(
      SsmClient client, DeleteResourceDataSyncRequest request) {
    return client.deleteResourceDataSync(request);
  }

  public static List<ResourceDataSyncItem> listResourceDataSync(
      SsmClient client, ListResourceDataSyncRequest request) {
    return client.listResourceDataSyncPaginator(request).stream()
        .flatMap(r -> r.resourceDataSyncItems().stream())
        .collect(Collectors.toList());
  }

  public static UpdateResourceDataSyncResponse updateResourceDataSync(
      SsmClient client, UpdateResourceDataSyncRequest request) {
    return client.updateResourceDataSync(request);
  }
}
