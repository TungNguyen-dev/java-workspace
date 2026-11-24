package tungnn.tutor.java.aws.service.autoscaling;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.autoscaling.AutoScalingClient;
import software.amazon.awssdk.services.autoscaling.model.*;

public final class AutoScalingPolicyUtils {
    private AutoScalingPolicyUtils() {}

    public static DeletePolicyResponse deletePolicy(AutoScalingClient client, DeletePolicyRequest request) {
        return client.deletePolicy(request);
    }

    public static List<ScalingPolicy> describePolicies(AutoScalingClient client, DescribePoliciesRequest request) {
        return client.describePoliciesPaginator(request).stream()
                .flatMap(r -> r.scalingPolicies().stream())
                .collect(Collectors.toList());
    }

    public static DescribeTerminationPolicyTypesResponse describeTerminationPolicyTypes(
            AutoScalingClient client, DescribeTerminationPolicyTypesRequest request) {
        return client.describeTerminationPolicyTypes(request);
    }

    public static ExecutePolicyResponse executePolicy(AutoScalingClient client, ExecutePolicyRequest request) {
        return client.executePolicy(request);
    }

    public static GetPredictiveScalingForecastResponse getPredictiveScalingForecast(
            AutoScalingClient client, GetPredictiveScalingForecastRequest request) {
        return client.getPredictiveScalingForecast(request);
    }

    public static PutScalingPolicyResponse putScalingPolicy(AutoScalingClient client, PutScalingPolicyRequest request) {
        return client.putScalingPolicy(request);
    }
}
