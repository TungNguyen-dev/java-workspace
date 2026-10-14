// SesV2DedicatedIpUtil.java
package tungnn.tutor.java.aws.service.ses.v2;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

public final class SesV2DedicatedIpUtil {

  private SesV2DedicatedIpUtil() {}

  public static CreateDedicatedIpPoolResponse createDedicatedIpPool(
      SesV2Client client, CreateDedicatedIpPoolRequest request) {
    return client.createDedicatedIpPool(request);
  }

  public static DeleteDedicatedIpPoolResponse deleteDedicatedIpPool(
      SesV2Client client, DeleteDedicatedIpPoolRequest request) {
    return client.deleteDedicatedIpPool(request);
  }

  public static GetDedicatedIpResponse getDedicatedIp(
      SesV2Client client, GetDedicatedIpRequest request) {
    return client.getDedicatedIp(request);
  }

  public static GetDedicatedIpPoolResponse getDedicatedIpPool(
      SesV2Client client, GetDedicatedIpPoolRequest request) {
    return client.getDedicatedIpPool(request);
  }

  public static List<DedicatedIp> getDedicatedIps(
      SesV2Client client, GetDedicatedIpsRequest request) {
    return client.getDedicatedIpsPaginator(request).stream()
        .flatMap(r -> r.dedicatedIps().stream())
        .collect(Collectors.toList());
  }

  public static List<String> listDedicatedIpPools(
      SesV2Client client, ListDedicatedIpPoolsRequest request) {
    return client.listDedicatedIpPoolsPaginator(request).stream()
        .flatMap(r -> r.dedicatedIpPools().stream())
        .collect(Collectors.toList());
  }

  public static PutDedicatedIpInPoolResponse putDedicatedIpInPool(
      SesV2Client client, PutDedicatedIpInPoolRequest request) {
    return client.putDedicatedIpInPool(request);
  }

  public static PutDedicatedIpPoolScalingAttributesResponse putDedicatedIpPoolScalingAttributes(
      SesV2Client client, PutDedicatedIpPoolScalingAttributesRequest request) {
    return client.putDedicatedIpPoolScalingAttributes(request);
  }

  public static PutDedicatedIpWarmupAttributesResponse putDedicatedIpWarmupAttributes(
      SesV2Client client, PutDedicatedIpWarmupAttributesRequest request) {
    return client.putDedicatedIpWarmupAttributes(request);
  }
}
