package tungnn.tutor.java.aws.service.apigateway;

import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.*;

public final class ApiGatewayAuthorizerUtils {
    private ApiGatewayAuthorizerUtils() {}

    public static CreateAuthorizerResponse createAuthorizer(ApiGatewayClient client, CreateAuthorizerRequest request) {
        return client.createAuthorizer(request);
    }

    public static DeleteAuthorizerResponse deleteAuthorizer(ApiGatewayClient client, DeleteAuthorizerRequest request) {
        return client.deleteAuthorizer(request);
    }

    public static FlushStageAuthorizersCacheResponse flushStageAuthorizersCache(
            ApiGatewayClient client, FlushStageAuthorizersCacheRequest request) {
        return client.flushStageAuthorizersCache(request);
    }

    public static GetAuthorizerResponse getAuthorizer(ApiGatewayClient client, GetAuthorizerRequest request) {
        return client.getAuthorizer(request);
    }

    public static GetAuthorizersResponse getAuthorizers(ApiGatewayClient client, GetAuthorizersRequest request) {
        return client.getAuthorizers(request);
    }

    public static TestInvokeAuthorizerResponse testInvokeAuthorizer(
            ApiGatewayClient client, TestInvokeAuthorizerRequest request) {
        return client.testInvokeAuthorizer(request);
    }

    public static UpdateAuthorizerResponse updateAuthorizer(ApiGatewayClient client, UpdateAuthorizerRequest request) {
        return client.updateAuthorizer(request);
    }
}
