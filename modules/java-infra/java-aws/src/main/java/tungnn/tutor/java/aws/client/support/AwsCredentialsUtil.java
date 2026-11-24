package tungnn.tutor.java.aws.client.support;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.profiles.Profile;
import software.amazon.awssdk.services.sts.StsClient;
import tungnn.tutor.java.aws.service.sts.StsUtil;
import tungnn.tutor.java.ini.ini4j.IniUtil;

public final class AwsCredentialsUtil {
    public static final String AWS_ACCESS_KEY_ID = "aws_access_key_id";
    public static final String AWS_SECRET_ACCESS_KEY = "aws_secret_access_key";
    public static final String AWS_SESSION_TOKEN = "aws_session_token";
    private static final String PROPERTY_ROLE_ARN = "role_arn";
    private static final String PROPERTY_MFA_SERIAL = "mfa_serial";
    private static final String ENV_SHARED_CREDENTIALS_FILE = "AWS_SHARED_CREDENTIALS_FILE";
    private static final AwsCredentialsProvider DEFAULT_PROVIDER =
            DefaultCredentialsProvider.builder().build();

    private AwsCredentialsUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static AwsCredentialsProvider credentialsProvider(Profile profile) {
        Objects.requireNonNull(profile, "profile must not be null");
        if (AwsProfileUtil.isDefaultProfile(profile)) {
            return DEFAULT_PROVIDER;
        }
        return staticCredentialsProvider(profile);
    }

    public static Path credentialsFilePath() {
        var env = System.getenv(ENV_SHARED_CREDENTIALS_FILE);
        if (env != null && !env.isBlank()) {
            return Path.of(env);
        }
        return Path.of(System.getProperty("user.home"), ".aws", "credentials");
    }

    public static void saveSessionCredentials(String profileName, AwsSessionCredentials credentials) {
        Objects.requireNonNull(profileName, "profileName must not be null");
        Objects.requireNonNull(credentials, "credentials must not be null");
        IniUtil.append(
                credentialsFilePath(),
                profileName,
                Map.of(
                        AWS_ACCESS_KEY_ID, credentials.accessKeyId(),
                        AWS_SECRET_ACCESS_KEY, credentials.secretAccessKey(),
                        AWS_SESSION_TOKEN, credentials.sessionToken()));
    }

    public static AwsSessionCredentials assumeRoleWithMfa(StsClient stsClient, Profile profile, String mfaCode) {
        Objects.requireNonNull(stsClient, "stsClient must not be null");
        Objects.requireNonNull(profile, "profile must not be null");
        var roleArn = AwsProfileUtil.requiredProperty(profile, PROPERTY_ROLE_ARN);
        var mfaSerial = AwsProfileUtil.requiredProperty(profile, PROPERTY_MFA_SERIAL);
        var credentials = StsUtil.assumeRoleWithCredentials(stsClient, roleArn, mfaSerial, mfaCode);
        saveSessionCredentials(profile.name(), credentials);
        return credentials;
    }

    private static AwsCredentialsProvider staticCredentialsProvider(Profile profile) {
        var accessKey = AwsProfileUtil.requiredProperty(profile, AWS_ACCESS_KEY_ID);
        var secretKey = AwsProfileUtil.requiredProperty(profile, AWS_SECRET_ACCESS_KEY);
        var sessionToken = profile.property(AWS_SESSION_TOKEN).orElse(null);
        AwsCredentials credentials = sessionToken == null
                ? AwsBasicCredentials.create(accessKey, secretKey)
                : AwsSessionCredentials.create(accessKey, secretKey, sessionToken);
        return StaticCredentialsProvider.create(credentials);
    }
}
