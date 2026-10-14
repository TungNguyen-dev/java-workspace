package tungnn.tutor.java.aws.service.sfn;

import java.util.List;
import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.model.*;

public final class SfnStateMachineAliasUtil {

  private SfnStateMachineAliasUtil() {}

  public static CreateStateMachineAliasResponse createStateMachineAlias(
      SfnClient client, CreateStateMachineAliasRequest request) {
    return client.createStateMachineAlias(request);
  }

  public static DeleteStateMachineAliasResponse deleteStateMachineAlias(
      SfnClient client, DeleteStateMachineAliasRequest request) {
    return client.deleteStateMachineAlias(request);
  }

  public static DescribeStateMachineAliasResponse describeStateMachineAlias(
      SfnClient client, DescribeStateMachineAliasRequest request) {
    return client.describeStateMachineAlias(request);
  }

  public static UpdateStateMachineAliasResponse updateStateMachineAlias(
      SfnClient client, UpdateStateMachineAliasRequest request) {
    return client.updateStateMachineAlias(request);
  }

  public static List<StateMachineAliasListItem> listStateMachineAliases(
      SfnClient client, ListStateMachineAliasesRequest request) {
    return List.copyOf(client.listStateMachineAliases(request).stateMachineAliases());
  }
}
