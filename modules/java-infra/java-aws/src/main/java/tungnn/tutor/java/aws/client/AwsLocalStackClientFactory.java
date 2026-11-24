package tungnn.tutor.java.aws.client;

import java.net.URI;
import software.amazon.awssdk.awscore.AwsClient;
import software.amazon.awssdk.awscore.client.builder.AwsClientBuilder;
import software.amazon.awssdk.profiles.Profile;
import tungnn.tutor.java.aws.client.support.AwsClientUtils;

public class AwsLocalStackClientFactory extends AwsDefaultClientFactory {
    private static final URI LOCALSTACK_ENDPOINT = URI.create("http://localhost:4566");

    @Override
    protected <C extends AwsClient> C createClient(AwsClientBuilder<?, C> builder, Profile profile) {
        return AwsClientUtils.buildClient(builder, profile, createOverrideConfiguration(profile), LOCALSTACK_ENDPOINT);
    }
}
