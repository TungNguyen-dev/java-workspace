package tungnn.tutor.java.aws.service.cognitoidentityprovider;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

public final class CognitoUserPoolClientUtils {
    private CognitoUserPoolClientUtils() {}

    public static AddUserPoolClientSecretResponse addUserPoolClientSecret(
            CognitoIdentityProviderClient client, AddUserPoolClientSecretRequest request) {
        return client.addUserPoolClientSecret(request);
    }

    public static CreateUserPoolClientResponse createUserPoolClient(
            CognitoIdentityProviderClient client, CreateUserPoolClientRequest request) {
        return client.createUserPoolClient(request);
    }

    public static DeleteUserPoolClientResponse deleteUserPoolClient(
            CognitoIdentityProviderClient client, DeleteUserPoolClientRequest request) {
        return client.deleteUserPoolClient(request);
    }

    public static DeleteUserPoolClientSecretResponse deleteUserPoolClientSecret(
            CognitoIdentityProviderClient client, DeleteUserPoolClientSecretRequest request) {
        return client.deleteUserPoolClientSecret(request);
    }

    public static DescribeUserPoolClientResponse describeUserPoolClient(
            CognitoIdentityProviderClient client, DescribeUserPoolClientRequest request) {
        return client.describeUserPoolClient(request);
    }

    public static ListUserPoolClientSecretsResponse listUserPoolClientSecrets(
            CognitoIdentityProviderClient client, ListUserPoolClientSecretsRequest request) {
        return client.listUserPoolClientSecrets(request);
    }

    public static List<UserPoolClientDescription> listUserPoolClients(
            CognitoIdentityProviderClient client, ListUserPoolClientsRequest request) {
        return client.listUserPoolClientsPaginator(request).stream()
                .flatMap(r -> r.userPoolClients().stream())
                .collect(Collectors.toList());
    }

    public static UpdateUserPoolClientResponse updateUserPoolClient(
            CognitoIdentityProviderClient client, UpdateUserPoolClientRequest request) {
        return client.updateUserPoolClient(request);
    }
}
