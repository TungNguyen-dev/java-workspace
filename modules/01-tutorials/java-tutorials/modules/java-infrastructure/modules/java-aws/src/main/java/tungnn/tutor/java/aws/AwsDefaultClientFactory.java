package tungnn.tutor.java.aws;

import java.time.Duration;
import software.amazon.awssdk.awscore.AwsClient;
import software.amazon.awssdk.awscore.client.builder.AwsClientBuilder;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.client.config.SdkAdvancedClientOption;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.metrics.LoggingMetricPublisher;
import software.amazon.awssdk.profiles.Profile;
import software.amazon.awssdk.regions.Region;
import tungnn.tutor.java.aws.util.AwsCredentialsProviderUtil;

public class AwsDefaultClientFactory extends AwsAbstractClientFactory {

  private static final Duration API_CALL_TIMEOUT = Duration.ofSeconds(30);
  private static final Duration API_CALL_ATTEMPT_TIMEOUT = Duration.ofSeconds(10);
  private static final boolean METRICS_ENABLED =
      Boolean.parseBoolean(System.getenv("AWS_SDK_ENABLE_METRICS"));

  @Override
  protected <C extends AwsClient> C createClient(AwsClientBuilder<?, C> builder, Profile profile) {
    var region =
        profile.properties().containsKey("region")
            ? Region.of(profile.property("region").orElseThrow())
            : Region.AP_SOUTHEAST_1;

    return builder
        .region(region)
        .credentialsProvider(AwsCredentialsProviderUtil.credentialsProvider(profile))
        .overrideConfiguration(createOverrideConfiguration(profile))
        .build();
  }

  @Override
  protected ClientOverrideConfiguration createOverrideConfiguration(Profile profile) {
    var builder =
        ClientOverrideConfiguration.builder()
            .apiCallTimeout(API_CALL_TIMEOUT)
            .apiCallAttemptTimeout(API_CALL_ATTEMPT_TIMEOUT)
            .retryStrategy(RetryMode.ADAPTIVE_V2)
            .putAdvancedOption(
                SdkAdvancedClientOption.USER_AGENT_PREFIX, "env-%s".formatted(profile.name()));

    if (METRICS_ENABLED) {
      builder.addMetricPublisher(LoggingMetricPublisher.create());
    }

    return builder.build();
  }
}
