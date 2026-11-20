package tungnn.tutor.java.aws.service.apigateway;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.*;

public final class ApiGatewayUsagePlanUtil {
  private ApiGatewayUsagePlanUtil() {}

  public static CreateUsagePlanResponse createUsagePlan(
      ApiGatewayClient client, CreateUsagePlanRequest request) {
    return client.createUsagePlan(request);
  }

  public static CreateUsagePlanKeyResponse createUsagePlanKey(
      ApiGatewayClient client, CreateUsagePlanKeyRequest request) {
    return client.createUsagePlanKey(request);
  }

  public static DeleteUsagePlanResponse deleteUsagePlan(
      ApiGatewayClient client, DeleteUsagePlanRequest request) {
    return client.deleteUsagePlan(request);
  }

  public static DeleteUsagePlanKeyResponse deleteUsagePlanKey(
      ApiGatewayClient client, DeleteUsagePlanKeyRequest request) {
    return client.deleteUsagePlanKey(request);
  }

  public static GetUsageResponse getUsage(ApiGatewayClient client, GetUsageRequest request) {
    return client.getUsage(request);
  }

  public static GetUsagePlanResponse getUsagePlan(
      ApiGatewayClient client, GetUsagePlanRequest request) {
    return client.getUsagePlan(request);
  }

  public static GetUsagePlanKeyResponse getUsagePlanKey(
      ApiGatewayClient client, GetUsagePlanKeyRequest request) {
    return client.getUsagePlanKey(request);
  }

  public static List<UsagePlanKey> getUsagePlanKeys(
      ApiGatewayClient client, GetUsagePlanKeysRequest request) {
    return client.getUsagePlanKeysPaginator(request).stream()
        .flatMap(r -> r.items().stream())
        .collect(Collectors.toList());
  }

  public static List<UsagePlan> getUsagePlans(
      ApiGatewayClient client, GetUsagePlansRequest request) {
    return client.getUsagePlansPaginator(request).stream()
        .flatMap(r -> r.items().stream())
        .collect(Collectors.toList());
  }

  public static UpdateUsageResponse updateUsage(
      ApiGatewayClient client, UpdateUsageRequest request) {
    return client.updateUsage(request);
  }

  public static UpdateUsagePlanResponse updateUsagePlan(
      ApiGatewayClient client, UpdateUsagePlanRequest request) {
    return client.updateUsagePlan(request);
  }
}
