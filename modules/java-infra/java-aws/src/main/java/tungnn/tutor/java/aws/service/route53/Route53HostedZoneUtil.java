package tungnn.tutor.java.aws.service.route53;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.route53.Route53Client;
import software.amazon.awssdk.services.route53.model.*;

public final class Route53HostedZoneUtil {
    private Route53HostedZoneUtil() {}

    public static ActivateKeySigningKeyResponse activateKeySigningKey(
            Route53Client client, ActivateKeySigningKeyRequest request) {
        return client.activateKeySigningKey(request);
    }

    public static AssociateVpcWithHostedZoneResponse associateVPCWithHostedZone(
            Route53Client client, AssociateVpcWithHostedZoneRequest request) {
        return client.associateVPCWithHostedZone(request);
    }

    public static CreateHostedZoneResponse createHostedZone(Route53Client client, CreateHostedZoneRequest request) {
        return client.createHostedZone(request);
    }

    public static CreateKeySigningKeyResponse createKeySigningKey(
            Route53Client client, CreateKeySigningKeyRequest request) {
        return client.createKeySigningKey(request);
    }

    public static CreateQueryLoggingConfigResponse createQueryLoggingConfig(
            Route53Client client, CreateQueryLoggingConfigRequest request) {
        return client.createQueryLoggingConfig(request);
    }

    public static CreateReusableDelegationSetResponse createReusableDelegationSet(
            Route53Client client, CreateReusableDelegationSetRequest request) {
        return client.createReusableDelegationSet(request);
    }

    public static CreateTrafficPolicyResponse createTrafficPolicy(
            Route53Client client, CreateTrafficPolicyRequest request) {
        return client.createTrafficPolicy(request);
    }

    public static CreateTrafficPolicyInstanceResponse createTrafficPolicyInstance(
            Route53Client client, CreateTrafficPolicyInstanceRequest request) {
        return client.createTrafficPolicyInstance(request);
    }

    public static CreateTrafficPolicyVersionResponse createTrafficPolicyVersion(
            Route53Client client, CreateTrafficPolicyVersionRequest request) {
        return client.createTrafficPolicyVersion(request);
    }

    public static CreateVpcAssociationAuthorizationResponse createVPCAssociationAuthorization(
            Route53Client client, CreateVpcAssociationAuthorizationRequest request) {
        return client.createVPCAssociationAuthorization(request);
    }

    public static DeactivateKeySigningKeyResponse deactivateKeySigningKey(
            Route53Client client, DeactivateKeySigningKeyRequest request) {
        return client.deactivateKeySigningKey(request);
    }

    public static DeleteHostedZoneResponse deleteHostedZone(Route53Client client, DeleteHostedZoneRequest request) {
        return client.deleteHostedZone(request);
    }

    public static DeleteKeySigningKeyResponse deleteKeySigningKey(
            Route53Client client, DeleteKeySigningKeyRequest request) {
        return client.deleteKeySigningKey(request);
    }

    public static DeleteQueryLoggingConfigResponse deleteQueryLoggingConfig(
            Route53Client client, DeleteQueryLoggingConfigRequest request) {
        return client.deleteQueryLoggingConfig(request);
    }

    public static DeleteReusableDelegationSetResponse deleteReusableDelegationSet(
            Route53Client client, DeleteReusableDelegationSetRequest request) {
        return client.deleteReusableDelegationSet(request);
    }

    public static DeleteTrafficPolicyResponse deleteTrafficPolicy(
            Route53Client client, DeleteTrafficPolicyRequest request) {
        return client.deleteTrafficPolicy(request);
    }

    public static DeleteTrafficPolicyInstanceResponse deleteTrafficPolicyInstance(
            Route53Client client, DeleteTrafficPolicyInstanceRequest request) {
        return client.deleteTrafficPolicyInstance(request);
    }

    public static DeleteVpcAssociationAuthorizationResponse deleteVPCAssociationAuthorization(
            Route53Client client, DeleteVpcAssociationAuthorizationRequest request) {
        return client.deleteVPCAssociationAuthorization(request);
    }

    public static DisableHostedZoneDnssecResponse disableHostedZoneDNSSEC(
            Route53Client client, DisableHostedZoneDnssecRequest request) {
        return client.disableHostedZoneDNSSEC(request);
    }

    public static DisassociateVpcFromHostedZoneResponse disassociateVPCFromHostedZone(
            Route53Client client, DisassociateVpcFromHostedZoneRequest request) {
        return client.disassociateVPCFromHostedZone(request);
    }

    public static EnableHostedZoneDnssecResponse enableHostedZoneDNSSEC(
            Route53Client client, EnableHostedZoneDnssecRequest request) {
        return client.enableHostedZoneDNSSEC(request);
    }

    public static GetAccountLimitResponse getAccountLimit(Route53Client client, GetAccountLimitRequest request) {
        return client.getAccountLimit(request);
    }

    public static GetDnssecResponse getDNSSEC(Route53Client client, GetDnssecRequest request) {
        return client.getDNSSEC(request);
    }

    public static GetGeoLocationResponse getGeoLocation(Route53Client client, GetGeoLocationRequest request) {
        return client.getGeoLocation(request);
    }

    public static GetHostedZoneResponse getHostedZone(Route53Client client, GetHostedZoneRequest request) {
        return client.getHostedZone(request);
    }

    public static GetHostedZoneCountResponse getHostedZoneCount(
            Route53Client client, GetHostedZoneCountRequest request) {
        return client.getHostedZoneCount(request);
    }

    public static GetHostedZoneLimitResponse getHostedZoneLimit(
            Route53Client client, GetHostedZoneLimitRequest request) {
        return client.getHostedZoneLimit(request);
    }

    public static GetQueryLoggingConfigResponse getQueryLoggingConfig(
            Route53Client client, GetQueryLoggingConfigRequest request) {
        return client.getQueryLoggingConfig(request);
    }

    public static GetReusableDelegationSetResponse getReusableDelegationSet(
            Route53Client client, GetReusableDelegationSetRequest request) {
        return client.getReusableDelegationSet(request);
    }

    public static GetReusableDelegationSetLimitResponse getReusableDelegationSetLimit(
            Route53Client client, GetReusableDelegationSetLimitRequest request) {
        return client.getReusableDelegationSetLimit(request);
    }

    public static GetTrafficPolicyResponse getTrafficPolicy(Route53Client client, GetTrafficPolicyRequest request) {
        return client.getTrafficPolicy(request);
    }

    public static GetTrafficPolicyInstanceResponse getTrafficPolicyInstance(
            Route53Client client, GetTrafficPolicyInstanceRequest request) {
        return client.getTrafficPolicyInstance(request);
    }

    public static GetTrafficPolicyInstanceCountResponse getTrafficPolicyInstanceCount(
            Route53Client client, GetTrafficPolicyInstanceCountRequest request) {
        return client.getTrafficPolicyInstanceCount(request);
    }

    public static ListGeoLocationsResponse listGeoLocations(Route53Client client, ListGeoLocationsRequest request) {
        return client.listGeoLocations(request);
    }

    public static List<HostedZone> listHostedZones(Route53Client client, ListHostedZonesRequest request) {
        return client.listHostedZonesPaginator(request).stream()
                .flatMap(r -> r.hostedZones().stream())
                .collect(Collectors.toList());
    }

    public static ListHostedZonesByNameResponse listHostedZonesByName(
            Route53Client client, ListHostedZonesByNameRequest request) {
        return client.listHostedZonesByName(request);
    }

    public static ListHostedZonesByVpcResponse listHostedZonesByVPC(
            Route53Client client, ListHostedZonesByVpcRequest request) {
        return client.listHostedZonesByVPC(request);
    }

    public static List<QueryLoggingConfig> listQueryLoggingConfigs(
            Route53Client client, ListQueryLoggingConfigsRequest request) {
        return client.listQueryLoggingConfigsPaginator(request).stream()
                .flatMap(r -> r.queryLoggingConfigs().stream())
                .collect(Collectors.toList());
    }

    public static ListReusableDelegationSetsResponse listReusableDelegationSets(
            Route53Client client, ListReusableDelegationSetsRequest request) {
        return client.listReusableDelegationSets(request);
    }

    public static ListTrafficPoliciesResponse listTrafficPolicies(
            Route53Client client, ListTrafficPoliciesRequest request) {
        return client.listTrafficPolicies(request);
    }

    public static ListTrafficPolicyInstancesResponse listTrafficPolicyInstances(
            Route53Client client, ListTrafficPolicyInstancesRequest request) {
        return client.listTrafficPolicyInstances(request);
    }

    public static ListTrafficPolicyInstancesByHostedZoneResponse listTrafficPolicyInstancesByHostedZone(
            Route53Client client, ListTrafficPolicyInstancesByHostedZoneRequest request) {
        return client.listTrafficPolicyInstancesByHostedZone(request);
    }

    public static ListTrafficPolicyInstancesByPolicyResponse listTrafficPolicyInstancesByPolicy(
            Route53Client client, ListTrafficPolicyInstancesByPolicyRequest request) {
        return client.listTrafficPolicyInstancesByPolicy(request);
    }

    public static ListTrafficPolicyVersionsResponse listTrafficPolicyVersions(
            Route53Client client, ListTrafficPolicyVersionsRequest request) {
        return client.listTrafficPolicyVersions(request);
    }

    public static ListVpcAssociationAuthorizationsResponse listVPCAssociationAuthorizations(
            Route53Client client, ListVpcAssociationAuthorizationsRequest request) {
        return client.listVPCAssociationAuthorizations(request);
    }

    public static UpdateHostedZoneCommentResponse updateHostedZoneComment(
            Route53Client client, UpdateHostedZoneCommentRequest request) {
        return client.updateHostedZoneComment(request);
    }

    public static UpdateHostedZoneFeaturesResponse updateHostedZoneFeatures(
            Route53Client client, UpdateHostedZoneFeaturesRequest request) {
        return client.updateHostedZoneFeatures(request);
    }

    public static UpdateTrafficPolicyCommentResponse updateTrafficPolicyComment(
            Route53Client client, UpdateTrafficPolicyCommentRequest request) {
        return client.updateTrafficPolicyComment(request);
    }

    public static UpdateTrafficPolicyInstanceResponse updateTrafficPolicyInstance(
            Route53Client client, UpdateTrafficPolicyInstanceRequest request) {
        return client.updateTrafficPolicyInstance(request);
    }
}
