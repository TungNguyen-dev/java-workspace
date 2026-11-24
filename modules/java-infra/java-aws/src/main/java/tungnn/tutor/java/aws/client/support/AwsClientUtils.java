package tungnn.tutor.java.aws.client.support;

import java.net.URI;
import java.time.Duration;
import java.util.Objects;
import software.amazon.awssdk.awscore.AwsClient;
import software.amazon.awssdk.awscore.client.builder.AwsClientBuilder;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.client.config.SdkAdvancedClientOption;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.metrics.LoggingMetricPublisher;
import software.amazon.awssdk.profiles.Profile;

public final class AwsClientUtils {
    private static final Duration API_CALL_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration API_CALL_ATTEMPT_TIMEOUT = Duration.ofSeconds(10);

    private AwsClientUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static ClientOverrideConfiguration defaultOverrideConfiguration(Profile profile, boolean metricsEnabled) {
        Objects.requireNonNull(profile, "profile must not be null");
        var builder = ClientOverrideConfiguration.builder()
                .apiCallTimeout(API_CALL_TIMEOUT)
                .apiCallAttemptTimeout(API_CALL_ATTEMPT_TIMEOUT)
                .retryStrategy(RetryMode.ADAPTIVE_V2)
                .putAdvancedOption(SdkAdvancedClientOption.USER_AGENT_PREFIX, "env-%s".formatted(profile.name()));
        if (metricsEnabled) {
            builder.addMetricPublisher(LoggingMetricPublisher.create());
        }
        return builder.build();
    }

    public static <C extends AwsClient> C buildClient(
            AwsClientBuilder<?, C> builder, Profile profile, ClientOverrideConfiguration overrideConfiguration) {
        return configure(builder, profile, overrideConfiguration).build();
    }

    public static <C extends AwsClient> C buildClient(
            AwsClientBuilder<?, C> builder,
            Profile profile,
            ClientOverrideConfiguration overrideConfiguration,
            URI endpoint) {
        Objects.requireNonNull(endpoint, "endpoint must not be null");
        return configure(builder, profile, overrideConfiguration)
                .endpointOverride(endpoint)
                .build();
    }

    private static <C extends AwsClient> AwsClientBuilder<?, C> configure(
            AwsClientBuilder<?, C> builder, Profile profile, ClientOverrideConfiguration overrideConfiguration) {
        Objects.requireNonNull(builder, "builder must not be null");
        Objects.requireNonNull(profile, "profile must not be null");
        builder.region(AwsProfileUtils.resolveRegion(profile))
                .credentialsProvider(AwsCredentialsUtils.credentialsProvider(profile))
                .overrideConfiguration(overrideConfiguration);
        return builder;
    }
}
