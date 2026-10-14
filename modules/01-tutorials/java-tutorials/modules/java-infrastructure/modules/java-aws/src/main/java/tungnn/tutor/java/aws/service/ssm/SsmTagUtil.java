package tungnn.tutor.java.aws.service.ssm;

import java.util.List;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.*;

public final class SsmTagUtil {

  private SsmTagUtil() {}

  public static AddTagsToResourceResponse addTagsToResource(
      SsmClient client, AddTagsToResourceRequest request) {
    return client.addTagsToResource(request);
  }

  public static List<Tag> listTagsForResource(
      SsmClient client, ListTagsForResourceRequest request) {
    return List.copyOf(client.listTagsForResource(request).tagList());
  }

  public static RemoveTagsFromResourceResponse removeTagsFromResource(
      SsmClient client, RemoveTagsFromResourceRequest request) {
    return client.removeTagsFromResource(request);
  }
}
