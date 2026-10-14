package tungnn.tutor.java.aws.service.eventbridge;

import java.util.List;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.*;

public final class EndpointUtil {

  private EndpointUtil() {}

  public static CreateEndpointResponse createEndpoint(
      EventBridgeClient client, CreateEndpointRequest request) {
    return client.createEndpoint(request);
  }

  public static UpdateEndpointResponse updateEndpoint(
      EventBridgeClient client, UpdateEndpointRequest request) {
    return client.updateEndpoint(request);
  }

  public static DeleteEndpointResponse deleteEndpoint(
      EventBridgeClient client, DeleteEndpointRequest request) {
    return client.deleteEndpoint(request);
  }

  public static DescribeEndpointResponse describeEndpoint(
      EventBridgeClient client, DescribeEndpointRequest request) {
    return client.describeEndpoint(request);
  }

  public static List<Endpoint> listEndpoints(
      EventBridgeClient client, ListEndpointsRequest request) {
    return List.copyOf(client.listEndpoints(request).endpoints());
  }
}
