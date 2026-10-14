package tungnn.tutor.java.aws.service.ssm;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.*;

public final class SsmAutomationUtil {

  private SsmAutomationUtil() {}

  public static List<AutomationExecutionMetadata> describeAutomationExecutions(
      SsmClient client, DescribeAutomationExecutionsRequest request) {
    return client.describeAutomationExecutionsPaginator(request).stream()
        .flatMap(r -> r.automationExecutionMetadataList().stream())
        .collect(Collectors.toList());
  }

  public static List<StepExecution> describeAutomationStepExecutions(
      SsmClient client, DescribeAutomationStepExecutionsRequest request) {
    return client.describeAutomationStepExecutionsPaginator(request).stream()
        .flatMap(r -> r.stepExecutions().stream())
        .collect(Collectors.toList());
  }

  public static GetAutomationExecutionResponse getAutomationExecution(
      SsmClient client, GetAutomationExecutionRequest request) {
    return client.getAutomationExecution(request);
  }

  public static SendAutomationSignalResponse sendAutomationSignal(
      SsmClient client, SendAutomationSignalRequest request) {
    return client.sendAutomationSignal(request);
  }

  public static StartAutomationExecutionResponse startAutomationExecution(
      SsmClient client, StartAutomationExecutionRequest request) {
    return client.startAutomationExecution(request);
  }

  public static StartChangeRequestExecutionResponse startChangeRequestExecution(
      SsmClient client, StartChangeRequestExecutionRequest request) {
    return client.startChangeRequestExecution(request);
  }

  public static StopAutomationExecutionResponse stopAutomationExecution(
      SsmClient client, StopAutomationExecutionRequest request) {
    return client.stopAutomationExecution(request);
  }
}
