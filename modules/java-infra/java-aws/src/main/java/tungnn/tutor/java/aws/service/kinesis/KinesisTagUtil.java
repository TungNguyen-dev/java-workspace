package tungnn.tutor.java.aws.service.kinesis;

import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.*;

public final class KinesisTagUtil {
    private KinesisTagUtil() {}

    public static AddTagsToStreamResponse addTagsToStream(KinesisClient client, AddTagsToStreamRequest request) {
        return client.addTagsToStream(request);
    }

    public static ListTagsForResourceResponse listTagsForResource(
            KinesisClient client, ListTagsForResourceRequest request) {
        return client.listTagsForResource(request);
    }

    public static ListTagsForStreamResponse listTagsForStream(KinesisClient client, ListTagsForStreamRequest request) {
        return client.listTagsForStream(request);
    }

    public static RemoveTagsFromStreamResponse removeTagsFromStream(
            KinesisClient client, RemoveTagsFromStreamRequest request) {
        return client.removeTagsFromStream(request);
    }
}
