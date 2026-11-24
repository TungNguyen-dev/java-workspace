package tungnn.tutor.java.aws.client;

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
    public <C extends AwsClient> C getClient(Class<C> clientType, AwsClientBuilder<?, C> builder, Profile profile) {
        Objects.requireNonNull(clientType, "clientType must not be null");
        Objects.requireNonNull(builder, "builder must not be null");
        Objects.requireNonNull(profile, "profile must not be null");
        var key = new ClientCacheKey(clientType, profile.name());
        return (C) cache.computeIfAbsent(key, _ -> createClient(builder, profile));
    }

    @Override
    public void clearCache() {
        RuntimeException failure = null;
        for (var key : cache.keySet()) {
            var client = cache.remove(key);
            if (client == null) {
                continue;
            }
            try {
                client.close();
            } catch (RuntimeException e) {
                if (failure == null) {
                    failure = e;
                } else {
                    failure.addSuppressed(e);
                }
            }
        }
        if (failure != null) {
            throw failure;
        }
    }

    protected abstract <C extends AwsClient> C createClient(AwsClientBuilder<?, C> builder, Profile profile);

    protected abstract ClientOverrideConfiguration createOverrideConfiguration(Profile profile);

    private record ClientCacheKey(Class<?> clientType, String profileName) {}
}
