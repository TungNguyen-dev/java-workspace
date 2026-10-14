package tungnn.tutor.java.aws.service.eventbridge;

import java.util.List;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.*;

public final class ApiDestinationUtil {

  private ApiDestinationUtil() {}

  public static CreateApiDestinationResponse createApiDestination(
      EventBridgeClient client, CreateApiDestinationRequest request) {
    return client.createApiDestination(request);
  }

  public static UpdateApiDestinationResponse updateApiDestination(
      EventBridgeClient client, UpdateApiDestinationRequest request) {
    return client.updateApiDestination(request);
  }

  public static DeleteApiDestinationResponse deleteApiDestination(
      EventBridgeClient client, DeleteApiDestinationRequest request) {
    return client.deleteApiDestination(request);
  }

  public static DescribeApiDestinationResponse describeApiDestination(
      EventBridgeClient client, DescribeApiDestinationRequest request) {
    return client.describeApiDestination(request);
  }

  public static List<ApiDestination> listApiDestinations(
      EventBridgeClient client, ListApiDestinationsRequest request) {
    return List.copyOf(client.listApiDestinations(request).apiDestinations());
  }
}
