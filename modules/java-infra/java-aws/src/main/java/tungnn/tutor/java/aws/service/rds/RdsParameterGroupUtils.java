package tungnn.tutor.java.aws.service.rds;

import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.*;

public final class RdsParameterGroupUtils {
    private RdsParameterGroupUtils() {}

    public static CopyDbClusterParameterGroupResponse copyDBClusterParameterGroup(
            RdsClient client, CopyDbClusterParameterGroupRequest request) {
        return client.copyDBClusterParameterGroup(request);
    }

    public static CopyDbParameterGroupResponse copyDBParameterGroup(
            RdsClient client, CopyDbParameterGroupRequest request) {
        return client.copyDBParameterGroup(request);
    }

    public static CreateDbClusterParameterGroupResponse createDBClusterParameterGroup(
            RdsClient client, CreateDbClusterParameterGroupRequest request) {
        return client.createDBClusterParameterGroup(request);
    }

    public static CreateDbParameterGroupResponse createDBParameterGroup(
            RdsClient client, CreateDbParameterGroupRequest request) {
        return client.createDBParameterGroup(request);
    }

    public static DeleteDbClusterParameterGroupResponse deleteDBClusterParameterGroup(
            RdsClient client, DeleteDbClusterParameterGroupRequest request) {
        return client.deleteDBClusterParameterGroup(request);
    }

    public static DeleteDbParameterGroupResponse deleteDBParameterGroup(
            RdsClient client, DeleteDbParameterGroupRequest request) {
        return client.deleteDBParameterGroup(request);
    }

    public static DescribeDbClusterParameterGroupsResponse describeDBClusterParameterGroups(
            RdsClient client, DescribeDbClusterParameterGroupsRequest request) {
        return client.describeDBClusterParameterGroups(request);
    }

    public static DescribeDbClusterParametersResponse describeDBClusterParameters(
            RdsClient client, DescribeDbClusterParametersRequest request) {
        return client.describeDBClusterParameters(request);
    }

    public static DescribeDbParameterGroupsResponse describeDBParameterGroups(
            RdsClient client, DescribeDbParameterGroupsRequest request) {
        return client.describeDBParameterGroups(request);
    }

    public static DescribeDbParametersResponse describeDBParameters(
            RdsClient client, DescribeDbParametersRequest request) {
        return client.describeDBParameters(request);
    }

    public static DescribeEngineDefaultClusterParametersResponse describeEngineDefaultClusterParameters(
            RdsClient client, DescribeEngineDefaultClusterParametersRequest request) {
        return client.describeEngineDefaultClusterParameters(request);
    }

    public static DescribeEngineDefaultParametersResponse describeEngineDefaultParameters(
            RdsClient client, DescribeEngineDefaultParametersRequest request) {
        return client.describeEngineDefaultParameters(request);
    }

    public static ModifyDbClusterParameterGroupResponse modifyDBClusterParameterGroup(
            RdsClient client, ModifyDbClusterParameterGroupRequest request) {
        return client.modifyDBClusterParameterGroup(request);
    }

    public static ModifyDbParameterGroupResponse modifyDBParameterGroup(
            RdsClient client, ModifyDbParameterGroupRequest request) {
        return client.modifyDBParameterGroup(request);
    }

    public static ResetDbClusterParameterGroupResponse resetDBClusterParameterGroup(
            RdsClient client, ResetDbClusterParameterGroupRequest request) {
        return client.resetDBClusterParameterGroup(request);
    }

    public static ResetDbParameterGroupResponse resetDBParameterGroup(
            RdsClient client, ResetDbParameterGroupRequest request) {
        return client.resetDBParameterGroup(request);
    }
}
