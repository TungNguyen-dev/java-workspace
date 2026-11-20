package tungnn.tutor.java.aws.service.elbv2;

import software.amazon.awssdk.services.elasticloadbalancingv2.ElasticLoadBalancingV2Client;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.*;

public final class ElbTagUtil {
  private ElbTagUtil() {}

  public static AddTagsResponse addTags(
      ElasticLoadBalancingV2Client client, AddTagsRequest request) {
    return client.addTags(request);
  }

  public static DescribeTagsResponse describeTags(
      ElasticLoadBalancingV2Client client, DescribeTagsRequest request) {
    return client.describeTags(request);
  }

  public static RemoveTagsResponse removeTags(
      ElasticLoadBalancingV2Client client, RemoveTagsRequest request) {
    return client.removeTags(request);
  }
}
