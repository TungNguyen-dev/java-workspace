package tungnn.tutor.java.aws.service.sfn;

import java.util.List;
import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.model.*;

public final class SfnTagUtil {

  private SfnTagUtil() {}

  public static TagResourceResponse tagResource(SfnClient client, TagResourceRequest request) {
    return client.tagResource(request);
  }

  public static UntagResourceResponse untagResource(
      SfnClient client, UntagResourceRequest request) {
    return client.untagResource(request);
  }

  public static List<Tag> listTagsForResource(
      SfnClient client, ListTagsForResourceRequest request) {
    return List.copyOf(client.listTagsForResource(request).tags());
  }
}
