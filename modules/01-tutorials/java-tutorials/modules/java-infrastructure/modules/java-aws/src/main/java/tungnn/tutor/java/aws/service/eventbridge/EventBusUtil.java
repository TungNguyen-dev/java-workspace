package tungnn.tutor.java.aws.service.eventbridge;

import java.util.List;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.*;

public final class EventBusUtil {

  private EventBusUtil() {}

  public static CreateEventBusResponse createEventBus(
      EventBridgeClient client, CreateEventBusRequest request) {
    return client.createEventBus(request);
  }

  public static UpdateEventBusResponse updateEventBus(
      EventBridgeClient client, UpdateEventBusRequest request) {
    return client.updateEventBus(request);
  }

  public static DeleteEventBusResponse deleteEventBus(
      EventBridgeClient client, DeleteEventBusRequest request) {
    return client.deleteEventBus(request);
  }

  public static DescribeEventBusResponse describeEventBus(
      EventBridgeClient client, DescribeEventBusRequest request) {
    return client.describeEventBus(request);
  }

  public static List<EventBus> listEventBuses(
      EventBridgeClient client, ListEventBusesRequest request) {
    return List.copyOf(client.listEventBuses(request).eventBuses());
  }
}
