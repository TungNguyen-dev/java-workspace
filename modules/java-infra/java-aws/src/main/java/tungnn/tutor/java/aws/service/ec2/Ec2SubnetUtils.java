package tungnn.tutor.java.aws.service.ec2;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

public final class Ec2SubnetUtils {
    private Ec2SubnetUtils() {}

    public static AssociateSubnetCidrBlockResponse associateSubnetCidrBlock(
            Ec2Client client, AssociateSubnetCidrBlockRequest request) {
        return client.associateSubnetCidrBlock(request);
    }

    public static CreateDefaultSubnetResponse createDefaultSubnet(
            Ec2Client client, CreateDefaultSubnetRequest request) {
        return client.createDefaultSubnet(request);
    }

    public static CreateSubnetResponse createSubnet(Ec2Client client, CreateSubnetRequest request) {
        return client.createSubnet(request);
    }

    public static CreateSubnetCidrReservationResponse createSubnetCidrReservation(
            Ec2Client client, CreateSubnetCidrReservationRequest request) {
        return client.createSubnetCidrReservation(request);
    }

    public static DeleteSubnetResponse deleteSubnet(Ec2Client client, DeleteSubnetRequest request) {
        return client.deleteSubnet(request);
    }

    public static DeleteSubnetCidrReservationResponse deleteSubnetCidrReservation(
            Ec2Client client, DeleteSubnetCidrReservationRequest request) {
        return client.deleteSubnetCidrReservation(request);
    }

    public static List<Subnet> describeSubnets(Ec2Client client, DescribeSubnetsRequest request) {
        return client.describeSubnetsPaginator(request).stream()
                .flatMap(r -> r.subnets().stream())
                .collect(Collectors.toList());
    }

    public static DisassociateSubnetCidrBlockResponse disassociateSubnetCidrBlock(
            Ec2Client client, DisassociateSubnetCidrBlockRequest request) {
        return client.disassociateSubnetCidrBlock(request);
    }

    public static GetSubnetCidrReservationsResponse getSubnetCidrReservations(
            Ec2Client client, GetSubnetCidrReservationsRequest request) {
        return client.getSubnetCidrReservations(request);
    }

    public static ModifySubnetAttributeResponse modifySubnetAttribute(
            Ec2Client client, ModifySubnetAttributeRequest request) {
        return client.modifySubnetAttribute(request);
    }
}
