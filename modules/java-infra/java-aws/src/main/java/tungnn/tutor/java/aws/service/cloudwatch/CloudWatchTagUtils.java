package tungnn.tutor.java.aws.service.cloudwatch;

import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;

public final class CloudWatchTagUtils {
    private CloudWatchTagUtils() {}

    public static ListTagsForResourceResponse listTagsForResource(
            CloudWatchClient client, ListTagsForResourceRequest request) {
        return client.listTagsForResource(request);
    }

    public static TagResourceResponse tagResource(CloudWatchClient client, TagResourceRequest request) {
        return client.tagResource(request);
    }

    public static UntagResourceResponse untagResource(CloudWatchClient client, UntagResourceRequest request) {
        return client.untagResource(request);
    }
}
