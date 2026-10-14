package tungnn.tutor.java.aws.service.ssm;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.*;

public final class SsmPatchUtil {

  private SsmPatchUtil() {}

  public static CreatePatchBaselineResponse createPatchBaseline(
      SsmClient client, CreatePatchBaselineRequest request) {
    return client.createPatchBaseline(request);
  }

  public static DeletePatchBaselineResponse deletePatchBaseline(
      SsmClient client, DeletePatchBaselineRequest request) {
    return client.deletePatchBaseline(request);
  }

  public static DeregisterPatchBaselineForPatchGroupResponse deregisterPatchBaselineForPatchGroup(
      SsmClient client, DeregisterPatchBaselineForPatchGroupRequest request) {
    return client.deregisterPatchBaselineForPatchGroup(request);
  }

  public static List<Patch> describeAvailablePatches(
      SsmClient client, DescribeAvailablePatchesRequest request) {
    return client.describeAvailablePatchesPaginator(request).stream()
        .flatMap(r -> r.patches().stream())
        .collect(Collectors.toList());
  }

  public static List<EffectivePatch> describeEffectivePatchesForPatchBaseline(
      SsmClient client, DescribeEffectivePatchesForPatchBaselineRequest request) {
    return client.describeEffectivePatchesForPatchBaselinePaginator(request).stream()
        .flatMap(r -> r.effectivePatches().stream())
        .collect(Collectors.toList());
  }

  public static List<PatchComplianceData> describeInstancePatches(
      SsmClient client, DescribeInstancePatchesRequest request) {
    return client.describeInstancePatchesPaginator(request).stream()
        .flatMap(r -> r.patches().stream())
        .collect(Collectors.toList());
  }

  public static List<InstancePatchState> describeInstancePatchStates(
      SsmClient client, DescribeInstancePatchStatesRequest request) {
    return client.describeInstancePatchStatesPaginator(request).stream()
        .flatMap(r -> r.instancePatchStates().stream())
        .collect(Collectors.toList());
  }

  public static List<InstancePatchState> describeInstancePatchStatesForPatchGroup(
      SsmClient client, DescribeInstancePatchStatesForPatchGroupRequest request) {
    return client.describeInstancePatchStatesForPatchGroupPaginator(request).stream()
        .flatMap(r -> r.instancePatchStates().stream())
        .collect(Collectors.toList());
  }

  public static List<PatchBaselineIdentity> describePatchBaselines(
      SsmClient client, DescribePatchBaselinesRequest request) {
    return client.describePatchBaselinesPaginator(request).stream()
        .flatMap(r -> r.baselineIdentities().stream())
        .collect(Collectors.toList());
  }

  public static List<PatchGroupPatchBaselineMapping> describePatchGroups(
      SsmClient client, DescribePatchGroupsRequest request) {
    return client.describePatchGroupsPaginator(request).stream()
        .flatMap(r -> r.mappings().stream())
        .collect(Collectors.toList());
  }

  public static DescribePatchGroupStateResponse describePatchGroupState(
      SsmClient client, DescribePatchGroupStateRequest request) {
    return client.describePatchGroupState(request);
  }

  public static List<Map<String, String>> describePatchProperties(
      SsmClient client, DescribePatchPropertiesRequest request) {
    return client.describePatchPropertiesPaginator(request).stream()
        .flatMap(r -> r.properties().stream())
        .collect(Collectors.toList());
  }

  public static GetDefaultPatchBaselineResponse getDefaultPatchBaseline(
      SsmClient client, GetDefaultPatchBaselineRequest request) {
    return client.getDefaultPatchBaseline(request);
  }

  public static GetDeployablePatchSnapshotForInstanceResponse getDeployablePatchSnapshotForInstance(
      SsmClient client, GetDeployablePatchSnapshotForInstanceRequest request) {
    return client.getDeployablePatchSnapshotForInstance(request);
  }

  public static GetPatchBaselineResponse getPatchBaseline(
      SsmClient client, GetPatchBaselineRequest request) {
    return client.getPatchBaseline(request);
  }

  public static GetPatchBaselineForPatchGroupResponse getPatchBaselineForPatchGroup(
      SsmClient client, GetPatchBaselineForPatchGroupRequest request) {
    return client.getPatchBaselineForPatchGroup(request);
  }

  public static RegisterDefaultPatchBaselineResponse registerDefaultPatchBaseline(
      SsmClient client, RegisterDefaultPatchBaselineRequest request) {
    return client.registerDefaultPatchBaseline(request);
  }

  public static RegisterPatchBaselineForPatchGroupResponse registerPatchBaselineForPatchGroup(
      SsmClient client, RegisterPatchBaselineForPatchGroupRequest request) {
    return client.registerPatchBaselineForPatchGroup(request);
  }

  public static UpdatePatchBaselineResponse updatePatchBaseline(
      SsmClient client, UpdatePatchBaselineRequest request) {
    return client.updatePatchBaseline(request);
  }
}
