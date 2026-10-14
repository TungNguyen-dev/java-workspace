package tungnn.tutor.java.aws.service.iam;

import java.util.List;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.*;

public final class IamPolicyUtil {

  private IamPolicyUtil() {}

  public static List<Policy> listAllPolicies(IamClient client) {
    return client
        .listPoliciesPaginator(ListPoliciesRequest.builder().scope(PolicyScopeType.ALL).build())
        .stream()
        .flatMap(r -> r.policies().stream())
        .toList();
  }

  public static Policy createPolicy(
      IamClient client, String policyName, String policyDocumentJson) {

    return client
        .createPolicy(
            CreatePolicyRequest.builder()
                .policyName(policyName)
                .policyDocument(policyDocumentJson)
                .build())
        .policy();
  }

  public static void deletePolicy(IamClient client, String policyArn) {
    client.deletePolicy(DeletePolicyRequest.builder().policyArn(policyArn).build());
  }
}
