package tungnn.tutor.java.aws.service.ssm;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.*;

public final class SsmInstanceUtil {

  private SsmInstanceUtil() {}

  public static List<InstanceInformation> describeInstanceInformation(
      SsmClient client, DescribeInstanceInformationRequest request) {
    return client.describeInstanceInformationPaginator(request).stream()
        .flatMap(r -> r.instanceInformationList().stream())
        .collect(Collectors.toList());
  }

  public static List<InstanceAssociationStatusInfo> describeInstanceAssociationsStatus(
      SsmClient client, DescribeInstanceAssociationsStatusRequest request) {
    return client.describeInstanceAssociationsStatusPaginator(request).stream()
        .flatMap(r -> r.instanceAssociationStatusInfos().stream())
        .collect(Collectors.toList());
  }

  public static List<InstanceProperty> describeInstanceProperties(
      SsmClient client, DescribeInstancePropertiesRequest request) {
    return client.describeInstancePropertiesPaginator(request).stream()
        .flatMap(r -> r.instanceProperties().stream())
        .collect(Collectors.toList());
  }

  public static List<InstanceAssociation> describeEffectiveInstanceAssociations(
      SsmClient client, DescribeEffectiveInstanceAssociationsRequest request) {
    return client.describeEffectiveInstanceAssociationsPaginator(request).stream()
        .flatMap(r -> r.associations().stream())
        .collect(Collectors.toList());
  }
}
