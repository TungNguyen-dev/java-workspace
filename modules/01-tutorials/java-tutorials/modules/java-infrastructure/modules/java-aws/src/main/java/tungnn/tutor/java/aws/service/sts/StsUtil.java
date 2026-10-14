package tungnn.tutor.java.aws.service.sts;

import java.util.Objects;
import java.util.UUID;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;

public final class StsUtil {

  private StsUtil() {
    throw new UnsupportedOperationException("Utility class");
  }

  public static AwsSessionCredentials assumeRoleWithCredentials(
      StsClient stsClient, String roleArn, String mfaSerialNumber, String mfaTokenCode) {

    Objects.requireNonNull(stsClient);
    Objects.requireNonNull(roleArn);

    var roleSessionName = "session-" + UUID.randomUUID();
    var builder = AssumeRoleRequest.builder().roleArn(roleArn).roleSessionName(roleSessionName);

    if (mfaSerialNumber != null && mfaTokenCode != null) {
      builder.serialNumber(mfaSerialNumber).tokenCode(mfaTokenCode);
    }

    var response = stsClient.assumeRole(builder.build());

    var credentials = response.credentials();
    if (credentials == null) {
      throw new IllegalStateException("Missing credentials in STS response");
    }

    return AwsSessionCredentials.create(
        credentials.accessKeyId(), credentials.secretAccessKey(), credentials.sessionToken());
  }
}
