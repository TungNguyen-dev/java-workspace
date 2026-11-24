package tungnn.tutor.java.aws.client.profile;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import software.amazon.awssdk.profiles.Profile;
import software.amazon.awssdk.profiles.ProfileFile;
import tungnn.tutor.java.aws.client.AwsClientFactory;
import tungnn.tutor.java.aws.client.support.AwsProfileUtil;

public final class AwsProfileRegistry {
    private final Supplier<ProfileFile> profileFileSupplier;
    private volatile Map<String, Profile> profiles;

    public AwsProfileRegistry() {
        this(ProfileFile::defaultProfileFile);
    }

    public AwsProfileRegistry(ProfileFile profileFile) {
        this(constant(profileFile));
    }

    public AwsProfileRegistry(Supplier<ProfileFile> profileFileSupplier) {
        this.profileFileSupplier = Objects.requireNonNull(profileFileSupplier, "profileFileSupplier must not be null");
        this.profiles = loadProfiles();
    }

    private static Supplier<ProfileFile> constant(ProfileFile profileFile) {
        Objects.requireNonNull(profileFile, "profileFile must not be null");
        return () -> profileFile;
    }

    public void reload() {
        profiles = loadProfiles();
    }

    public void reloadCredentials(AwsClientFactory clientFactory) {
        Objects.requireNonNull(clientFactory, "clientFactory must not be null");
        reload();
        clientFactory.clearCache();
    }

    public boolean exists(String profileName) {
        return profiles.containsKey(profileName);
    }

    public Profile profile(String profileName) {
        return AwsProfileUtil.findProfile(profiles, profileName);
    }

    public Profile defaultProfile() {
        return profile(AwsProfileUtil.defaultProfileName());
    }

    public Map<String, Profile> allProfiles() {
        return profiles;
    }

    private Map<String, Profile> loadProfiles() {
        return AwsProfileUtil.loadProfiles(profileFileSupplier.get());
    }
}
