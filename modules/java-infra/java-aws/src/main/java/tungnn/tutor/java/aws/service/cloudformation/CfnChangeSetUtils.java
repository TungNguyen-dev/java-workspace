package tungnn.tutor.java.aws.service.cloudformation;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.cloudformation.CloudFormationClient;
import software.amazon.awssdk.services.cloudformation.model.*;

public final class CfnChangeSetUtils {
    private CfnChangeSetUtils() {}

    public static CreateChangeSetResponse createChangeSet(CloudFormationClient client, CreateChangeSetRequest request) {
        return client.createChangeSet(request);
    }

    public static DeleteChangeSetResponse deleteChangeSet(CloudFormationClient client, DeleteChangeSetRequest request) {
        return client.deleteChangeSet(request);
    }

    public static List<Change> describeChangeSet(CloudFormationClient client, DescribeChangeSetRequest request) {
        return client.describeChangeSetPaginator(request).stream()
                .flatMap(r -> r.changes().stream())
                .collect(Collectors.toList());
    }

    public static DescribeChangeSetHooksResponse describeChangeSetHooks(
            CloudFormationClient client, DescribeChangeSetHooksRequest request) {
        return client.describeChangeSetHooks(request);
    }

    public static ExecuteChangeSetResponse executeChangeSet(
            CloudFormationClient client, ExecuteChangeSetRequest request) {
        return client.executeChangeSet(request);
    }

    public static List<ChangeSetSummary> listChangeSets(CloudFormationClient client, ListChangeSetsRequest request) {
        return client.listChangeSetsPaginator(request).stream()
                .flatMap(r -> r.summaries().stream())
                .collect(Collectors.toList());
    }
}
