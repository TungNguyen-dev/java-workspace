package tungnn.tutor.java.aws.service.eventbridge;

import java.util.List;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.*;

public final class PartnerEventSourceUtil {

  private PartnerEventSourceUtil() {}

  public static CreatePartnerEventSourceResponse createPartnerEventSource(
      EventBridgeClient client, CreatePartnerEventSourceRequest request) {
    return client.createPartnerEventSource(request);
  }

  public static DeletePartnerEventSourceResponse deletePartnerEventSource(
      EventBridgeClient client, DeletePartnerEventSourceRequest request) {
    return client.deletePartnerEventSource(request);
  }

  public static DescribePartnerEventSourceResponse describePartnerEventSource(
      EventBridgeClient client, DescribePartnerEventSourceRequest request) {
    return client.describePartnerEventSource(request);
  }

  public static PutPartnerEventsResponse putPartnerEvents(
      EventBridgeClient client, PutPartnerEventsRequest request) {
    return client.putPartnerEvents(request);
  }

  public static List<PartnerEventSource> listPartnerEventSources(
      EventBridgeClient client, ListPartnerEventSourcesRequest request) {
    return List.copyOf(client.listPartnerEventSources(request).partnerEventSources());
  }

  public static List<PartnerEventSourceAccount> listPartnerEventSourceAccounts(
      EventBridgeClient client, ListPartnerEventSourceAccountsRequest request) {
    return List.copyOf(client.listPartnerEventSourceAccounts(request).partnerEventSourceAccounts());
  }
}
