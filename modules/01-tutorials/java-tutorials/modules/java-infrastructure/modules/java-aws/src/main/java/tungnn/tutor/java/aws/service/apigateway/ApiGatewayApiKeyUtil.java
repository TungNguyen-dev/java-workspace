package tungnn.tutor.java.aws.service.apigateway;

import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.*;

public final class ApiGatewayApiKeyUtil {
  private ApiGatewayApiKeyUtil() {}

  public static CreateApiKeyResponse createApiKey(
      ApiGatewayClient client, CreateApiKeyRequest request) {
    return client.createApiKey(request);
  }

  public static DeleteApiKeyResponse deleteApiKey(
      ApiGatewayClient client, DeleteApiKeyRequest request) {
    return client.deleteApiKey(request);
  }

  public static GetApiKeyResponse getApiKey(ApiGatewayClient client, GetApiKeyRequest request) {
    return client.getApiKey(request);
  }

  public static GetApiKeysResponse getApiKeys(ApiGatewayClient client, GetApiKeysRequest request) {
    return client.getApiKeys(request);
  }

  public static ImportApiKeysResponse importApiKeys(
      ApiGatewayClient client, ImportApiKeysRequest request) {
    return client.importApiKeys(request);
  }

  public static UpdateApiKeyResponse updateApiKey(
      ApiGatewayClient client, UpdateApiKeyRequest request) {
    return client.updateApiKey(request);
  }
}
