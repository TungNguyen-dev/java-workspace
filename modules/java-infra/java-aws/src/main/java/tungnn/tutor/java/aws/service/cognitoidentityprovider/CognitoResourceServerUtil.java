package tungnn.tutor.java.aws.service.cognitoidentityprovider;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

public final class CognitoResourceServerUtil {
    private CognitoResourceServerUtil() {}

    public static CreateResourceServerResponse createResourceServer(
            CognitoIdentityProviderClient client, CreateResourceServerRequest request) {
        return client.createResourceServer(request);
    }

    public static DeleteResourceServerResponse deleteResourceServer(
            CognitoIdentityProviderClient client, DeleteResourceServerRequest request) {
        return client.deleteResourceServer(request);
    }

    public static DescribeResourceServerResponse describeResourceServer(
            CognitoIdentityProviderClient client, DescribeResourceServerRequest request) {
        return client.describeResourceServer(request);
    }

    public static List<ResourceServerType> listResourceServers(
            CognitoIdentityProviderClient client, ListResourceServersRequest request) {
        return client.listResourceServersPaginator(request).stream()
                .flatMap(r -> r.resourceServers().stream())
                .collect(Collectors.toList());
    }

    public static UpdateResourceServerResponse updateResourceServer(
            CognitoIdentityProviderClient client, UpdateResourceServerRequest request) {
        return client.updateResourceServer(request);
    }
}
