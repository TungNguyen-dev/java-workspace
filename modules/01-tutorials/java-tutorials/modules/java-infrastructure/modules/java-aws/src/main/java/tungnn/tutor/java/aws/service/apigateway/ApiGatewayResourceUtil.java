package tungnn.tutor.java.aws.service.apigateway;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.*;

public final class ApiGatewayResourceUtil {
  private ApiGatewayResourceUtil() {}

  public static CreateResourceResponse createResource(
      ApiGatewayClient client, CreateResourceRequest request) {
    return client.createResource(request);
  }

  public static DeleteIntegrationResponse deleteIntegration(
      ApiGatewayClient client, DeleteIntegrationRequest request) {
    return client.deleteIntegration(request);
  }

  public static DeleteIntegrationResponseResponse deleteIntegrationResponse(
      ApiGatewayClient client, DeleteIntegrationResponseRequest request) {
    return client.deleteIntegrationResponse(request);
  }

  public static DeleteMethodResponse deleteMethod(
      ApiGatewayClient client, DeleteMethodRequest request) {
    return client.deleteMethod(request);
  }

  public static DeleteMethodResponseResponse deleteMethodResponse(
      ApiGatewayClient client, DeleteMethodResponseRequest request) {
    return client.deleteMethodResponse(request);
  }

  public static DeleteResourceResponse deleteResource(
      ApiGatewayClient client, DeleteResourceRequest request) {
    return client.deleteResource(request);
  }

  public static GetIntegrationResponse getIntegration(
      ApiGatewayClient client, GetIntegrationRequest request) {
    return client.getIntegration(request);
  }

  public static GetIntegrationResponseResponse getIntegrationResponse(
      ApiGatewayClient client, GetIntegrationResponseRequest request) {
    return client.getIntegrationResponse(request);
  }

  public static GetMethodResponse getMethod(ApiGatewayClient client, GetMethodRequest request) {
    return client.getMethod(request);
  }

  public static GetMethodResponseResponse getMethodResponse(
      ApiGatewayClient client, GetMethodResponseRequest request) {
    return client.getMethodResponse(request);
  }

  public static GetResourceResponse getResource(
      ApiGatewayClient client, GetResourceRequest request) {
    return client.getResource(request);
  }

  public static List<Resource> getResources(ApiGatewayClient client, GetResourcesRequest request) {
    return client.getResourcesPaginator(request).stream()
        .flatMap(r -> r.items().stream())
        .collect(Collectors.toList());
  }

  public static PutIntegrationResponse putIntegration(
      ApiGatewayClient client, PutIntegrationRequest request) {
    return client.putIntegration(request);
  }

  public static PutIntegrationResponseResponse putIntegrationResponse(
      ApiGatewayClient client, PutIntegrationResponseRequest request) {
    return client.putIntegrationResponse(request);
  }

  public static PutMethodResponse putMethod(ApiGatewayClient client, PutMethodRequest request) {
    return client.putMethod(request);
  }

  public static PutMethodResponseResponse putMethodResponse(
      ApiGatewayClient client, PutMethodResponseRequest request) {
    return client.putMethodResponse(request);
  }

  public static TestInvokeMethodResponse testInvokeMethod(
      ApiGatewayClient client, TestInvokeMethodRequest request) {
    return client.testInvokeMethod(request);
  }

  public static UpdateIntegrationResponse updateIntegration(
      ApiGatewayClient client, UpdateIntegrationRequest request) {
    return client.updateIntegration(request);
  }

  public static UpdateIntegrationResponseResponse updateIntegrationResponse(
      ApiGatewayClient client, UpdateIntegrationResponseRequest request) {
    return client.updateIntegrationResponse(request);
  }

  public static UpdateMethodResponse updateMethod(
      ApiGatewayClient client, UpdateMethodRequest request) {
    return client.updateMethod(request);
  }

  public static UpdateMethodResponseResponse updateMethodResponse(
      ApiGatewayClient client, UpdateMethodResponseRequest request) {
    return client.updateMethodResponse(request);
  }

  public static UpdateResourceResponse updateResource(
      ApiGatewayClient client, UpdateResourceRequest request) {
    return client.updateResource(request);
  }
}
