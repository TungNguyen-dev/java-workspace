package tungnn.tutor.java.aws.service.ecs;

import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.*;

public final class EcsServiceDeploymentUtil {

  private EcsServiceDeploymentUtil() {}

  public static ContinueServiceDeploymentResponse continueServiceDeployment(
      EcsClient client, ContinueServiceDeploymentRequest request) {
    return client.continueServiceDeployment(request);
  }

  public static StopServiceDeploymentResponse stopServiceDeployment(
      EcsClient client, StopServiceDeploymentRequest request) {
    return client.stopServiceDeployment(request);
  }

  public static DescribeServiceDeploymentsResponse describeServiceDeployments(
      EcsClient client, DescribeServiceDeploymentsRequest request) {
    return client.describeServiceDeployments(request);
  }

  public static ListServiceDeploymentsResponse listServiceDeployments(
      EcsClient client, ListServiceDeploymentsRequest request) {
    return client.listServiceDeployments(request);
  }

  public static DescribeServiceRevisionsResponse describeServiceRevisions(
      EcsClient client, DescribeServiceRevisionsRequest request) {
    return client.describeServiceRevisions(request);
  }
}
