package tungnn.tutor.java.aws.service.secretsmanager;

import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.*;

public final class SecretsManagerPolicyUtils {
    private SecretsManagerPolicyUtils() {}

    public static GetResourcePolicyResponse getResourcePolicy(
            SecretsManagerClient client, GetResourcePolicyRequest request) {
        return client.getResourcePolicy(request);
    }

    public static PutResourcePolicyResponse putResourcePolicy(
            SecretsManagerClient client, PutResourcePolicyRequest request) {
        return client.putResourcePolicy(request);
    }

    public static DeleteResourcePolicyResponse deleteResourcePolicy(
            SecretsManagerClient client, DeleteResourcePolicyRequest request) {
        return client.deleteResourcePolicy(request);
    }

    public static ValidateResourcePolicyResponse validateResourcePolicy(
            SecretsManagerClient client, ValidateResourcePolicyRequest request) {
        return client.validateResourcePolicy(request);
    }
}
