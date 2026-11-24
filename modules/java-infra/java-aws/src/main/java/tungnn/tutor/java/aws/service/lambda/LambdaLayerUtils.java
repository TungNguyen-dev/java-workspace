package tungnn.tutor.java.aws.service.lambda;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.*;

public final class LambdaLayerUtils {
    private LambdaLayerUtils() {}

    public static AddLayerVersionPermissionResponse addLayerVersionPermission(
            LambdaClient client, AddLayerVersionPermissionRequest request) {
        return client.addLayerVersionPermission(request);
    }

    public static DeleteLayerVersionResponse deleteLayerVersion(
            LambdaClient client, DeleteLayerVersionRequest request) {
        return client.deleteLayerVersion(request);
    }

    public static GetLayerVersionResponse getLayerVersion(LambdaClient client, GetLayerVersionRequest request) {
        return client.getLayerVersion(request);
    }

    public static GetLayerVersionByArnResponse getLayerVersionByArn(
            LambdaClient client, GetLayerVersionByArnRequest request) {
        return client.getLayerVersionByArn(request);
    }

    public static GetLayerVersionPolicyResponse getLayerVersionPolicy(
            LambdaClient client, GetLayerVersionPolicyRequest request) {
        return client.getLayerVersionPolicy(request);
    }

    public static List<LayerVersionsListItem> listLayerVersions(LambdaClient client, ListLayerVersionsRequest request) {
        return client.listLayerVersionsPaginator(request).stream()
                .flatMap(r -> r.layerVersions().stream())
                .collect(Collectors.toList());
    }

    public static List<LayersListItem> listLayers(LambdaClient client, ListLayersRequest request) {
        return client.listLayersPaginator(request).stream()
                .flatMap(r -> r.layers().stream())
                .collect(Collectors.toList());
    }

    public static PublishLayerVersionResponse publishLayerVersion(
            LambdaClient client, PublishLayerVersionRequest request) {
        return client.publishLayerVersion(request);
    }

    public static RemoveLayerVersionPermissionResponse removeLayerVersionPermission(
            LambdaClient client, RemoveLayerVersionPermissionRequest request) {
        return client.removeLayerVersionPermission(request);
    }
}
