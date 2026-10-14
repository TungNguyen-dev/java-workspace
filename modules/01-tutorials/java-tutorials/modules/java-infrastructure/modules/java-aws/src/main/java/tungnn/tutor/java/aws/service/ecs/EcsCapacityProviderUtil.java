package tungnn.tutor.java.aws.service.ecs;

import java.util.List;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.*;

public final class EcsCapacityProviderUtil {

  private EcsCapacityProviderUtil() {}

  public static CreateCapacityProviderResponse createCapacityProvider(
      EcsClient client, CreateCapacityProviderRequest request) {
    return client.createCapacityProvider(request);
  }

  public static DeleteCapacityProviderResponse deleteCapacityProvider(
      EcsClient client, DeleteCapacityProviderRequest request) {
    return client.deleteCapacityProvider(request);
  }

  public static UpdateCapacityProviderResponse updateCapacityProvider(
      EcsClient client, UpdateCapacityProviderRequest request) {
    return client.updateCapacityProvider(request);
  }

  public static List<CapacityProvider> describeCapacityProviders(
      EcsClient client, DescribeCapacityProvidersRequest request) {
    return List.copyOf(client.describeCapacityProviders(request).capacityProviders());
  }
}
