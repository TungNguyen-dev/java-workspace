package tungnn.tutor.java.aws.service.cloudfront;

import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.model.*;

public final class CloudFrontInvalidationUtil {
  private CloudFrontInvalidationUtil() {}

  public static CreateInvalidationResponse createInvalidation(
      CloudFrontClient client, CreateInvalidationRequest request) {
    return client.createInvalidation(request);
  }

  public static CreateInvalidationForDistributionTenantResponse
      createInvalidationForDistributionTenant(
          CloudFrontClient client, CreateInvalidationForDistributionTenantRequest request) {
    return client.createInvalidationForDistributionTenant(request);
  }

  public static GetInvalidationResponse getInvalidation(
      CloudFrontClient client, GetInvalidationRequest request) {
    return client.getInvalidation(request);
  }

  public static GetInvalidationForDistributionTenantResponse getInvalidationForDistributionTenant(
      CloudFrontClient client, GetInvalidationForDistributionTenantRequest request) {
    return client.getInvalidationForDistributionTenant(request);
  }

  public static ListInvalidationsResponse listInvalidations(
      CloudFrontClient client, ListInvalidationsRequest request) {
    return client.listInvalidations(request);
  }

  public static ListInvalidationsForDistributionTenantResponse
      listInvalidationsForDistributionTenant(
          CloudFrontClient client, ListInvalidationsForDistributionTenantRequest request) {
    return client.listInvalidationsForDistributionTenant(request);
  }
}
