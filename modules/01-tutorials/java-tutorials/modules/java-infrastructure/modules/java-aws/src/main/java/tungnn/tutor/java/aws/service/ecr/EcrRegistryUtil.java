package tungnn.tutor.java.aws.service.ecr;

import software.amazon.awssdk.services.ecr.EcrClient;
import software.amazon.awssdk.services.ecr.model.*;

public final class EcrRegistryUtil {

  private EcrRegistryUtil() {}

  public static DescribeRegistryResponse describeRegistry(
      EcrClient ecrClient, DescribeRegistryRequest request) {
    return ecrClient.describeRegistry(request);
  }

  public static GetAuthorizationTokenResponse getAuthorizationToken(
      EcrClient ecrClient, GetAuthorizationTokenRequest request) {
    return ecrClient.getAuthorizationToken(request);
  }

  public static GetRegistryPolicyResponse getRegistryPolicy(
      EcrClient ecrClient, GetRegistryPolicyRequest request) {
    return ecrClient.getRegistryPolicy(request);
  }

  public static PutRegistryPolicyResponse putRegistryPolicy(
      EcrClient ecrClient, PutRegistryPolicyRequest request) {
    return ecrClient.putRegistryPolicy(request);
  }

  public static DeleteRegistryPolicyResponse deleteRegistryPolicy(
      EcrClient ecrClient, DeleteRegistryPolicyRequest request) {
    return ecrClient.deleteRegistryPolicy(request);
  }

  public static GetRegistryScanningConfigurationResponse getRegistryScanningConfiguration(
      EcrClient ecrClient, GetRegistryScanningConfigurationRequest request) {
    return ecrClient.getRegistryScanningConfiguration(request);
  }

  public static PutRegistryScanningConfigurationResponse putRegistryScanningConfiguration(
      EcrClient ecrClient, PutRegistryScanningConfigurationRequest request) {
    return ecrClient.putRegistryScanningConfiguration(request);
  }

  public static BatchGetRepositoryScanningConfigurationResponse
      batchGetRepositoryScanningConfiguration(
          EcrClient ecrClient, BatchGetRepositoryScanningConfigurationRequest request) {
    return ecrClient.batchGetRepositoryScanningConfiguration(request);
  }

  public static PutReplicationConfigurationResponse putReplicationConfiguration(
      EcrClient ecrClient, PutReplicationConfigurationRequest request) {
    return ecrClient.putReplicationConfiguration(request);
  }
}
