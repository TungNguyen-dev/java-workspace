package tungnn.tutor.java.aws.service.ssm;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.*;

public final class SsmOpsMetadataUtil {

  private SsmOpsMetadataUtil() {}

  public static CreateOpsMetadataResponse createOpsMetadata(
      SsmClient client, CreateOpsMetadataRequest request) {
    return client.createOpsMetadata(request);
  }

  public static DeleteOpsMetadataResponse deleteOpsMetadata(
      SsmClient client, DeleteOpsMetadataRequest request) {
    return client.deleteOpsMetadata(request);
  }

  public static GetOpsMetadataResponse getOpsMetadata(
      SsmClient client, GetOpsMetadataRequest request) {
    return client.getOpsMetadata(request);
  }

  public static List<OpsEntity> getOpsSummary(SsmClient client, GetOpsSummaryRequest request) {
    return client.getOpsSummaryPaginator(request).stream()
        .flatMap(r -> r.entities().stream())
        .collect(Collectors.toList());
  }

  public static List<OpsMetadata> listOpsMetadata(
      SsmClient client, ListOpsMetadataRequest request) {
    return client.listOpsMetadataPaginator(request).stream()
        .flatMap(r -> r.opsMetadataList().stream())
        .collect(Collectors.toList());
  }

  public static UpdateOpsMetadataResponse updateOpsMetadata(
      SsmClient client, UpdateOpsMetadataRequest request) {
    return client.updateOpsMetadata(request);
  }
}
