package tungnn.tutor.java.aws.service.lambda;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.*;

public final class LambdaEventSourceMappingUtil {
    private LambdaEventSourceMappingUtil() {}

    public static CreateEventSourceMappingResponse createEventSourceMapping(
            LambdaClient client, CreateEventSourceMappingRequest request) {
        return client.createEventSourceMapping(request);
    }

    public static DeleteEventSourceMappingResponse deleteEventSourceMapping(
            LambdaClient client, DeleteEventSourceMappingRequest request) {
        return client.deleteEventSourceMapping(request);
    }

    public static GetEventSourceMappingResponse getEventSourceMapping(
            LambdaClient client, GetEventSourceMappingRequest request) {
        return client.getEventSourceMapping(request);
    }

    public static List<EventSourceMappingConfiguration> listEventSourceMappings(
            LambdaClient client, ListEventSourceMappingsRequest request) {
        return client.listEventSourceMappingsPaginator(request).stream()
                .flatMap(r -> r.eventSourceMappings().stream())
                .collect(Collectors.toList());
    }

    public static UpdateEventSourceMappingResponse updateEventSourceMapping(
            LambdaClient client, UpdateEventSourceMappingRequest request) {
        return client.updateEventSourceMapping(request);
    }
}
