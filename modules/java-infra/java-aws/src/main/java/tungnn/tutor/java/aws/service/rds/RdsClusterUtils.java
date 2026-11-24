package tungnn.tutor.java.aws.service.rds;

import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.*;

public final class RdsClusterUtils {
    private RdsClusterUtils() {}

    public static AddRoleToDbClusterResponse addRoleToDBCluster(RdsClient client, AddRoleToDbClusterRequest request) {
        return client.addRoleToDBCluster(request);
    }

    public static BacktrackDbClusterResponse backtrackDBCluster(RdsClient client, BacktrackDbClusterRequest request) {
        return client.backtrackDBCluster(request);
    }

    public static CreateDbClusterResponse createDBCluster(RdsClient client, CreateDbClusterRequest request) {
        return client.createDBCluster(request);
    }

    public static CreateDbClusterEndpointResponse createDBClusterEndpoint(
            RdsClient client, CreateDbClusterEndpointRequest request) {
        return client.createDBClusterEndpoint(request);
    }

    public static CreateDbShardGroupResponse createDBShardGroup(RdsClient client, CreateDbShardGroupRequest request) {
        return client.createDBShardGroup(request);
    }

    public static CreateGlobalClusterResponse createGlobalCluster(
            RdsClient client, CreateGlobalClusterRequest request) {
        return client.createGlobalCluster(request);
    }

    public static DeleteDbClusterResponse deleteDBCluster(RdsClient client, DeleteDbClusterRequest request) {
        return client.deleteDBCluster(request);
    }

    public static DeleteDbClusterEndpointResponse deleteDBClusterEndpoint(
            RdsClient client, DeleteDbClusterEndpointRequest request) {
        return client.deleteDBClusterEndpoint(request);
    }

    public static DeleteDbShardGroupResponse deleteDBShardGroup(RdsClient client, DeleteDbShardGroupRequest request) {
        return client.deleteDBShardGroup(request);
    }

    public static DeleteGlobalClusterResponse deleteGlobalCluster(
            RdsClient client, DeleteGlobalClusterRequest request) {
        return client.deleteGlobalCluster(request);
    }

    public static DescribeDbClusterBacktracksResponse describeDBClusterBacktracks(
            RdsClient client, DescribeDbClusterBacktracksRequest request) {
        return client.describeDBClusterBacktracks(request);
    }

    public static DescribeDbClusterEndpointsResponse describeDBClusterEndpoints(
            RdsClient client, DescribeDbClusterEndpointsRequest request) {
        return client.describeDBClusterEndpoints(request);
    }

    public static DescribeDbClustersResponse describeDBClusters(RdsClient client, DescribeDbClustersRequest request) {
        return client.describeDBClusters(request);
    }

    public static DescribeDbShardGroupsResponse describeDBShardGroups(
            RdsClient client, DescribeDbShardGroupsRequest request) {
        return client.describeDBShardGroups(request);
    }

    public static DescribeGlobalClustersResponse describeGlobalClusters(
            RdsClient client, DescribeGlobalClustersRequest request) {
        return client.describeGlobalClusters(request);
    }

    public static DescribeServerlessV2PlatformVersionsResponse describeServerlessV2PlatformVersions(
            RdsClient client, DescribeServerlessV2PlatformVersionsRequest request) {
        return client.describeServerlessV2PlatformVersions(request);
    }

    public static DisableHttpEndpointResponse disableHttpEndpoint(
            RdsClient client, DisableHttpEndpointRequest request) {
        return client.disableHttpEndpoint(request);
    }

    public static EnableHttpEndpointResponse enableHttpEndpoint(RdsClient client, EnableHttpEndpointRequest request) {
        return client.enableHttpEndpoint(request);
    }

    public static FailoverDbClusterResponse failoverDBCluster(RdsClient client, FailoverDbClusterRequest request) {
        return client.failoverDBCluster(request);
    }

    public static FailoverGlobalClusterResponse failoverGlobalCluster(
            RdsClient client, FailoverGlobalClusterRequest request) {
        return client.failoverGlobalCluster(request);
    }

    public static ModifyActivityStreamResponse modifyActivityStream(
            RdsClient client, ModifyActivityStreamRequest request) {
        return client.modifyActivityStream(request);
    }

    public static ModifyCurrentDbClusterCapacityResponse modifyCurrentDBClusterCapacity(
            RdsClient client, ModifyCurrentDbClusterCapacityRequest request) {
        return client.modifyCurrentDBClusterCapacity(request);
    }

    public static ModifyDbClusterResponse modifyDBCluster(RdsClient client, ModifyDbClusterRequest request) {
        return client.modifyDBCluster(request);
    }

    public static ModifyDbClusterEndpointResponse modifyDBClusterEndpoint(
            RdsClient client, ModifyDbClusterEndpointRequest request) {
        return client.modifyDBClusterEndpoint(request);
    }

    public static ModifyDbShardGroupResponse modifyDBShardGroup(RdsClient client, ModifyDbShardGroupRequest request) {
        return client.modifyDBShardGroup(request);
    }

    public static ModifyGlobalClusterResponse modifyGlobalCluster(
            RdsClient client, ModifyGlobalClusterRequest request) {
        return client.modifyGlobalCluster(request);
    }

    public static PromoteReadReplicaDbClusterResponse promoteReadReplicaDBCluster(
            RdsClient client, PromoteReadReplicaDbClusterRequest request) {
        return client.promoteReadReplicaDBCluster(request);
    }

    public static RebootDbClusterResponse rebootDBCluster(RdsClient client, RebootDbClusterRequest request) {
        return client.rebootDBCluster(request);
    }

    public static RebootDbShardGroupResponse rebootDBShardGroup(RdsClient client, RebootDbShardGroupRequest request) {
        return client.rebootDBShardGroup(request);
    }

    public static RemoveFromGlobalClusterResponse removeFromGlobalCluster(
            RdsClient client, RemoveFromGlobalClusterRequest request) {
        return client.removeFromGlobalCluster(request);
    }

    public static RemoveRoleFromDbClusterResponse removeRoleFromDBCluster(
            RdsClient client, RemoveRoleFromDbClusterRequest request) {
        return client.removeRoleFromDBCluster(request);
    }

    public static RestoreDbClusterFromS3Response restoreDBClusterFromS3(
            RdsClient client, RestoreDbClusterFromS3Request request) {
        return client.restoreDBClusterFromS3(request);
    }

    public static RestoreDbClusterToPointInTimeResponse restoreDBClusterToPointInTime(
            RdsClient client, RestoreDbClusterToPointInTimeRequest request) {
        return client.restoreDBClusterToPointInTime(request);
    }

    public static StartActivityStreamResponse startActivityStream(
            RdsClient client, StartActivityStreamRequest request) {
        return client.startActivityStream(request);
    }

    public static StartDbClusterResponse startDBCluster(RdsClient client, StartDbClusterRequest request) {
        return client.startDBCluster(request);
    }

    public static StopActivityStreamResponse stopActivityStream(RdsClient client, StopActivityStreamRequest request) {
        return client.stopActivityStream(request);
    }

    public static StopDbClusterResponse stopDBCluster(RdsClient client, StopDbClusterRequest request) {
        return client.stopDBCluster(request);
    }

    public static SwitchoverGlobalClusterResponse switchoverGlobalCluster(
            RdsClient client, SwitchoverGlobalClusterRequest request) {
        return client.switchoverGlobalCluster(request);
    }
}
