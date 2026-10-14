package tungnn.tutor.java.aws.service.ecs;

import java.util.List;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.*;

public final class EcsTagUtil {

  private EcsTagUtil() {}

  public static TagResourceResponse tagResource(EcsClient client, TagResourceRequest request) {
    return client.tagResource(request);
  }

  public static UntagResourceResponse untagResource(
      EcsClient client, UntagResourceRequest request) {
    return client.untagResource(request);
  }

  public static List<Tag> listTagsForResource(
      EcsClient client, ListTagsForResourceRequest request) {
    return List.copyOf(client.listTagsForResource(request).tags());
  }
}
