package tungnn.tutor.java.aws.service.sns;

import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

public final class SnsTagUtil {
    private SnsTagUtil() {}

    public static TagResourceResponse tagResource(SnsClient client, TagResourceRequest request) {
        return client.tagResource(request);
    }

    public static UntagResourceResponse untagResource(SnsClient client, UntagResourceRequest request) {
        return client.untagResource(request);
    }

    public static ListTagsForResourceResponse listTagsForResource(
            SnsClient client, ListTagsForResourceRequest request) {
        return client.listTagsForResource(request);
    }
}
