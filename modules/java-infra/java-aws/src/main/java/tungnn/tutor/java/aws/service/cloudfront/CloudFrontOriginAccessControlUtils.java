package tungnn.tutor.java.aws.service.cloudfront;

import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.model.*;

public final class CloudFrontOriginAccessControlUtils {
    private CloudFrontOriginAccessControlUtils() {}

    public static CreateCloudFrontOriginAccessIdentityResponse createCloudFrontOriginAccessIdentity(
            CloudFrontClient client, CreateCloudFrontOriginAccessIdentityRequest request) {
        return client.createCloudFrontOriginAccessIdentity(request);
    }

    public static CreateOriginAccessControlResponse createOriginAccessControl(
            CloudFrontClient client, CreateOriginAccessControlRequest request) {
        return client.createOriginAccessControl(request);
    }

    public static DeleteCloudFrontOriginAccessIdentityResponse deleteCloudFrontOriginAccessIdentity(
            CloudFrontClient client, DeleteCloudFrontOriginAccessIdentityRequest request) {
        return client.deleteCloudFrontOriginAccessIdentity(request);
    }

    public static DeleteOriginAccessControlResponse deleteOriginAccessControl(
            CloudFrontClient client, DeleteOriginAccessControlRequest request) {
        return client.deleteOriginAccessControl(request);
    }

    public static GetCloudFrontOriginAccessIdentityResponse getCloudFrontOriginAccessIdentity(
            CloudFrontClient client, GetCloudFrontOriginAccessIdentityRequest request) {
        return client.getCloudFrontOriginAccessIdentity(request);
    }

    public static GetCloudFrontOriginAccessIdentityConfigResponse getCloudFrontOriginAccessIdentityConfig(
            CloudFrontClient client, GetCloudFrontOriginAccessIdentityConfigRequest request) {
        return client.getCloudFrontOriginAccessIdentityConfig(request);
    }

    public static GetOriginAccessControlResponse getOriginAccessControl(
            CloudFrontClient client, GetOriginAccessControlRequest request) {
        return client.getOriginAccessControl(request);
    }

    public static GetOriginAccessControlConfigResponse getOriginAccessControlConfig(
            CloudFrontClient client, GetOriginAccessControlConfigRequest request) {
        return client.getOriginAccessControlConfig(request);
    }

    public static ListCloudFrontOriginAccessIdentitiesResponse listCloudFrontOriginAccessIdentities(
            CloudFrontClient client, ListCloudFrontOriginAccessIdentitiesRequest request) {
        return client.listCloudFrontOriginAccessIdentities(request);
    }

    public static ListOriginAccessControlsResponse listOriginAccessControls(
            CloudFrontClient client, ListOriginAccessControlsRequest request) {
        return client.listOriginAccessControls(request);
    }

    public static UpdateCloudFrontOriginAccessIdentityResponse updateCloudFrontOriginAccessIdentity(
            CloudFrontClient client, UpdateCloudFrontOriginAccessIdentityRequest request) {
        return client.updateCloudFrontOriginAccessIdentity(request);
    }

    public static UpdateOriginAccessControlResponse updateOriginAccessControl(
            CloudFrontClient client, UpdateOriginAccessControlRequest request) {
        return client.updateOriginAccessControl(request);
    }
}
