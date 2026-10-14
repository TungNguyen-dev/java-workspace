package tungnn.tutor.java.aws.util;

import java.util.Objects;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.profiles.Profile;
import tungnn.tutor.java.aws.AwsClientFactory;
import tungnn.tutor.java.aws.AwsProfileRegistry;

public final class AwsCredentialsProviderUtil {

  private static final String DEFAULT_PROFILE_NAME = "default";

  private static final String AWS_ACCESS_KEY_ID = "aws_access_key_id";
  private static final String AWS_SECRET_ACCESS_KEY = "aws_secret_access_key";
  private static final String AWS_SESSION_TOKEN = "aws_session_token";

  private static final AwsCredentialsProvider DEFAULT_PROVIDER =
      DefaultCredentialsProvider.builder().build();

  private AwsCredentialsProviderUtil() {}

  public static AwsCredentialsProvider credentialsProvider(Profile profile) {
    Objects.requireNonNull(profile, "profile must not be null");

    if (isDefaultProfile(profile)) {
      return DEFAULT_PROVIDER;
    }

    return staticCredentialsProvider(profile);
  }

  public static void reloadCredentials(
      AwsProfileRegistry profileRegistry, AwsClientFactory clientsFactory) {
    profileRegistry.reload();
    clientsFactory.clearCache();
  }

  private static AwsCredentialsProvider staticCredentialsProvider(Profile profile) {
    var accessKey = requiredProperty(profile, AWS_ACCESS_KEY_ID);
    var secretKey = requiredProperty(profile, AWS_SECRET_ACCESS_KEY);
    var sessionToken = profile.property(AWS_SESSION_TOKEN).orElse(null);

    AwsCredentials credentials =
        sessionToken == null
            ? AwsBasicCredentials.create(accessKey, secretKey)
            : AwsSessionCredentials.create(accessKey, secretKey, sessionToken);

    return StaticCredentialsProvider.create(credentials);
  }

  private static String requiredProperty(Profile profile, String propertyName) {
    return profile
        .property(propertyName)
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Missing required property '%s' in profile '%s'"
                        .formatted(propertyName, profile.name())));
  }

  private static boolean isDefaultProfile(Profile profile) {
    return DEFAULT_PROFILE_NAME.equalsIgnoreCase(profile.name());
  }
}
