package tungnn.tutor.java.aws.service.ssm;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.*;

public final class SsmOpsItemUtil {

  private SsmOpsItemUtil() {}

  public static AssociateOpsItemRelatedItemResponse associateOpsItemRelatedItem(
      SsmClient client, AssociateOpsItemRelatedItemRequest request) {
    return client.associateOpsItemRelatedItem(request);
  }

  public static CreateOpsItemResponse createOpsItem(
      SsmClient client, CreateOpsItemRequest request) {
    return client.createOpsItem(request);
  }

  public static DeleteOpsItemResponse deleteOpsItem(
      SsmClient client, DeleteOpsItemRequest request) {
    return client.deleteOpsItem(request);
  }

  public static List<OpsItemSummary> describeOpsItems(
      SsmClient client, DescribeOpsItemsRequest request) {
    return client.describeOpsItemsPaginator(request).stream()
        .flatMap(r -> r.opsItemSummaries().stream())
        .collect(Collectors.toList());
  }

  public static DisassociateOpsItemRelatedItemResponse disassociateOpsItemRelatedItem(
      SsmClient client, DisassociateOpsItemRelatedItemRequest request) {
    return client.disassociateOpsItemRelatedItem(request);
  }

  public static GetOpsItemResponse getOpsItem(SsmClient client, GetOpsItemRequest request) {
    return client.getOpsItem(request);
  }

  public static List<OpsItemEventSummary> listOpsItemEvents(
      SsmClient client, ListOpsItemEventsRequest request) {
    return client.listOpsItemEventsPaginator(request).stream()
        .flatMap(r -> r.summaries().stream())
        .collect(Collectors.toList());
  }

  public static List<OpsItemRelatedItemSummary> listOpsItemRelatedItems(
      SsmClient client, ListOpsItemRelatedItemsRequest request) {
    return client.listOpsItemRelatedItemsPaginator(request).stream()
        .flatMap(r -> r.summaries().stream())
        .collect(Collectors.toList());
  }

  public static UpdateOpsItemResponse updateOpsItem(
      SsmClient client, UpdateOpsItemRequest request) {
    return client.updateOpsItem(request);
  }
}
