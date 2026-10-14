package tungnn.tutor.java.aws.service.ssm;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.*;

public final class SsmActivationUtil {

  private SsmActivationUtil() {}

  public static CreateActivationResponse createActivation(
      SsmClient client, CreateActivationRequest request) {
    return client.createActivation(request);
  }

  public static DeleteActivationResponse deleteActivation(
      SsmClient client, DeleteActivationRequest request) {
    return client.deleteActivation(request);
  }

  public static List<Activation> describeActivations(
      SsmClient client, DescribeActivationsRequest request) {
    return client.describeActivationsPaginator(request).stream()
        .flatMap(r -> r.activationList().stream())
        .collect(Collectors.toList());
  }

  public static DeregisterManagedInstanceResponse deregisterManagedInstance(
      SsmClient client, DeregisterManagedInstanceRequest request) {
    return client.deregisterManagedInstance(request);
  }

  public static UpdateManagedInstanceRoleResponse updateManagedInstanceRole(
      SsmClient client, UpdateManagedInstanceRoleRequest request) {
    return client.updateManagedInstanceRole(request);
  }
}
