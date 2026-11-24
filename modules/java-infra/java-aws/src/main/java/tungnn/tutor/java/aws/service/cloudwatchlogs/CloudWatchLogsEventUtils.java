package tungnn.tutor.java.aws.service.cloudwatchlogs;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.*;

public final class CloudWatchLogsEventUtils {
    private CloudWatchLogsEventUtils() {}

    public static List<FilteredLogEvent> filterLogEvents(CloudWatchLogsClient client, FilterLogEventsRequest request) {
        return client.filterLogEventsPaginator(request).stream()
                .flatMap(r -> r.events().stream())
                .collect(Collectors.toList());
    }

    public static List<OutputLogEvent> getLogEvents(CloudWatchLogsClient client, GetLogEventsRequest request) {
        return client.getLogEventsPaginator(request).stream()
                .flatMap(r -> r.events().stream())
                .collect(Collectors.toList());
    }

    public static GetQueryResultsResponse getQueryResults(CloudWatchLogsClient client, GetQueryResultsRequest request) {
        return client.getQueryResults(request);
    }

    public static PutLogEventsResponse putLogEvents(CloudWatchLogsClient client, PutLogEventsRequest request) {
        return client.putLogEvents(request);
    }

    public static StartQueryResponse startQuery(CloudWatchLogsClient client, StartQueryRequest request) {
        return client.startQuery(request);
    }
}
