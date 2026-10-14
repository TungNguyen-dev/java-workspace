package tungnn.tutor.java.aws;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import software.amazon.awssdk.awscore.AwsClient;
import software.amazon.awssdk.awscore.client.builder.AwsClientBuilder;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.profiles.Profile;

public abstract class AwsAbstractClientFactory implements AwsClientFactory {

  private final ConcurrentMap<ClientCacheKey, AwsClient> cache = new ConcurrentHashMap<>();

  @Override
  @SuppressWarnings("unchecked")
  public <C extends AwsClient> C getClient(
      Class<C> clientType, AwsClientBuilder<?, C> builder, Profile profile) {

    Objects.requireNonNull(clientType, "clientType must not be null");
    Objects.requireNonNull(builder, "builder must not be null");
    Objects.requireNonNull(profile, "profile must not be null");

    var key = new ClientCacheKey(clientType, profile.name());

    return (C) cache.computeIfAbsent(key, ignored -> createClient(builder, profile));
  }

  @Override
  public void clearCache() {
    cache.clear();
  }

  protected abstract <C extends AwsClient> C createClient(
      AwsClientBuilder<?, C> builder, Profile profile);

  protected abstract ClientOverrideConfiguration createOverrideConfiguration(Profile profile);

  private record ClientCacheKey(Class<?> clientType, String profileName) {}
}
