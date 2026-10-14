package tungnn.tutor.java.aws.service.ecs;

import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.*;

public final class EcsExpressGatewayServiceUtil {

  private EcsExpressGatewayServiceUtil() {}

  public static CreateExpressGatewayServiceResponse createExpressGatewayService(
      EcsClient client, CreateExpressGatewayServiceRequest request) {
    return client.createExpressGatewayService(request);
  }

  public static DeleteExpressGatewayServiceResponse deleteExpressGatewayService(
      EcsClient client, DeleteExpressGatewayServiceRequest request) {
    return client.deleteExpressGatewayService(request);
  }

  public static UpdateExpressGatewayServiceResponse updateExpressGatewayService(
      EcsClient client, UpdateExpressGatewayServiceRequest request) {
    return client.updateExpressGatewayService(request);
  }

  public static DescribeExpressGatewayServiceResponse describeExpressGatewayService(
      EcsClient client, DescribeExpressGatewayServiceRequest request) {
    return client.describeExpressGatewayService(request);
  }
}
