package tungnn.tutor.java.aws.service.autoscaling;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.autoscaling.AutoScalingClient;
import software.amazon.awssdk.services.autoscaling.model.*;

public final class AutoScalingInstanceUtils {
    private AutoScalingInstanceUtils() {}

    public static AttachInstancesResponse attachInstances(AutoScalingClient client, AttachInstancesRequest request) {
        return client.attachInstances(request);
    }

    public static CancelInstanceRefreshResponse cancelInstanceRefresh(
            AutoScalingClient client, CancelInstanceRefreshRequest request) {
        return client.cancelInstanceRefresh(request);
    }

    public static List<AutoScalingInstanceDetails> describeAutoScalingInstances(
            AutoScalingClient client, DescribeAutoScalingInstancesRequest request) {
        return client.describeAutoScalingInstancesPaginator(request).stream()
                .flatMap(r -> r.autoScalingInstances().stream())
                .collect(Collectors.toList());
    }

    public static List<InstanceRefresh> describeInstanceRefreshes(
            AutoScalingClient client, DescribeInstanceRefreshesRequest request) {
        return client.describeInstanceRefreshesPaginator(request).stream()
                .flatMap(r -> r.instanceRefreshes().stream())
                .collect(Collectors.toList());
    }

    public static DetachInstancesResponse detachInstances(AutoScalingClient client, DetachInstancesRequest request) {
        return client.detachInstances(request);
    }

    public static EnterStandbyResponse enterStandby(AutoScalingClient client, EnterStandbyRequest request) {
        return client.enterStandby(request);
    }

    public static ExitStandbyResponse exitStandby(AutoScalingClient client, ExitStandbyRequest request) {
        return client.exitStandby(request);
    }

    public static LaunchInstancesResponse launchInstances(AutoScalingClient client, LaunchInstancesRequest request) {
        return client.launchInstances(request);
    }

    public static RollbackInstanceRefreshResponse rollbackInstanceRefresh(
            AutoScalingClient client, RollbackInstanceRefreshRequest request) {
        return client.rollbackInstanceRefresh(request);
    }

    public static SetInstanceHealthResponse setInstanceHealth(
            AutoScalingClient client, SetInstanceHealthRequest request) {
        return client.setInstanceHealth(request);
    }

    public static SetInstanceProtectionResponse setInstanceProtection(
            AutoScalingClient client, SetInstanceProtectionRequest request) {
        return client.setInstanceProtection(request);
    }

    public static StartInstanceRefreshResponse startInstanceRefresh(
            AutoScalingClient client, StartInstanceRefreshRequest request) {
        return client.startInstanceRefresh(request);
    }

    public static TerminateInstanceInAutoScalingGroupResponse terminateInstanceInAutoScalingGroup(
            AutoScalingClient client, TerminateInstanceInAutoScalingGroupRequest request) {
        return client.terminateInstanceInAutoScalingGroup(request);
    }
}
