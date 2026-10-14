package tungnn.tutor.java.aws.demo;

import java.util.Scanner;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.sts.StsClient;
import tungnn.tutor.java.aws.AwsDefaultClientFactory;
import tungnn.tutor.java.aws.AwsProfileRegistry;
import tungnn.tutor.java.aws.service.iam.IamRoleUtil;
import tungnn.tutor.java.aws.usecase.AwsAssumeRoleWithMfaUseCase;
import tungnn.tutor.java.aws.util.AwsCredentialsProviderUtil;

public class DemoAssumeRole {

  static void main() {
    var profileRegistry = new AwsProfileRegistry();
    var clientsFactory = new AwsDefaultClientFactory();

    // Create stsClient with defaultProfile
    var defaultProfile = profileRegistry.defaultProfile();
    var stsClient = clientsFactory.getClient(StsClient.class, StsClient.builder(), defaultProfile);
    var assumeRoleWithMfaUseCase = new AwsAssumeRoleWithMfaUseCase(stsClient);

    // Assume role and store credentials
    var profileDev = profileRegistry.profile("dev");
    var token = "";
    try (var scanner = new Scanner(System.in)) {
      System.out.print("Enter mfa token: ");
      token = scanner.next();
      scanner.nextLine();
    }
    assumeRoleWithMfaUseCase.execute(profileDev, token);

    // Reload credentials
    AwsCredentialsProviderUtil.reloadCredentials(profileRegistry, clientsFactory);

    // Test resource in dev account
    var iamClient = clientsFactory.getClient(IamClient.class, IamClient.builder(), profileDev);
    var roles = IamRoleUtil.listAllRoles(iamClient);
    roles.forEach(System.out::println);
  }
}
