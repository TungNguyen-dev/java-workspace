package tungnn.tutor.java.aws.service.eventbridge;

import java.util.List;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.*;

public final class EventSourceUtil {

  private EventSourceUtil() {}

  public static ActivateEventSourceResponse activateEventSource(
      EventBridgeClient client, ActivateEventSourceRequest request) {
    return client.activateEventSource(request);
  }

  public static DeactivateEventSourceResponse deactivateEventSource(
      EventBridgeClient client, DeactivateEventSourceRequest request) {
    return client.deactivateEventSource(request);
  }

  public static DescribeEventSourceResponse describeEventSource(
      EventBridgeClient client, DescribeEventSourceRequest request) {
    return client.describeEventSource(request);
  }

  public static List<EventSource> listEventSources(
      EventBridgeClient client, ListEventSourcesRequest request) {
    return List.copyOf(client.listEventSources(request).eventSources());
  }
}
