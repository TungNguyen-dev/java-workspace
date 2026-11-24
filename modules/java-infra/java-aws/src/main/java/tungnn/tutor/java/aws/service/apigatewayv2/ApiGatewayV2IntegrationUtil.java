package tungnn.tutor.java.aws.service.apigatewayv2;

import software.amazon.awssdk.services.apigatewayv2.ApiGatewayV2Client;
import software.amazon.awssdk.services.apigatewayv2.model.*;

public final class ApiGatewayV2IntegrationUtil {
    private ApiGatewayV2IntegrationUtil() {}

    public static CreateIntegrationResponse createIntegration(
            ApiGatewayV2Client client, CreateIntegrationRequest request) {
        return client.createIntegration(request);
    }

    public static CreateIntegrationResponseResponse createIntegrationResponse(
            ApiGatewayV2Client client, CreateIntegrationResponseRequest request) {
        return client.createIntegrationResponse(request);
    }

    public static DeleteIntegrationResponse deleteIntegration(
            ApiGatewayV2Client client, DeleteIntegrationRequest request) {
        return client.deleteIntegration(request);
    }

    public static DeleteIntegrationResponseResponse deleteIntegrationResponse(
            ApiGatewayV2Client client, DeleteIntegrationResponseRequest request) {
        return client.deleteIntegrationResponse(request);
    }

    public static GetIntegrationResponse getIntegration(ApiGatewayV2Client client, GetIntegrationRequest request) {
        return client.getIntegration(request);
    }

    public static GetIntegrationResponseResponse getIntegrationResponse(
            ApiGatewayV2Client client, GetIntegrationResponseRequest request) {
        return client.getIntegrationResponse(request);
    }

    public static GetIntegrationResponsesResponse getIntegrationResponses(
            ApiGatewayV2Client client, GetIntegrationResponsesRequest request) {
        return client.getIntegrationResponses(request);
    }

    public static GetIntegrationsResponse getIntegrations(ApiGatewayV2Client client, GetIntegrationsRequest request) {
        return client.getIntegrations(request);
    }

    public static UpdateIntegrationResponse updateIntegration(
            ApiGatewayV2Client client, UpdateIntegrationRequest request) {
        return client.updateIntegration(request);
    }

    public static UpdateIntegrationResponseResponse updateIntegrationResponse(
            ApiGatewayV2Client client, UpdateIntegrationResponseRequest request) {
        return client.updateIntegrationResponse(request);
    }
}
