package tungnn.tutor.java.aws.service.rds;

import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.*;

public final class RdsSubnetGroupUtils {
    private RdsSubnetGroupUtils() {}

    public static CreateDbSubnetGroupResponse createDBSubnetGroup(
            RdsClient client, CreateDbSubnetGroupRequest request) {
        return client.createDBSubnetGroup(request);
    }

    public static DeleteDbSubnetGroupResponse deleteDBSubnetGroup(
            RdsClient client, DeleteDbSubnetGroupRequest request) {
        return client.deleteDBSubnetGroup(request);
    }

    public static DescribeDbSubnetGroupsResponse describeDBSubnetGroups(
            RdsClient client, DescribeDbSubnetGroupsRequest request) {
        return client.describeDBSubnetGroups(request);
    }

    public static ModifyDbSubnetGroupResponse modifyDBSubnetGroup(
            RdsClient client, ModifyDbSubnetGroupRequest request) {
        return client.modifyDBSubnetGroup(request);
    }
}
