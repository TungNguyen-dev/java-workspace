package tungnn.tutor.java.aws.service.cloudfront;

import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.model.*;

public final class CloudFrontTagUtil {
  private CloudFrontTagUtil() {}

  public static ListTagsForResourceResponse listTagsForResource(
      CloudFrontClient client, ListTagsForResourceRequest request) {
    return client.listTagsForResource(request);
  }

  public static TagResourceResponse tagResource(
      CloudFrontClient client, TagResourceRequest request) {
    return client.tagResource(request);
  }

  public static UntagResourceResponse untagResource(
      CloudFrontClient client, UntagResourceRequest request) {
    return client.untagResource(request);
  }
}
