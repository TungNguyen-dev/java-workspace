package tungnn.tutor.java.aws.service.iam;

import java.util.List;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.*;

public final class IamUserUtil {

  private IamUserUtil() {}

  public static List<User> listAllUsers(IamClient client) {
    return client.listUsersPaginator().stream().flatMap(r -> r.users().stream()).toList();
  }

  public static User getUser(IamClient client, String userName) {
    return client.getUser(GetUserRequest.builder().userName(userName).build()).user();
  }

  public static User createUser(IamClient client, String userName) {
    return client.createUser(CreateUserRequest.builder().userName(userName).build()).user();
  }

  public static void updateUser(IamClient client, String oldUserName, String newUserName) {
    client.updateUser(
        UpdateUserRequest.builder().userName(oldUserName).newUserName(newUserName).build());
  }

  public static void deleteUser(IamClient client, String userName) {
    client.deleteUser(DeleteUserRequest.builder().userName(userName).build());
  }
}
