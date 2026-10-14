package tungnn.tutor.java.aws.service.eventbridge;

import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.*;

public final class PermissionUtil {

  private PermissionUtil() {}

  public static PutPermissionResponse putPermission(
      EventBridgeClient client, PutPermissionRequest request) {
    return client.putPermission(request);
  }

  public static RemovePermissionResponse removePermission(
      EventBridgeClient client, RemovePermissionRequest request) {
    return client.removePermission(request);
  }
}
