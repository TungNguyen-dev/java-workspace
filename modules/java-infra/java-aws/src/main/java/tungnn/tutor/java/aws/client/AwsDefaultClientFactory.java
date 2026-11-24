package tungnn.tutor.java.aws.client;

import software.amazon.awssdk.awscore.AwsClient;
import software.amazon.awssdk.awscore.client.builder.AwsClientBuilder;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.profiles.Profile;
import tungnn.tutor.java.aws.client.support.AwsClientUtil;

public class AwsDefaultClientFactory extends AwsAbstractClientFactory {
    private static final boolean METRICS_ENABLED = Boolean.parseBoolean(System.getenv("AWS_SDK_ENABLE_METRICS"));

    @Override
    protected <C extends AwsClient> C createClient(AwsClientBuilder<?, C> builder, Profile profile) {
        return AwsClientUtil.buildClient(builder, profile, createOverrideConfiguration(profile));
    }

    @Override
    protected ClientOverrideConfiguration createOverrideConfiguration(Profile profile) {
        return AwsClientUtil.defaultOverrideConfiguration(profile, METRICS_ENABLED);
    }
}
