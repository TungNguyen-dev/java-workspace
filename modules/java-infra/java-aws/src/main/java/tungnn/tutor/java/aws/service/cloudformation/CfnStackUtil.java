package tungnn.tutor.java.aws.service.cloudformation;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.cloudformation.CloudFormationClient;
import software.amazon.awssdk.services.cloudformation.model.*;

public final class CfnStackUtil {
  private CfnStackUtil() {}

  public static CancelUpdateStackResponse cancelUpdateStack(
      CloudFormationClient client, CancelUpdateStackRequest request) {
    return client.cancelUpdateStack(request);
  }

  public static ContinueUpdateRollbackResponse continueUpdateRollback(
      CloudFormationClient client, ContinueUpdateRollbackRequest request) {
    return client.continueUpdateRollback(request);
  }

  public static CreateStackResponse createStack(
      CloudFormationClient client, CreateStackRequest request) {
    return client.createStack(request);
  }

  public static CreateStackRefactorResponse createStackRefactor(
      CloudFormationClient client, CreateStackRefactorRequest request) {
    return client.createStackRefactor(request);
  }

  public static DeleteStackResponse deleteStack(
      CloudFormationClient client, DeleteStackRequest request) {
    return client.deleteStack(request);
  }

  public static List<AccountLimit> describeAccountLimits(
      CloudFormationClient client, DescribeAccountLimitsRequest request) {
    return client.describeAccountLimitsPaginator(request).stream()
        .flatMap(r -> r.accountLimits().stream())
        .collect(Collectors.toList());
  }

  public static List<OperationEvent> describeEvents(
      CloudFormationClient client, DescribeEventsRequest request) {
    return client.describeEventsPaginator(request).stream()
        .flatMap(r -> r.operationEvents().stream())
        .collect(Collectors.toList());
  }

  public static List<StackEvent> describeStackEvents(
      CloudFormationClient client, DescribeStackEventsRequest request) {
    return client.describeStackEventsPaginator(request).stream()
        .flatMap(r -> r.stackEvents().stream())
        .collect(Collectors.toList());
  }

  public static DescribeStackRefactorResponse describeStackRefactor(
      CloudFormationClient client, DescribeStackRefactorRequest request) {
    return client.describeStackRefactor(request);
  }

  public static List<Stack> describeStacks(
      CloudFormationClient client, DescribeStacksRequest request) {
    return client.describeStacksPaginator(request).stream()
        .flatMap(r -> r.stacks().stream())
        .collect(Collectors.toList());
  }

  public static ExecuteStackRefactorResponse executeStackRefactor(
      CloudFormationClient client, ExecuteStackRefactorRequest request) {
    return client.executeStackRefactor(request);
  }

  public static GetHookResultResponse getHookResult(
      CloudFormationClient client, GetHookResultRequest request) {
    return client.getHookResult(request);
  }

  public static GetStackPolicyResponse getStackPolicy(
      CloudFormationClient client, GetStackPolicyRequest request) {
    return client.getStackPolicy(request);
  }

  public static ListHookResultsResponse listHookResults(
      CloudFormationClient client, ListHookResultsRequest request) {
    return client.listHookResults(request);
  }

  public static List<StackRefactorAction> listStackRefactorActions(
      CloudFormationClient client, ListStackRefactorActionsRequest request) {
    return client.listStackRefactorActionsPaginator(request).stream()
        .flatMap(r -> r.stackRefactorActions().stream())
        .collect(Collectors.toList());
  }

  public static List<StackRefactorSummary> listStackRefactors(
      CloudFormationClient client, ListStackRefactorsRequest request) {
    return client.listStackRefactorsPaginator(request).stream()
        .flatMap(r -> r.stackRefactorSummaries().stream())
        .collect(Collectors.toList());
  }

  public static List<StackSummary> listStacks(
      CloudFormationClient client, ListStacksRequest request) {
    return client.listStacksPaginator(request).stream()
        .flatMap(r -> r.stackSummaries().stream())
        .collect(Collectors.toList());
  }

  public static RollbackStackResponse rollbackStack(
      CloudFormationClient client, RollbackStackRequest request) {
    return client.rollbackStack(request);
  }

  public static SetStackPolicyResponse setStackPolicy(
      CloudFormationClient client, SetStackPolicyRequest request) {
    return client.setStackPolicy(request);
  }

  public static UpdateStackResponse updateStack(
      CloudFormationClient client, UpdateStackRequest request) {
    return client.updateStack(request);
  }

  public static UpdateTerminationProtectionResponse updateTerminationProtection(
      CloudFormationClient client, UpdateTerminationProtectionRequest request) {
    return client.updateTerminationProtection(request);
  }
}
