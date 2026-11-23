package tungnn.tutor.java.aws.service.cognitoidentity;

import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentity.model.*;

public final class CognitoIdentityCredentialUtil {
  private CognitoIdentityCredentialUtil() {}

  public static DeleteIdentitiesResponse deleteIdentities(
      CognitoIdentityClient client, DeleteIdentitiesRequest request) {
    return client.deleteIdentities(request);
  }

  public static DescribeIdentityResponse describeIdentity(
      CognitoIdentityClient client, DescribeIdentityRequest request) {
    return client.describeIdentity(request);
  }

  public static GetCredentialsForIdentityResponse getCredentialsForIdentity(
      CognitoIdentityClient client, GetCredentialsForIdentityRequest request) {
    return client.getCredentialsForIdentity(request);
  }

  public static GetIdResponse getId(CognitoIdentityClient client, GetIdRequest request) {
    return client.getId(request);
  }

  public static GetIdentityPoolRolesResponse getIdentityPoolRoles(
      CognitoIdentityClient client, GetIdentityPoolRolesRequest request) {
    return client.getIdentityPoolRoles(request);
  }

  public static GetOpenIdTokenResponse getOpenIdToken(
      CognitoIdentityClient client, GetOpenIdTokenRequest request) {
    return client.getOpenIdToken(request);
  }

  public static GetOpenIdTokenForDeveloperIdentityResponse getOpenIdTokenForDeveloperIdentity(
      CognitoIdentityClient client, GetOpenIdTokenForDeveloperIdentityRequest request) {
    return client.getOpenIdTokenForDeveloperIdentity(request);
  }

  public static GetPrincipalTagAttributeMapResponse getPrincipalTagAttributeMap(
      CognitoIdentityClient client, GetPrincipalTagAttributeMapRequest request) {
    return client.getPrincipalTagAttributeMap(request);
  }

  public static ListIdentitiesResponse listIdentities(
      CognitoIdentityClient client, ListIdentitiesRequest request) {
    return client.listIdentities(request);
  }

  public static LookupDeveloperIdentityResponse lookupDeveloperIdentity(
      CognitoIdentityClient client, LookupDeveloperIdentityRequest request) {
    return client.lookupDeveloperIdentity(request);
  }

  public static MergeDeveloperIdentitiesResponse mergeDeveloperIdentities(
      CognitoIdentityClient client, MergeDeveloperIdentitiesRequest request) {
    return client.mergeDeveloperIdentities(request);
  }

  public static SetIdentityPoolRolesResponse setIdentityPoolRoles(
      CognitoIdentityClient client, SetIdentityPoolRolesRequest request) {
    return client.setIdentityPoolRoles(request);
  }

  public static SetPrincipalTagAttributeMapResponse setPrincipalTagAttributeMap(
      CognitoIdentityClient client, SetPrincipalTagAttributeMapRequest request) {
    return client.setPrincipalTagAttributeMap(request);
  }

  public static UnlinkDeveloperIdentityResponse unlinkDeveloperIdentity(
      CognitoIdentityClient client, UnlinkDeveloperIdentityRequest request) {
    return client.unlinkDeveloperIdentity(request);
  }

  public static UnlinkIdentityResponse unlinkIdentity(
      CognitoIdentityClient client, UnlinkIdentityRequest request) {
    return client.unlinkIdentity(request);
  }
}
