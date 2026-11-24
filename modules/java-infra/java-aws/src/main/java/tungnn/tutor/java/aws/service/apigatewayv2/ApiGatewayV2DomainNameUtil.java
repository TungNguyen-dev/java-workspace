package tungnn.tutor.java.aws.service.apigatewayv2;

import software.amazon.awssdk.services.apigatewayv2.ApiGatewayV2Client;
import software.amazon.awssdk.services.apigatewayv2.model.*;

public final class ApiGatewayV2DomainNameUtil {
    private ApiGatewayV2DomainNameUtil() {}

    public static CreateApiMappingResponse createApiMapping(
            ApiGatewayV2Client client, CreateApiMappingRequest request) {
        return client.createApiMapping(request);
    }

    public static CreateDomainNameResponse createDomainName(
            ApiGatewayV2Client client, CreateDomainNameRequest request) {
        return client.createDomainName(request);
    }

    public static DeleteApiMappingResponse deleteApiMapping(
            ApiGatewayV2Client client, DeleteApiMappingRequest request) {
        return client.deleteApiMapping(request);
    }

    public static DeleteDomainNameResponse deleteDomainName(
            ApiGatewayV2Client client, DeleteDomainNameRequest request) {
        return client.deleteDomainName(request);
    }

    public static GetApiMappingResponse getApiMapping(ApiGatewayV2Client client, GetApiMappingRequest request) {
        return client.getApiMapping(request);
    }

    public static GetApiMappingsResponse getApiMappings(ApiGatewayV2Client client, GetApiMappingsRequest request) {
        return client.getApiMappings(request);
    }

    public static GetDomainNameResponse getDomainName(ApiGatewayV2Client client, GetDomainNameRequest request) {
        return client.getDomainName(request);
    }

    public static GetDomainNamesResponse getDomainNames(ApiGatewayV2Client client, GetDomainNamesRequest request) {
        return client.getDomainNames(request);
    }

    public static UpdateApiMappingResponse updateApiMapping(
            ApiGatewayV2Client client, UpdateApiMappingRequest request) {
        return client.updateApiMapping(request);
    }

    public static UpdateDomainNameResponse updateDomainName(
            ApiGatewayV2Client client, UpdateDomainNameRequest request) {
        return client.updateDomainName(request);
    }
}
