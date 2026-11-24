package tungnn.tutor.java.aws.service.lambda;

import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.*;

public final class LambdaTagUtil {
    private LambdaTagUtil() {}

    public static ListTagsResponse listTags(LambdaClient client, ListTagsRequest request) {
        return client.listTags(request);
    }

    public static TagResourceResponse tagResource(LambdaClient client, TagResourceRequest request) {
        return client.tagResource(request);
    }

    public static UntagResourceResponse untagResource(LambdaClient client, UntagResourceRequest request) {
        return client.untagResource(request);
    }
}
