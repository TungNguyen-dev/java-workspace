package tungnn.tutor.java.aws.service.cloudfront;

import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.model.*;

public final class CloudFrontDistributionUtils {
    private CloudFrontDistributionUtils() {}

    public static AssociateAliasResponse associateAlias(CloudFrontClient client, AssociateAliasRequest request) {
        return client.associateAlias(request);
    }

    public static AssociateDistributionTenantWebAclResponse associateDistributionTenantWebACL(
            CloudFrontClient client, AssociateDistributionTenantWebAclRequest request) {
        return client.associateDistributionTenantWebACL(request);
    }

    public static AssociateDistributionWebAclResponse associateDistributionWebACL(
            CloudFrontClient client, AssociateDistributionWebAclRequest request) {
        return client.associateDistributionWebACL(request);
    }

    public static CopyDistributionResponse copyDistribution(CloudFrontClient client, CopyDistributionRequest request) {
        return client.copyDistribution(request);
    }

    public static CreateAnycastIpListResponse createAnycastIpList(
            CloudFrontClient client, CreateAnycastIpListRequest request) {
        return client.createAnycastIpList(request);
    }

    public static CreateConnectionGroupResponse createConnectionGroup(
            CloudFrontClient client, CreateConnectionGroupRequest request) {
        return client.createConnectionGroup(request);
    }

    public static CreateContinuousDeploymentPolicyResponse createContinuousDeploymentPolicy(
            CloudFrontClient client, CreateContinuousDeploymentPolicyRequest request) {
        return client.createContinuousDeploymentPolicy(request);
    }

    public static CreateDistributionResponse createDistribution(
            CloudFrontClient client, CreateDistributionRequest request) {
        return client.createDistribution(request);
    }

    public static CreateDistributionTenantResponse createDistributionTenant(
            CloudFrontClient client, CreateDistributionTenantRequest request) {
        return client.createDistributionTenant(request);
    }

    public static CreateDistributionWithTagsResponse createDistributionWithTags(
            CloudFrontClient client, CreateDistributionWithTagsRequest request) {
        return client.createDistributionWithTags(request);
    }

    public static CreateMonitoringSubscriptionResponse createMonitoringSubscription(
            CloudFrontClient client, CreateMonitoringSubscriptionRequest request) {
        return client.createMonitoringSubscription(request);
    }

    public static CreateStreamingDistributionResponse createStreamingDistribution(
            CloudFrontClient client, CreateStreamingDistributionRequest request) {
        return client.createStreamingDistribution(request);
    }

    public static CreateStreamingDistributionWithTagsResponse createStreamingDistributionWithTags(
            CloudFrontClient client, CreateStreamingDistributionWithTagsRequest request) {
        return client.createStreamingDistributionWithTags(request);
    }

    public static CreateTrustStoreResponse createTrustStore(CloudFrontClient client, CreateTrustStoreRequest request) {
        return client.createTrustStore(request);
    }

    public static CreateVpcOriginResponse createVpcOrigin(CloudFrontClient client, CreateVpcOriginRequest request) {
        return client.createVpcOrigin(request);
    }

    public static DeleteAnycastIpListResponse deleteAnycastIpList(
            CloudFrontClient client, DeleteAnycastIpListRequest request) {
        return client.deleteAnycastIpList(request);
    }

    public static DeleteConnectionGroupResponse deleteConnectionGroup(
            CloudFrontClient client, DeleteConnectionGroupRequest request) {
        return client.deleteConnectionGroup(request);
    }

    public static DeleteContinuousDeploymentPolicyResponse deleteContinuousDeploymentPolicy(
            CloudFrontClient client, DeleteContinuousDeploymentPolicyRequest request) {
        return client.deleteContinuousDeploymentPolicy(request);
    }

    public static DeleteDistributionResponse deleteDistribution(
            CloudFrontClient client, DeleteDistributionRequest request) {
        return client.deleteDistribution(request);
    }

    public static DeleteDistributionTenantResponse deleteDistributionTenant(
            CloudFrontClient client, DeleteDistributionTenantRequest request) {
        return client.deleteDistributionTenant(request);
    }

    public static DeleteMonitoringSubscriptionResponse deleteMonitoringSubscription(
            CloudFrontClient client, DeleteMonitoringSubscriptionRequest request) {
        return client.deleteMonitoringSubscription(request);
    }

    public static DeleteResourcePolicyResponse deleteResourcePolicy(
            CloudFrontClient client, DeleteResourcePolicyRequest request) {
        return client.deleteResourcePolicy(request);
    }

    public static DeleteStreamingDistributionResponse deleteStreamingDistribution(
            CloudFrontClient client, DeleteStreamingDistributionRequest request) {
        return client.deleteStreamingDistribution(request);
    }

    public static DeleteTrustStoreResponse deleteTrustStore(CloudFrontClient client, DeleteTrustStoreRequest request) {
        return client.deleteTrustStore(request);
    }

    public static DeleteVpcOriginResponse deleteVpcOrigin(CloudFrontClient client, DeleteVpcOriginRequest request) {
        return client.deleteVpcOrigin(request);
    }

    public static DisassociateDistributionTenantWebAclResponse disassociateDistributionTenantWebACL(
            CloudFrontClient client, DisassociateDistributionTenantWebAclRequest request) {
        return client.disassociateDistributionTenantWebACL(request);
    }

    public static DisassociateDistributionWebAclResponse disassociateDistributionWebACL(
            CloudFrontClient client, DisassociateDistributionWebAclRequest request) {
        return client.disassociateDistributionWebACL(request);
    }

    public static GetAnycastIpListResponse getAnycastIpList(CloudFrontClient client, GetAnycastIpListRequest request) {
        return client.getAnycastIpList(request);
    }

    public static GetConnectionGroupResponse getConnectionGroup(
            CloudFrontClient client, GetConnectionGroupRequest request) {
        return client.getConnectionGroup(request);
    }

    public static GetConnectionGroupByRoutingEndpointResponse getConnectionGroupByRoutingEndpoint(
            CloudFrontClient client, GetConnectionGroupByRoutingEndpointRequest request) {
        return client.getConnectionGroupByRoutingEndpoint(request);
    }

    public static GetContinuousDeploymentPolicyResponse getContinuousDeploymentPolicy(
            CloudFrontClient client, GetContinuousDeploymentPolicyRequest request) {
        return client.getContinuousDeploymentPolicy(request);
    }

    public static GetContinuousDeploymentPolicyConfigResponse getContinuousDeploymentPolicyConfig(
            CloudFrontClient client, GetContinuousDeploymentPolicyConfigRequest request) {
        return client.getContinuousDeploymentPolicyConfig(request);
    }

    public static GetDistributionResponse getDistribution(CloudFrontClient client, GetDistributionRequest request) {
        return client.getDistribution(request);
    }

    public static GetDistributionConfigResponse getDistributionConfig(
            CloudFrontClient client, GetDistributionConfigRequest request) {
        return client.getDistributionConfig(request);
    }

    public static GetDistributionTenantResponse getDistributionTenant(
            CloudFrontClient client, GetDistributionTenantRequest request) {
        return client.getDistributionTenant(request);
    }

    public static GetDistributionTenantByDomainResponse getDistributionTenantByDomain(
            CloudFrontClient client, GetDistributionTenantByDomainRequest request) {
        return client.getDistributionTenantByDomain(request);
    }

    public static GetManagedCertificateDetailsResponse getManagedCertificateDetails(
            CloudFrontClient client, GetManagedCertificateDetailsRequest request) {
        return client.getManagedCertificateDetails(request);
    }

    public static GetMonitoringSubscriptionResponse getMonitoringSubscription(
            CloudFrontClient client, GetMonitoringSubscriptionRequest request) {
        return client.getMonitoringSubscription(request);
    }

    public static GetResourcePolicyResponse getResourcePolicy(
            CloudFrontClient client, GetResourcePolicyRequest request) {
        return client.getResourcePolicy(request);
    }

    public static GetStreamingDistributionResponse getStreamingDistribution(
            CloudFrontClient client, GetStreamingDistributionRequest request) {
        return client.getStreamingDistribution(request);
    }

    public static GetStreamingDistributionConfigResponse getStreamingDistributionConfig(
            CloudFrontClient client, GetStreamingDistributionConfigRequest request) {
        return client.getStreamingDistributionConfig(request);
    }

    public static GetTrustStoreResponse getTrustStore(CloudFrontClient client, GetTrustStoreRequest request) {
        return client.getTrustStore(request);
    }

    public static GetVpcOriginResponse getVpcOrigin(CloudFrontClient client, GetVpcOriginRequest request) {
        return client.getVpcOrigin(request);
    }

    public static ListAnycastIpListsResponse listAnycastIpLists(
            CloudFrontClient client, ListAnycastIpListsRequest request) {
        return client.listAnycastIpLists(request);
    }

    public static ListConflictingAliasesResponse listConflictingAliases(
            CloudFrontClient client, ListConflictingAliasesRequest request) {
        return client.listConflictingAliases(request);
    }

    public static ListConnectionGroupsResponse listConnectionGroups(
            CloudFrontClient client, ListConnectionGroupsRequest request) {
        return client.listConnectionGroups(request);
    }

    public static ListContinuousDeploymentPoliciesResponse listContinuousDeploymentPolicies(
            CloudFrontClient client, ListContinuousDeploymentPoliciesRequest request) {
        return client.listContinuousDeploymentPolicies(request);
    }

    public static ListDistributionTenantsResponse listDistributionTenants(
            CloudFrontClient client, ListDistributionTenantsRequest request) {
        return client.listDistributionTenants(request);
    }

    public static ListDistributionTenantsByCustomizationResponse listDistributionTenantsByCustomization(
            CloudFrontClient client, ListDistributionTenantsByCustomizationRequest request) {
        return client.listDistributionTenantsByCustomization(request);
    }

    public static ListDistributionsResponse listDistributions(
            CloudFrontClient client, ListDistributionsRequest request) {
        return client.listDistributions(request);
    }

    public static ListDistributionsByAnycastIpListIdResponse listDistributionsByAnycastIpListId(
            CloudFrontClient client, ListDistributionsByAnycastIpListIdRequest request) {
        return client.listDistributionsByAnycastIpListId(request);
    }

    public static ListDistributionsByCachePolicyIdResponse listDistributionsByCachePolicyId(
            CloudFrontClient client, ListDistributionsByCachePolicyIdRequest request) {
        return client.listDistributionsByCachePolicyId(request);
    }

    public static ListDistributionsByConnectionFunctionResponse listDistributionsByConnectionFunction(
            CloudFrontClient client, ListDistributionsByConnectionFunctionRequest request) {
        return client.listDistributionsByConnectionFunction(request);
    }

    public static ListDistributionsByConnectionModeResponse listDistributionsByConnectionMode(
            CloudFrontClient client, ListDistributionsByConnectionModeRequest request) {
        return client.listDistributionsByConnectionMode(request);
    }

    public static ListDistributionsByKeyGroupResponse listDistributionsByKeyGroup(
            CloudFrontClient client, ListDistributionsByKeyGroupRequest request) {
        return client.listDistributionsByKeyGroup(request);
    }

    public static ListDistributionsByOriginRequestPolicyIdResponse listDistributionsByOriginRequestPolicyId(
            CloudFrontClient client, ListDistributionsByOriginRequestPolicyIdRequest request) {
        return client.listDistributionsByOriginRequestPolicyId(request);
    }

    public static ListDistributionsByOwnedResourceResponse listDistributionsByOwnedResource(
            CloudFrontClient client, ListDistributionsByOwnedResourceRequest request) {
        return client.listDistributionsByOwnedResource(request);
    }

    public static ListDistributionsByRealtimeLogConfigResponse listDistributionsByRealtimeLogConfig(
            CloudFrontClient client, ListDistributionsByRealtimeLogConfigRequest request) {
        return client.listDistributionsByRealtimeLogConfig(request);
    }

    public static ListDistributionsByResponseHeadersPolicyIdResponse listDistributionsByResponseHeadersPolicyId(
            CloudFrontClient client, ListDistributionsByResponseHeadersPolicyIdRequest request) {
        return client.listDistributionsByResponseHeadersPolicyId(request);
    }

    public static ListDistributionsByTrustStoreResponse listDistributionsByTrustStore(
            CloudFrontClient client, ListDistributionsByTrustStoreRequest request) {
        return client.listDistributionsByTrustStore(request);
    }

    public static ListDistributionsByVpcOriginIdResponse listDistributionsByVpcOriginId(
            CloudFrontClient client, ListDistributionsByVpcOriginIdRequest request) {
        return client.listDistributionsByVpcOriginId(request);
    }

    public static ListDistributionsByWebAclIdResponse listDistributionsByWebACLId(
            CloudFrontClient client, ListDistributionsByWebAclIdRequest request) {
        return client.listDistributionsByWebACLId(request);
    }

    public static ListDomainConflictsResponse listDomainConflicts(
            CloudFrontClient client, ListDomainConflictsRequest request) {
        return client.listDomainConflicts(request);
    }

    public static ListStreamingDistributionsResponse listStreamingDistributions(
            CloudFrontClient client, ListStreamingDistributionsRequest request) {
        return client.listStreamingDistributions(request);
    }

    public static ListTrustStoresResponse listTrustStores(CloudFrontClient client, ListTrustStoresRequest request) {
        return client.listTrustStores(request);
    }

    public static ListVpcOriginsResponse listVpcOrigins(CloudFrontClient client, ListVpcOriginsRequest request) {
        return client.listVpcOrigins(request);
    }

    public static PutResourcePolicyResponse putResourcePolicy(
            CloudFrontClient client, PutResourcePolicyRequest request) {
        return client.putResourcePolicy(request);
    }

    public static UpdateAnycastIpListResponse updateAnycastIpList(
            CloudFrontClient client, UpdateAnycastIpListRequest request) {
        return client.updateAnycastIpList(request);
    }

    public static UpdateConnectionGroupResponse updateConnectionGroup(
            CloudFrontClient client, UpdateConnectionGroupRequest request) {
        return client.updateConnectionGroup(request);
    }

    public static UpdateContinuousDeploymentPolicyResponse updateContinuousDeploymentPolicy(
            CloudFrontClient client, UpdateContinuousDeploymentPolicyRequest request) {
        return client.updateContinuousDeploymentPolicy(request);
    }

    public static UpdateDistributionResponse updateDistribution(
            CloudFrontClient client, UpdateDistributionRequest request) {
        return client.updateDistribution(request);
    }

    public static UpdateDistributionTenantResponse updateDistributionTenant(
            CloudFrontClient client, UpdateDistributionTenantRequest request) {
        return client.updateDistributionTenant(request);
    }

    public static UpdateDistributionWithStagingConfigResponse updateDistributionWithStagingConfig(
            CloudFrontClient client, UpdateDistributionWithStagingConfigRequest request) {
        return client.updateDistributionWithStagingConfig(request);
    }

    public static UpdateDomainAssociationResponse updateDomainAssociation(
            CloudFrontClient client, UpdateDomainAssociationRequest request) {
        return client.updateDomainAssociation(request);
    }

    public static UpdateStreamingDistributionResponse updateStreamingDistribution(
            CloudFrontClient client, UpdateStreamingDistributionRequest request) {
        return client.updateStreamingDistribution(request);
    }

    public static UpdateTrustStoreResponse updateTrustStore(CloudFrontClient client, UpdateTrustStoreRequest request) {
        return client.updateTrustStore(request);
    }

    public static UpdateVpcOriginResponse updateVpcOrigin(CloudFrontClient client, UpdateVpcOriginRequest request) {
        return client.updateVpcOrigin(request);
    }

    public static VerifyDnsConfigurationResponse verifyDnsConfiguration(
            CloudFrontClient client, VerifyDnsConfigurationRequest request) {
        return client.verifyDnsConfiguration(request);
    }
}
