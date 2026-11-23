package tungnn.tutor.java.aws.service.apigateway;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.*;

public final class ApiGatewayRestApiUtil {
  private ApiGatewayRestApiUtil() {}

  public static CreateDocumentationPartResponse createDocumentationPart(
      ApiGatewayClient client, CreateDocumentationPartRequest request) {
    return client.createDocumentationPart(request);
  }

  public static CreateDocumentationVersionResponse createDocumentationVersion(
      ApiGatewayClient client, CreateDocumentationVersionRequest request) {
    return client.createDocumentationVersion(request);
  }

  public static CreateRequestValidatorResponse createRequestValidator(
      ApiGatewayClient client, CreateRequestValidatorRequest request) {
    return client.createRequestValidator(request);
  }

  public static CreateRestApiResponse createRestApi(
      ApiGatewayClient client, CreateRestApiRequest request) {
    return client.createRestApi(request);
  }

  public static CreateVpcLinkResponse createVpcLink(
      ApiGatewayClient client, CreateVpcLinkRequest request) {
    return client.createVpcLink(request);
  }

  public static DeleteClientCertificateResponse deleteClientCertificate(
      ApiGatewayClient client, DeleteClientCertificateRequest request) {
    return client.deleteClientCertificate(request);
  }

  public static DeleteDocumentationPartResponse deleteDocumentationPart(
      ApiGatewayClient client, DeleteDocumentationPartRequest request) {
    return client.deleteDocumentationPart(request);
  }

  public static DeleteDocumentationVersionResponse deleteDocumentationVersion(
      ApiGatewayClient client, DeleteDocumentationVersionRequest request) {
    return client.deleteDocumentationVersion(request);
  }

  public static DeleteGatewayResponseResponse deleteGatewayResponse(
      ApiGatewayClient client, DeleteGatewayResponseRequest request) {
    return client.deleteGatewayResponse(request);
  }

  public static DeleteRequestValidatorResponse deleteRequestValidator(
      ApiGatewayClient client, DeleteRequestValidatorRequest request) {
    return client.deleteRequestValidator(request);
  }

  public static DeleteRestApiResponse deleteRestApi(
      ApiGatewayClient client, DeleteRestApiRequest request) {
    return client.deleteRestApi(request);
  }

  public static DeleteVpcLinkResponse deleteVpcLink(
      ApiGatewayClient client, DeleteVpcLinkRequest request) {
    return client.deleteVpcLink(request);
  }

  public static GenerateClientCertificateResponse generateClientCertificate(
      ApiGatewayClient client, GenerateClientCertificateRequest request) {
    return client.generateClientCertificate(request);
  }

  public static GetAccountResponse getAccount(ApiGatewayClient client, GetAccountRequest request) {
    return client.getAccount(request);
  }

  public static GetClientCertificateResponse getClientCertificate(
      ApiGatewayClient client, GetClientCertificateRequest request) {
    return client.getClientCertificate(request);
  }

  public static List<ClientCertificate> getClientCertificates(
      ApiGatewayClient client, GetClientCertificatesRequest request) {
    return client.getClientCertificatesPaginator(request).stream()
        .flatMap(r -> r.items().stream())
        .collect(Collectors.toList());
  }

  public static GetDocumentationPartResponse getDocumentationPart(
      ApiGatewayClient client, GetDocumentationPartRequest request) {
    return client.getDocumentationPart(request);
  }

  public static GetDocumentationPartsResponse getDocumentationParts(
      ApiGatewayClient client, GetDocumentationPartsRequest request) {
    return client.getDocumentationParts(request);
  }

  public static GetDocumentationVersionResponse getDocumentationVersion(
      ApiGatewayClient client, GetDocumentationVersionRequest request) {
    return client.getDocumentationVersion(request);
  }

  public static GetDocumentationVersionsResponse getDocumentationVersions(
      ApiGatewayClient client, GetDocumentationVersionsRequest request) {
    return client.getDocumentationVersions(request);
  }

  public static GetExportResponse getExport(ApiGatewayClient client, GetExportRequest request) {
    return client.getExport(request);
  }

  public static GetGatewayResponseResponse getGatewayResponse(
      ApiGatewayClient client, GetGatewayResponseRequest request) {
    return client.getGatewayResponse(request);
  }

  public static GetGatewayResponsesResponse getGatewayResponses(
      ApiGatewayClient client, GetGatewayResponsesRequest request) {
    return client.getGatewayResponses(request);
  }

  public static GetRequestValidatorResponse getRequestValidator(
      ApiGatewayClient client, GetRequestValidatorRequest request) {
    return client.getRequestValidator(request);
  }

  public static GetRequestValidatorsResponse getRequestValidators(
      ApiGatewayClient client, GetRequestValidatorsRequest request) {
    return client.getRequestValidators(request);
  }

  public static GetRestApiResponse getRestApi(ApiGatewayClient client, GetRestApiRequest request) {
    return client.getRestApi(request);
  }

  public static List<RestApi> getRestApis(ApiGatewayClient client, GetRestApisRequest request) {
    return client.getRestApisPaginator(request).stream()
        .flatMap(r -> r.items().stream())
        .collect(Collectors.toList());
  }

  public static GetSdkResponse getSdk(ApiGatewayClient client, GetSdkRequest request) {
    return client.getSdk(request);
  }

  public static GetSdkTypeResponse getSdkType(ApiGatewayClient client, GetSdkTypeRequest request) {
    return client.getSdkType(request);
  }

  public static GetSdkTypesResponse getSdkTypes(
      ApiGatewayClient client, GetSdkTypesRequest request) {
    return client.getSdkTypes(request);
  }

  public static GetVpcLinkResponse getVpcLink(ApiGatewayClient client, GetVpcLinkRequest request) {
    return client.getVpcLink(request);
  }

  public static List<VpcLink> getVpcLinks(ApiGatewayClient client, GetVpcLinksRequest request) {
    return client.getVpcLinksPaginator(request).stream()
        .flatMap(r -> r.items().stream())
        .collect(Collectors.toList());
  }

  public static ImportDocumentationPartsResponse importDocumentationParts(
      ApiGatewayClient client, ImportDocumentationPartsRequest request) {
    return client.importDocumentationParts(request);
  }

  public static ImportRestApiResponse importRestApi(
      ApiGatewayClient client, ImportRestApiRequest request) {
    return client.importRestApi(request);
  }

  public static PutGatewayResponseResponse putGatewayResponse(
      ApiGatewayClient client, PutGatewayResponseRequest request) {
    return client.putGatewayResponse(request);
  }

  public static PutRestApiResponse putRestApi(ApiGatewayClient client, PutRestApiRequest request) {
    return client.putRestApi(request);
  }

  public static UpdateAccountResponse updateAccount(
      ApiGatewayClient client, UpdateAccountRequest request) {
    return client.updateAccount(request);
  }

  public static UpdateClientCertificateResponse updateClientCertificate(
      ApiGatewayClient client, UpdateClientCertificateRequest request) {
    return client.updateClientCertificate(request);
  }

  public static UpdateDocumentationPartResponse updateDocumentationPart(
      ApiGatewayClient client, UpdateDocumentationPartRequest request) {
    return client.updateDocumentationPart(request);
  }

  public static UpdateDocumentationVersionResponse updateDocumentationVersion(
      ApiGatewayClient client, UpdateDocumentationVersionRequest request) {
    return client.updateDocumentationVersion(request);
  }

  public static UpdateGatewayResponseResponse updateGatewayResponse(
      ApiGatewayClient client, UpdateGatewayResponseRequest request) {
    return client.updateGatewayResponse(request);
  }

  public static UpdateRequestValidatorResponse updateRequestValidator(
      ApiGatewayClient client, UpdateRequestValidatorRequest request) {
    return client.updateRequestValidator(request);
  }

  public static UpdateRestApiResponse updateRestApi(
      ApiGatewayClient client, UpdateRestApiRequest request) {
    return client.updateRestApi(request);
  }

  public static UpdateVpcLinkResponse updateVpcLink(
      ApiGatewayClient client, UpdateVpcLinkRequest request) {
    return client.updateVpcLink(request);
  }
}
