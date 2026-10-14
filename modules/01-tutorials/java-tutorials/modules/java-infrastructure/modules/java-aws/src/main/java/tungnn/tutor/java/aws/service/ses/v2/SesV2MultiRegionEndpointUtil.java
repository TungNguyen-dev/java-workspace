// SesV2MultiRegionEndpointUtil.java
package tungnn.tutor.java.aws.service.ses.v2;

import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

public final class SesV2MultiRegionEndpointUtil {

  private SesV2MultiRegionEndpointUtil() {}

  public static CreateMultiRegionEndpointResponse createMultiRegionEndpoint(
      SesV2Client client, CreateMultiRegionEndpointRequest request) {
    return client.createMultiRegionEndpoint(request);
  }

  public static DeleteMultiRegionEndpointResponse deleteMultiRegionEndpoint(
      SesV2Client client, DeleteMultiRegionEndpointRequest request) {
    return client.deleteMultiRegionEndpoint(request);
  }

  public static GetMultiRegionEndpointResponse getMultiRegionEndpoint(
      SesV2Client client, GetMultiRegionEndpointRequest request) {
    return client.getMultiRegionEndpoint(request);
  }

  public static ListMultiRegionEndpointsResponse listMultiRegionEndpoints(
      SesV2Client client, ListMultiRegionEndpointsRequest request) {
    return client.listMultiRegionEndpoints(request);
  }
}
