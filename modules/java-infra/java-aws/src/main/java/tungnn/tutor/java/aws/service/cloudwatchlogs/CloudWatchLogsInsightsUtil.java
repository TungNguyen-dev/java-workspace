package tungnn.tutor.java.aws.service.cloudwatchlogs;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.*;

public final class CloudWatchLogsInsightsUtil {
    private CloudWatchLogsInsightsUtil() {}

    public static CreateLogAnomalyDetectorResponse createLogAnomalyDetector(
            CloudWatchLogsClient client, CreateLogAnomalyDetectorRequest request) {
        return client.createLogAnomalyDetector(request);
    }

    public static CreateScheduledQueryResponse createScheduledQuery(
            CloudWatchLogsClient client, CreateScheduledQueryRequest request) {
        return client.createScheduledQuery(request);
    }

    public static DeleteLogAnomalyDetectorResponse deleteLogAnomalyDetector(
            CloudWatchLogsClient client, DeleteLogAnomalyDetectorRequest request) {
        return client.deleteLogAnomalyDetector(request);
    }

    public static DeleteQueryDefinitionResponse deleteQueryDefinition(
            CloudWatchLogsClient client, DeleteQueryDefinitionRequest request) {
        return client.deleteQueryDefinition(request);
    }

    public static DeleteScheduledQueryResponse deleteScheduledQuery(
            CloudWatchLogsClient client, DeleteScheduledQueryRequest request) {
        return client.deleteScheduledQuery(request);
    }

    public static DescribeFieldIndexesResponse describeFieldIndexes(
            CloudWatchLogsClient client, DescribeFieldIndexesRequest request) {
        return client.describeFieldIndexes(request);
    }

    public static DescribeQueriesResponse describeQueries(CloudWatchLogsClient client, DescribeQueriesRequest request) {
        return client.describeQueries(request);
    }

    public static DescribeQueryDefinitionsResponse describeQueryDefinitions(
            CloudWatchLogsClient client, DescribeQueryDefinitionsRequest request) {
        return client.describeQueryDefinitions(request);
    }

    public static GetLogAnomalyDetectorResponse getLogAnomalyDetector(
            CloudWatchLogsClient client, GetLogAnomalyDetectorRequest request) {
        return client.getLogAnomalyDetector(request);
    }

    public static GetLogFieldsResponse getLogFields(CloudWatchLogsClient client, GetLogFieldsRequest request) {
        return client.getLogFields(request);
    }

    public static GetLogGroupFieldsResponse getLogGroupFields(
            CloudWatchLogsClient client, GetLogGroupFieldsRequest request) {
        return client.getLogGroupFields(request);
    }

    public static GetLogRecordResponse getLogRecord(CloudWatchLogsClient client, GetLogRecordRequest request) {
        return client.getLogRecord(request);
    }

    public static GetScheduledQueryResponse getScheduledQuery(
            CloudWatchLogsClient client, GetScheduledQueryRequest request) {
        return client.getScheduledQuery(request);
    }

    public static List<TriggerHistoryRecord> getScheduledQueryHistory(
            CloudWatchLogsClient client, GetScheduledQueryHistoryRequest request) {
        return client.getScheduledQueryHistoryPaginator(request).stream()
                .flatMap(r -> r.triggerHistory().stream())
                .collect(Collectors.toList());
    }

    public static List<Anomaly> listAnomalies(CloudWatchLogsClient client, ListAnomaliesRequest request) {
        return client.listAnomaliesPaginator(request).stream()
                .flatMap(r -> r.anomalies().stream())
                .collect(Collectors.toList());
    }

    public static List<AnomalyDetector> listLogAnomalyDetectors(
            CloudWatchLogsClient client, ListLogAnomalyDetectorsRequest request) {
        return client.listLogAnomalyDetectorsPaginator(request).stream()
                .flatMap(r -> r.anomalyDetectors().stream())
                .collect(Collectors.toList());
    }

    public static List<ScheduledQuerySummary> listScheduledQueries(
            CloudWatchLogsClient client, ListScheduledQueriesRequest request) {
        return client.listScheduledQueriesPaginator(request).stream()
                .flatMap(r -> r.scheduledQueries().stream())
                .collect(Collectors.toList());
    }

    public static PutQueryDefinitionResponse putQueryDefinition(
            CloudWatchLogsClient client, PutQueryDefinitionRequest request) {
        return client.putQueryDefinition(request);
    }

    public static StopQueryResponse stopQuery(CloudWatchLogsClient client, StopQueryRequest request) {
        return client.stopQuery(request);
    }

    public static UpdateAnomalyResponse updateAnomaly(CloudWatchLogsClient client, UpdateAnomalyRequest request) {
        return client.updateAnomaly(request);
    }

    public static UpdateLogAnomalyDetectorResponse updateLogAnomalyDetector(
            CloudWatchLogsClient client, UpdateLogAnomalyDetectorRequest request) {
        return client.updateLogAnomalyDetector(request);
    }

    public static UpdateScheduledQueryResponse updateScheduledQuery(
            CloudWatchLogsClient client, UpdateScheduledQueryRequest request) {
        return client.updateScheduledQuery(request);
    }
}
