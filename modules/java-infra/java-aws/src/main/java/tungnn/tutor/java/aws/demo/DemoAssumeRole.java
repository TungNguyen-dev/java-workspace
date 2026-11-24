package tungnn.tutor.java.aws.demo;

import java.util.Scanner;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.sts.StsClient;
import tungnn.tutor.java.aws.client.AwsDefaultClientFactory;
import tungnn.tutor.java.aws.client.profile.AwsProfileRegistry;
import tungnn.tutor.java.aws.client.support.AwsCredentialsUtil;
import tungnn.tutor.java.aws.service.iam.IamRoleUtil;

public class DemoAssumeRole {
    static void main() {
        var profileRegistry = new AwsProfileRegistry();
        try (var clientFactory = new AwsDefaultClientFactory()) {
            var defaultProfile = profileRegistry.defaultProfile();
            var stsClient = clientFactory.getClient(StsClient.class, StsClient.builder(), defaultProfile);
            var token = "";
            try (var scanner = new Scanner(System.in)) {
                System.out.print("Enter mfa token: ");
                token = scanner.next();
                scanner.nextLine();
            }
            AwsCredentialsUtil.assumeRoleWithMfa(stsClient, profileRegistry.profile("dev"), token);
            profileRegistry.reloadCredentials(clientFactory);
            var profileDev = profileRegistry.profile("dev");
            var iamClient = clientFactory.getClient(IamClient.class, IamClient.builder(), profileDev);
            var roles = IamRoleUtil.listAllRoles(iamClient);
            roles.forEach(System.out::println);
        }
    }
}
