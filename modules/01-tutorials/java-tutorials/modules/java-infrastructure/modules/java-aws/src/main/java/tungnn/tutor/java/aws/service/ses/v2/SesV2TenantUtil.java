// SesV2TenantUtil.java
package tungnn.tutor.java.aws.service.ses.v2;

import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

public final class SesV2TenantUtil {

  private SesV2TenantUtil() {}

  public static CreateTenantResponse createTenant(SesV2Client client, CreateTenantRequest request) {
    return client.createTenant(request);
  }

  public static CreateTenantResourceAssociationResponse createTenantResourceAssociation(
      SesV2Client client, CreateTenantResourceAssociationRequest request) {
    return client.createTenantResourceAssociation(request);
  }

  public static DeleteTenantResponse deleteTenant(SesV2Client client, DeleteTenantRequest request) {
    return client.deleteTenant(request);
  }

  public static DeleteTenantResourceAssociationResponse deleteTenantResourceAssociation(
      SesV2Client client, DeleteTenantResourceAssociationRequest request) {
    return client.deleteTenantResourceAssociation(request);
  }

  public static GetTenantResponse getTenant(SesV2Client client, GetTenantRequest request) {
    return client.getTenant(request);
  }

  public static ListResourceTenantsResponse listResourceTenants(
      SesV2Client client, ListResourceTenantsRequest request) {
    return client.listResourceTenants(request);
  }

  public static ListTenantResourcesResponse listTenantResources(
      SesV2Client client, ListTenantResourcesRequest request) {
    return client.listTenantResources(request);
  }

  public static ListTenantsResponse listTenants(SesV2Client client, ListTenantsRequest request) {
    return client.listTenants(request);
  }

  public static PutTenantSuppressionAttributesResponse putTenantSuppressionAttributes(
      SesV2Client client, PutTenantSuppressionAttributesRequest request) {
    return client.putTenantSuppressionAttributes(request);
  }
}
