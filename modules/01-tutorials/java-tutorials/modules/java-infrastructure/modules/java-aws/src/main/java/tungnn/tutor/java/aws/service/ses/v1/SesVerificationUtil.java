// SesVerificationUtil.java
package tungnn.tutor.java.aws.service.ses.v1;

import java.util.List;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

public final class SesVerificationUtil {

  private SesVerificationUtil() {}

  public static VerifyDomainDkimResponse verifyDomainDkim(
      SesClient client, VerifyDomainDkimRequest request) {
    return client.verifyDomainDkim(request);
  }

  public static VerifyDomainIdentityResponse verifyDomainIdentity(
      SesClient client, VerifyDomainIdentityRequest request) {
    return client.verifyDomainIdentity(request);
  }

  public static VerifyEmailAddressResponse verifyEmailAddress(
      SesClient client, VerifyEmailAddressRequest request) {
    return client.verifyEmailAddress(request);
  }

  public static VerifyEmailIdentityResponse verifyEmailIdentity(
      SesClient client, VerifyEmailIdentityRequest request) {
    return client.verifyEmailIdentity(request);
  }

  public static DeleteVerifiedEmailAddressResponse deleteVerifiedEmailAddress(
      SesClient client, DeleteVerifiedEmailAddressRequest request) {
    return client.deleteVerifiedEmailAddress(request);
  }

  public static List<String> listVerifiedEmailAddresses(
      SesClient client, ListVerifiedEmailAddressesRequest request) {
    return List.copyOf(client.listVerifiedEmailAddresses(request).verifiedEmailAddresses());
  }
}
