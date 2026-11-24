package tungnn.tutor.java.aws.service.ec2;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

public final class Ec2AddressUtil {
    private Ec2AddressUtil() {}

    public static AcceptAddressTransferResponse acceptAddressTransfer(
            Ec2Client client, AcceptAddressTransferRequest request) {
        return client.acceptAddressTransfer(request);
    }

    public static AllocateAddressResponse allocateAddress(Ec2Client client, AllocateAddressRequest request) {
        return client.allocateAddress(request);
    }

    public static AssociateAddressResponse associateAddress(Ec2Client client, AssociateAddressRequest request) {
        return client.associateAddress(request);
    }

    public static List<AddressTransfer> describeAddressTransfers(
            Ec2Client client, DescribeAddressTransfersRequest request) {
        return client.describeAddressTransfersPaginator(request).stream()
                .flatMap(r -> r.addressTransfers().stream())
                .collect(Collectors.toList());
    }

    public static DescribeAddressesResponse describeAddresses(Ec2Client client, DescribeAddressesRequest request) {
        return client.describeAddresses(request);
    }

    public static List<AddressAttribute> describeAddressesAttribute(
            Ec2Client client, DescribeAddressesAttributeRequest request) {
        return client.describeAddressesAttributePaginator(request).stream()
                .flatMap(r -> r.addresses().stream())
                .collect(Collectors.toList());
    }

    public static List<MovingAddressStatus> describeMovingAddresses(
            Ec2Client client, DescribeMovingAddressesRequest request) {
        return client.describeMovingAddressesPaginator(request).stream()
                .flatMap(r -> r.movingAddressStatuses().stream())
                .collect(Collectors.toList());
    }

    public static DisableAddressTransferResponse disableAddressTransfer(
            Ec2Client client, DisableAddressTransferRequest request) {
        return client.disableAddressTransfer(request);
    }

    public static DisassociateAddressResponse disassociateAddress(
            Ec2Client client, DisassociateAddressRequest request) {
        return client.disassociateAddress(request);
    }

    public static EnableAddressTransferResponse enableAddressTransfer(
            Ec2Client client, EnableAddressTransferRequest request) {
        return client.enableAddressTransfer(request);
    }

    public static ModifyAddressAttributeResponse modifyAddressAttribute(
            Ec2Client client, ModifyAddressAttributeRequest request) {
        return client.modifyAddressAttribute(request);
    }

    public static MoveAddressToVpcResponse moveAddressToVpc(Ec2Client client, MoveAddressToVpcRequest request) {
        return client.moveAddressToVpc(request);
    }

    public static ReleaseAddressResponse releaseAddress(Ec2Client client, ReleaseAddressRequest request) {
        return client.releaseAddress(request);
    }

    public static ResetAddressAttributeResponse resetAddressAttribute(
            Ec2Client client, ResetAddressAttributeRequest request) {
        return client.resetAddressAttribute(request);
    }

    public static RestoreAddressToClassicResponse restoreAddressToClassic(
            Ec2Client client, RestoreAddressToClassicRequest request) {
        return client.restoreAddressToClassic(request);
    }
}
