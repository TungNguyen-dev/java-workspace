package tungnn.tutor.java.aws.usecase;

import java.nio.file.Path;
import java.util.Map;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.profiles.Profile;
import software.amazon.awssdk.services.sts.StsClient;
import tungnn.tutor.java.aws.service.sts.StsUtil;
import tungnn.tutor.java.ini.ini4j.IniUtil;

public class AwsAssumeRoleWithMfaUseCase {

  private final StsClient stsClient;

  public AwsAssumeRoleWithMfaUseCase(StsClient stsClient) {
    this.stsClient = stsClient;
  }

  public void execute(Profile profile, String mfaCode) {
    var roleArn =
        profile
            .property("role_arn")
            .orElseThrow(() -> new IllegalStateException("Missing role_arn"));

    var mfaSerial =
        profile
            .property("mfa_serial")
            .orElseThrow(() -> new IllegalStateException("Missing mfa_serial"));

    var credentials = StsUtil.assumeRoleWithCredentials(stsClient, roleArn, mfaSerial, mfaCode);

    saveCredentials(profile.name(), credentials);
  }

  /* ===================== INTERNAL ===================== */

  private void saveCredentials(String profile, AwsSessionCredentials credentials) {
    var path = resolveCredentialsPath();

    IniUtil.append(
        path,
        profile,
        Map.of(
            "aws_access_key_id", credentials.accessKeyId(),
            "aws_secret_access_key", credentials.secretAccessKey(),
            "aws_session_token", credentials.sessionToken()));
  }

  private Path resolveCredentialsPath() {
    var env = System.getenv("AWS_SHARED_CREDENTIALS_FILE");

    if (env != null && !env.isBlank()) {
      return Path.of(env);
    }

    return Path.of(System.getProperty("user.home"), ".aws", "credentials");
  }
}
