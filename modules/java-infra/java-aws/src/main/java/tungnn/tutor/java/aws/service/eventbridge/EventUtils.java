package tungnn.tutor.java.aws.service.eventbridge;

import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.*;

public final class EventUtils {
    private EventUtils() {}

    public static PutEventsResponse putEvents(EventBridgeClient client, PutEventsRequest request) {
        return client.putEvents(request);
    }

    public static TestEventPatternResponse testEventPattern(EventBridgeClient client, TestEventPatternRequest request) {
        return client.testEventPattern(request);
    }
}
