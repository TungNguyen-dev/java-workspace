package tungnn.tutor.java.aws.service.sfn;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.model.*;

public final class SfnStateMachineUtil {

  private SfnStateMachineUtil() {}

  public static CreateStateMachineResponse createStateMachine(
      SfnClient client, CreateStateMachineRequest request) {
    return client.createStateMachine(request);
  }

  public static DeleteStateMachineResponse deleteStateMachine(
      SfnClient client, DeleteStateMachineRequest request) {
    return client.deleteStateMachine(request);
  }

  public static DescribeStateMachineResponse describeStateMachine(
      SfnClient client, DescribeStateMachineRequest request) {
    return client.describeStateMachine(request);
  }

  public static UpdateStateMachineResponse updateStateMachine(
      SfnClient client, UpdateStateMachineRequest request) {
    return client.updateStateMachine(request);
  }

  public static ValidateStateMachineDefinitionResponse validateStateMachineDefinition(
      SfnClient client, ValidateStateMachineDefinitionRequest request) {
    return client.validateStateMachineDefinition(request);
  }

  public static TestStateResponse testState(SfnClient client, TestStateRequest request) {
    return client.testState(request);
  }

  public static List<StateMachineListItem> listStateMachines(
      SfnClient client, ListStateMachinesRequest request) {
    return client.listStateMachinesPaginator(request).stream()
        .flatMap(page -> page.stateMachines().stream())
        .collect(Collectors.toList());
  }
}
