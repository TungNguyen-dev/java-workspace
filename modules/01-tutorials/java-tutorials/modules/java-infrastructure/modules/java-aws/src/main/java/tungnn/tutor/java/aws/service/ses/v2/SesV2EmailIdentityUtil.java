// SesV2EmailIdentityUtil.java
package tungnn.tutor.java.aws.service.ses.v2;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

public final class SesV2EmailIdentityUtil {

  private SesV2EmailIdentityUtil() {}

  public static CreateEmailIdentityResponse createEmailIdentity(
      SesV2Client client, CreateEmailIdentityRequest request) {
    return client.createEmailIdentity(request);
  }

  public static CreateEmailIdentityPolicyResponse createEmailIdentityPolicy(
      SesV2Client client, CreateEmailIdentityPolicyRequest request) {
    return client.createEmailIdentityPolicy(request);
  }

  public static DeleteEmailIdentityResponse deleteEmailIdentity(
      SesV2Client client, DeleteEmailIdentityRequest request) {
    return client.deleteEmailIdentity(request);
  }

  public static DeleteEmailIdentityPolicyResponse deleteEmailIdentityPolicy(
      SesV2Client client, DeleteEmailIdentityPolicyRequest request) {
    return client.deleteEmailIdentityPolicy(request);
  }

  public static GetEmailIdentityResponse getEmailIdentity(
      SesV2Client client, GetEmailIdentityRequest request) {
    return client.getEmailIdentity(request);
  }

  public static GetEmailIdentityPoliciesResponse getEmailIdentityPolicies(
      SesV2Client client, GetEmailIdentityPoliciesRequest request) {
    return client.getEmailIdentityPolicies(request);
  }

  public static List<IdentityInfo> listEmailIdentities(
      SesV2Client client, ListEmailIdentitiesRequest request) {
    return client.listEmailIdentitiesPaginator(request).stream()
        .flatMap(r -> r.emailIdentities().stream())
        .collect(Collectors.toList());
  }

  public static PutEmailIdentityConfigurationSetAttributesResponse
      putEmailIdentityConfigurationSetAttributes(
          SesV2Client client, PutEmailIdentityConfigurationSetAttributesRequest request) {
    return client.putEmailIdentityConfigurationSetAttributes(request);
  }

  public static PutEmailIdentityDkimAttributesResponse putEmailIdentityDkimAttributes(
      SesV2Client client, PutEmailIdentityDkimAttributesRequest request) {
    return client.putEmailIdentityDkimAttributes(request);
  }

  public static PutEmailIdentityDkimSigningAttributesResponse putEmailIdentityDkimSigningAttributes(
      SesV2Client client, PutEmailIdentityDkimSigningAttributesRequest request) {
    return client.putEmailIdentityDkimSigningAttributes(request);
  }

  public static PutEmailIdentityFeedbackAttributesResponse putEmailIdentityFeedbackAttributes(
      SesV2Client client, PutEmailIdentityFeedbackAttributesRequest request) {
    return client.putEmailIdentityFeedbackAttributes(request);
  }

  public static PutEmailIdentityMailFromAttributesResponse putEmailIdentityMailFromAttributes(
      SesV2Client client, PutEmailIdentityMailFromAttributesRequest request) {
    return client.putEmailIdentityMailFromAttributes(request);
  }

  public static UpdateEmailIdentityPolicyResponse updateEmailIdentityPolicy(
      SesV2Client client, UpdateEmailIdentityPolicyRequest request) {
    return client.updateEmailIdentityPolicy(request);
  }
}
