package tungnn.tutor.java.aws.service.lambda;

import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.*;

public final class LambdaPermissionUtil {
    private LambdaPermissionUtil() {}

    public static AddPermissionResponse addPermission(LambdaClient client, AddPermissionRequest request) {
        return client.addPermission(request);
    }

    public static GetPolicyResponse getPolicy(LambdaClient client, GetPolicyRequest request) {
        return client.getPolicy(request);
    }

    public static RemovePermissionResponse removePermission(LambdaClient client, RemovePermissionRequest request) {
        return client.removePermission(request);
    }
}
