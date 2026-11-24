package tungnn.tutor.java.aws.service.dynamodb;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

public final class DynamoTagUtil {
    private DynamoTagUtil() {}

    public static TagResourceResponse tagResource(DynamoDbClient client, TagResourceRequest request) {
        return client.tagResource(request);
    }

    public static UntagResourceResponse untagResource(DynamoDbClient client, UntagResourceRequest request) {
        return client.untagResource(request);
    }

    public static ListTagsOfResourceResponse listTagsOfResource(
            DynamoDbClient client, ListTagsOfResourceRequest request) {
        return client.listTagsOfResource(request);
    }
}
