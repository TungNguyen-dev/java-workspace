package tungnn.tutor.java.aws;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.profiles.Profile;
import software.amazon.awssdk.profiles.ProfileFile;

public final class AwsProfileRegistry {

  private static final String ENV_AWS_PROFILE = "AWS_PROFILE";
  private static final String DEFAULT_PROFILE_NAME = "default";

  private volatile Map<String, Profile> profiles;

  private volatile ProfileFile profileFile;

  public AwsProfileRegistry() {
    this(ProfileFile.defaultProfileFile());
  }

  public AwsProfileRegistry(ProfileFile profileFile) {
    this.profileFile = Objects.requireNonNull(profileFile, "profileFile must not be null");
    this.profiles = Map.copyOf(profileFile.profiles());
  }

  public void reload() {
    profiles = Map.copyOf(profileFile.profiles());
  }

  public void reloadProfileFile() {
    profileFile = ProfileFile.defaultProfileFile();
    reload();
  }

  public boolean exists(String profileName) {
    return profiles.containsKey(profileName);
  }

  public Profile profile(String profileName) {
    return Optional.ofNullable(profiles.get(profileName))
        .orElseThrow(() -> new IllegalArgumentException("AWS profile not found: " + profileName));
  }

  public Profile defaultProfile() {
    return profile(resolveDefaultProfileName());
  }

  public Map<String, Profile> allProfiles() {
    return profiles;
  }

  private String resolveDefaultProfileName() {
    return Optional.ofNullable(System.getenv(ENV_AWS_PROFILE))
        .filter(name -> !name.isBlank())
        .orElse(DEFAULT_PROFILE_NAME);
  }
}
