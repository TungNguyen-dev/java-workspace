package tungnn.tutor.java.aws.service.ec2;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

public final class Ec2VpcUtil {
  private Ec2VpcUtil() {}

  public static AcceptVpcEndpointConnectionsResponse acceptVpcEndpointConnections(
      Ec2Client client, AcceptVpcEndpointConnectionsRequest request) {
    return client.acceptVpcEndpointConnections(request);
  }

  public static AcceptVpcPeeringConnectionResponse acceptVpcPeeringConnection(
      Ec2Client client, AcceptVpcPeeringConnectionRequest request) {
    return client.acceptVpcPeeringConnection(request);
  }

  public static AssignPrivateNatGatewayAddressResponse assignPrivateNatGatewayAddress(
      Ec2Client client, AssignPrivateNatGatewayAddressRequest request) {
    return client.assignPrivateNatGatewayAddress(request);
  }

  public static AssociateDhcpOptionsResponse associateDhcpOptions(
      Ec2Client client, AssociateDhcpOptionsRequest request) {
    return client.associateDhcpOptions(request);
  }

  public static AssociateNatGatewayAddressResponse associateNatGatewayAddress(
      Ec2Client client, AssociateNatGatewayAddressRequest request) {
    return client.associateNatGatewayAddress(request);
  }

  public static AssociateVpcCidrBlockResponse associateVpcCidrBlock(
      Ec2Client client, AssociateVpcCidrBlockRequest request) {
    return client.associateVpcCidrBlock(request);
  }

  public static AttachClassicLinkVpcResponse attachClassicLinkVpc(
      Ec2Client client, AttachClassicLinkVpcRequest request) {
    return client.attachClassicLinkVpc(request);
  }

  public static AttachInternetGatewayResponse attachInternetGateway(
      Ec2Client client, AttachInternetGatewayRequest request) {
    return client.attachInternetGateway(request);
  }

  public static CreateDefaultVpcResponse createDefaultVpc(
      Ec2Client client, CreateDefaultVpcRequest request) {
    return client.createDefaultVpc(request);
  }

  public static CreateDhcpOptionsResponse createDhcpOptions(
      Ec2Client client, CreateDhcpOptionsRequest request) {
    return client.createDhcpOptions(request);
  }

  public static CreateEgressOnlyInternetGatewayResponse createEgressOnlyInternetGateway(
      Ec2Client client, CreateEgressOnlyInternetGatewayRequest request) {
    return client.createEgressOnlyInternetGateway(request);
  }

  public static CreateInternetGatewayResponse createInternetGateway(
      Ec2Client client, CreateInternetGatewayRequest request) {
    return client.createInternetGateway(request);
  }

  public static CreateLocalGatewayRouteTableVpcAssociationResponse
      createLocalGatewayRouteTableVpcAssociation(
          Ec2Client client, CreateLocalGatewayRouteTableVpcAssociationRequest request) {
    return client.createLocalGatewayRouteTableVpcAssociation(request);
  }

  public static CreateNatGatewayResponse createNatGateway(
      Ec2Client client, CreateNatGatewayRequest request) {
    return client.createNatGateway(request);
  }

  public static CreateNetworkAclResponse createNetworkAcl(
      Ec2Client client, CreateNetworkAclRequest request) {
    return client.createNetworkAcl(request);
  }

  public static CreateNetworkAclEntryResponse createNetworkAclEntry(
      Ec2Client client, CreateNetworkAclEntryRequest request) {
    return client.createNetworkAclEntry(request);
  }

  public static CreateVpcResponse createVpc(Ec2Client client, CreateVpcRequest request) {
    return client.createVpc(request);
  }

  public static CreateVpcBlockPublicAccessExclusionResponse createVpcBlockPublicAccessExclusion(
      Ec2Client client, CreateVpcBlockPublicAccessExclusionRequest request) {
    return client.createVpcBlockPublicAccessExclusion(request);
  }

  public static CreateVpcEncryptionControlResponse createVpcEncryptionControl(
      Ec2Client client, CreateVpcEncryptionControlRequest request) {
    return client.createVpcEncryptionControl(request);
  }

  public static CreateVpcEndpointResponse createVpcEndpoint(
      Ec2Client client, CreateVpcEndpointRequest request) {
    return client.createVpcEndpoint(request);
  }

  public static CreateVpcEndpointConnectionNotificationResponse
      createVpcEndpointConnectionNotification(
          Ec2Client client, CreateVpcEndpointConnectionNotificationRequest request) {
    return client.createVpcEndpointConnectionNotification(request);
  }

  public static CreateVpcEndpointServiceConfigurationResponse createVpcEndpointServiceConfiguration(
      Ec2Client client, CreateVpcEndpointServiceConfigurationRequest request) {
    return client.createVpcEndpointServiceConfiguration(request);
  }

  public static CreateVpcPeeringConnectionResponse createVpcPeeringConnection(
      Ec2Client client, CreateVpcPeeringConnectionRequest request) {
    return client.createVpcPeeringConnection(request);
  }

  public static DeleteDhcpOptionsResponse deleteDhcpOptions(
      Ec2Client client, DeleteDhcpOptionsRequest request) {
    return client.deleteDhcpOptions(request);
  }

  public static DeleteEgressOnlyInternetGatewayResponse deleteEgressOnlyInternetGateway(
      Ec2Client client, DeleteEgressOnlyInternetGatewayRequest request) {
    return client.deleteEgressOnlyInternetGateway(request);
  }

  public static DeleteInternetGatewayResponse deleteInternetGateway(
      Ec2Client client, DeleteInternetGatewayRequest request) {
    return client.deleteInternetGateway(request);
  }

  public static DeleteLocalGatewayRouteTableVpcAssociationResponse
      deleteLocalGatewayRouteTableVpcAssociation(
          Ec2Client client, DeleteLocalGatewayRouteTableVpcAssociationRequest request) {
    return client.deleteLocalGatewayRouteTableVpcAssociation(request);
  }

  public static DeleteNatGatewayResponse deleteNatGateway(
      Ec2Client client, DeleteNatGatewayRequest request) {
    return client.deleteNatGateway(request);
  }

  public static DeleteNetworkAclResponse deleteNetworkAcl(
      Ec2Client client, DeleteNetworkAclRequest request) {
    return client.deleteNetworkAcl(request);
  }

  public static DeleteNetworkAclEntryResponse deleteNetworkAclEntry(
      Ec2Client client, DeleteNetworkAclEntryRequest request) {
    return client.deleteNetworkAclEntry(request);
  }

  public static DeleteVpcResponse deleteVpc(Ec2Client client, DeleteVpcRequest request) {
    return client.deleteVpc(request);
  }

  public static DeleteVpcBlockPublicAccessExclusionResponse deleteVpcBlockPublicAccessExclusion(
      Ec2Client client, DeleteVpcBlockPublicAccessExclusionRequest request) {
    return client.deleteVpcBlockPublicAccessExclusion(request);
  }

  public static DeleteVpcEncryptionControlResponse deleteVpcEncryptionControl(
      Ec2Client client, DeleteVpcEncryptionControlRequest request) {
    return client.deleteVpcEncryptionControl(request);
  }

  public static DeleteVpcEndpointConnectionNotificationsResponse
      deleteVpcEndpointConnectionNotifications(
          Ec2Client client, DeleteVpcEndpointConnectionNotificationsRequest request) {
    return client.deleteVpcEndpointConnectionNotifications(request);
  }

  public static DeleteVpcEndpointServiceConfigurationsResponse
      deleteVpcEndpointServiceConfigurations(
          Ec2Client client, DeleteVpcEndpointServiceConfigurationsRequest request) {
    return client.deleteVpcEndpointServiceConfigurations(request);
  }

  public static DeleteVpcEndpointsResponse deleteVpcEndpoints(
      Ec2Client client, DeleteVpcEndpointsRequest request) {
    return client.deleteVpcEndpoints(request);
  }

  public static DeleteVpcPeeringConnectionResponse deleteVpcPeeringConnection(
      Ec2Client client, DeleteVpcPeeringConnectionRequest request) {
    return client.deleteVpcPeeringConnection(request);
  }

  public static List<ClassicLinkInstance> describeClassicLinkInstances(
      Ec2Client client, DescribeClassicLinkInstancesRequest request) {
    return client.describeClassicLinkInstancesPaginator(request).stream()
        .flatMap(r -> r.instances().stream())
        .collect(Collectors.toList());
  }

  public static List<DhcpOptions> describeDhcpOptions(
      Ec2Client client, DescribeDhcpOptionsRequest request) {
    return client.describeDhcpOptionsPaginator(request).stream()
        .flatMap(r -> r.dhcpOptions().stream())
        .collect(Collectors.toList());
  }

  public static List<EgressOnlyInternetGateway> describeEgressOnlyInternetGateways(
      Ec2Client client, DescribeEgressOnlyInternetGatewaysRequest request) {
    return client.describeEgressOnlyInternetGatewaysPaginator(request).stream()
        .flatMap(r -> r.egressOnlyInternetGateways().stream())
        .collect(Collectors.toList());
  }

  public static List<InternetGateway> describeInternetGateways(
      Ec2Client client, DescribeInternetGatewaysRequest request) {
    return client.describeInternetGatewaysPaginator(request).stream()
        .flatMap(r -> r.internetGateways().stream())
        .collect(Collectors.toList());
  }

  public static List<LocalGatewayRouteTableVpcAssociation>
      describeLocalGatewayRouteTableVpcAssociations(
          Ec2Client client, DescribeLocalGatewayRouteTableVpcAssociationsRequest request) {
    return client.describeLocalGatewayRouteTableVpcAssociationsPaginator(request).stream()
        .flatMap(r -> r.localGatewayRouteTableVpcAssociations().stream())
        .collect(Collectors.toList());
  }

  public static List<NatGateway> describeNatGateways(
      Ec2Client client, DescribeNatGatewaysRequest request) {
    return client.describeNatGatewaysPaginator(request).stream()
        .flatMap(r -> r.natGateways().stream())
        .collect(Collectors.toList());
  }

  public static List<NetworkAcl> describeNetworkAcls(
      Ec2Client client, DescribeNetworkAclsRequest request) {
    return client.describeNetworkAclsPaginator(request).stream()
        .flatMap(r -> r.networkAcls().stream())
        .collect(Collectors.toList());
  }

  public static DescribeVpcAttributeResponse describeVpcAttribute(
      Ec2Client client, DescribeVpcAttributeRequest request) {
    return client.describeVpcAttribute(request);
  }

  public static DescribeVpcBlockPublicAccessExclusionsResponse
      describeVpcBlockPublicAccessExclusions(
          Ec2Client client, DescribeVpcBlockPublicAccessExclusionsRequest request) {
    return client.describeVpcBlockPublicAccessExclusions(request);
  }

  public static DescribeVpcBlockPublicAccessOptionsResponse describeVpcBlockPublicAccessOptions(
      Ec2Client client, DescribeVpcBlockPublicAccessOptionsRequest request) {
    return client.describeVpcBlockPublicAccessOptions(request);
  }

  public static DescribeVpcClassicLinkResponse describeVpcClassicLink(
      Ec2Client client, DescribeVpcClassicLinkRequest request) {
    return client.describeVpcClassicLink(request);
  }

  public static List<ClassicLinkDnsSupport> describeVpcClassicLinkDnsSupport(
      Ec2Client client, DescribeVpcClassicLinkDnsSupportRequest request) {
    return client.describeVpcClassicLinkDnsSupportPaginator(request).stream()
        .flatMap(r -> r.vpcs().stream())
        .collect(Collectors.toList());
  }

  public static DescribeVpcEncryptionControlsResponse describeVpcEncryptionControls(
      Ec2Client client, DescribeVpcEncryptionControlsRequest request) {
    return client.describeVpcEncryptionControls(request);
  }

  public static DescribeVpcEndpointAssociationsResponse describeVpcEndpointAssociations(
      Ec2Client client, DescribeVpcEndpointAssociationsRequest request) {
    return client.describeVpcEndpointAssociations(request);
  }

  public static List<ConnectionNotification> describeVpcEndpointConnectionNotifications(
      Ec2Client client, DescribeVpcEndpointConnectionNotificationsRequest request) {
    return client.describeVpcEndpointConnectionNotificationsPaginator(request).stream()
        .flatMap(r -> r.connectionNotificationSet().stream())
        .collect(Collectors.toList());
  }

  public static List<VpcEndpointConnection> describeVpcEndpointConnections(
      Ec2Client client, DescribeVpcEndpointConnectionsRequest request) {
    return client.describeVpcEndpointConnectionsPaginator(request).stream()
        .flatMap(r -> r.vpcEndpointConnections().stream())
        .collect(Collectors.toList());
  }

  public static List<ServiceConfiguration> describeVpcEndpointServiceConfigurations(
      Ec2Client client, DescribeVpcEndpointServiceConfigurationsRequest request) {
    return client.describeVpcEndpointServiceConfigurationsPaginator(request).stream()
        .flatMap(r -> r.serviceConfigurations().stream())
        .collect(Collectors.toList());
  }

  public static List<AllowedPrincipal> describeVpcEndpointServicePermissions(
      Ec2Client client, DescribeVpcEndpointServicePermissionsRequest request) {
    return client.describeVpcEndpointServicePermissionsPaginator(request).stream()
        .flatMap(r -> r.allowedPrincipals().stream())
        .collect(Collectors.toList());
  }

  public static DescribeVpcEndpointServicesResponse describeVpcEndpointServices(
      Ec2Client client, DescribeVpcEndpointServicesRequest request) {
    return client.describeVpcEndpointServices(request);
  }

  public static List<VpcEndpoint> describeVpcEndpoints(
      Ec2Client client, DescribeVpcEndpointsRequest request) {
    return client.describeVpcEndpointsPaginator(request).stream()
        .flatMap(r -> r.vpcEndpoints().stream())
        .collect(Collectors.toList());
  }

  public static List<VpcPeeringConnection> describeVpcPeeringConnections(
      Ec2Client client, DescribeVpcPeeringConnectionsRequest request) {
    return client.describeVpcPeeringConnectionsPaginator(request).stream()
        .flatMap(r -> r.vpcPeeringConnections().stream())
        .collect(Collectors.toList());
  }

  public static List<Vpc> describeVpcs(Ec2Client client, DescribeVpcsRequest request) {
    return client.describeVpcsPaginator(request).stream()
        .flatMap(r -> r.vpcs().stream())
        .collect(Collectors.toList());
  }

  public static DetachClassicLinkVpcResponse detachClassicLinkVpc(
      Ec2Client client, DetachClassicLinkVpcRequest request) {
    return client.detachClassicLinkVpc(request);
  }

  public static DetachInternetGatewayResponse detachInternetGateway(
      Ec2Client client, DetachInternetGatewayRequest request) {
    return client.detachInternetGateway(request);
  }

  public static DisableVpcClassicLinkResponse disableVpcClassicLink(
      Ec2Client client, DisableVpcClassicLinkRequest request) {
    return client.disableVpcClassicLink(request);
  }

  public static DisableVpcClassicLinkDnsSupportResponse disableVpcClassicLinkDnsSupport(
      Ec2Client client, DisableVpcClassicLinkDnsSupportRequest request) {
    return client.disableVpcClassicLinkDnsSupport(request);
  }

  public static DisassociateNatGatewayAddressResponse disassociateNatGatewayAddress(
      Ec2Client client, DisassociateNatGatewayAddressRequest request) {
    return client.disassociateNatGatewayAddress(request);
  }

  public static DisassociateVpcCidrBlockResponse disassociateVpcCidrBlock(
      Ec2Client client, DisassociateVpcCidrBlockRequest request) {
    return client.disassociateVpcCidrBlock(request);
  }

  public static EnableVpcClassicLinkResponse enableVpcClassicLink(
      Ec2Client client, EnableVpcClassicLinkRequest request) {
    return client.enableVpcClassicLink(request);
  }

  public static EnableVpcClassicLinkDnsSupportResponse enableVpcClassicLinkDnsSupport(
      Ec2Client client, EnableVpcClassicLinkDnsSupportRequest request) {
    return client.enableVpcClassicLinkDnsSupport(request);
  }

  public static GetVpcResourcesBlockingEncryptionEnforcementResponse
      getVpcResourcesBlockingEncryptionEnforcement(
          Ec2Client client, GetVpcResourcesBlockingEncryptionEnforcementRequest request) {
    return client.getVpcResourcesBlockingEncryptionEnforcement(request);
  }

  public static ModifyVpcAttributeResponse modifyVpcAttribute(
      Ec2Client client, ModifyVpcAttributeRequest request) {
    return client.modifyVpcAttribute(request);
  }

  public static ModifyVpcBlockPublicAccessExclusionResponse modifyVpcBlockPublicAccessExclusion(
      Ec2Client client, ModifyVpcBlockPublicAccessExclusionRequest request) {
    return client.modifyVpcBlockPublicAccessExclusion(request);
  }

  public static ModifyVpcBlockPublicAccessOptionsResponse modifyVpcBlockPublicAccessOptions(
      Ec2Client client, ModifyVpcBlockPublicAccessOptionsRequest request) {
    return client.modifyVpcBlockPublicAccessOptions(request);
  }

  public static ModifyVpcEncryptionControlResponse modifyVpcEncryptionControl(
      Ec2Client client, ModifyVpcEncryptionControlRequest request) {
    return client.modifyVpcEncryptionControl(request);
  }

  public static ModifyVpcEndpointResponse modifyVpcEndpoint(
      Ec2Client client, ModifyVpcEndpointRequest request) {
    return client.modifyVpcEndpoint(request);
  }

  public static ModifyVpcEndpointConnectionNotificationResponse
      modifyVpcEndpointConnectionNotification(
          Ec2Client client, ModifyVpcEndpointConnectionNotificationRequest request) {
    return client.modifyVpcEndpointConnectionNotification(request);
  }

  public static ModifyVpcEndpointServiceConfigurationResponse modifyVpcEndpointServiceConfiguration(
      Ec2Client client, ModifyVpcEndpointServiceConfigurationRequest request) {
    return client.modifyVpcEndpointServiceConfiguration(request);
  }

  public static ModifyVpcEndpointServicePayerResponsibilityResponse
      modifyVpcEndpointServicePayerResponsibility(
          Ec2Client client, ModifyVpcEndpointServicePayerResponsibilityRequest request) {
    return client.modifyVpcEndpointServicePayerResponsibility(request);
  }

  public static ModifyVpcEndpointServicePermissionsResponse modifyVpcEndpointServicePermissions(
      Ec2Client client, ModifyVpcEndpointServicePermissionsRequest request) {
    return client.modifyVpcEndpointServicePermissions(request);
  }

  public static ModifyVpcPeeringConnectionOptionsResponse modifyVpcPeeringConnectionOptions(
      Ec2Client client, ModifyVpcPeeringConnectionOptionsRequest request) {
    return client.modifyVpcPeeringConnectionOptions(request);
  }

  public static ModifyVpcTenancyResponse modifyVpcTenancy(
      Ec2Client client, ModifyVpcTenancyRequest request) {
    return client.modifyVpcTenancy(request);
  }

  public static RejectVpcEndpointConnectionsResponse rejectVpcEndpointConnections(
      Ec2Client client, RejectVpcEndpointConnectionsRequest request) {
    return client.rejectVpcEndpointConnections(request);
  }

  public static RejectVpcPeeringConnectionResponse rejectVpcPeeringConnection(
      Ec2Client client, RejectVpcPeeringConnectionRequest request) {
    return client.rejectVpcPeeringConnection(request);
  }

  public static ReplaceNetworkAclAssociationResponse replaceNetworkAclAssociation(
      Ec2Client client, ReplaceNetworkAclAssociationRequest request) {
    return client.replaceNetworkAclAssociation(request);
  }

  public static ReplaceNetworkAclEntryResponse replaceNetworkAclEntry(
      Ec2Client client, ReplaceNetworkAclEntryRequest request) {
    return client.replaceNetworkAclEntry(request);
  }

  public static StartVpcEndpointServicePrivateDnsVerificationResponse
      startVpcEndpointServicePrivateDnsVerification(
          Ec2Client client, StartVpcEndpointServicePrivateDnsVerificationRequest request) {
    return client.startVpcEndpointServicePrivateDnsVerification(request);
  }

  public static UnassignPrivateNatGatewayAddressResponse unassignPrivateNatGatewayAddress(
      Ec2Client client, UnassignPrivateNatGatewayAddressRequest request) {
    return client.unassignPrivateNatGatewayAddress(request);
  }
}
