package tungnn.tutor.java.aws.service.apigateway;

import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.*;

public final class ApiGatewayTagUtil {
  private ApiGatewayTagUtil() {}

  public static GetTagsResponse getTags(ApiGatewayClient client, GetTagsRequest request) {
    return client.getTags(request);
  }

  public static TagResourceResponse tagResource(
      ApiGatewayClient client, TagResourceRequest request) {
    return client.tagResource(request);
  }

  public static UntagResourceResponse untagResource(
      ApiGatewayClient client, UntagResourceRequest request) {
    return client.untagResource(request);
  }
}
