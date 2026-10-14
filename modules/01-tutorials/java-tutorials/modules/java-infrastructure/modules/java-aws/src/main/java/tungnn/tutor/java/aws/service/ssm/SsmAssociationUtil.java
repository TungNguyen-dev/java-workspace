package tungnn.tutor.java.aws.service.ssm;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.*;

public final class SsmAssociationUtil {

  private SsmAssociationUtil() {}

  public static CreateAssociationResponse createAssociation(
      SsmClient client, CreateAssociationRequest request) {
    return client.createAssociation(request);
  }

  public static CreateAssociationBatchResponse createAssociationBatch(
      SsmClient client, CreateAssociationBatchRequest request) {
    return client.createAssociationBatch(request);
  }

  public static DeleteAssociationResponse deleteAssociation(
      SsmClient client, DeleteAssociationRequest request) {
    return client.deleteAssociation(request);
  }

  public static DescribeAssociationResponse describeAssociation(
      SsmClient client, DescribeAssociationRequest request) {
    return client.describeAssociation(request);
  }

  public static List<AssociationExecution> describeAssociationExecutions(
      SsmClient client, DescribeAssociationExecutionsRequest request) {
    return client.describeAssociationExecutionsPaginator(request).stream()
        .flatMap(r -> r.associationExecutions().stream())
        .collect(Collectors.toList());
  }

  public static List<AssociationExecutionTarget> describeAssociationExecutionTargets(
      SsmClient client, DescribeAssociationExecutionTargetsRequest request) {
    return client.describeAssociationExecutionTargetsPaginator(request).stream()
        .flatMap(r -> r.associationExecutionTargets().stream())
        .collect(Collectors.toList());
  }

  public static List<Association> listAssociations(
      SsmClient client, ListAssociationsRequest request) {
    return client.listAssociationsPaginator(request).stream()
        .flatMap(r -> r.associations().stream())
        .collect(Collectors.toList());
  }

  public static List<AssociationVersionInfo> listAssociationVersions(
      SsmClient client, ListAssociationVersionsRequest request) {
    return client.listAssociationVersionsPaginator(request).stream()
        .flatMap(r -> r.associationVersions().stream())
        .collect(Collectors.toList());
  }

  public static StartAssociationsOnceResponse startAssociationsOnce(
      SsmClient client, StartAssociationsOnceRequest request) {
    return client.startAssociationsOnce(request);
  }

  public static UpdateAssociationResponse updateAssociation(
      SsmClient client, UpdateAssociationRequest request) {
    return client.updateAssociation(request);
  }

  public static UpdateAssociationStatusResponse updateAssociationStatus(
      SsmClient client, UpdateAssociationStatusRequest request) {
    return client.updateAssociationStatus(request);
  }
}
