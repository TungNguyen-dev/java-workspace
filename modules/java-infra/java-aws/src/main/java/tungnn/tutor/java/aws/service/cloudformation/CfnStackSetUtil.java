package tungnn.tutor.java.aws.service.cloudformation;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.cloudformation.CloudFormationClient;
import software.amazon.awssdk.services.cloudformation.model.*;

public final class CfnStackSetUtil {
  private CfnStackSetUtil() {}

  public static CreateStackInstancesResponse createStackInstances(
      CloudFormationClient client, CreateStackInstancesRequest request) {
    return client.createStackInstances(request);
  }

  public static CreateStackSetResponse createStackSet(
      CloudFormationClient client, CreateStackSetRequest request) {
    return client.createStackSet(request);
  }

  public static DeleteStackInstancesResponse deleteStackInstances(
      CloudFormationClient client, DeleteStackInstancesRequest request) {
    return client.deleteStackInstances(request);
  }

  public static DeleteStackSetResponse deleteStackSet(
      CloudFormationClient client, DeleteStackSetRequest request) {
    return client.deleteStackSet(request);
  }

  public static DescribeStackInstanceResponse describeStackInstance(
      CloudFormationClient client, DescribeStackInstanceRequest request) {
    return client.describeStackInstance(request);
  }

  public static DescribeStackSetResponse describeStackSet(
      CloudFormationClient client, DescribeStackSetRequest request) {
    return client.describeStackSet(request);
  }

  public static DescribeStackSetOperationResponse describeStackSetOperation(
      CloudFormationClient client, DescribeStackSetOperationRequest request) {
    return client.describeStackSetOperation(request);
  }

  public static DetectStackSetDriftResponse detectStackSetDrift(
      CloudFormationClient client, DetectStackSetDriftRequest request) {
    return client.detectStackSetDrift(request);
  }

  public static ImportStacksToStackSetResponse importStacksToStackSet(
      CloudFormationClient client, ImportStacksToStackSetRequest request) {
    return client.importStacksToStackSet(request);
  }

  public static ListStackInstanceResourceDriftsResponse listStackInstanceResourceDrifts(
      CloudFormationClient client, ListStackInstanceResourceDriftsRequest request) {
    return client.listStackInstanceResourceDrifts(request);
  }

  public static List<StackInstanceSummary> listStackInstances(
      CloudFormationClient client, ListStackInstancesRequest request) {
    return client.listStackInstancesPaginator(request).stream()
        .flatMap(r -> r.summaries().stream())
        .collect(Collectors.toList());
  }

  public static ListStackSetAutoDeploymentTargetsResponse listStackSetAutoDeploymentTargets(
      CloudFormationClient client, ListStackSetAutoDeploymentTargetsRequest request) {
    return client.listStackSetAutoDeploymentTargets(request);
  }

  public static List<StackSetOperationResultSummary> listStackSetOperationResults(
      CloudFormationClient client, ListStackSetOperationResultsRequest request) {
    return client.listStackSetOperationResultsPaginator(request).stream()
        .flatMap(r -> r.summaries().stream())
        .collect(Collectors.toList());
  }

  public static List<StackSetOperationSummary> listStackSetOperations(
      CloudFormationClient client, ListStackSetOperationsRequest request) {
    return client.listStackSetOperationsPaginator(request).stream()
        .flatMap(r -> r.summaries().stream())
        .collect(Collectors.toList());
  }

  public static List<StackSetSummary> listStackSets(
      CloudFormationClient client, ListStackSetsRequest request) {
    return client.listStackSetsPaginator(request).stream()
        .flatMap(r -> r.summaries().stream())
        .collect(Collectors.toList());
  }

  public static StopStackSetOperationResponse stopStackSetOperation(
      CloudFormationClient client, StopStackSetOperationRequest request) {
    return client.stopStackSetOperation(request);
  }

  public static UpdateStackInstancesResponse updateStackInstances(
      CloudFormationClient client, UpdateStackInstancesRequest request) {
    return client.updateStackInstances(request);
  }

  public static UpdateStackSetResponse updateStackSet(
      CloudFormationClient client, UpdateStackSetRequest request) {
    return client.updateStackSet(request);
  }
}
