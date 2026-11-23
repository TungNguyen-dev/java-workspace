package tungnn.tutor.java.aws.service.cognitoidentityprovider;

import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

public final class CognitoDomainUtil {
  private CognitoDomainUtil() {}

  public static CreateUserPoolDomainResponse createUserPoolDomain(
      CognitoIdentityProviderClient client, CreateUserPoolDomainRequest request) {
    return client.createUserPoolDomain(request);
  }

  public static DeleteUserPoolDomainResponse deleteUserPoolDomain(
      CognitoIdentityProviderClient client, DeleteUserPoolDomainRequest request) {
    return client.deleteUserPoolDomain(request);
  }

  public static DescribeUserPoolDomainResponse describeUserPoolDomain(
      CognitoIdentityProviderClient client, DescribeUserPoolDomainRequest request) {
    return client.describeUserPoolDomain(request);
  }

  public static UpdateUserPoolDomainResponse updateUserPoolDomain(
      CognitoIdentityProviderClient client, UpdateUserPoolDomainRequest request) {
    return client.updateUserPoolDomain(request);
  }
}
