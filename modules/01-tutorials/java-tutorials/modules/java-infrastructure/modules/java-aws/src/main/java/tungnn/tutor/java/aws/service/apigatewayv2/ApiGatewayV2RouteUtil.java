package tungnn.tutor.java.aws.service.apigatewayv2;

import software.amazon.awssdk.services.apigatewayv2.ApiGatewayV2Client;
import software.amazon.awssdk.services.apigatewayv2.model.*;

public final class ApiGatewayV2RouteUtil {
  private ApiGatewayV2RouteUtil() {}

  public static CreateRouteResponse createRoute(
      ApiGatewayV2Client client, CreateRouteRequest request) {
    return client.createRoute(request);
  }

  public static CreateRouteResponseResponse createRouteResponse(
      ApiGatewayV2Client client, CreateRouteResponseRequest request) {
    return client.createRouteResponse(request);
  }

  public static DeleteRouteResponse deleteRoute(
      ApiGatewayV2Client client, DeleteRouteRequest request) {
    return client.deleteRoute(request);
  }

  public static DeleteRouteRequestParameterResponse deleteRouteRequestParameter(
      ApiGatewayV2Client client, DeleteRouteRequestParameterRequest request) {
    return client.deleteRouteRequestParameter(request);
  }

  public static DeleteRouteResponseResponse deleteRouteResponse(
      ApiGatewayV2Client client, DeleteRouteResponseRequest request) {
    return client.deleteRouteResponse(request);
  }

  public static DeleteRouteSettingsResponse deleteRouteSettings(
      ApiGatewayV2Client client, DeleteRouteSettingsRequest request) {
    return client.deleteRouteSettings(request);
  }

  public static GetRouteResponse getRoute(ApiGatewayV2Client client, GetRouteRequest request) {
    return client.getRoute(request);
  }

  public static GetRouteResponseResponse getRouteResponse(
      ApiGatewayV2Client client, GetRouteResponseRequest request) {
    return client.getRouteResponse(request);
  }

  public static GetRouteResponsesResponse getRouteResponses(
      ApiGatewayV2Client client, GetRouteResponsesRequest request) {
    return client.getRouteResponses(request);
  }

  public static GetRoutesResponse getRoutes(ApiGatewayV2Client client, GetRoutesRequest request) {
    return client.getRoutes(request);
  }

  public static UpdateRouteResponse updateRoute(
      ApiGatewayV2Client client, UpdateRouteRequest request) {
    return client.updateRoute(request);
  }

  public static UpdateRouteResponseResponse updateRouteResponse(
      ApiGatewayV2Client client, UpdateRouteResponseRequest request) {
    return client.updateRouteResponse(request);
  }
}
