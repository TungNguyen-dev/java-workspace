package tungnn.tutor.java.aws.client.support;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.profiles.Profile;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.regions.Region;

public final class AwsProfileUtil {
    public static final String DEFAULT_PROFILE_NAME = "default";
    public static final Region DEFAULT_REGION = Region.AP_SOUTHEAST_1;
    private static final String ENV_AWS_PROFILE = "AWS_PROFILE";
    private static final String PROPERTY_REGION = "region";

    private AwsProfileUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Map<String, Profile> loadProfiles(ProfileFile profileFile) {
        Objects.requireNonNull(profileFile, "profileFile must not be null");
        return Map.copyOf(profileFile.profiles());
    }

    public static String defaultProfileName() {
        return Optional.ofNullable(System.getenv(ENV_AWS_PROFILE))
                .filter(name -> !name.isBlank())
                .orElse(DEFAULT_PROFILE_NAME);
    }

    public static boolean isDefaultProfile(Profile profile) {
        Objects.requireNonNull(profile, "profile must not be null");
        return DEFAULT_PROFILE_NAME.equalsIgnoreCase(profile.name());
    }

    public static Profile findProfile(Map<String, Profile> profiles, String profileName) {
        Objects.requireNonNull(profiles, "profiles must not be null");
        return Optional.ofNullable(profiles.get(profileName))
                .orElseThrow(() -> new IllegalArgumentException("AWS profile not found: " + profileName));
    }

    public static String requiredProperty(Profile profile, String propertyName) {
        Objects.requireNonNull(profile, "profile must not be null");
        return profile.property(propertyName)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Missing required property '%s' in profile '%s'".formatted(propertyName, profile.name())));
    }

    public static Region resolveRegion(Profile profile) {
        Objects.requireNonNull(profile, "profile must not be null");
        return profile.property(PROPERTY_REGION).map(Region::of).orElse(DEFAULT_REGION);
    }
}
