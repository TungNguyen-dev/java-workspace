package tungnn.tutor.java.aws.service.cognitoidentity;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentity.model.*;

public final class CognitoIdentityPoolUtil {
    private CognitoIdentityPoolUtil() {}

    public static CreateIdentityPoolResponse createIdentityPool(
            CognitoIdentityClient client, CreateIdentityPoolRequest request) {
        return client.createIdentityPool(request);
    }

    public static DeleteIdentityPoolResponse deleteIdentityPool(
            CognitoIdentityClient client, DeleteIdentityPoolRequest request) {
        return client.deleteIdentityPool(request);
    }

    public static DescribeIdentityPoolResponse describeIdentityPool(
            CognitoIdentityClient client, DescribeIdentityPoolRequest request) {
        return client.describeIdentityPool(request);
    }

    public static List<IdentityPoolShortDescription> listIdentityPools(
            CognitoIdentityClient client, ListIdentityPoolsRequest request) {
        return client.listIdentityPoolsPaginator(request).stream()
                .flatMap(r -> r.identityPools().stream())
                .collect(Collectors.toList());
    }

    public static UpdateIdentityPoolResponse updateIdentityPool(
            CognitoIdentityClient client, UpdateIdentityPoolRequest request) {
        return client.updateIdentityPool(request);
    }
}
