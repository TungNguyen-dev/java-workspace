package tungnn.tutor.java.aws.service.cognitoidentityprovider;

import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

public final class CognitoMfaUtil {
  private CognitoMfaUtil() {}

  public static AssociateSoftwareTokenResponse associateSoftwareToken(
      CognitoIdentityProviderClient client, AssociateSoftwareTokenRequest request) {
    return client.associateSoftwareToken(request);
  }

  public static CompleteWebAuthnRegistrationResponse completeWebAuthnRegistration(
      CognitoIdentityProviderClient client, CompleteWebAuthnRegistrationRequest request) {
    return client.completeWebAuthnRegistration(request);
  }

  public static DeleteWebAuthnCredentialResponse deleteWebAuthnCredential(
      CognitoIdentityProviderClient client, DeleteWebAuthnCredentialRequest request) {
    return client.deleteWebAuthnCredential(request);
  }

  public static GetUserAuthFactorsResponse getUserAuthFactors(
      CognitoIdentityProviderClient client, GetUserAuthFactorsRequest request) {
    return client.getUserAuthFactors(request);
  }

  public static GetUserPoolMfaConfigResponse getUserPoolMfaConfig(
      CognitoIdentityProviderClient client, GetUserPoolMfaConfigRequest request) {
    return client.getUserPoolMfaConfig(request);
  }

  public static ListWebAuthnCredentialsResponse listWebAuthnCredentials(
      CognitoIdentityProviderClient client, ListWebAuthnCredentialsRequest request) {
    return client.listWebAuthnCredentials(request);
  }

  public static SetUserPoolMfaConfigResponse setUserPoolMfaConfig(
      CognitoIdentityProviderClient client, SetUserPoolMfaConfigRequest request) {
    return client.setUserPoolMfaConfig(request);
  }

  public static StartWebAuthnRegistrationResponse startWebAuthnRegistration(
      CognitoIdentityProviderClient client, StartWebAuthnRegistrationRequest request) {
    return client.startWebAuthnRegistration(request);
  }

  public static VerifySoftwareTokenResponse verifySoftwareToken(
      CognitoIdentityProviderClient client, VerifySoftwareTokenRequest request) {
    return client.verifySoftwareToken(request);
  }
}
