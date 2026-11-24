package tungnn.tutor.java.aws.service.cloudwatchlogs;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.*;

public final class CloudWatchLogsStreamUtil {
    private CloudWatchLogsStreamUtil() {}

    public static CreateLogStreamResponse createLogStream(CloudWatchLogsClient client, CreateLogStreamRequest request) {
        return client.createLogStream(request);
    }

    public static DeleteLogStreamResponse deleteLogStream(CloudWatchLogsClient client, DeleteLogStreamRequest request) {
        return client.deleteLogStream(request);
    }

    public static List<LogStream> describeLogStreams(CloudWatchLogsClient client, DescribeLogStreamsRequest request) {
        return client.describeLogStreamsPaginator(request).stream()
                .flatMap(r -> r.logStreams().stream())
                .collect(Collectors.toList());
    }
}
