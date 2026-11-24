package tungnn.tutor.java.aws.service.autoscaling;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.autoscaling.AutoScalingClient;
import software.amazon.awssdk.services.autoscaling.model.*;

public final class AutoScalingGroupUtils {
    private AutoScalingGroupUtils() {}

    public static AttachLoadBalancerTargetGroupsResponse attachLoadBalancerTargetGroups(
            AutoScalingClient client, AttachLoadBalancerTargetGroupsRequest request) {
        return client.attachLoadBalancerTargetGroups(request);
    }

    public static AttachLoadBalancersResponse attachLoadBalancers(
            AutoScalingClient client, AttachLoadBalancersRequest request) {
        return client.attachLoadBalancers(request);
    }

    public static AttachTrafficSourcesResponse attachTrafficSources(
            AutoScalingClient client, AttachTrafficSourcesRequest request) {
        return client.attachTrafficSources(request);
    }

    public static CreateAutoScalingGroupResponse createAutoScalingGroup(
            AutoScalingClient client, CreateAutoScalingGroupRequest request) {
        return client.createAutoScalingGroup(request);
    }

    public static DeleteAutoScalingGroupResponse deleteAutoScalingGroup(
            AutoScalingClient client, DeleteAutoScalingGroupRequest request) {
        return client.deleteAutoScalingGroup(request);
    }

    public static DeleteNotificationConfigurationResponse deleteNotificationConfiguration(
            AutoScalingClient client, DeleteNotificationConfigurationRequest request) {
        return client.deleteNotificationConfiguration(request);
    }

    public static DeleteWarmPoolResponse deleteWarmPool(AutoScalingClient client, DeleteWarmPoolRequest request) {
        return client.deleteWarmPool(request);
    }

    public static DescribeAccountLimitsResponse describeAccountLimits(
            AutoScalingClient client, DescribeAccountLimitsRequest request) {
        return client.describeAccountLimits(request);
    }

    public static DescribeAdjustmentTypesResponse describeAdjustmentTypes(
            AutoScalingClient client, DescribeAdjustmentTypesRequest request) {
        return client.describeAdjustmentTypes(request);
    }

    public static List<AutoScalingGroup> describeAutoScalingGroups(
            AutoScalingClient client, DescribeAutoScalingGroupsRequest request) {
        return client.describeAutoScalingGroupsPaginator(request).stream()
                .flatMap(r -> r.autoScalingGroups().stream())
                .collect(Collectors.toList());
    }

    public static DescribeAutoScalingNotificationTypesResponse describeAutoScalingNotificationTypes(
            AutoScalingClient client, DescribeAutoScalingNotificationTypesRequest request) {
        return client.describeAutoScalingNotificationTypes(request);
    }

    public static List<LoadBalancerTargetGroupState> describeLoadBalancerTargetGroups(
            AutoScalingClient client, DescribeLoadBalancerTargetGroupsRequest request) {
        return client.describeLoadBalancerTargetGroupsPaginator(request).stream()
                .flatMap(r -> r.loadBalancerTargetGroups().stream())
                .collect(Collectors.toList());
    }

    public static List<LoadBalancerState> describeLoadBalancers(
            AutoScalingClient client, DescribeLoadBalancersRequest request) {
        return client.describeLoadBalancersPaginator(request).stream()
                .flatMap(r -> r.loadBalancers().stream())
                .collect(Collectors.toList());
    }

    public static DescribeMetricCollectionTypesResponse describeMetricCollectionTypes(
            AutoScalingClient client, DescribeMetricCollectionTypesRequest request) {
        return client.describeMetricCollectionTypes(request);
    }

    public static List<NotificationConfiguration> describeNotificationConfigurations(
            AutoScalingClient client, DescribeNotificationConfigurationsRequest request) {
        return client.describeNotificationConfigurationsPaginator(request).stream()
                .flatMap(r -> r.notificationConfigurations().stream())
                .collect(Collectors.toList());
    }

    public static List<Activity> describeScalingActivities(
            AutoScalingClient client, DescribeScalingActivitiesRequest request) {
        return client.describeScalingActivitiesPaginator(request).stream()
                .flatMap(r -> r.activities().stream())
                .collect(Collectors.toList());
    }

    public static DescribeScalingProcessTypesResponse describeScalingProcessTypes(
            AutoScalingClient client, DescribeScalingProcessTypesRequest request) {
        return client.describeScalingProcessTypes(request);
    }

    public static List<TrafficSourceState> describeTrafficSources(
            AutoScalingClient client, DescribeTrafficSourcesRequest request) {
        return client.describeTrafficSourcesPaginator(request).stream()
                .flatMap(r -> r.trafficSources().stream())
                .collect(Collectors.toList());
    }

    public static List<Instance> describeWarmPool(AutoScalingClient client, DescribeWarmPoolRequest request) {
        return client.describeWarmPoolPaginator(request).stream()
                .flatMap(r -> r.instances().stream())
                .collect(Collectors.toList());
    }

    public static DetachLoadBalancerTargetGroupsResponse detachLoadBalancerTargetGroups(
            AutoScalingClient client, DetachLoadBalancerTargetGroupsRequest request) {
        return client.detachLoadBalancerTargetGroups(request);
    }

    public static DetachLoadBalancersResponse detachLoadBalancers(
            AutoScalingClient client, DetachLoadBalancersRequest request) {
        return client.detachLoadBalancers(request);
    }

    public static DetachTrafficSourcesResponse detachTrafficSources(
            AutoScalingClient client, DetachTrafficSourcesRequest request) {
        return client.detachTrafficSources(request);
    }

    public static DisableMetricsCollectionResponse disableMetricsCollection(
            AutoScalingClient client, DisableMetricsCollectionRequest request) {
        return client.disableMetricsCollection(request);
    }

    public static EnableMetricsCollectionResponse enableMetricsCollection(
            AutoScalingClient client, EnableMetricsCollectionRequest request) {
        return client.enableMetricsCollection(request);
    }

    public static PutNotificationConfigurationResponse putNotificationConfiguration(
            AutoScalingClient client, PutNotificationConfigurationRequest request) {
        return client.putNotificationConfiguration(request);
    }

    public static PutWarmPoolResponse putWarmPool(AutoScalingClient client, PutWarmPoolRequest request) {
        return client.putWarmPool(request);
    }

    public static ResumeProcessesResponse resumeProcesses(AutoScalingClient client, ResumeProcessesRequest request) {
        return client.resumeProcesses(request);
    }

    public static SetDesiredCapacityResponse setDesiredCapacity(
            AutoScalingClient client, SetDesiredCapacityRequest request) {
        return client.setDesiredCapacity(request);
    }

    public static SuspendProcessesResponse suspendProcesses(AutoScalingClient client, SuspendProcessesRequest request) {
        return client.suspendProcesses(request);
    }

    public static UpdateAutoScalingGroupResponse updateAutoScalingGroup(
            AutoScalingClient client, UpdateAutoScalingGroupRequest request) {
        return client.updateAutoScalingGroup(request);
    }
}
