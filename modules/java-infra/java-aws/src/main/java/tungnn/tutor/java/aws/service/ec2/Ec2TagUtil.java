package tungnn.tutor.java.aws.service.ec2;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

public final class Ec2TagUtil {
    private Ec2TagUtil() {}

    public static CreateTagsResponse createTags(Ec2Client client, CreateTagsRequest request) {
        return client.createTags(request);
    }

    public static DeleteTagsResponse deleteTags(Ec2Client client, DeleteTagsRequest request) {
        return client.deleteTags(request);
    }

    public static List<TagDescription> describeTags(Ec2Client client, DescribeTagsRequest request) {
        return client.describeTagsPaginator(request).stream()
                .flatMap(r -> r.tags().stream())
                .collect(Collectors.toList());
    }
}
