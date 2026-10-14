package tungnn.tutor.java.aws.demo;

import software.amazon.awssdk.services.iam.IamClient;
import tungnn.tutor.java.aws.AwsDefaultClientFactory;
import tungnn.tutor.java.aws.AwsProfileRegistry;
import tungnn.tutor.java.aws.service.iam.IamRoleUtil;

public class DemoDefaultProfile {

  static void main() {
    var profileRegistry = new AwsProfileRegistry();
    var clientsFactory = new AwsDefaultClientFactory();

    var defaultProfile = profileRegistry.defaultProfile();
    var iamClient = clientsFactory.getClient(IamClient.class, IamClient.builder(), defaultProfile);
    var roles = IamRoleUtil.listAllRoles(iamClient);
    roles.forEach(System.out::println);
  }
}
