package tungnn.tutor.java.aws.demo;

import software.amazon.awssdk.services.iam.IamClient;
import tungnn.tutor.java.aws.client.AwsDefaultClientFactory;
import tungnn.tutor.java.aws.client.profile.AwsProfileRegistry;
import tungnn.tutor.java.aws.service.iam.IamRoleUtil;

public class DemoDefaultProfile {
    static void main() {
        var profileRegistry = new AwsProfileRegistry();
        try (var clientFactory = new AwsDefaultClientFactory()) {
            var defaultProfile = profileRegistry.defaultProfile();
            var iamClient = clientFactory.getClient(IamClient.class, IamClient.builder(), defaultProfile);
            var roles = IamRoleUtil.listAllRoles(iamClient);
            roles.forEach(System.out::println);
        }
    }
}
