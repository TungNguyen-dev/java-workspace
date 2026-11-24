package tungnn.tutor.java.aws.service.autoscaling;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.autoscaling.AutoScalingClient;
import software.amazon.awssdk.services.autoscaling.model.*;

public final class AutoScalingLaunchConfigurationUtils {
    private AutoScalingLaunchConfigurationUtils() {}

    public static CreateLaunchConfigurationResponse createLaunchConfiguration(
            AutoScalingClient client, CreateLaunchConfigurationRequest request) {
        return client.createLaunchConfiguration(request);
    }

    public static DeleteLaunchConfigurationResponse deleteLaunchConfiguration(
            AutoScalingClient client, DeleteLaunchConfigurationRequest request) {
        return client.deleteLaunchConfiguration(request);
    }

    public static List<LaunchConfiguration> describeLaunchConfigurations(
            AutoScalingClient client, DescribeLaunchConfigurationsRequest request) {
        return client.describeLaunchConfigurationsPaginator(request).stream()
                .flatMap(r -> r.launchConfigurations().stream())
                .collect(Collectors.toList());
    }
}
