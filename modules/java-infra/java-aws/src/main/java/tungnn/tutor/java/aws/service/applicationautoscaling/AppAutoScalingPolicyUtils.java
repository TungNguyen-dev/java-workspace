package tungnn.tutor.java.aws.service.applicationautoscaling;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.applicationautoscaling.ApplicationAutoScalingClient;
import software.amazon.awssdk.services.applicationautoscaling.model.*;

public final class AppAutoScalingPolicyUtils {
    private AppAutoScalingPolicyUtils() {}

    public static DeleteScalingPolicyResponse deleteScalingPolicy(
            ApplicationAutoScalingClient client, DeleteScalingPolicyRequest request) {
        return client.deleteScalingPolicy(request);
    }

    public static List<ScalingPolicy> describeScalingPolicies(
            ApplicationAutoScalingClient client, DescribeScalingPoliciesRequest request) {
        return client.describeScalingPoliciesPaginator(request).stream()
                .flatMap(r -> r.scalingPolicies().stream())
                .collect(Collectors.toList());
    }

    public static GetPredictiveScalingForecastResponse getPredictiveScalingForecast(
            ApplicationAutoScalingClient client, GetPredictiveScalingForecastRequest request) {
        return client.getPredictiveScalingForecast(request);
    }

    public static PutScalingPolicyResponse putScalingPolicy(
            ApplicationAutoScalingClient client, PutScalingPolicyRequest request) {
        return client.putScalingPolicy(request);
    }
}
