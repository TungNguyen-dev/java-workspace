package tungnn.tutor.java.aws;

import java.net.URI;
import software.amazon.awssdk.awscore.AwsClient;
import software.amazon.awssdk.awscore.client.builder.AwsClientBuilder;
import software.amazon.awssdk.profiles.Profile;
import software.amazon.awssdk.regions.Region;
import tungnn.tutor.java.aws.util.AwsCredentialsProviderUtil;

public class AwsLocalStackClientFactory extends AwsDefaultClientFactory {

  @Override
  protected <C extends AwsClient> C createClient(AwsClientBuilder<?, C> builder, Profile profile) {
    var region =
        profile.properties().containsKey("region")
            ? Region.of(profile.property("region").orElseThrow())
            : Region.AP_SOUTHEAST_1;

    return builder
        .endpointOverride(URI.create("http://localhost:4566"))
        .region(region)
        .credentialsProvider(AwsCredentialsProviderUtil.credentialsProvider(profile))
        .overrideConfiguration(createOverrideConfiguration(profile))
        .build();
  }
}
