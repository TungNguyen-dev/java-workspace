package tungnn.tutor.java.aws.service.eventbridge.scheduler;

import java.util.List;
import software.amazon.awssdk.services.scheduler.SchedulerClient;
import software.amazon.awssdk.services.scheduler.model.*;

public final class TagUtil {

  private TagUtil() {}

  public static List<Tag> listTagsForResource(
      SchedulerClient client, ListTagsForResourceRequest request) {
    return List.copyOf(client.listTagsForResource(request).tags());
  }

  public static TagResourceResponse tagResource(
      SchedulerClient client, TagResourceRequest request) {
    return client.tagResource(request);
  }

  public static UntagResourceResponse untagResource(
      SchedulerClient client, UntagResourceRequest request) {
    return client.untagResource(request);
  }
}
