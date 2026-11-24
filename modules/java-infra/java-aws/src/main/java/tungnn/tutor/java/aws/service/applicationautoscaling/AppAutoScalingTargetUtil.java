package tungnn.tutor.java.aws.service.applicationautoscaling;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.applicationautoscaling.ApplicationAutoScalingClient;
import software.amazon.awssdk.services.applicationautoscaling.model.*;

public final class AppAutoScalingTargetUtil {
    private AppAutoScalingTargetUtil() {}

    public static DeregisterScalableTargetResponse deregisterScalableTarget(
            ApplicationAutoScalingClient client, DeregisterScalableTargetRequest request) {
        return client.deregisterScalableTarget(request);
    }

    public static List<ScalableTarget> describeScalableTargets(
            ApplicationAutoScalingClient client, DescribeScalableTargetsRequest request) {
        return client.describeScalableTargetsPaginator(request).stream()
                .flatMap(r -> r.scalableTargets().stream())
                .collect(Collectors.toList());
    }

    public static List<ScalingActivity> describeScalingActivities(
            ApplicationAutoScalingClient client, DescribeScalingActivitiesRequest request) {
        return client.describeScalingActivitiesPaginator(request).stream()
                .flatMap(r -> r.scalingActivities().stream())
                .collect(Collectors.toList());
    }

    public static ListTagsForResourceResponse listTagsForResource(
            ApplicationAutoScalingClient client, ListTagsForResourceRequest request) {
        return client.listTagsForResource(request);
    }

    public static RegisterScalableTargetResponse registerScalableTarget(
            ApplicationAutoScalingClient client, RegisterScalableTargetRequest request) {
        return client.registerScalableTarget(request);
    }

    public static TagResourceResponse tagResource(ApplicationAutoScalingClient client, TagResourceRequest request) {
        return client.tagResource(request);
    }

    public static UntagResourceResponse untagResource(
            ApplicationAutoScalingClient client, UntagResourceRequest request) {
        return client.untagResource(request);
    }
}
