package tungnn.tutor.java.aws.service.ecs;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.*;

public final class EcsClusterUtil {

  private EcsClusterUtil() {}

  public static CreateClusterResponse createCluster(
      EcsClient client, CreateClusterRequest request) {
    return client.createCluster(request);
  }

  public static DeleteClusterResponse deleteCluster(
      EcsClient client, DeleteClusterRequest request) {
    return client.deleteCluster(request);
  }

  public static UpdateClusterResponse updateCluster(
      EcsClient client, UpdateClusterRequest request) {
    return client.updateCluster(request);
  }

  public static UpdateClusterSettingsResponse updateClusterSettings(
      EcsClient client, UpdateClusterSettingsRequest request) {
    return client.updateClusterSettings(request);
  }

  public static PutClusterCapacityProvidersResponse putClusterCapacityProviders(
      EcsClient client, PutClusterCapacityProvidersRequest request) {
    return client.putClusterCapacityProviders(request);
  }

  public static List<Cluster> describeClusters(EcsClient client, DescribeClustersRequest request) {
    return List.copyOf(client.describeClusters(request).clusters());
  }

  public static List<String> listClusters(EcsClient client, ListClustersRequest request) {
    return client.listClustersPaginator(request).stream()
        .flatMap(r -> r.clusterArns().stream())
        .collect(Collectors.toList());
  }
}
