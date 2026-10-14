package tungnn.tutor.java.aws.service.eventbridge;

import java.util.List;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.*;

public final class TagUtil {

  private TagUtil() {}

  public static TagResourceResponse tagResource(
      EventBridgeClient client, TagResourceRequest request) {
    return client.tagResource(request);
  }

  public static UntagResourceResponse untagResource(
      EventBridgeClient client, UntagResourceRequest request) {
    return client.untagResource(request);
  }

  public static List<Tag> listTagsForResource(
      EventBridgeClient client, ListTagsForResourceRequest request) {
    return List.copyOf(client.listTagsForResource(request).tags());
  }
}
