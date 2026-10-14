package tungnn.tutor.java.aws.service.iam;

import java.util.List;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.CreateRoleRequest;
import software.amazon.awssdk.services.iam.model.DeleteRoleRequest;
import software.amazon.awssdk.services.iam.model.Role;

public final class IamRoleUtil {

  private IamRoleUtil() {}

  public static List<Role> listAllRoles(IamClient client) {
    return client.listRolesPaginator().stream().flatMap(r -> r.roles().stream()).toList();
  }

  public static Role createRole(IamClient client, String roleName, String assumeRolePolicyJson) {
    return client
        .createRole(
            CreateRoleRequest.builder()
                .roleName(roleName)
                .assumeRolePolicyDocument(assumeRolePolicyJson)
                .build())
        .role();
  }

  public static void deleteRole(IamClient client, String roleName) {
    client.deleteRole(DeleteRoleRequest.builder().roleName(roleName).build());
  }
}
