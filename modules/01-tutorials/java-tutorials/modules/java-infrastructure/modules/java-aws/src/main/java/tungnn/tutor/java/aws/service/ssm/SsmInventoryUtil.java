package tungnn.tutor.java.aws.service.ssm;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.*;

public final class SsmInventoryUtil {

  private SsmInventoryUtil() {}

  public static DeleteInventoryResponse deleteInventory(
      SsmClient client, DeleteInventoryRequest request) {
    return client.deleteInventory(request);
  }

  public static List<InventoryDeletionStatusItem> describeInventoryDeletions(
      SsmClient client, DescribeInventoryDeletionsRequest request) {
    return client.describeInventoryDeletionsPaginator(request).stream()
        .flatMap(r -> r.inventoryDeletions().stream())
        .collect(Collectors.toList());
  }

  public static List<InventoryResultEntity> getInventory(
      SsmClient client, GetInventoryRequest request) {
    return client.getInventoryPaginator(request).stream()
        .flatMap(r -> r.entities().stream())
        .collect(Collectors.toList());
  }

  public static List<InventoryItemSchema> getInventorySchema(
      SsmClient client, GetInventorySchemaRequest request) {
    return client.getInventorySchemaPaginator(request).stream()
        .flatMap(r -> r.schemas().stream())
        .collect(Collectors.toList());
  }

  public static List<Map<String, String>> listInventoryEntries(
      SsmClient client, ListInventoryEntriesRequest request) {
    return List.copyOf(client.listInventoryEntries(request).entries());
  }

  public static PutInventoryResponse putInventory(SsmClient client, PutInventoryRequest request) {
    return client.putInventory(request);
  }
}
