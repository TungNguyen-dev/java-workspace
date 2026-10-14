// SesV2ReputationEntityUtil.java
package tungnn.tutor.java.aws.service.ses.v2;

import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

public final class SesV2ReputationEntityUtil {

  private SesV2ReputationEntityUtil() {}

  public static GetReputationEntityResponse getReputationEntity(
      SesV2Client client, GetReputationEntityRequest request) {
    return client.getReputationEntity(request);
  }

  public static ListReputationEntitiesResponse listReputationEntities(
      SesV2Client client, ListReputationEntitiesRequest request) {
    return client.listReputationEntities(request);
  }

  public static UpdateReputationEntityCustomerManagedStatusResponse
      updateReputationEntityCustomerManagedStatus(
          SesV2Client client, UpdateReputationEntityCustomerManagedStatusRequest request) {
    return client.updateReputationEntityCustomerManagedStatus(request);
  }

  public static UpdateReputationEntityPolicyResponse updateReputationEntityPolicy(
      SesV2Client client, UpdateReputationEntityPolicyRequest request) {
    return client.updateReputationEntityPolicy(request);
  }
}
