package tungnn.tutor.java.aws.service.cloudformation;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.cloudformation.CloudFormationClient;
import software.amazon.awssdk.services.cloudformation.model.*;

public final class CfnExportUtils {
    private CfnExportUtils() {}

    public static List<Export> listExports(CloudFormationClient client, ListExportsRequest request) {
        return client.listExportsPaginator(request).stream()
                .flatMap(r -> r.exports().stream())
                .collect(Collectors.toList());
    }

    public static List<String> listImports(CloudFormationClient client, ListImportsRequest request) {
        return client.listImportsPaginator(request).stream()
                .flatMap(r -> r.imports().stream())
                .collect(Collectors.toList());
    }
}
