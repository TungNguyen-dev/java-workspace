package tungnn.tutor.java.aws.service.ec2;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

public final class Ec2RouteTableUtil {
  private Ec2RouteTableUtil() {}

  public static AssociateRouteTableResponse associateRouteTable(
      Ec2Client client, AssociateRouteTableRequest request) {
    return client.associateRouteTable(request);
  }

  public static CreateRouteResponse createRoute(Ec2Client client, CreateRouteRequest request) {
    return client.createRoute(request);
  }

  public static CreateRouteTableResponse createRouteTable(
      Ec2Client client, CreateRouteTableRequest request) {
    return client.createRouteTable(request);
  }

  public static DeleteRouteResponse deleteRoute(Ec2Client client, DeleteRouteRequest request) {
    return client.deleteRoute(request);
  }

  public static DeleteRouteTableResponse deleteRouteTable(
      Ec2Client client, DeleteRouteTableRequest request) {
    return client.deleteRouteTable(request);
  }

  public static List<RouteTable> describeRouteTables(
      Ec2Client client, DescribeRouteTablesRequest request) {
    return client.describeRouteTablesPaginator(request).stream()
        .flatMap(r -> r.routeTables().stream())
        .collect(Collectors.toList());
  }

  public static DisableVgwRoutePropagationResponse disableVgwRoutePropagation(
      Ec2Client client, DisableVgwRoutePropagationRequest request) {
    return client.disableVgwRoutePropagation(request);
  }

  public static DisassociateRouteTableResponse disassociateRouteTable(
      Ec2Client client, DisassociateRouteTableRequest request) {
    return client.disassociateRouteTable(request);
  }

  public static EnableVgwRoutePropagationResponse enableVgwRoutePropagation(
      Ec2Client client, EnableVgwRoutePropagationRequest request) {
    return client.enableVgwRoutePropagation(request);
  }

  public static ReplaceRouteResponse replaceRoute(Ec2Client client, ReplaceRouteRequest request) {
    return client.replaceRoute(request);
  }

  public static ReplaceRouteTableAssociationResponse replaceRouteTableAssociation(
      Ec2Client client, ReplaceRouteTableAssociationRequest request) {
    return client.replaceRouteTableAssociation(request);
  }
}
