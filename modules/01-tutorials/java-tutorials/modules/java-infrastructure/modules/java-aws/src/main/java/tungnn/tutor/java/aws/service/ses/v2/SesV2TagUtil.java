// SesV2TagUtil.java
package tungnn.tutor.java.aws.service.ses.v2;

import java.util.List;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

public final class SesV2TagUtil {

  private SesV2TagUtil() {}

  public static List<Tag> listTagsForResource(
      SesV2Client client, ListTagsForResourceRequest request) {
    return List.copyOf(client.listTagsForResource(request).tags());
  }

  public static TagResourceResponse tagResource(SesV2Client client, TagResourceRequest request) {
    return client.tagResource(request);
  }

  public static UntagResourceResponse untagResource(
      SesV2Client client, UntagResourceRequest request) {
    return client.untagResource(request);
  }
}
