package tungnn.tutor.java.aws.service.kms;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.*;

public final class KmsKeyUtil {
  private KmsKeyUtil() {}

  public static CreateKeyResponse createKey(KmsClient client, CreateKeyRequest request) {
    return client.createKey(request);
  }

  public static DescribeKeyResponse describeKey(KmsClient client, DescribeKeyRequest request) {
    return client.describeKey(request);
  }

  public static List<KeyListEntry> listKeys(KmsClient client, ListKeysRequest request) {
    return client.listKeysPaginator(request).stream()
        .flatMap(r -> r.keys().stream())
        .collect(Collectors.toList());
  }

  public static EnableKeyResponse enableKey(KmsClient client, EnableKeyRequest request) {
    return client.enableKey(request);
  }

  public static DisableKeyResponse disableKey(KmsClient client, DisableKeyRequest request) {
    return client.disableKey(request);
  }

  public static ScheduleKeyDeletionResponse scheduleKeyDeletion(
      KmsClient client, ScheduleKeyDeletionRequest request) {
    return client.scheduleKeyDeletion(request);
  }

  public static CancelKeyDeletionResponse cancelKeyDeletion(
      KmsClient client, CancelKeyDeletionRequest request) {
    return client.cancelKeyDeletion(request);
  }

  public static EnableKeyRotationResponse enableKeyRotation(
      KmsClient client, EnableKeyRotationRequest request) {
    return client.enableKeyRotation(request);
  }

  public static DisableKeyRotationResponse disableKeyRotation(
      KmsClient client, DisableKeyRotationRequest request) {
    return client.disableKeyRotation(request);
  }

  public static GetKeyRotationStatusResponse getKeyRotationStatus(
      KmsClient client, GetKeyRotationStatusRequest request) {
    return client.getKeyRotationStatus(request);
  }

  public static RotateKeyOnDemandResponse rotateKeyOnDemand(
      KmsClient client, RotateKeyOnDemandRequest request) {
    return client.rotateKeyOnDemand(request);
  }

  public static List<RotationsListEntry> listKeyRotations(
      KmsClient client, ListKeyRotationsRequest request) {
    return client.listKeyRotationsPaginator(request).stream()
        .flatMap(r -> r.rotations().stream())
        .collect(Collectors.toList());
  }

  public static GetKeyPolicyResponse getKeyPolicy(KmsClient client, GetKeyPolicyRequest request) {
    return client.getKeyPolicy(request);
  }

  public static PutKeyPolicyResponse putKeyPolicy(KmsClient client, PutKeyPolicyRequest request) {
    return client.putKeyPolicy(request);
  }

  public static List<String> listKeyPolicies(KmsClient client, ListKeyPoliciesRequest request) {
    return client.listKeyPoliciesPaginator(request).stream()
        .flatMap(r -> r.policyNames().stream())
        .collect(Collectors.toList());
  }

  public static UpdateKeyDescriptionResponse updateKeyDescription(
      KmsClient client, UpdateKeyDescriptionRequest request) {
    return client.updateKeyDescription(request);
  }

  public static ReplicateKeyResponse replicateKey(KmsClient client, ReplicateKeyRequest request) {
    return client.replicateKey(request);
  }

  public static UpdatePrimaryRegionResponse updatePrimaryRegion(
      KmsClient client, UpdatePrimaryRegionRequest request) {
    return client.updatePrimaryRegion(request);
  }

  public static GetPublicKeyResponse getPublicKey(KmsClient client, GetPublicKeyRequest request) {
    return client.getPublicKey(request);
  }

  public static GetKeyLastUsageResponse getKeyLastUsage(
      KmsClient client, GetKeyLastUsageRequest request) {
    return client.getKeyLastUsage(request);
  }

  public static GetParametersForImportResponse getParametersForImport(
      KmsClient client, GetParametersForImportRequest request) {
    return client.getParametersForImport(request);
  }

  public static ImportKeyMaterialResponse importKeyMaterial(
      KmsClient client, ImportKeyMaterialRequest request) {
    return client.importKeyMaterial(request);
  }

  public static DeleteImportedKeyMaterialResponse deleteImportedKeyMaterial(
      KmsClient client, DeleteImportedKeyMaterialRequest request) {
    return client.deleteImportedKeyMaterial(request);
  }

  public static CreateCustomKeyStoreResponse createCustomKeyStore(
      KmsClient client, CreateCustomKeyStoreRequest request) {
    return client.createCustomKeyStore(request);
  }

  public static DeleteCustomKeyStoreResponse deleteCustomKeyStore(
      KmsClient client, DeleteCustomKeyStoreRequest request) {
    return client.deleteCustomKeyStore(request);
  }

  public static List<CustomKeyStoresListEntry> describeCustomKeyStores(
      KmsClient client, DescribeCustomKeyStoresRequest request) {
    return client.describeCustomKeyStoresPaginator(request).stream()
        .flatMap(r -> r.customKeyStores().stream())
        .collect(Collectors.toList());
  }

  public static ConnectCustomKeyStoreResponse connectCustomKeyStore(
      KmsClient client, ConnectCustomKeyStoreRequest request) {
    return client.connectCustomKeyStore(request);
  }

  public static DisconnectCustomKeyStoreResponse disconnectCustomKeyStore(
      KmsClient client, DisconnectCustomKeyStoreRequest request) {
    return client.disconnectCustomKeyStore(request);
  }

  public static UpdateCustomKeyStoreResponse updateCustomKeyStore(
      KmsClient client, UpdateCustomKeyStoreRequest request) {
    return client.updateCustomKeyStore(request);
  }

  public static TagResourceResponse tagResource(KmsClient client, TagResourceRequest request) {
    return client.tagResource(request);
  }

  public static UntagResourceResponse untagResource(
      KmsClient client, UntagResourceRequest request) {
    return client.untagResource(request);
  }

  public static List<Tag> listResourceTags(KmsClient client, ListResourceTagsRequest request) {
    return client.listResourceTagsPaginator(request).stream()
        .flatMap(r -> r.tags().stream())
        .collect(Collectors.toList());
  }
}
