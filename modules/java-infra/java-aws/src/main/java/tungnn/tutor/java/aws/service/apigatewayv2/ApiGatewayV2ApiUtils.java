package tungnn.tutor.java.aws.service.apigatewayv2;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.apigatewayv2.ApiGatewayV2Client;
import software.amazon.awssdk.services.apigatewayv2.model.*;

public final class ApiGatewayV2ApiUtils {
    private ApiGatewayV2ApiUtils() {}

    public static CreateApiResponse createApi(ApiGatewayV2Client client, CreateApiRequest request) {
        return client.createApi(request);
    }

    public static CreateModelResponse createModel(ApiGatewayV2Client client, CreateModelRequest request) {
        return client.createModel(request);
    }

    public static CreatePortalResponse createPortal(ApiGatewayV2Client client, CreatePortalRequest request) {
        return client.createPortal(request);
    }

    public static CreatePortalProductResponse createPortalProduct(
            ApiGatewayV2Client client, CreatePortalProductRequest request) {
        return client.createPortalProduct(request);
    }

    public static CreateProductPageResponse createProductPage(
            ApiGatewayV2Client client, CreateProductPageRequest request) {
        return client.createProductPage(request);
    }

    public static CreateProductRestEndpointPageResponse createProductRestEndpointPage(
            ApiGatewayV2Client client, CreateProductRestEndpointPageRequest request) {
        return client.createProductRestEndpointPage(request);
    }

    public static CreateRoutingRuleResponse createRoutingRule(
            ApiGatewayV2Client client, CreateRoutingRuleRequest request) {
        return client.createRoutingRule(request);
    }

    public static CreateVpcLinkResponse createVpcLink(ApiGatewayV2Client client, CreateVpcLinkRequest request) {
        return client.createVpcLink(request);
    }

    public static DeleteApiResponse deleteApi(ApiGatewayV2Client client, DeleteApiRequest request) {
        return client.deleteApi(request);
    }

    public static DeleteCorsConfigurationResponse deleteCorsConfiguration(
            ApiGatewayV2Client client, DeleteCorsConfigurationRequest request) {
        return client.deleteCorsConfiguration(request);
    }

    public static DeleteModelResponse deleteModel(ApiGatewayV2Client client, DeleteModelRequest request) {
        return client.deleteModel(request);
    }

    public static DeletePortalResponse deletePortal(ApiGatewayV2Client client, DeletePortalRequest request) {
        return client.deletePortal(request);
    }

    public static DeletePortalProductResponse deletePortalProduct(
            ApiGatewayV2Client client, DeletePortalProductRequest request) {
        return client.deletePortalProduct(request);
    }

    public static DeletePortalProductSharingPolicyResponse deletePortalProductSharingPolicy(
            ApiGatewayV2Client client, DeletePortalProductSharingPolicyRequest request) {
        return client.deletePortalProductSharingPolicy(request);
    }

    public static DeleteProductPageResponse deleteProductPage(
            ApiGatewayV2Client client, DeleteProductPageRequest request) {
        return client.deleteProductPage(request);
    }

    public static DeleteProductRestEndpointPageResponse deleteProductRestEndpointPage(
            ApiGatewayV2Client client, DeleteProductRestEndpointPageRequest request) {
        return client.deleteProductRestEndpointPage(request);
    }

    public static DeleteRoutingRuleResponse deleteRoutingRule(
            ApiGatewayV2Client client, DeleteRoutingRuleRequest request) {
        return client.deleteRoutingRule(request);
    }

    public static DeleteVpcLinkResponse deleteVpcLink(ApiGatewayV2Client client, DeleteVpcLinkRequest request) {
        return client.deleteVpcLink(request);
    }

    public static DisablePortalResponse disablePortal(ApiGatewayV2Client client, DisablePortalRequest request) {
        return client.disablePortal(request);
    }

    public static ExportApiResponse exportApi(ApiGatewayV2Client client, ExportApiRequest request) {
        return client.exportApi(request);
    }

    public static GetApiResponse getApi(ApiGatewayV2Client client, GetApiRequest request) {
        return client.getApi(request);
    }

    public static GetApisResponse getApis(ApiGatewayV2Client client, GetApisRequest request) {
        return client.getApis(request);
    }

    public static GetModelResponse getModel(ApiGatewayV2Client client, GetModelRequest request) {
        return client.getModel(request);
    }

    public static GetModelTemplateResponse getModelTemplate(
            ApiGatewayV2Client client, GetModelTemplateRequest request) {
        return client.getModelTemplate(request);
    }

    public static GetModelsResponse getModels(ApiGatewayV2Client client, GetModelsRequest request) {
        return client.getModels(request);
    }

    public static GetPortalResponse getPortal(ApiGatewayV2Client client, GetPortalRequest request) {
        return client.getPortal(request);
    }

    public static GetPortalProductResponse getPortalProduct(
            ApiGatewayV2Client client, GetPortalProductRequest request) {
        return client.getPortalProduct(request);
    }

    public static GetPortalProductSharingPolicyResponse getPortalProductSharingPolicy(
            ApiGatewayV2Client client, GetPortalProductSharingPolicyRequest request) {
        return client.getPortalProductSharingPolicy(request);
    }

    public static GetProductPageResponse getProductPage(ApiGatewayV2Client client, GetProductPageRequest request) {
        return client.getProductPage(request);
    }

    public static GetProductRestEndpointPageResponse getProductRestEndpointPage(
            ApiGatewayV2Client client, GetProductRestEndpointPageRequest request) {
        return client.getProductRestEndpointPage(request);
    }

    public static GetRoutingRuleResponse getRoutingRule(ApiGatewayV2Client client, GetRoutingRuleRequest request) {
        return client.getRoutingRule(request);
    }

    public static GetVpcLinkResponse getVpcLink(ApiGatewayV2Client client, GetVpcLinkRequest request) {
        return client.getVpcLink(request);
    }

    public static GetVpcLinksResponse getVpcLinks(ApiGatewayV2Client client, GetVpcLinksRequest request) {
        return client.getVpcLinks(request);
    }

    public static ImportApiResponse importApi(ApiGatewayV2Client client, ImportApiRequest request) {
        return client.importApi(request);
    }

    public static List<PortalProductSummary> listPortalProducts(
            ApiGatewayV2Client client, ListPortalProductsRequest request) {
        return client.listPortalProductsPaginator(request).stream()
                .flatMap(r -> r.items().stream())
                .collect(Collectors.toList());
    }

    public static List<PortalSummary> listPortals(ApiGatewayV2Client client, ListPortalsRequest request) {
        return client.listPortalsPaginator(request).stream()
                .flatMap(r -> r.items().stream())
                .collect(Collectors.toList());
    }

    public static List<ProductPageSummaryNoBody> listProductPages(
            ApiGatewayV2Client client, ListProductPagesRequest request) {
        return client.listProductPagesPaginator(request).stream()
                .flatMap(r -> r.items().stream())
                .collect(Collectors.toList());
    }

    public static List<ProductRestEndpointPageSummaryNoBody> listProductRestEndpointPages(
            ApiGatewayV2Client client, ListProductRestEndpointPagesRequest request) {
        return client.listProductRestEndpointPagesPaginator(request).stream()
                .flatMap(r -> r.items().stream())
                .collect(Collectors.toList());
    }

    public static List<RoutingRule> listRoutingRules(ApiGatewayV2Client client, ListRoutingRulesRequest request) {
        return client.listRoutingRulesPaginator(request).stream()
                .flatMap(r -> r.routingRules().stream())
                .collect(Collectors.toList());
    }

    public static PreviewPortalResponse previewPortal(ApiGatewayV2Client client, PreviewPortalRequest request) {
        return client.previewPortal(request);
    }

    public static PublishPortalResponse publishPortal(ApiGatewayV2Client client, PublishPortalRequest request) {
        return client.publishPortal(request);
    }

    public static PutPortalProductSharingPolicyResponse putPortalProductSharingPolicy(
            ApiGatewayV2Client client, PutPortalProductSharingPolicyRequest request) {
        return client.putPortalProductSharingPolicy(request);
    }

    public static PutRoutingRuleResponse putRoutingRule(ApiGatewayV2Client client, PutRoutingRuleRequest request) {
        return client.putRoutingRule(request);
    }

    public static ReimportApiResponse reimportApi(ApiGatewayV2Client client, ReimportApiRequest request) {
        return client.reimportApi(request);
    }

    public static UpdateApiResponse updateApi(ApiGatewayV2Client client, UpdateApiRequest request) {
        return client.updateApi(request);
    }

    public static UpdateModelResponse updateModel(ApiGatewayV2Client client, UpdateModelRequest request) {
        return client.updateModel(request);
    }

    public static UpdatePortalResponse updatePortal(ApiGatewayV2Client client, UpdatePortalRequest request) {
        return client.updatePortal(request);
    }

    public static UpdatePortalProductResponse updatePortalProduct(
            ApiGatewayV2Client client, UpdatePortalProductRequest request) {
        return client.updatePortalProduct(request);
    }

    public static UpdateProductPageResponse updateProductPage(
            ApiGatewayV2Client client, UpdateProductPageRequest request) {
        return client.updateProductPage(request);
    }

    public static UpdateProductRestEndpointPageResponse updateProductRestEndpointPage(
            ApiGatewayV2Client client, UpdateProductRestEndpointPageRequest request) {
        return client.updateProductRestEndpointPage(request);
    }

    public static UpdateVpcLinkResponse updateVpcLink(ApiGatewayV2Client client, UpdateVpcLinkRequest request) {
        return client.updateVpcLink(request);
    }
}
