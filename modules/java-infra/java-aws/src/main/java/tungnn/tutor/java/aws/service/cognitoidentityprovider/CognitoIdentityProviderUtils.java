package tungnn.tutor.java.aws.service.cognitoidentityprovider;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

public final class CognitoIdentityProviderUtils {
    private CognitoIdentityProviderUtils() {}

    public static AdminDisableProviderForUserResponse adminDisableProviderForUser(
            CognitoIdentityProviderClient client, AdminDisableProviderForUserRequest request) {
        return client.adminDisableProviderForUser(request);
    }

    public static AdminLinkProviderForUserResponse adminLinkProviderForUser(
            CognitoIdentityProviderClient client, AdminLinkProviderForUserRequest request) {
        return client.adminLinkProviderForUser(request);
    }

    public static CreateIdentityProviderResponse createIdentityProvider(
            CognitoIdentityProviderClient client, CreateIdentityProviderRequest request) {
        return client.createIdentityProvider(request);
    }

    public static DeleteIdentityProviderResponse deleteIdentityProvider(
            CognitoIdentityProviderClient client, DeleteIdentityProviderRequest request) {
        return client.deleteIdentityProvider(request);
    }

    public static DescribeIdentityProviderResponse describeIdentityProvider(
            CognitoIdentityProviderClient client, DescribeIdentityProviderRequest request) {
        return client.describeIdentityProvider(request);
    }

    public static GetIdentityProviderByIdentifierResponse getIdentityProviderByIdentifier(
            CognitoIdentityProviderClient client, GetIdentityProviderByIdentifierRequest request) {
        return client.getIdentityProviderByIdentifier(request);
    }

    public static List<ProviderDescription> listIdentityProviders(
            CognitoIdentityProviderClient client, ListIdentityProvidersRequest request) {
        return client.listIdentityProvidersPaginator(request).stream()
                .flatMap(r -> r.providers().stream())
                .collect(Collectors.toList());
    }

    public static UpdateIdentityProviderResponse updateIdentityProvider(
            CognitoIdentityProviderClient client, UpdateIdentityProviderRequest request) {
        return client.updateIdentityProvider(request);
    }
}
