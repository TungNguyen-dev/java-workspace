package tungnn.tutor.java.aws;

import software.amazon.awssdk.awscore.AwsClient;
import software.amazon.awssdk.awscore.client.builder.AwsClientBuilder;
import software.amazon.awssdk.profiles.Profile;

public interface AwsClientFactory {

  <C extends AwsClient> C getClient(
      Class<C> clientType, AwsClientBuilder<?, C> clientBuilder, Profile profile);

  void clearCache();
}
