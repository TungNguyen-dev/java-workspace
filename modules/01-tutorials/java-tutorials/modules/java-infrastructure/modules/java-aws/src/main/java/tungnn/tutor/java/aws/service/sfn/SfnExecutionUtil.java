package tungnn.tutor.java.aws.service.sfn;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.model.*;

public final class SfnExecutionUtil {

  private SfnExecutionUtil() {}

  public static StartExecutionResponse startExecution(
      SfnClient client, StartExecutionRequest request) {
    return client.startExecution(request);
  }

  public static StartSyncExecutionResponse startSyncExecution(
      SfnClient client, StartSyncExecutionRequest request) {
    return client.startSyncExecution(request);
  }

  public static StopExecutionResponse stopExecution(
      SfnClient client, StopExecutionRequest request) {
    return client.stopExecution(request);
  }

  public static RedriveExecutionResponse redriveExecution(
      SfnClient client, RedriveExecutionRequest request) {
    return client.redriveExecution(request);
  }

  public static DescribeExecutionResponse describeExecution(
      SfnClient client, DescribeExecutionRequest request) {
    return client.describeExecution(request);
  }

  public static DescribeStateMachineForExecutionResponse describeStateMachineForExecution(
      SfnClient client, DescribeStateMachineForExecutionRequest request) {
    return client.describeStateMachineForExecution(request);
  }

  public static List<ExecutionListItem> listExecutions(
      SfnClient client, ListExecutionsRequest request) {
    return client.listExecutionsPaginator(request).stream()
        .flatMap(page -> page.executions().stream())
        .collect(Collectors.toList());
  }

  public static List<HistoryEvent> getExecutionHistory(
      SfnClient client, GetExecutionHistoryRequest request) {
    return client.getExecutionHistoryPaginator(request).stream()
        .flatMap(page -> page.events().stream())
        .collect(Collectors.toList());
  }
}
