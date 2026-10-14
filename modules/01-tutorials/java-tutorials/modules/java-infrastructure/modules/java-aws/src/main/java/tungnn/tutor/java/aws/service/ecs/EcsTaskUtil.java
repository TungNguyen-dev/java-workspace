package tungnn.tutor.java.aws.service.ecs;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.*;

public final class EcsTaskUtil {

  private EcsTaskUtil() {}

  public static List<Task> runTask(EcsClient client, RunTaskRequest request) {
    return List.copyOf(client.runTask(request).tasks());
  }

  public static List<Task> startTask(EcsClient client, StartTaskRequest request) {
    return List.copyOf(client.startTask(request).tasks());
  }

  public static StopTaskResponse stopTask(EcsClient client, StopTaskRequest request) {
    return client.stopTask(request);
  }

  public static List<Task> describeTasks(EcsClient client, DescribeTasksRequest request) {
    return List.copyOf(client.describeTasks(request).tasks());
  }

  public static List<String> listTasks(EcsClient client, ListTasksRequest request) {
    return client.listTasksPaginator(request).stream()
        .flatMap(r -> r.taskArns().stream())
        .collect(Collectors.toList());
  }

  public static ExecuteCommandResponse executeCommand(
      EcsClient client, ExecuteCommandRequest request) {
    return client.executeCommand(request);
  }

  public static List<ProtectedTask> getTaskProtection(
      EcsClient client, GetTaskProtectionRequest request) {
    return List.copyOf(client.getTaskProtection(request).protectedTasks());
  }

  public static List<ProtectedTask> updateTaskProtection(
      EcsClient client, UpdateTaskProtectionRequest request) {
    return List.copyOf(client.updateTaskProtection(request).protectedTasks());
  }

  public static SubmitTaskStateChangeResponse submitTaskStateChange(
      EcsClient client, SubmitTaskStateChangeRequest request) {
    return client.submitTaskStateChange(request);
  }

  public static SubmitContainerStateChangeResponse submitContainerStateChange(
      EcsClient client, SubmitContainerStateChangeRequest request) {
    return client.submitContainerStateChange(request);
  }

  public static SubmitAttachmentStateChangesResponse submitAttachmentStateChanges(
      EcsClient client, SubmitAttachmentStateChangesRequest request) {
    return client.submitAttachmentStateChanges(request);
  }
}
