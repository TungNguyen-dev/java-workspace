package tungnn.tutor.java.aws.service.cloudfront;

import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.model.*;

public final class CloudFrontCachePolicyUtil {
  private CloudFrontCachePolicyUtil() {}

  public static CreateCachePolicyResponse createCachePolicy(
      CloudFrontClient client, CreateCachePolicyRequest request) {
    return client.createCachePolicy(request);
  }

  public static CreateFieldLevelEncryptionConfigResponse createFieldLevelEncryptionConfig(
      CloudFrontClient client, CreateFieldLevelEncryptionConfigRequest request) {
    return client.createFieldLevelEncryptionConfig(request);
  }

  public static CreateFieldLevelEncryptionProfileResponse createFieldLevelEncryptionProfile(
      CloudFrontClient client, CreateFieldLevelEncryptionProfileRequest request) {
    return client.createFieldLevelEncryptionProfile(request);
  }

  public static CreateKeyGroupResponse createKeyGroup(
      CloudFrontClient client, CreateKeyGroupRequest request) {
    return client.createKeyGroup(request);
  }

  public static CreateOriginRequestPolicyResponse createOriginRequestPolicy(
      CloudFrontClient client, CreateOriginRequestPolicyRequest request) {
    return client.createOriginRequestPolicy(request);
  }

  public static CreatePublicKeyResponse createPublicKey(
      CloudFrontClient client, CreatePublicKeyRequest request) {
    return client.createPublicKey(request);
  }

  public static CreateRealtimeLogConfigResponse createRealtimeLogConfig(
      CloudFrontClient client, CreateRealtimeLogConfigRequest request) {
    return client.createRealtimeLogConfig(request);
  }

  public static CreateResponseHeadersPolicyResponse createResponseHeadersPolicy(
      CloudFrontClient client, CreateResponseHeadersPolicyRequest request) {
    return client.createResponseHeadersPolicy(request);
  }

  public static DeleteCachePolicyResponse deleteCachePolicy(
      CloudFrontClient client, DeleteCachePolicyRequest request) {
    return client.deleteCachePolicy(request);
  }

  public static DeleteFieldLevelEncryptionConfigResponse deleteFieldLevelEncryptionConfig(
      CloudFrontClient client, DeleteFieldLevelEncryptionConfigRequest request) {
    return client.deleteFieldLevelEncryptionConfig(request);
  }

  public static DeleteFieldLevelEncryptionProfileResponse deleteFieldLevelEncryptionProfile(
      CloudFrontClient client, DeleteFieldLevelEncryptionProfileRequest request) {
    return client.deleteFieldLevelEncryptionProfile(request);
  }

  public static DeleteKeyGroupResponse deleteKeyGroup(
      CloudFrontClient client, DeleteKeyGroupRequest request) {
    return client.deleteKeyGroup(request);
  }

  public static DeleteOriginRequestPolicyResponse deleteOriginRequestPolicy(
      CloudFrontClient client, DeleteOriginRequestPolicyRequest request) {
    return client.deleteOriginRequestPolicy(request);
  }

  public static DeletePublicKeyResponse deletePublicKey(
      CloudFrontClient client, DeletePublicKeyRequest request) {
    return client.deletePublicKey(request);
  }

  public static DeleteRealtimeLogConfigResponse deleteRealtimeLogConfig(
      CloudFrontClient client, DeleteRealtimeLogConfigRequest request) {
    return client.deleteRealtimeLogConfig(request);
  }

  public static DeleteResponseHeadersPolicyResponse deleteResponseHeadersPolicy(
      CloudFrontClient client, DeleteResponseHeadersPolicyRequest request) {
    return client.deleteResponseHeadersPolicy(request);
  }

  public static GetCachePolicyResponse getCachePolicy(
      CloudFrontClient client, GetCachePolicyRequest request) {
    return client.getCachePolicy(request);
  }

  public static GetCachePolicyConfigResponse getCachePolicyConfig(
      CloudFrontClient client, GetCachePolicyConfigRequest request) {
    return client.getCachePolicyConfig(request);
  }

  public static GetFieldLevelEncryptionResponse getFieldLevelEncryption(
      CloudFrontClient client, GetFieldLevelEncryptionRequest request) {
    return client.getFieldLevelEncryption(request);
  }

  public static GetFieldLevelEncryptionConfigResponse getFieldLevelEncryptionConfig(
      CloudFrontClient client, GetFieldLevelEncryptionConfigRequest request) {
    return client.getFieldLevelEncryptionConfig(request);
  }

  public static GetFieldLevelEncryptionProfileResponse getFieldLevelEncryptionProfile(
      CloudFrontClient client, GetFieldLevelEncryptionProfileRequest request) {
    return client.getFieldLevelEncryptionProfile(request);
  }

  public static GetFieldLevelEncryptionProfileConfigResponse getFieldLevelEncryptionProfileConfig(
      CloudFrontClient client, GetFieldLevelEncryptionProfileConfigRequest request) {
    return client.getFieldLevelEncryptionProfileConfig(request);
  }

  public static GetKeyGroupResponse getKeyGroup(
      CloudFrontClient client, GetKeyGroupRequest request) {
    return client.getKeyGroup(request);
  }

  public static GetKeyGroupConfigResponse getKeyGroupConfig(
      CloudFrontClient client, GetKeyGroupConfigRequest request) {
    return client.getKeyGroupConfig(request);
  }

  public static GetOriginRequestPolicyResponse getOriginRequestPolicy(
      CloudFrontClient client, GetOriginRequestPolicyRequest request) {
    return client.getOriginRequestPolicy(request);
  }

  public static GetOriginRequestPolicyConfigResponse getOriginRequestPolicyConfig(
      CloudFrontClient client, GetOriginRequestPolicyConfigRequest request) {
    return client.getOriginRequestPolicyConfig(request);
  }

  public static GetPublicKeyResponse getPublicKey(
      CloudFrontClient client, GetPublicKeyRequest request) {
    return client.getPublicKey(request);
  }

  public static GetPublicKeyConfigResponse getPublicKeyConfig(
      CloudFrontClient client, GetPublicKeyConfigRequest request) {
    return client.getPublicKeyConfig(request);
  }

  public static GetRealtimeLogConfigResponse getRealtimeLogConfig(
      CloudFrontClient client, GetRealtimeLogConfigRequest request) {
    return client.getRealtimeLogConfig(request);
  }

  public static GetResponseHeadersPolicyResponse getResponseHeadersPolicy(
      CloudFrontClient client, GetResponseHeadersPolicyRequest request) {
    return client.getResponseHeadersPolicy(request);
  }

  public static GetResponseHeadersPolicyConfigResponse getResponseHeadersPolicyConfig(
      CloudFrontClient client, GetResponseHeadersPolicyConfigRequest request) {
    return client.getResponseHeadersPolicyConfig(request);
  }

  public static ListCachePoliciesResponse listCachePolicies(
      CloudFrontClient client, ListCachePoliciesRequest request) {
    return client.listCachePolicies(request);
  }

  public static ListFieldLevelEncryptionConfigsResponse listFieldLevelEncryptionConfigs(
      CloudFrontClient client, ListFieldLevelEncryptionConfigsRequest request) {
    return client.listFieldLevelEncryptionConfigs(request);
  }

  public static ListFieldLevelEncryptionProfilesResponse listFieldLevelEncryptionProfiles(
      CloudFrontClient client, ListFieldLevelEncryptionProfilesRequest request) {
    return client.listFieldLevelEncryptionProfiles(request);
  }

  public static ListKeyGroupsResponse listKeyGroups(
      CloudFrontClient client, ListKeyGroupsRequest request) {
    return client.listKeyGroups(request);
  }

  public static ListOriginRequestPoliciesResponse listOriginRequestPolicies(
      CloudFrontClient client, ListOriginRequestPoliciesRequest request) {
    return client.listOriginRequestPolicies(request);
  }

  public static ListPublicKeysResponse listPublicKeys(
      CloudFrontClient client, ListPublicKeysRequest request) {
    return client.listPublicKeys(request);
  }

  public static ListRealtimeLogConfigsResponse listRealtimeLogConfigs(
      CloudFrontClient client, ListRealtimeLogConfigsRequest request) {
    return client.listRealtimeLogConfigs(request);
  }

  public static ListResponseHeadersPoliciesResponse listResponseHeadersPolicies(
      CloudFrontClient client, ListResponseHeadersPoliciesRequest request) {
    return client.listResponseHeadersPolicies(request);
  }

  public static UpdateCachePolicyResponse updateCachePolicy(
      CloudFrontClient client, UpdateCachePolicyRequest request) {
    return client.updateCachePolicy(request);
  }

  public static UpdateFieldLevelEncryptionConfigResponse updateFieldLevelEncryptionConfig(
      CloudFrontClient client, UpdateFieldLevelEncryptionConfigRequest request) {
    return client.updateFieldLevelEncryptionConfig(request);
  }

  public static UpdateFieldLevelEncryptionProfileResponse updateFieldLevelEncryptionProfile(
      CloudFrontClient client, UpdateFieldLevelEncryptionProfileRequest request) {
    return client.updateFieldLevelEncryptionProfile(request);
  }

  public static UpdateKeyGroupResponse updateKeyGroup(
      CloudFrontClient client, UpdateKeyGroupRequest request) {
    return client.updateKeyGroup(request);
  }

  public static UpdateOriginRequestPolicyResponse updateOriginRequestPolicy(
      CloudFrontClient client, UpdateOriginRequestPolicyRequest request) {
    return client.updateOriginRequestPolicy(request);
  }

  public static UpdatePublicKeyResponse updatePublicKey(
      CloudFrontClient client, UpdatePublicKeyRequest request) {
    return client.updatePublicKey(request);
  }

  public static UpdateRealtimeLogConfigResponse updateRealtimeLogConfig(
      CloudFrontClient client, UpdateRealtimeLogConfigRequest request) {
    return client.updateRealtimeLogConfig(request);
  }

  public static UpdateResponseHeadersPolicyResponse updateResponseHeadersPolicy(
      CloudFrontClient client, UpdateResponseHeadersPolicyRequest request) {
    return client.updateResponseHeadersPolicy(request);
  }
}
