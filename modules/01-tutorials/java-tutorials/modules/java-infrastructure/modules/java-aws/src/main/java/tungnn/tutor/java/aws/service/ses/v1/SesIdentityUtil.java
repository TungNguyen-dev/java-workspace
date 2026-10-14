// SesIdentityUtil.java
package tungnn.tutor.java.aws.service.ses.v1;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

public final class SesIdentityUtil {

  private SesIdentityUtil() {}

  public static DeleteIdentityResponse deleteIdentity(
      SesClient client, DeleteIdentityRequest request) {
    return client.deleteIdentity(request);
  }

  public static GetIdentityDkimAttributesResponse getIdentityDkimAttributes(
      SesClient client, GetIdentityDkimAttributesRequest request) {
    return client.getIdentityDkimAttributes(request);
  }

  public static GetIdentityMailFromDomainAttributesResponse getIdentityMailFromDomainAttributes(
      SesClient client, GetIdentityMailFromDomainAttributesRequest request) {
    return client.getIdentityMailFromDomainAttributes(request);
  }

  public static GetIdentityNotificationAttributesResponse getIdentityNotificationAttributes(
      SesClient client, GetIdentityNotificationAttributesRequest request) {
    return client.getIdentityNotificationAttributes(request);
  }

  public static GetIdentityVerificationAttributesResponse getIdentityVerificationAttributes(
      SesClient client, GetIdentityVerificationAttributesRequest request) {
    return client.getIdentityVerificationAttributes(request);
  }

  public static List<String> listIdentities(SesClient client, ListIdentitiesRequest request) {
    return client.listIdentitiesPaginator(request).stream()
        .flatMap(r -> r.identities().stream())
        .collect(Collectors.toList());
  }

  public static SetIdentityDkimEnabledResponse setIdentityDkimEnabled(
      SesClient client, SetIdentityDkimEnabledRequest request) {
    return client.setIdentityDkimEnabled(request);
  }

  public static SetIdentityFeedbackForwardingEnabledResponse setIdentityFeedbackForwardingEnabled(
      SesClient client, SetIdentityFeedbackForwardingEnabledRequest request) {
    return client.setIdentityFeedbackForwardingEnabled(request);
  }

  public static SetIdentityHeadersInNotificationsEnabledResponse
      setIdentityHeadersInNotificationsEnabled(
          SesClient client, SetIdentityHeadersInNotificationsEnabledRequest request) {
    return client.setIdentityHeadersInNotificationsEnabled(request);
  }

  public static SetIdentityMailFromDomainResponse setIdentityMailFromDomain(
      SesClient client, SetIdentityMailFromDomainRequest request) {
    return client.setIdentityMailFromDomain(request);
  }

  public static SetIdentityNotificationTopicResponse setIdentityNotificationTopic(
      SesClient client, SetIdentityNotificationTopicRequest request) {
    return client.setIdentityNotificationTopic(request);
  }
}
