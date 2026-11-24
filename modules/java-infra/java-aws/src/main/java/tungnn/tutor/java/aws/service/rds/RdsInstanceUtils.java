package tungnn.tutor.java.aws.service.rds;

import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.*;

public final class RdsInstanceUtils {
    private RdsInstanceUtils() {}

    public static AddRoleToDbInstanceResponse addRoleToDBInstance(
            RdsClient client, AddRoleToDbInstanceRequest request) {
        return client.addRoleToDBInstance(request);
    }

    public static ApplyPendingMaintenanceActionResponse applyPendingMaintenanceAction(
            RdsClient client, ApplyPendingMaintenanceActionRequest request) {
        return client.applyPendingMaintenanceAction(request);
    }

    public static AuthorizeDbSecurityGroupIngressResponse authorizeDBSecurityGroupIngress(
            RdsClient client, AuthorizeDbSecurityGroupIngressRequest request) {
        return client.authorizeDBSecurityGroupIngress(request);
    }

    public static CopyOptionGroupResponse copyOptionGroup(RdsClient client, CopyOptionGroupRequest request) {
        return client.copyOptionGroup(request);
    }

    public static CreateBlueGreenDeploymentResponse createBlueGreenDeployment(
            RdsClient client, CreateBlueGreenDeploymentRequest request) {
        return client.createBlueGreenDeployment(request);
    }

    public static CreateCustomDbEngineVersionResponse createCustomDBEngineVersion(
            RdsClient client, CreateCustomDbEngineVersionRequest request) {
        return client.createCustomDBEngineVersion(request);
    }

    public static CreateDbInstanceResponse createDBInstance(RdsClient client, CreateDbInstanceRequest request) {
        return client.createDBInstance(request);
    }

    public static CreateDbInstanceReadReplicaResponse createDBInstanceReadReplica(
            RdsClient client, CreateDbInstanceReadReplicaRequest request) {
        return client.createDBInstanceReadReplica(request);
    }

    public static CreateDbProxyResponse createDBProxy(RdsClient client, CreateDbProxyRequest request) {
        return client.createDBProxy(request);
    }

    public static CreateDbProxyEndpointResponse createDBProxyEndpoint(
            RdsClient client, CreateDbProxyEndpointRequest request) {
        return client.createDBProxyEndpoint(request);
    }

    public static CreateDbSecurityGroupResponse createDBSecurityGroup(
            RdsClient client, CreateDbSecurityGroupRequest request) {
        return client.createDBSecurityGroup(request);
    }

    public static CreateIntegrationResponse createIntegration(RdsClient client, CreateIntegrationRequest request) {
        return client.createIntegration(request);
    }

    public static CreateOptionGroupResponse createOptionGroup(RdsClient client, CreateOptionGroupRequest request) {
        return client.createOptionGroup(request);
    }

    public static CreateTenantDatabaseResponse createTenantDatabase(
            RdsClient client, CreateTenantDatabaseRequest request) {
        return client.createTenantDatabase(request);
    }

    public static DeleteBlueGreenDeploymentResponse deleteBlueGreenDeployment(
            RdsClient client, DeleteBlueGreenDeploymentRequest request) {
        return client.deleteBlueGreenDeployment(request);
    }

    public static DeleteCustomDbEngineVersionResponse deleteCustomDBEngineVersion(
            RdsClient client, DeleteCustomDbEngineVersionRequest request) {
        return client.deleteCustomDBEngineVersion(request);
    }

    public static DeleteDbInstanceResponse deleteDBInstance(RdsClient client, DeleteDbInstanceRequest request) {
        return client.deleteDBInstance(request);
    }

    public static DeleteDbProxyResponse deleteDBProxy(RdsClient client, DeleteDbProxyRequest request) {
        return client.deleteDBProxy(request);
    }

    public static DeleteDbProxyEndpointResponse deleteDBProxyEndpoint(
            RdsClient client, DeleteDbProxyEndpointRequest request) {
        return client.deleteDBProxyEndpoint(request);
    }

    public static DeleteDbSecurityGroupResponse deleteDBSecurityGroup(
            RdsClient client, DeleteDbSecurityGroupRequest request) {
        return client.deleteDBSecurityGroup(request);
    }

    public static DeleteIntegrationResponse deleteIntegration(RdsClient client, DeleteIntegrationRequest request) {
        return client.deleteIntegration(request);
    }

    public static DeleteOptionGroupResponse deleteOptionGroup(RdsClient client, DeleteOptionGroupRequest request) {
        return client.deleteOptionGroup(request);
    }

    public static DeleteTenantDatabaseResponse deleteTenantDatabase(
            RdsClient client, DeleteTenantDatabaseRequest request) {
        return client.deleteTenantDatabase(request);
    }

    public static DeregisterDbProxyTargetsResponse deregisterDBProxyTargets(
            RdsClient client, DeregisterDbProxyTargetsRequest request) {
        return client.deregisterDBProxyTargets(request);
    }

    public static DescribeAccountAttributesResponse describeAccountAttributes(
            RdsClient client, DescribeAccountAttributesRequest request) {
        return client.describeAccountAttributes(request);
    }

    public static DescribeBlueGreenDeploymentsResponse describeBlueGreenDeployments(
            RdsClient client, DescribeBlueGreenDeploymentsRequest request) {
        return client.describeBlueGreenDeployments(request);
    }

    public static DescribeCertificatesResponse describeCertificates(
            RdsClient client, DescribeCertificatesRequest request) {
        return client.describeCertificates(request);
    }

    public static DescribeDbEngineVersionsResponse describeDBEngineVersions(
            RdsClient client, DescribeDbEngineVersionsRequest request) {
        return client.describeDBEngineVersions(request);
    }

    public static DescribeDbInstancesResponse describeDBInstances(
            RdsClient client, DescribeDbInstancesRequest request) {
        return client.describeDBInstances(request);
    }

    public static DescribeDbLogFilesResponse describeDBLogFiles(RdsClient client, DescribeDbLogFilesRequest request) {
        return client.describeDBLogFiles(request);
    }

    public static DescribeDbMajorEngineVersionsResponse describeDBMajorEngineVersions(
            RdsClient client, DescribeDbMajorEngineVersionsRequest request) {
        return client.describeDBMajorEngineVersions(request);
    }

    public static DescribeDbProxiesResponse describeDBProxies(RdsClient client, DescribeDbProxiesRequest request) {
        return client.describeDBProxies(request);
    }

    public static DescribeDbProxyEndpointsResponse describeDBProxyEndpoints(
            RdsClient client, DescribeDbProxyEndpointsRequest request) {
        return client.describeDBProxyEndpoints(request);
    }

    public static DescribeDbProxyTargetGroupsResponse describeDBProxyTargetGroups(
            RdsClient client, DescribeDbProxyTargetGroupsRequest request) {
        return client.describeDBProxyTargetGroups(request);
    }

    public static DescribeDbProxyTargetsResponse describeDBProxyTargets(
            RdsClient client, DescribeDbProxyTargetsRequest request) {
        return client.describeDBProxyTargets(request);
    }

    public static DescribeDbRecommendationsResponse describeDBRecommendations(
            RdsClient client, DescribeDbRecommendationsRequest request) {
        return client.describeDBRecommendations(request);
    }

    public static DescribeDbSecurityGroupsResponse describeDBSecurityGroups(
            RdsClient client, DescribeDbSecurityGroupsRequest request) {
        return client.describeDBSecurityGroups(request);
    }

    public static DescribeIntegrationsResponse describeIntegrations(
            RdsClient client, DescribeIntegrationsRequest request) {
        return client.describeIntegrations(request);
    }

    public static DescribeOptionGroupOptionsResponse describeOptionGroupOptions(
            RdsClient client, DescribeOptionGroupOptionsRequest request) {
        return client.describeOptionGroupOptions(request);
    }

    public static DescribeOptionGroupsResponse describeOptionGroups(
            RdsClient client, DescribeOptionGroupsRequest request) {
        return client.describeOptionGroups(request);
    }

    public static DescribeOrderableDbInstanceOptionsResponse describeOrderableDBInstanceOptions(
            RdsClient client, DescribeOrderableDbInstanceOptionsRequest request) {
        return client.describeOrderableDBInstanceOptions(request);
    }

    public static DescribePendingMaintenanceActionsResponse describePendingMaintenanceActions(
            RdsClient client, DescribePendingMaintenanceActionsRequest request) {
        return client.describePendingMaintenanceActions(request);
    }

    public static DescribeReservedDbInstancesResponse describeReservedDBInstances(
            RdsClient client, DescribeReservedDbInstancesRequest request) {
        return client.describeReservedDBInstances(request);
    }

    public static DescribeReservedDbInstancesOfferingsResponse describeReservedDBInstancesOfferings(
            RdsClient client, DescribeReservedDbInstancesOfferingsRequest request) {
        return client.describeReservedDBInstancesOfferings(request);
    }

    public static DescribeSourceRegionsResponse describeSourceRegions(
            RdsClient client, DescribeSourceRegionsRequest request) {
        return client.describeSourceRegions(request);
    }

    public static DescribeTenantDatabasesResponse describeTenantDatabases(
            RdsClient client, DescribeTenantDatabasesRequest request) {
        return client.describeTenantDatabases(request);
    }

    public static DescribeValidDbInstanceModificationsResponse describeValidDBInstanceModifications(
            RdsClient client, DescribeValidDbInstanceModificationsRequest request) {
        return client.describeValidDBInstanceModifications(request);
    }

    public static DownloadDbLogFilePortionResponse downloadDBLogFilePortion(
            RdsClient client, DownloadDbLogFilePortionRequest request) {
        return client.downloadDBLogFilePortion(request);
    }

    public static ModifyCertificatesResponse modifyCertificates(RdsClient client, ModifyCertificatesRequest request) {
        return client.modifyCertificates(request);
    }

    public static ModifyCustomDbEngineVersionResponse modifyCustomDBEngineVersion(
            RdsClient client, ModifyCustomDbEngineVersionRequest request) {
        return client.modifyCustomDBEngineVersion(request);
    }

    public static ModifyDbInstanceResponse modifyDBInstance(RdsClient client, ModifyDbInstanceRequest request) {
        return client.modifyDBInstance(request);
    }

    public static ModifyDbProxyResponse modifyDBProxy(RdsClient client, ModifyDbProxyRequest request) {
        return client.modifyDBProxy(request);
    }

    public static ModifyDbProxyEndpointResponse modifyDBProxyEndpoint(
            RdsClient client, ModifyDbProxyEndpointRequest request) {
        return client.modifyDBProxyEndpoint(request);
    }

    public static ModifyDbProxyTargetGroupResponse modifyDBProxyTargetGroup(
            RdsClient client, ModifyDbProxyTargetGroupRequest request) {
        return client.modifyDBProxyTargetGroup(request);
    }

    public static ModifyDbRecommendationResponse modifyDBRecommendation(
            RdsClient client, ModifyDbRecommendationRequest request) {
        return client.modifyDBRecommendation(request);
    }

    public static ModifyIntegrationResponse modifyIntegration(RdsClient client, ModifyIntegrationRequest request) {
        return client.modifyIntegration(request);
    }

    public static ModifyOptionGroupResponse modifyOptionGroup(RdsClient client, ModifyOptionGroupRequest request) {
        return client.modifyOptionGroup(request);
    }

    public static ModifyTenantDatabaseResponse modifyTenantDatabase(
            RdsClient client, ModifyTenantDatabaseRequest request) {
        return client.modifyTenantDatabase(request);
    }

    public static PromoteReadReplicaResponse promoteReadReplica(RdsClient client, PromoteReadReplicaRequest request) {
        return client.promoteReadReplica(request);
    }

    public static PurchaseReservedDbInstancesOfferingResponse purchaseReservedDBInstancesOffering(
            RdsClient client, PurchaseReservedDbInstancesOfferingRequest request) {
        return client.purchaseReservedDBInstancesOffering(request);
    }

    public static RebootDbInstanceResponse rebootDBInstance(RdsClient client, RebootDbInstanceRequest request) {
        return client.rebootDBInstance(request);
    }

    public static RegisterDbProxyTargetsResponse registerDBProxyTargets(
            RdsClient client, RegisterDbProxyTargetsRequest request) {
        return client.registerDBProxyTargets(request);
    }

    public static RemoveRoleFromDbInstanceResponse removeRoleFromDBInstance(
            RdsClient client, RemoveRoleFromDbInstanceRequest request) {
        return client.removeRoleFromDBInstance(request);
    }

    public static RestoreDbInstanceFromS3Response restoreDBInstanceFromS3(
            RdsClient client, RestoreDbInstanceFromS3Request request) {
        return client.restoreDBInstanceFromS3(request);
    }

    public static RestoreDbInstanceToPointInTimeResponse restoreDBInstanceToPointInTime(
            RdsClient client, RestoreDbInstanceToPointInTimeRequest request) {
        return client.restoreDBInstanceToPointInTime(request);
    }

    public static RevokeDbSecurityGroupIngressResponse revokeDBSecurityGroupIngress(
            RdsClient client, RevokeDbSecurityGroupIngressRequest request) {
        return client.revokeDBSecurityGroupIngress(request);
    }

    public static StartDbInstanceResponse startDBInstance(RdsClient client, StartDbInstanceRequest request) {
        return client.startDBInstance(request);
    }

    public static StopDbInstanceResponse stopDBInstance(RdsClient client, StopDbInstanceRequest request) {
        return client.stopDBInstance(request);
    }

    public static SwitchoverBlueGreenDeploymentResponse switchoverBlueGreenDeployment(
            RdsClient client, SwitchoverBlueGreenDeploymentRequest request) {
        return client.switchoverBlueGreenDeployment(request);
    }

    public static SwitchoverReadReplicaResponse switchoverReadReplica(
            RdsClient client, SwitchoverReadReplicaRequest request) {
        return client.switchoverReadReplica(request);
    }
}
