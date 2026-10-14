package tungnn.tutor.java.aws.service.eventbridge;

import java.util.List;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.*;

public final class ArchiveUtil {

  private ArchiveUtil() {}

  public static CreateArchiveResponse createArchive(
      EventBridgeClient client, CreateArchiveRequest request) {
    return client.createArchive(request);
  }

  public static UpdateArchiveResponse updateArchive(
      EventBridgeClient client, UpdateArchiveRequest request) {
    return client.updateArchive(request);
  }

  public static DeleteArchiveResponse deleteArchive(
      EventBridgeClient client, DeleteArchiveRequest request) {
    return client.deleteArchive(request);
  }

  public static DescribeArchiveResponse describeArchive(
      EventBridgeClient client, DescribeArchiveRequest request) {
    return client.describeArchive(request);
  }

  public static List<Archive> listArchives(EventBridgeClient client, ListArchivesRequest request) {
    return List.copyOf(client.listArchives(request).archives());
  }
}
