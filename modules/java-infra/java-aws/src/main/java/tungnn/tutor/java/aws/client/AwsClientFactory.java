package tungnn.tutor.java.aws.client;

import software.amazon.awssdk.awscore.AwsClient;
import software.amazon.awssdk.awscore.client.builder.AwsClientBuilder;
import software.amazon.awssdk.profiles.Profile;

public interface AwsClientFactory extends AutoCloseable {
    <C extends AwsClient> C getClient(Class<C> clientType, AwsClientBuilder<?, C> clientBuilder, Profile profile);

    void clearCache();

    @Override
    default void close() {
        clearCache();
    }
}
