// SesIdentityPolicyUtil.java
package tungnn.tutor.java.aws.service.ses.v1;

import java.util.List;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

public final class SesIdentityPolicyUtil {

  private SesIdentityPolicyUtil() {}

  public static DeleteIdentityPolicyResponse deleteIdentityPolicy(
      SesClient client, DeleteIdentityPolicyRequest request) {
    return client.deleteIdentityPolicy(request);
  }

  public static GetIdentityPoliciesResponse getIdentityPolicies(
      SesClient client, GetIdentityPoliciesRequest request) {
    return client.getIdentityPolicies(request);
  }

  public static List<String> listIdentityPolicies(
      SesClient client, ListIdentityPoliciesRequest request) {
    return List.copyOf(client.listIdentityPolicies(request).policyNames());
  }

  public static PutIdentityPolicyResponse putIdentityPolicy(
      SesClient client, PutIdentityPolicyRequest request) {
    return client.putIdentityPolicy(request);
  }
}
