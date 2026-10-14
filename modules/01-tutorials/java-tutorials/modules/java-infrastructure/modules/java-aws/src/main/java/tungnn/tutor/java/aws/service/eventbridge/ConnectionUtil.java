package tungnn.tutor.java.aws.service.eventbridge;

import java.util.List;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.*;

public final class ConnectionUtil {

  private ConnectionUtil() {}

  public static CreateConnectionResponse createConnection(
      EventBridgeClient client, CreateConnectionRequest request) {
    return client.createConnection(request);
  }

  public static UpdateConnectionResponse updateConnection(
      EventBridgeClient client, UpdateConnectionRequest request) {
    return client.updateConnection(request);
  }

  public static DeleteConnectionResponse deleteConnection(
      EventBridgeClient client, DeleteConnectionRequest request) {
    return client.deleteConnection(request);
  }

  public static DeauthorizeConnectionResponse deauthorizeConnection(
      EventBridgeClient client, DeauthorizeConnectionRequest request) {
    return client.deauthorizeConnection(request);
  }

  public static DescribeConnectionResponse describeConnection(
      EventBridgeClient client, DescribeConnectionRequest request) {
    return client.describeConnection(request);
  }

  public static List<Connection> listConnections(
      EventBridgeClient client, ListConnectionsRequest request) {
    return List.copyOf(client.listConnections(request).connections());
  }
}
