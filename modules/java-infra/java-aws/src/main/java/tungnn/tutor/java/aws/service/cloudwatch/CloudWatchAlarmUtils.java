package tungnn.tutor.java.aws.service.cloudwatch;

import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;

public final class CloudWatchAlarmUtils {
    private CloudWatchAlarmUtils() {}

    public static DeleteAlarmMuteRuleResponse deleteAlarmMuteRule(
            CloudWatchClient client, DeleteAlarmMuteRuleRequest request) {
        return client.deleteAlarmMuteRule(request);
    }

    public static DeleteAlarmsResponse deleteAlarms(CloudWatchClient client, DeleteAlarmsRequest request) {
        return client.deleteAlarms(request);
    }

    public static DeleteAnomalyDetectorResponse deleteAnomalyDetector(
            CloudWatchClient client, DeleteAnomalyDetectorRequest request) {
        return client.deleteAnomalyDetector(request);
    }

    public static DescribeAlarmContributorsResponse describeAlarmContributors(
            CloudWatchClient client, DescribeAlarmContributorsRequest request) {
        return client.describeAlarmContributors(request);
    }

    public static DescribeAlarmHistoryResponse describeAlarmHistory(
            CloudWatchClient client, DescribeAlarmHistoryRequest request) {
        return client.describeAlarmHistory(request);
    }

    public static DescribeAlarmsResponse describeAlarms(CloudWatchClient client, DescribeAlarmsRequest request) {
        return client.describeAlarms(request);
    }

    public static DescribeAlarmsForMetricResponse describeAlarmsForMetric(
            CloudWatchClient client, DescribeAlarmsForMetricRequest request) {
        return client.describeAlarmsForMetric(request);
    }

    public static DescribeAnomalyDetectorsResponse describeAnomalyDetectors(
            CloudWatchClient client, DescribeAnomalyDetectorsRequest request) {
        return client.describeAnomalyDetectors(request);
    }

    public static DisableAlarmActionsResponse disableAlarmActions(
            CloudWatchClient client, DisableAlarmActionsRequest request) {
        return client.disableAlarmActions(request);
    }

    public static EnableAlarmActionsResponse enableAlarmActions(
            CloudWatchClient client, EnableAlarmActionsRequest request) {
        return client.enableAlarmActions(request);
    }

    public static GetAlarmMuteRuleResponse getAlarmMuteRule(CloudWatchClient client, GetAlarmMuteRuleRequest request) {
        return client.getAlarmMuteRule(request);
    }

    public static ListAlarmMuteRulesResponse listAlarmMuteRules(
            CloudWatchClient client, ListAlarmMuteRulesRequest request) {
        return client.listAlarmMuteRules(request);
    }

    public static PutAlarmMuteRuleResponse putAlarmMuteRule(CloudWatchClient client, PutAlarmMuteRuleRequest request) {
        return client.putAlarmMuteRule(request);
    }

    public static PutAnomalyDetectorResponse putAnomalyDetector(
            CloudWatchClient client, PutAnomalyDetectorRequest request) {
        return client.putAnomalyDetector(request);
    }

    public static PutCompositeAlarmResponse putCompositeAlarm(
            CloudWatchClient client, PutCompositeAlarmRequest request) {
        return client.putCompositeAlarm(request);
    }

    public static PutMetricAlarmResponse putMetricAlarm(CloudWatchClient client, PutMetricAlarmRequest request) {
        return client.putMetricAlarm(request);
    }

    public static SetAlarmStateResponse setAlarmState(CloudWatchClient client, SetAlarmStateRequest request) {
        return client.setAlarmState(request);
    }
}
