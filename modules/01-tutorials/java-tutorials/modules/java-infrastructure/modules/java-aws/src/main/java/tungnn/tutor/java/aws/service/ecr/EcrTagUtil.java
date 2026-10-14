package tungnn.tutor.java.aws.service.ecr;

import java.util.List;
import software.amazon.awssdk.services.ecr.EcrClient;
import software.amazon.awssdk.services.ecr.model.*;

public final class EcrTagUtil {

  private EcrTagUtil() {}

  public static List<Tag> listTagsForResource(
      EcrClient ecrClient, ListTagsForResourceRequest request) {
    return List.copyOf(ecrClient.listTagsForResource(request).tags());
  }

  public static TagResourceResponse tagResource(EcrClient ecrClient, TagResourceRequest request) {
    return ecrClient.tagResource(request);
  }

  public static UntagResourceResponse untagResource(
      EcrClient ecrClient, UntagResourceRequest request) {
    return ecrClient.untagResource(request);
  }
}
