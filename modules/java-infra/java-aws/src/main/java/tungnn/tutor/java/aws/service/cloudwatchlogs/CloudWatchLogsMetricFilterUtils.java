package tungnn.tutor.java.aws.service.cloudwatchlogs;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.*;

public final class CloudWatchLogsMetricFilterUtils {
    private CloudWatchLogsMetricFilterUtils() {}

    public static DeleteMetricFilterResponse deleteMetricFilter(
            CloudWatchLogsClient client, DeleteMetricFilterRequest request) {
        return client.deleteMetricFilter(request);
    }

    public static List<MetricFilter> describeMetricFilters(
            CloudWatchLogsClient client, DescribeMetricFiltersRequest request) {
        return client.describeMetricFiltersPaginator(request).stream()
                .flatMap(r -> r.metricFilters().stream())
                .collect(Collectors.toList());
    }

    public static PutMetricFilterResponse putMetricFilter(CloudWatchLogsClient client, PutMetricFilterRequest request) {
        return client.putMetricFilter(request);
    }

    public static TestMetricFilterResponse testMetricFilter(
            CloudWatchLogsClient client, TestMetricFilterRequest request) {
        return client.testMetricFilter(request);
    }
}
