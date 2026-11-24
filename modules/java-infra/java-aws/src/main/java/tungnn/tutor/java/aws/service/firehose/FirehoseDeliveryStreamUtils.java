package tungnn.tutor.java.aws.service.firehose;

import software.amazon.awssdk.services.firehose.FirehoseClient;
import software.amazon.awssdk.services.firehose.model.*;

public final class FirehoseDeliveryStreamUtils {
    private FirehoseDeliveryStreamUtils() {}

    public static CreateDeliveryStreamResponse createDeliveryStream(
            FirehoseClient client, CreateDeliveryStreamRequest request) {
        return client.createDeliveryStream(request);
    }

    public static DeleteDeliveryStreamResponse deleteDeliveryStream(
            FirehoseClient client, DeleteDeliveryStreamRequest request) {
        return client.deleteDeliveryStream(request);
    }

    public static DescribeDeliveryStreamResponse describeDeliveryStream(
            FirehoseClient client, DescribeDeliveryStreamRequest request) {
        return client.describeDeliveryStream(request);
    }

    public static ListDeliveryStreamsResponse listDeliveryStreams(
            FirehoseClient client, ListDeliveryStreamsRequest request) {
        return client.listDeliveryStreams(request);
    }

    public static TagDeliveryStreamResponse tagDeliveryStream(FirehoseClient client, TagDeliveryStreamRequest request) {
        return client.tagDeliveryStream(request);
    }

    public static UntagDeliveryStreamResponse untagDeliveryStream(
            FirehoseClient client, UntagDeliveryStreamRequest request) {
        return client.untagDeliveryStream(request);
    }

    public static UpdateDestinationResponse updateDestination(FirehoseClient client, UpdateDestinationRequest request) {
        return client.updateDestination(request);
    }
}
