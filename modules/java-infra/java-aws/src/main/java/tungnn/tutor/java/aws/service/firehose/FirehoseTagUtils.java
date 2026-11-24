package tungnn.tutor.java.aws.service.firehose;

import software.amazon.awssdk.services.firehose.FirehoseClient;
import software.amazon.awssdk.services.firehose.model.*;

public final class FirehoseTagUtils {
    private FirehoseTagUtils() {}

    public static ListTagsForDeliveryStreamResponse listTagsForDeliveryStream(
            FirehoseClient client, ListTagsForDeliveryStreamRequest request) {
        return client.listTagsForDeliveryStream(request);
    }
}
