package tungnn.tutor.java.aws.service.ssm;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.*;

public final class SsmMaintenanceWindowUtil {

  private SsmMaintenanceWindowUtil() {}

  public static CancelMaintenanceWindowExecutionResponse cancelMaintenanceWindowExecution(
      SsmClient client, CancelMaintenanceWindowExecutionRequest request) {
    return client.cancelMaintenanceWindowExecution(request);
  }

  public static CreateMaintenanceWindowResponse createMaintenanceWindow(
      SsmClient client, CreateMaintenanceWindowRequest request) {
    return client.createMaintenanceWindow(request);
  }

  public static DeleteMaintenanceWindowResponse deleteMaintenanceWindow(
      SsmClient client, DeleteMaintenanceWindowRequest request) {
    return client.deleteMaintenanceWindow(request);
  }

  public static DeregisterTargetFromMaintenanceWindowResponse deregisterTargetFromMaintenanceWindow(
      SsmClient client, DeregisterTargetFromMaintenanceWindowRequest request) {
    return client.deregisterTargetFromMaintenanceWindow(request);
  }

  public static DeregisterTaskFromMaintenanceWindowResponse deregisterTaskFromMaintenanceWindow(
      SsmClient client, DeregisterTaskFromMaintenanceWindowRequest request) {
    return client.deregisterTaskFromMaintenanceWindow(request);
  }

  public static List<MaintenanceWindowExecution> describeMaintenanceWindowExecutions(
      SsmClient client, DescribeMaintenanceWindowExecutionsRequest request) {
    return client.describeMaintenanceWindowExecutionsPaginator(request).stream()
        .flatMap(r -> r.windowExecutions().stream())
        .collect(Collectors.toList());
  }

  public static List<MaintenanceWindowExecutionTaskInvocationIdentity>
      describeMaintenanceWindowExecutionTaskInvocations(
          SsmClient client, DescribeMaintenanceWindowExecutionTaskInvocationsRequest request) {
    return client.describeMaintenanceWindowExecutionTaskInvocationsPaginator(request).stream()
        .flatMap(r -> r.windowExecutionTaskInvocationIdentities().stream())
        .collect(Collectors.toList());
  }

  public static List<MaintenanceWindowExecutionTaskIdentity>
      describeMaintenanceWindowExecutionTasks(
          SsmClient client, DescribeMaintenanceWindowExecutionTasksRequest request) {
    return client.describeMaintenanceWindowExecutionTasksPaginator(request).stream()
        .flatMap(r -> r.windowExecutionTaskIdentities().stream())
        .collect(Collectors.toList());
  }

  public static List<MaintenanceWindowIdentity> describeMaintenanceWindows(
      SsmClient client, DescribeMaintenanceWindowsRequest request) {
    return client.describeMaintenanceWindowsPaginator(request).stream()
        .flatMap(r -> r.windowIdentities().stream())
        .collect(Collectors.toList());
  }

  public static List<ScheduledWindowExecution> describeMaintenanceWindowSchedule(
      SsmClient client, DescribeMaintenanceWindowScheduleRequest request) {
    return client.describeMaintenanceWindowSchedulePaginator(request).stream()
        .flatMap(r -> r.scheduledWindowExecutions().stream())
        .collect(Collectors.toList());
  }

  public static List<MaintenanceWindowIdentityForTarget> describeMaintenanceWindowsForTarget(
      SsmClient client, DescribeMaintenanceWindowsForTargetRequest request) {
    return client.describeMaintenanceWindowsForTargetPaginator(request).stream()
        .flatMap(r -> r.windowIdentities().stream())
        .collect(Collectors.toList());
  }

  public static List<MaintenanceWindowTarget> describeMaintenanceWindowTargets(
      SsmClient client, DescribeMaintenanceWindowTargetsRequest request) {
    return client.describeMaintenanceWindowTargetsPaginator(request).stream()
        .flatMap(r -> r.targets().stream())
        .collect(Collectors.toList());
  }

  public static List<MaintenanceWindowTask> describeMaintenanceWindowTasks(
      SsmClient client, DescribeMaintenanceWindowTasksRequest request) {
    return client.describeMaintenanceWindowTasksPaginator(request).stream()
        .flatMap(r -> r.tasks().stream())
        .collect(Collectors.toList());
  }

  public static GetMaintenanceWindowResponse getMaintenanceWindow(
      SsmClient client, GetMaintenanceWindowRequest request) {
    return client.getMaintenanceWindow(request);
  }

  public static GetMaintenanceWindowExecutionResponse getMaintenanceWindowExecution(
      SsmClient client, GetMaintenanceWindowExecutionRequest request) {
    return client.getMaintenanceWindowExecution(request);
  }

  public static GetMaintenanceWindowExecutionTaskResponse getMaintenanceWindowExecutionTask(
      SsmClient client, GetMaintenanceWindowExecutionTaskRequest request) {
    return client.getMaintenanceWindowExecutionTask(request);
  }

  public static GetMaintenanceWindowExecutionTaskInvocationResponse
      getMaintenanceWindowExecutionTaskInvocation(
          SsmClient client, GetMaintenanceWindowExecutionTaskInvocationRequest request) {
    return client.getMaintenanceWindowExecutionTaskInvocation(request);
  }

  public static GetMaintenanceWindowTaskResponse getMaintenanceWindowTask(
      SsmClient client, GetMaintenanceWindowTaskRequest request) {
    return client.getMaintenanceWindowTask(request);
  }

  public static RegisterTargetWithMaintenanceWindowResponse registerTargetWithMaintenanceWindow(
      SsmClient client, RegisterTargetWithMaintenanceWindowRequest request) {
    return client.registerTargetWithMaintenanceWindow(request);
  }

  public static RegisterTaskWithMaintenanceWindowResponse registerTaskWithMaintenanceWindow(
      SsmClient client, RegisterTaskWithMaintenanceWindowRequest request) {
    return client.registerTaskWithMaintenanceWindow(request);
  }

  public static UpdateMaintenanceWindowResponse updateMaintenanceWindow(
      SsmClient client, UpdateMaintenanceWindowRequest request) {
    return client.updateMaintenanceWindow(request);
  }

  public static UpdateMaintenanceWindowTargetResponse updateMaintenanceWindowTarget(
      SsmClient client, UpdateMaintenanceWindowTargetRequest request) {
    return client.updateMaintenanceWindowTarget(request);
  }

  public static UpdateMaintenanceWindowTaskResponse updateMaintenanceWindowTask(
      SsmClient client, UpdateMaintenanceWindowTaskRequest request) {
    return client.updateMaintenanceWindowTask(request);
  }
}
