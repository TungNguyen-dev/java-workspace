package tungnn.tutor.java.aws.service.apigateway;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.*;

public final class ApiGatewayDomainNameUtil {
    private ApiGatewayDomainNameUtil() {}

    public static CreateBasePathMappingResponse createBasePathMapping(
            ApiGatewayClient client, CreateBasePathMappingRequest request) {
        return client.createBasePathMapping(request);
    }

    public static CreateDomainNameResponse createDomainName(ApiGatewayClient client, CreateDomainNameRequest request) {
        return client.createDomainName(request);
    }

    public static CreateDomainNameAccessAssociationResponse createDomainNameAccessAssociation(
            ApiGatewayClient client, CreateDomainNameAccessAssociationRequest request) {
        return client.createDomainNameAccessAssociation(request);
    }

    public static DeleteBasePathMappingResponse deleteBasePathMapping(
            ApiGatewayClient client, DeleteBasePathMappingRequest request) {
        return client.deleteBasePathMapping(request);
    }

    public static DeleteDomainNameResponse deleteDomainName(ApiGatewayClient client, DeleteDomainNameRequest request) {
        return client.deleteDomainName(request);
    }

    public static DeleteDomainNameAccessAssociationResponse deleteDomainNameAccessAssociation(
            ApiGatewayClient client, DeleteDomainNameAccessAssociationRequest request) {
        return client.deleteDomainNameAccessAssociation(request);
    }

    public static GetBasePathMappingResponse getBasePathMapping(
            ApiGatewayClient client, GetBasePathMappingRequest request) {
        return client.getBasePathMapping(request);
    }

    public static List<BasePathMapping> getBasePathMappings(
            ApiGatewayClient client, GetBasePathMappingsRequest request) {
        return client.getBasePathMappingsPaginator(request).stream()
                .flatMap(r -> r.items().stream())
                .collect(Collectors.toList());
    }

    public static GetDomainNameResponse getDomainName(ApiGatewayClient client, GetDomainNameRequest request) {
        return client.getDomainName(request);
    }

    public static GetDomainNameAccessAssociationsResponse getDomainNameAccessAssociations(
            ApiGatewayClient client, GetDomainNameAccessAssociationsRequest request) {
        return client.getDomainNameAccessAssociations(request);
    }

    public static List<DomainName> getDomainNames(ApiGatewayClient client, GetDomainNamesRequest request) {
        return client.getDomainNamesPaginator(request).stream()
                .flatMap(r -> r.items().stream())
                .collect(Collectors.toList());
    }

    public static RejectDomainNameAccessAssociationResponse rejectDomainNameAccessAssociation(
            ApiGatewayClient client, RejectDomainNameAccessAssociationRequest request) {
        return client.rejectDomainNameAccessAssociation(request);
    }

    public static UpdateBasePathMappingResponse updateBasePathMapping(
            ApiGatewayClient client, UpdateBasePathMappingRequest request) {
        return client.updateBasePathMapping(request);
    }

    public static UpdateDomainNameResponse updateDomainName(ApiGatewayClient client, UpdateDomainNameRequest request) {
        return client.updateDomainName(request);
    }
}
