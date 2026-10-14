// SesV2SuppressedDestinationUtil.java
package tungnn.tutor.java.aws.service.ses.v2;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

public final class SesV2SuppressedDestinationUtil {

  private SesV2SuppressedDestinationUtil() {}

  public static DeleteSuppressedDestinationResponse deleteSuppressedDestination(
      SesV2Client client, DeleteSuppressedDestinationRequest request) {
    return client.deleteSuppressedDestination(request);
  }

  public static GetSuppressedDestinationResponse getSuppressedDestination(
      SesV2Client client, GetSuppressedDestinationRequest request) {
    return client.getSuppressedDestination(request);
  }

  public static List<SuppressedDestinationSummary> listSuppressedDestinations(
      SesV2Client client, ListSuppressedDestinationsRequest request) {
    return client.listSuppressedDestinationsPaginator(request).stream()
        .flatMap(r -> r.suppressedDestinationSummaries().stream())
        .collect(Collectors.toList());
  }

  public static PutSuppressedDestinationResponse putSuppressedDestination(
      SesV2Client client, PutSuppressedDestinationRequest request) {
    return client.putSuppressedDestination(request);
  }
}
