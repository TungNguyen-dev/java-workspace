package tungnn.tutor.java.aws.service.ecs;

import java.util.List;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.*;

public final class EcsTaskSetUtil {

  private EcsTaskSetUtil() {}

  public static CreateTaskSetResponse createTaskSet(
      EcsClient client, CreateTaskSetRequest request) {
    return client.createTaskSet(request);
  }

  public static DeleteTaskSetResponse deleteTaskSet(
      EcsClient client, DeleteTaskSetRequest request) {
    return client.deleteTaskSet(request);
  }

  public static UpdateTaskSetResponse updateTaskSet(
      EcsClient client, UpdateTaskSetRequest request) {
    return client.updateTaskSet(request);
  }

  public static List<TaskSet> describeTaskSets(EcsClient client, DescribeTaskSetsRequest request) {
    return List.copyOf(client.describeTaskSets(request).taskSets());
  }
}
