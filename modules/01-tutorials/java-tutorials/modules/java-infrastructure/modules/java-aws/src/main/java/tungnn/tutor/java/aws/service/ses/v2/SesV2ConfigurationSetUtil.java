// SesV2ConfigurationSetUtil.java
package tungnn.tutor.java.aws.service.ses.v2;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

public final class SesV2ConfigurationSetUtil {

  private SesV2ConfigurationSetUtil() {}

  public static CreateConfigurationSetResponse createConfigurationSet(
      SesV2Client client, CreateConfigurationSetRequest request) {
    return client.createConfigurationSet(request);
  }

  public static CreateConfigurationSetEventDestinationResponse
      createConfigurationSetEventDestination(
          SesV2Client client, CreateConfigurationSetEventDestinationRequest request) {
    return client.createConfigurationSetEventDestination(request);
  }

  public static DeleteConfigurationSetResponse deleteConfigurationSet(
      SesV2Client client, DeleteConfigurationSetRequest request) {
    return client.deleteConfigurationSet(request);
  }

  public static DeleteConfigurationSetEventDestinationResponse
      deleteConfigurationSetEventDestination(
          SesV2Client client, DeleteConfigurationSetEventDestinationRequest request) {
    return client.deleteConfigurationSetEventDestination(request);
  }

  public static GetConfigurationSetResponse getConfigurationSet(
      SesV2Client client, GetConfigurationSetRequest request) {
    return client.getConfigurationSet(request);
  }

  public static GetConfigurationSetEventDestinationsResponse getConfigurationSetEventDestinations(
      SesV2Client client, GetConfigurationSetEventDestinationsRequest request) {
    return client.getConfigurationSetEventDestinations(request);
  }

  public static List<String> listConfigurationSets(
      SesV2Client client, ListConfigurationSetsRequest request) {
    return client.listConfigurationSetsPaginator(request).stream()
        .flatMap(r -> r.configurationSets().stream())
        .collect(Collectors.toList());
  }

  public static PutConfigurationSetArchivingOptionsResponse putConfigurationSetArchivingOptions(
      SesV2Client client, PutConfigurationSetArchivingOptionsRequest request) {
    return client.putConfigurationSetArchivingOptions(request);
  }

  public static PutConfigurationSetDeliveryOptionsResponse putConfigurationSetDeliveryOptions(
      SesV2Client client, PutConfigurationSetDeliveryOptionsRequest request) {
    return client.putConfigurationSetDeliveryOptions(request);
  }

  public static PutConfigurationSetReputationOptionsResponse putConfigurationSetReputationOptions(
      SesV2Client client, PutConfigurationSetReputationOptionsRequest request) {
    return client.putConfigurationSetReputationOptions(request);
  }

  public static PutConfigurationSetSendingOptionsResponse putConfigurationSetSendingOptions(
      SesV2Client client, PutConfigurationSetSendingOptionsRequest request) {
    return client.putConfigurationSetSendingOptions(request);
  }

  public static PutConfigurationSetSuppressionOptionsResponse putConfigurationSetSuppressionOptions(
      SesV2Client client, PutConfigurationSetSuppressionOptionsRequest request) {
    return client.putConfigurationSetSuppressionOptions(request);
  }

  public static PutConfigurationSetTrackingOptionsResponse putConfigurationSetTrackingOptions(
      SesV2Client client, PutConfigurationSetTrackingOptionsRequest request) {
    return client.putConfigurationSetTrackingOptions(request);
  }

  public static PutConfigurationSetVdmOptionsResponse putConfigurationSetVdmOptions(
      SesV2Client client, PutConfigurationSetVdmOptionsRequest request) {
    return client.putConfigurationSetVdmOptions(request);
  }

  public static UpdateConfigurationSetEventDestinationResponse
      updateConfigurationSetEventDestination(
          SesV2Client client, UpdateConfigurationSetEventDestinationRequest request) {
    return client.updateConfigurationSetEventDestination(request);
  }
}
