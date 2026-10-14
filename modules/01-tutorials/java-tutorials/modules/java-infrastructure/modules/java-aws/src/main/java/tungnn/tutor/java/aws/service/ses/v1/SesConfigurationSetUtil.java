package tungnn.tutor.java.aws.service.ses.v1;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

public final class SesConfigurationSetUtil {

  private SesConfigurationSetUtil() {}

  public static CreateConfigurationSetResponse createConfigurationSet(
      SesClient client, CreateConfigurationSetRequest request) {
    return client.createConfigurationSet(request);
  }

  public static CreateConfigurationSetEventDestinationResponse
      createConfigurationSetEventDestination(
          SesClient client, CreateConfigurationSetEventDestinationRequest request) {
    return client.createConfigurationSetEventDestination(request);
  }

  public static CreateConfigurationSetTrackingOptionsResponse createConfigurationSetTrackingOptions(
      SesClient client, CreateConfigurationSetTrackingOptionsRequest request) {
    return client.createConfigurationSetTrackingOptions(request);
  }

  public static DeleteConfigurationSetResponse deleteConfigurationSet(
      SesClient client, DeleteConfigurationSetRequest request) {
    return client.deleteConfigurationSet(request);
  }

  public static DeleteConfigurationSetEventDestinationResponse
      deleteConfigurationSetEventDestination(
          SesClient client, DeleteConfigurationSetEventDestinationRequest request) {
    return client.deleteConfigurationSetEventDestination(request);
  }

  public static DeleteConfigurationSetTrackingOptionsResponse deleteConfigurationSetTrackingOptions(
      SesClient client, DeleteConfigurationSetTrackingOptionsRequest request) {
    return client.deleteConfigurationSetTrackingOptions(request);
  }

  public static DescribeConfigurationSetResponse describeConfigurationSet(
      SesClient client, DescribeConfigurationSetRequest request) {
    return client.describeConfigurationSet(request);
  }

  public static List<ConfigurationSet> listConfigurationSets(
      SesClient client, ListConfigurationSetsRequest request) {
    return Stream.iterate(
            client.listConfigurationSets(request),
            Objects::nonNull,
            prev ->
                prev.nextToken() == null
                    ? null
                    : client.listConfigurationSets(
                        request.toBuilder().nextToken(prev.nextToken()).build()))
        .flatMap(r -> r.configurationSets().stream())
        .collect(Collectors.toList());
  }

  public static PutConfigurationSetDeliveryOptionsResponse putConfigurationSetDeliveryOptions(
      SesClient client, PutConfigurationSetDeliveryOptionsRequest request) {
    return client.putConfigurationSetDeliveryOptions(request);
  }

  public static UpdateConfigurationSetEventDestinationResponse
      updateConfigurationSetEventDestination(
          SesClient client, UpdateConfigurationSetEventDestinationRequest request) {
    return client.updateConfigurationSetEventDestination(request);
  }

  public static UpdateConfigurationSetReputationMetricsEnabledResponse
      updateConfigurationSetReputationMetricsEnabled(
          SesClient client, UpdateConfigurationSetReputationMetricsEnabledRequest request) {
    return client.updateConfigurationSetReputationMetricsEnabled(request);
  }

  public static UpdateConfigurationSetSendingEnabledResponse updateConfigurationSetSendingEnabled(
      SesClient client, UpdateConfigurationSetSendingEnabledRequest request) {
    return client.updateConfigurationSetSendingEnabled(request);
  }

  public static UpdateConfigurationSetTrackingOptionsResponse updateConfigurationSetTrackingOptions(
      SesClient client, UpdateConfigurationSetTrackingOptionsRequest request) {
    return client.updateConfigurationSetTrackingOptions(request);
  }
}
