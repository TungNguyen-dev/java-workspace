package tungnn.tutor.java.aws.service.apigatewayv2;

import software.amazon.awssdk.services.apigatewayv2.ApiGatewayV2Client;
import software.amazon.awssdk.services.apigatewayv2.model.*;

public final class ApiGatewayV2TagUtil {
  private ApiGatewayV2TagUtil() {}

  public static GetTagsResponse getTags(ApiGatewayV2Client client, GetTagsRequest request) {
    return client.getTags(request);
  }

  public static TagResourceResponse tagResource(
      ApiGatewayV2Client client, TagResourceRequest request) {
    return client.tagResource(request);
  }

  public static UntagResourceResponse untagResource(
      ApiGatewayV2Client client, UntagResourceRequest request) {
    return client.untagResource(request);
  }
}
