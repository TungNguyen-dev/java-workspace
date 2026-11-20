package tungnn.tutor.java.aws.service.secretsmanager;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.*;

public final class SecretsManagerSecretUtil {
  private SecretsManagerSecretUtil() {}

  public static CreateSecretResponse createSecret(
      SecretsManagerClient client, CreateSecretRequest request) {
    return client.createSecret(request);
  }

  public static DeleteSecretResponse deleteSecret(
      SecretsManagerClient client, DeleteSecretRequest request) {
    return client.deleteSecret(request);
  }

  public static DescribeSecretResponse describeSecret(
      SecretsManagerClient client, DescribeSecretRequest request) {
    return client.describeSecret(request);
  }

  public static GetSecretValueResponse getSecretValue(
      SecretsManagerClient client, GetSecretValueRequest request) {
    return client.getSecretValue(request);
  }

  public static List<SecretValueEntry> batchGetSecretValue(
      SecretsManagerClient client, BatchGetSecretValueRequest request) {
    return client.batchGetSecretValuePaginator(request).stream()
        .flatMap(r -> r.secretValues().stream())
        .collect(Collectors.toList());
  }

  public static GetRandomPasswordResponse getRandomPassword(
      SecretsManagerClient client, GetRandomPasswordRequest request) {
    return client.getRandomPassword(request);
  }

  public static List<SecretListEntry> listSecrets(
      SecretsManagerClient client, ListSecretsRequest request) {
    return client.listSecretsPaginator(request).stream()
        .flatMap(r -> r.secretList().stream())
        .collect(Collectors.toList());
  }

  public static List<SecretVersionsListEntry> listSecretVersionIds(
      SecretsManagerClient client, ListSecretVersionIdsRequest request) {
    return client.listSecretVersionIdsPaginator(request).stream()
        .flatMap(r -> r.versions().stream())
        .collect(Collectors.toList());
  }

  public static PutSecretValueResponse putSecretValue(
      SecretsManagerClient client, PutSecretValueRequest request) {
    return client.putSecretValue(request);
  }

  public static RestoreSecretResponse restoreSecret(
      SecretsManagerClient client, RestoreSecretRequest request) {
    return client.restoreSecret(request);
  }

  public static UpdateSecretResponse updateSecret(
      SecretsManagerClient client, UpdateSecretRequest request) {
    return client.updateSecret(request);
  }

  public static UpdateSecretVersionStageResponse updateSecretVersionStage(
      SecretsManagerClient client, UpdateSecretVersionStageRequest request) {
    return client.updateSecretVersionStage(request);
  }

  public static RotateSecretResponse rotateSecret(
      SecretsManagerClient client, RotateSecretRequest request) {
    return client.rotateSecret(request);
  }

  public static CancelRotateSecretResponse cancelRotateSecret(
      SecretsManagerClient client, CancelRotateSecretRequest request) {
    return client.cancelRotateSecret(request);
  }

  public static ReplicateSecretToRegionsResponse replicateSecretToRegions(
      SecretsManagerClient client, ReplicateSecretToRegionsRequest request) {
    return client.replicateSecretToRegions(request);
  }

  public static RemoveRegionsFromReplicationResponse removeRegionsFromReplication(
      SecretsManagerClient client, RemoveRegionsFromReplicationRequest request) {
    return client.removeRegionsFromReplication(request);
  }

  public static StopReplicationToReplicaResponse stopReplicationToReplica(
      SecretsManagerClient client, StopReplicationToReplicaRequest request) {
    return client.stopReplicationToReplica(request);
  }

  public static TagResourceResponse tagResource(
      SecretsManagerClient client, TagResourceRequest request) {
    return client.tagResource(request);
  }

  public static UntagResourceResponse untagResource(
      SecretsManagerClient client, UntagResourceRequest request) {
    return client.untagResource(request);
  }
}
