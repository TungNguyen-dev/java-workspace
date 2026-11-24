package tungnn.tutor.java.aws.service.ec2;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

public final class Ec2NetworkInterfaceUtil {
    private Ec2NetworkInterfaceUtil() {}

    public static AssignIpv6AddressesResponse assignIpv6Addresses(
            Ec2Client client, AssignIpv6AddressesRequest request) {
        return client.assignIpv6Addresses(request);
    }

    public static AssignPrivateIpAddressesResponse assignPrivateIpAddresses(
            Ec2Client client, AssignPrivateIpAddressesRequest request) {
        return client.assignPrivateIpAddresses(request);
    }

    public static AssociateTrunkInterfaceResponse associateTrunkInterface(
            Ec2Client client, AssociateTrunkInterfaceRequest request) {
        return client.associateTrunkInterface(request);
    }

    public static AttachNetworkInterfaceResponse attachNetworkInterface(
            Ec2Client client, AttachNetworkInterfaceRequest request) {
        return client.attachNetworkInterface(request);
    }

    public static CreateNetworkInterfaceResponse createNetworkInterface(
            Ec2Client client, CreateNetworkInterfaceRequest request) {
        return client.createNetworkInterface(request);
    }

    public static CreateNetworkInterfacePermissionResponse createNetworkInterfacePermission(
            Ec2Client client, CreateNetworkInterfacePermissionRequest request) {
        return client.createNetworkInterfacePermission(request);
    }

    public static DeleteNetworkInterfaceResponse deleteNetworkInterface(
            Ec2Client client, DeleteNetworkInterfaceRequest request) {
        return client.deleteNetworkInterface(request);
    }

    public static DeleteNetworkInterfacePermissionResponse deleteNetworkInterfacePermission(
            Ec2Client client, DeleteNetworkInterfacePermissionRequest request) {
        return client.deleteNetworkInterfacePermission(request);
    }

    public static DescribeNetworkInterfaceAttributeResponse describeNetworkInterfaceAttribute(
            Ec2Client client, DescribeNetworkInterfaceAttributeRequest request) {
        return client.describeNetworkInterfaceAttribute(request);
    }

    public static List<NetworkInterfacePermission> describeNetworkInterfacePermissions(
            Ec2Client client, DescribeNetworkInterfacePermissionsRequest request) {
        return client.describeNetworkInterfacePermissionsPaginator(request).stream()
                .flatMap(r -> r.networkInterfacePermissions().stream())
                .collect(Collectors.toList());
    }

    public static List<NetworkInterface> describeNetworkInterfaces(
            Ec2Client client, DescribeNetworkInterfacesRequest request) {
        return client.describeNetworkInterfacesPaginator(request).stream()
                .flatMap(r -> r.networkInterfaces().stream())
                .collect(Collectors.toList());
    }

    public static List<SecondaryInterface> describeSecondaryInterfaces(
            Ec2Client client, DescribeSecondaryInterfacesRequest request) {
        return client.describeSecondaryInterfacesPaginator(request).stream()
                .flatMap(r -> r.secondaryInterfaces().stream())
                .collect(Collectors.toList());
    }

    public static List<TrunkInterfaceAssociation> describeTrunkInterfaceAssociations(
            Ec2Client client, DescribeTrunkInterfaceAssociationsRequest request) {
        return client.describeTrunkInterfaceAssociationsPaginator(request).stream()
                .flatMap(r -> r.interfaceAssociations().stream())
                .collect(Collectors.toList());
    }

    public static DetachNetworkInterfaceResponse detachNetworkInterface(
            Ec2Client client, DetachNetworkInterfaceRequest request) {
        return client.detachNetworkInterface(request);
    }

    public static DisassociateTrunkInterfaceResponse disassociateTrunkInterface(
            Ec2Client client, DisassociateTrunkInterfaceRequest request) {
        return client.disassociateTrunkInterface(request);
    }

    public static ModifyNetworkInterfaceAttributeResponse modifyNetworkInterfaceAttribute(
            Ec2Client client, ModifyNetworkInterfaceAttributeRequest request) {
        return client.modifyNetworkInterfaceAttribute(request);
    }

    public static ResetNetworkInterfaceAttributeResponse resetNetworkInterfaceAttribute(
            Ec2Client client, ResetNetworkInterfaceAttributeRequest request) {
        return client.resetNetworkInterfaceAttribute(request);
    }

    public static UnassignIpv6AddressesResponse unassignIpv6Addresses(
            Ec2Client client, UnassignIpv6AddressesRequest request) {
        return client.unassignIpv6Addresses(request);
    }

    public static UnassignPrivateIpAddressesResponse unassignPrivateIpAddresses(
            Ec2Client client, UnassignPrivateIpAddressesRequest request) {
        return client.unassignPrivateIpAddresses(request);
    }
}
