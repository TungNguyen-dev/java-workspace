package tungnn.tutor.java.aws.service.kinesis;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.*;

public final class KinesisStreamUtils {
    private KinesisStreamUtils() {}

    public static CreateStreamResponse createStream(KinesisClient client, CreateStreamRequest request) {
        return client.createStream(request);
    }

    public static DecreaseStreamRetentionPeriodResponse decreaseStreamRetentionPeriod(
            KinesisClient client, DecreaseStreamRetentionPeriodRequest request) {
        return client.decreaseStreamRetentionPeriod(request);
    }

    public static DeleteResourcePolicyResponse deleteResourcePolicy(
            KinesisClient client, DeleteResourcePolicyRequest request) {
        return client.deleteResourcePolicy(request);
    }

    public static DeleteStreamResponse deleteStream(KinesisClient client, DeleteStreamRequest request) {
        return client.deleteStream(request);
    }

    public static DescribeAccountSettingsResponse describeAccountSettings(
            KinesisClient client, DescribeAccountSettingsRequest request) {
        return client.describeAccountSettings(request);
    }

    public static DescribeLimitsResponse describeLimits(KinesisClient client, DescribeLimitsRequest request) {
        return client.describeLimits(request);
    }

    public static DescribeStreamResponse describeStream(KinesisClient client, DescribeStreamRequest request) {
        return client.describeStream(request);
    }

    public static DescribeStreamSummaryResponse describeStreamSummary(
            KinesisClient client, DescribeStreamSummaryRequest request) {
        return client.describeStreamSummary(request);
    }

    public static DisableEnhancedMonitoringResponse disableEnhancedMonitoring(
            KinesisClient client, DisableEnhancedMonitoringRequest request) {
        return client.disableEnhancedMonitoring(request);
    }

    public static EnableEnhancedMonitoringResponse enableEnhancedMonitoring(
            KinesisClient client, EnableEnhancedMonitoringRequest request) {
        return client.enableEnhancedMonitoring(request);
    }

    public static GetResourcePolicyResponse getResourcePolicy(KinesisClient client, GetResourcePolicyRequest request) {
        return client.getResourcePolicy(request);
    }

    public static IncreaseStreamRetentionPeriodResponse increaseStreamRetentionPeriod(
            KinesisClient client, IncreaseStreamRetentionPeriodRequest request) {
        return client.increaseStreamRetentionPeriod(request);
    }

    public static List<StreamSummary> listStreams(KinesisClient client, ListStreamsRequest request) {
        return client.listStreamsPaginator(request).stream()
                .flatMap(r -> r.streamSummaries().stream())
                .collect(Collectors.toList());
    }

    public static PutResourcePolicyResponse putResourcePolicy(KinesisClient client, PutResourcePolicyRequest request) {
        return client.putResourcePolicy(request);
    }

    public static TagResourceResponse tagResource(KinesisClient client, TagResourceRequest request) {
        return client.tagResource(request);
    }

    public static UntagResourceResponse untagResource(KinesisClient client, UntagResourceRequest request) {
        return client.untagResource(request);
    }

    public static UpdateAccountSettingsResponse updateAccountSettings(
            KinesisClient client, UpdateAccountSettingsRequest request) {
        return client.updateAccountSettings(request);
    }

    public static UpdateMaxRecordSizeResponse updateMaxRecordSize(
            KinesisClient client, UpdateMaxRecordSizeRequest request) {
        return client.updateMaxRecordSize(request);
    }

    public static UpdateStreamModeResponse updateStreamMode(KinesisClient client, UpdateStreamModeRequest request) {
        return client.updateStreamMode(request);
    }

    public static UpdateStreamWarmThroughputResponse updateStreamWarmThroughput(
            KinesisClient client, UpdateStreamWarmThroughputRequest request) {
        return client.updateStreamWarmThroughput(request);
    }
}
