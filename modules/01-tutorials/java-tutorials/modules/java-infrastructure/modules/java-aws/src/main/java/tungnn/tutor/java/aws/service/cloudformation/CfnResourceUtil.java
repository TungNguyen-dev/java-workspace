package tungnn.tutor.java.aws.service.cloudformation;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.cloudformation.CloudFormationClient;
import software.amazon.awssdk.services.cloudformation.model.*;

public final class CfnResourceUtil {
  private CfnResourceUtil() {}

  public static DescribeStackDriftDetectionStatusResponse describeStackDriftDetectionStatus(
      CloudFormationClient client, DescribeStackDriftDetectionStatusRequest request) {
    return client.describeStackDriftDetectionStatus(request);
  }

  public static DescribeStackResourceResponse describeStackResource(
      CloudFormationClient client, DescribeStackResourceRequest request) {
    return client.describeStackResource(request);
  }

  public static List<StackResourceDrift> describeStackResourceDrifts(
      CloudFormationClient client, DescribeStackResourceDriftsRequest request) {
    return client.describeStackResourceDriftsPaginator(request).stream()
        .flatMap(r -> r.stackResourceDrifts().stream())
        .collect(Collectors.toList());
  }

  public static DescribeStackResourcesResponse describeStackResources(
      CloudFormationClient client, DescribeStackResourcesRequest request) {
    return client.describeStackResources(request);
  }

  public static DetectStackDriftResponse detectStackDrift(
      CloudFormationClient client, DetectStackDriftRequest request) {
    return client.detectStackDrift(request);
  }

  public static DetectStackResourceDriftResponse detectStackResourceDrift(
      CloudFormationClient client, DetectStackResourceDriftRequest request) {
    return client.detectStackResourceDrift(request);
  }

  public static List<StackResourceSummary> listStackResources(
      CloudFormationClient client, ListStackResourcesRequest request) {
    return client.listStackResourcesPaginator(request).stream()
        .flatMap(r -> r.stackResourceSummaries().stream())
        .collect(Collectors.toList());
  }

  public static SignalResourceResponse signalResource(
      CloudFormationClient client, SignalResourceRequest request) {
    return client.signalResource(request);
  }
}
