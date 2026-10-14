package tungnn.tutor.java.aws.service.ecs;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.*;

public final class EcsTaskDefinitionUtil {

  private EcsTaskDefinitionUtil() {}

  public static RegisterTaskDefinitionResponse registerTaskDefinition(
      EcsClient client, RegisterTaskDefinitionRequest request) {
    return client.registerTaskDefinition(request);
  }

  public static DeregisterTaskDefinitionResponse deregisterTaskDefinition(
      EcsClient client, DeregisterTaskDefinitionRequest request) {
    return client.deregisterTaskDefinition(request);
  }

  public static DescribeTaskDefinitionResponse describeTaskDefinition(
      EcsClient client, DescribeTaskDefinitionRequest request) {
    return client.describeTaskDefinition(request);
  }

  public static List<TaskDefinition> deleteTaskDefinitions(
      EcsClient client, DeleteTaskDefinitionsRequest request) {
    return List.copyOf(client.deleteTaskDefinitions(request).taskDefinitions());
  }

  public static List<String> listTaskDefinitions(
      EcsClient client, ListTaskDefinitionsRequest request) {
    return client.listTaskDefinitionsPaginator(request).stream()
        .flatMap(r -> r.taskDefinitionArns().stream())
        .collect(Collectors.toList());
  }

  public static List<String> listTaskDefinitionFamilies(
      EcsClient client, ListTaskDefinitionFamiliesRequest request) {
    return client.listTaskDefinitionFamiliesPaginator(request).stream()
        .flatMap(r -> r.families().stream())
        .collect(Collectors.toList());
  }
}
