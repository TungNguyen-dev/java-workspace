package tungnn.tutor.java.aws.service.apigateway;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.*;

public final class ApiGatewayDeploymentUtils {
    private ApiGatewayDeploymentUtils() {}

    public static CreateDeploymentResponse createDeployment(ApiGatewayClient client, CreateDeploymentRequest request) {
        return client.createDeployment(request);
    }

    public static DeleteDeploymentResponse deleteDeployment(ApiGatewayClient client, DeleteDeploymentRequest request) {
        return client.deleteDeployment(request);
    }

    public static GetDeploymentResponse getDeployment(ApiGatewayClient client, GetDeploymentRequest request) {
        return client.getDeployment(request);
    }

    public static List<Deployment> getDeployments(ApiGatewayClient client, GetDeploymentsRequest request) {
        return client.getDeploymentsPaginator(request).stream()
                .flatMap(r -> r.items().stream())
                .collect(Collectors.toList());
    }

    public static UpdateDeploymentResponse updateDeployment(ApiGatewayClient client, UpdateDeploymentRequest request) {
        return client.updateDeployment(request);
    }
}
