package tungnn.tutor.java.aws.service.ec2;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

public final class Ec2SecurityGroupUtils {
    private Ec2SecurityGroupUtils() {}

    public static AssociateSecurityGroupVpcResponse associateSecurityGroupVpc(
            Ec2Client client, AssociateSecurityGroupVpcRequest request) {
        return client.associateSecurityGroupVpc(request);
    }

    public static AuthorizeSecurityGroupEgressResponse authorizeSecurityGroupEgress(
            Ec2Client client, AuthorizeSecurityGroupEgressRequest request) {
        return client.authorizeSecurityGroupEgress(request);
    }

    public static AuthorizeSecurityGroupIngressResponse authorizeSecurityGroupIngress(
            Ec2Client client, AuthorizeSecurityGroupIngressRequest request) {
        return client.authorizeSecurityGroupIngress(request);
    }

    public static CreateSecurityGroupResponse createSecurityGroup(
            Ec2Client client, CreateSecurityGroupRequest request) {
        return client.createSecurityGroup(request);
    }

    public static DeleteSecurityGroupResponse deleteSecurityGroup(
            Ec2Client client, DeleteSecurityGroupRequest request) {
        return client.deleteSecurityGroup(request);
    }

    public static DescribeSecurityGroupReferencesResponse describeSecurityGroupReferences(
            Ec2Client client, DescribeSecurityGroupReferencesRequest request) {
        return client.describeSecurityGroupReferences(request);
    }

    public static List<SecurityGroupRule> describeSecurityGroupRules(
            Ec2Client client, DescribeSecurityGroupRulesRequest request) {
        return client.describeSecurityGroupRulesPaginator(request).stream()
                .flatMap(r -> r.securityGroupRules().stream())
                .collect(Collectors.toList());
    }

    public static List<SecurityGroupVpcAssociation> describeSecurityGroupVpcAssociations(
            Ec2Client client, DescribeSecurityGroupVpcAssociationsRequest request) {
        return client.describeSecurityGroupVpcAssociationsPaginator(request).stream()
                .flatMap(r -> r.securityGroupVpcAssociations().stream())
                .collect(Collectors.toList());
    }

    public static List<SecurityGroup> describeSecurityGroups(Ec2Client client, DescribeSecurityGroupsRequest request) {
        return client.describeSecurityGroupsPaginator(request).stream()
                .flatMap(r -> r.securityGroups().stream())
                .collect(Collectors.toList());
    }

    public static List<StaleSecurityGroup> describeStaleSecurityGroups(
            Ec2Client client, DescribeStaleSecurityGroupsRequest request) {
        return client.describeStaleSecurityGroupsPaginator(request).stream()
                .flatMap(r -> r.staleSecurityGroupSet().stream())
                .collect(Collectors.toList());
    }

    public static DisassociateSecurityGroupVpcResponse disassociateSecurityGroupVpc(
            Ec2Client client, DisassociateSecurityGroupVpcRequest request) {
        return client.disassociateSecurityGroupVpc(request);
    }

    public static List<SecurityGroupForVpc> getSecurityGroupsForVpc(
            Ec2Client client, GetSecurityGroupsForVpcRequest request) {
        return client.getSecurityGroupsForVpcPaginator(request).stream()
                .flatMap(r -> r.securityGroupForVpcs().stream())
                .collect(Collectors.toList());
    }

    public static ModifySecurityGroupRulesResponse modifySecurityGroupRules(
            Ec2Client client, ModifySecurityGroupRulesRequest request) {
        return client.modifySecurityGroupRules(request);
    }

    public static RevokeSecurityGroupEgressResponse revokeSecurityGroupEgress(
            Ec2Client client, RevokeSecurityGroupEgressRequest request) {
        return client.revokeSecurityGroupEgress(request);
    }

    public static RevokeSecurityGroupIngressResponse revokeSecurityGroupIngress(
            Ec2Client client, RevokeSecurityGroupIngressRequest request) {
        return client.revokeSecurityGroupIngress(request);
    }

    public static UpdateSecurityGroupRuleDescriptionsEgressResponse updateSecurityGroupRuleDescriptionsEgress(
            Ec2Client client, UpdateSecurityGroupRuleDescriptionsEgressRequest request) {
        return client.updateSecurityGroupRuleDescriptionsEgress(request);
    }

    public static UpdateSecurityGroupRuleDescriptionsIngressResponse updateSecurityGroupRuleDescriptionsIngress(
            Ec2Client client, UpdateSecurityGroupRuleDescriptionsIngressRequest request) {
        return client.updateSecurityGroupRuleDescriptionsIngress(request);
    }
}
