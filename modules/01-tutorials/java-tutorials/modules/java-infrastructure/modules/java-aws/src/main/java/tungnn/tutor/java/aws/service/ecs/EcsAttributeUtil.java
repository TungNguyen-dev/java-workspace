package tungnn.tutor.java.aws.service.ecs;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.*;

public final class EcsAttributeUtil {

  private EcsAttributeUtil() {}

  public static List<Attribute> putAttributes(EcsClient client, PutAttributesRequest request) {
    return List.copyOf(client.putAttributes(request).attributes());
  }

  public static DeleteAttributesResponse deleteAttributes(
      EcsClient client, DeleteAttributesRequest request) {
    return client.deleteAttributes(request);
  }

  public static List<Attribute> listAttributes(EcsClient client, ListAttributesRequest request) {
    return client.listAttributesPaginator(request).stream()
        .flatMap(r -> r.attributes().stream())
        .collect(Collectors.toList());
  }
}
