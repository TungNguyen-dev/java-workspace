package tungnn.tutor.java.aws.service.elbv2;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.elasticloadbalancingv2.ElasticLoadBalancingV2Client;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.*;

public final class ElbTargetGroupUtil {
    private ElbTargetGroupUtil() {}

    public static CreateTargetGroupResponse createTargetGroup(
            ElasticLoadBalancingV2Client client, CreateTargetGroupRequest request) {
        return client.createTargetGroup(request);
    }

    public static DeleteTargetGroupResponse deleteTargetGroup(
            ElasticLoadBalancingV2Client client, DeleteTargetGroupRequest request) {
        return client.deleteTargetGroup(request);
    }

    public static DeregisterTargetsResponse deregisterTargets(
            ElasticLoadBalancingV2Client client, DeregisterTargetsRequest request) {
        return client.deregisterTargets(request);
    }

    public static DescribeTargetGroupAttributesResponse describeTargetGroupAttributes(
            ElasticLoadBalancingV2Client client, DescribeTargetGroupAttributesRequest request) {
        return client.describeTargetGroupAttributes(request);
    }

    public static List<TargetGroup> describeTargetGroups(
            ElasticLoadBalancingV2Client client, DescribeTargetGroupsRequest request) {
        return client.describeTargetGroupsPaginator(request).stream()
                .flatMap(r -> r.targetGroups().stream())
                .collect(Collectors.toList());
    }

    public static DescribeTargetHealthResponse describeTargetHealth(
            ElasticLoadBalancingV2Client client, DescribeTargetHealthRequest request) {
        return client.describeTargetHealth(request);
    }

    public static ModifyTargetGroupResponse modifyTargetGroup(
            ElasticLoadBalancingV2Client client, ModifyTargetGroupRequest request) {
        return client.modifyTargetGroup(request);
    }

    public static ModifyTargetGroupAttributesResponse modifyTargetGroupAttributes(
            ElasticLoadBalancingV2Client client, ModifyTargetGroupAttributesRequest request) {
        return client.modifyTargetGroupAttributes(request);
    }

    public static RegisterTargetsResponse registerTargets(
            ElasticLoadBalancingV2Client client, RegisterTargetsRequest request) {
        return client.registerTargets(request);
    }
}
