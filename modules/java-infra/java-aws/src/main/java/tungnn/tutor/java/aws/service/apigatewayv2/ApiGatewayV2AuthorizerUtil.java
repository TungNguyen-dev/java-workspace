package tungnn.tutor.java.aws.service.apigatewayv2;

import software.amazon.awssdk.services.apigatewayv2.ApiGatewayV2Client;
import software.amazon.awssdk.services.apigatewayv2.model.*;

public final class ApiGatewayV2AuthorizerUtil {
    private ApiGatewayV2AuthorizerUtil() {}

    public static CreateAuthorizerResponse createAuthorizer(
            ApiGatewayV2Client client, CreateAuthorizerRequest request) {
        return client.createAuthorizer(request);
    }

    public static DeleteAuthorizerResponse deleteAuthorizer(
            ApiGatewayV2Client client, DeleteAuthorizerRequest request) {
        return client.deleteAuthorizer(request);
    }

    public static GetAuthorizerResponse getAuthorizer(ApiGatewayV2Client client, GetAuthorizerRequest request) {
        return client.getAuthorizer(request);
    }

    public static GetAuthorizersResponse getAuthorizers(ApiGatewayV2Client client, GetAuthorizersRequest request) {
        return client.getAuthorizers(request);
    }

    public static ResetAuthorizersCacheResponse resetAuthorizersCache(
            ApiGatewayV2Client client, ResetAuthorizersCacheRequest request) {
        return client.resetAuthorizersCache(request);
    }

    public static UpdateAuthorizerResponse updateAuthorizer(
            ApiGatewayV2Client client, UpdateAuthorizerRequest request) {
        return client.updateAuthorizer(request);
    }
}
