package tungnn.tutor.java.aws.service.cognitoidentity;

import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentity.model.*;

public final class CognitoIdentityTagUtils {
    private CognitoIdentityTagUtils() {}

    public static ListTagsForResourceResponse listTagsForResource(
            CognitoIdentityClient client, ListTagsForResourceRequest request) {
        return client.listTagsForResource(request);
    }

    public static TagResourceResponse tagResource(CognitoIdentityClient client, TagResourceRequest request) {
        return client.tagResource(request);
    }

    public static UntagResourceResponse untagResource(CognitoIdentityClient client, UntagResourceRequest request) {
        return client.untagResource(request);
    }
}
