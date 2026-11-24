package tungnn.tutor.java.aws.service.elbv2;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.elasticloadbalancingv2.ElasticLoadBalancingV2Client;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.*;

public final class ElbLoadBalancerUtils {
    private ElbLoadBalancerUtils() {}

    public static CreateLoadBalancerResponse createLoadBalancer(
            ElasticLoadBalancingV2Client client, CreateLoadBalancerRequest request) {
        return client.createLoadBalancer(request);
    }

    public static DeleteLoadBalancerResponse deleteLoadBalancer(
            ElasticLoadBalancingV2Client client, DeleteLoadBalancerRequest request) {
        return client.deleteLoadBalancer(request);
    }

    public static List<Limit> describeAccountLimits(
            ElasticLoadBalancingV2Client client, DescribeAccountLimitsRequest request) {
        return client.describeAccountLimitsPaginator(request).stream()
                .flatMap(r -> r.limits().stream())
                .collect(Collectors.toList());
    }

    public static DescribeCapacityReservationResponse describeCapacityReservation(
            ElasticLoadBalancingV2Client client, DescribeCapacityReservationRequest request) {
        return client.describeCapacityReservation(request);
    }

    public static DescribeLoadBalancerAttributesResponse describeLoadBalancerAttributes(
            ElasticLoadBalancingV2Client client, DescribeLoadBalancerAttributesRequest request) {
        return client.describeLoadBalancerAttributes(request);
    }

    public static List<LoadBalancer> describeLoadBalancers(
            ElasticLoadBalancingV2Client client, DescribeLoadBalancersRequest request) {
        return client.describeLoadBalancersPaginator(request).stream()
                .flatMap(r -> r.loadBalancers().stream())
                .collect(Collectors.toList());
    }

    public static DescribeSslPoliciesResponse describeSSLPolicies(
            ElasticLoadBalancingV2Client client, DescribeSslPoliciesRequest request) {
        return client.describeSSLPolicies(request);
    }

    public static GetResourcePolicyResponse getResourcePolicy(
            ElasticLoadBalancingV2Client client, GetResourcePolicyRequest request) {
        return client.getResourcePolicy(request);
    }

    public static ModifyCapacityReservationResponse modifyCapacityReservation(
            ElasticLoadBalancingV2Client client, ModifyCapacityReservationRequest request) {
        return client.modifyCapacityReservation(request);
    }

    public static ModifyIpPoolsResponse modifyIpPools(
            ElasticLoadBalancingV2Client client, ModifyIpPoolsRequest request) {
        return client.modifyIpPools(request);
    }

    public static ModifyLoadBalancerAttributesResponse modifyLoadBalancerAttributes(
            ElasticLoadBalancingV2Client client, ModifyLoadBalancerAttributesRequest request) {
        return client.modifyLoadBalancerAttributes(request);
    }

    public static SetIpAddressTypeResponse setIpAddressType(
            ElasticLoadBalancingV2Client client, SetIpAddressTypeRequest request) {
        return client.setIpAddressType(request);
    }

    public static SetSecurityGroupsResponse setSecurityGroups(
            ElasticLoadBalancingV2Client client, SetSecurityGroupsRequest request) {
        return client.setSecurityGroups(request);
    }

    public static SetSubnetsResponse setSubnets(ElasticLoadBalancingV2Client client, SetSubnetsRequest request) {
        return client.setSubnets(request);
    }
}
