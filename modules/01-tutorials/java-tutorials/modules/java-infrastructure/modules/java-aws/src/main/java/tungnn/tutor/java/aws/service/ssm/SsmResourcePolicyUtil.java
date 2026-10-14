package tungnn.tutor.java.aws.service.ssm;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.*;

public final class SsmResourcePolicyUtil {

  private SsmResourcePolicyUtil() {}

  public static DeleteResourcePolicyResponse deleteResourcePolicy(
      SsmClient client, DeleteResourcePolicyRequest request) {
    return client.deleteResourcePolicy(request);
  }

  public static List<GetResourcePoliciesResponseEntry> getResourcePolicies(
      SsmClient client, GetResourcePoliciesRequest request) {
    return client.getResourcePoliciesPaginator(request).stream()
        .flatMap(r -> r.policies().stream())
        .collect(Collectors.toList());
  }

  public static PutResourcePolicyResponse putResourcePolicy(
      SsmClient client, PutResourcePolicyRequest request) {
    return client.putResourcePolicy(request);
  }
}
