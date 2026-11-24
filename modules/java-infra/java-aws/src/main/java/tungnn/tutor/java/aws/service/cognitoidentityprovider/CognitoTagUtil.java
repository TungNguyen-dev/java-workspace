package tungnn.tutor.java.aws.service.cognitoidentityprovider;

import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

public final class CognitoTagUtil {
    private CognitoTagUtil() {}

    public static ListTagsForResourceResponse listTagsForResource(
            CognitoIdentityProviderClient client, ListTagsForResourceRequest request) {
        return client.listTagsForResource(request);
    }

    public static UntagResourceResponse untagResource(
            CognitoIdentityProviderClient client, UntagResourceRequest request) {
        return client.untagResource(request);
    }
}
