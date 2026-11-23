package tungnn.tutor.java.aws.service.apigatewayv2;

import software.amazon.awssdk.services.apigatewayv2.ApiGatewayV2Client;
import software.amazon.awssdk.services.apigatewayv2.model.*;

public final class ApiGatewayV2DeploymentUtil {
  private ApiGatewayV2DeploymentUtil() {}

  public static CreateDeploymentResponse createDeployment(
      ApiGatewayV2Client client, CreateDeploymentRequest request) {
    return client.createDeployment(request);
  }

  public static DeleteDeploymentResponse deleteDeployment(
      ApiGatewayV2Client client, DeleteDeploymentRequest request) {
    return client.deleteDeployment(request);
  }

  public static GetDeploymentResponse getDeployment(
      ApiGatewayV2Client client, GetDeploymentRequest request) {
    return client.getDeployment(request);
  }

  public static GetDeploymentsResponse getDeployments(
      ApiGatewayV2Client client, GetDeploymentsRequest request) {
    return client.getDeployments(request);
  }

  public static UpdateDeploymentResponse updateDeployment(
      ApiGatewayV2Client client, UpdateDeploymentRequest request) {
    return client.updateDeployment(request);
  }
}
