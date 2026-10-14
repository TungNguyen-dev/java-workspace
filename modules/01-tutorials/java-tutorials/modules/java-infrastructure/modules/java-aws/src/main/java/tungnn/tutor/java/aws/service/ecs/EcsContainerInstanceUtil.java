package tungnn.tutor.java.aws.service.ecs;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.*;

public final class EcsContainerInstanceUtil {

  private EcsContainerInstanceUtil() {}

  public static RegisterContainerInstanceResponse registerContainerInstance(
      EcsClient client, RegisterContainerInstanceRequest request) {
    return client.registerContainerInstance(request);
  }

  public static DeregisterContainerInstanceResponse deregisterContainerInstance(
      EcsClient client, DeregisterContainerInstanceRequest request) {
    return client.deregisterContainerInstance(request);
  }

  public static List<ContainerInstance> describeContainerInstances(
      EcsClient client, DescribeContainerInstancesRequest request) {
    return List.copyOf(client.describeContainerInstances(request).containerInstances());
  }

  public static List<String> listContainerInstances(
      EcsClient client, ListContainerInstancesRequest request) {
    return client.listContainerInstancesPaginator(request).stream()
        .flatMap(r -> r.containerInstanceArns().stream())
        .collect(Collectors.toList());
  }

  public static List<ContainerInstance> updateContainerInstancesState(
      EcsClient client, UpdateContainerInstancesStateRequest request) {
    return List.copyOf(client.updateContainerInstancesState(request).containerInstances());
  }

  public static UpdateContainerAgentResponse updateContainerAgent(
      EcsClient client, UpdateContainerAgentRequest request) {
    return client.updateContainerAgent(request);
  }
}
