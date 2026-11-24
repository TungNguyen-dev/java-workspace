package tungnn.tutor.java.aws.service.s3;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

public final class S3ObjectUtil {
    private S3ObjectUtil() {}

    public static CopyObjectResponse copyObject(S3Client client, CopyObjectRequest request) {
        return client.copyObject(request);
    }

    public static DeleteObjectResponse deleteObject(S3Client client, DeleteObjectRequest request) {
        return client.deleteObject(request);
    }

    public static DeleteObjectAnnotationResponse deleteObjectAnnotation(
            S3Client client, DeleteObjectAnnotationRequest request) {
        return client.deleteObjectAnnotation(request);
    }

    public static DeleteObjectsResponse deleteObjects(S3Client client, DeleteObjectsRequest request) {
        return client.deleteObjects(request);
    }

    public static ResponseInputStream<GetObjectResponse> getObject(S3Client client, GetObjectRequest request) {
        return client.getObject(request);
    }

    public static GetObjectAclResponse getObjectAcl(S3Client client, GetObjectAclRequest request) {
        return client.getObjectAcl(request);
    }

    public static ResponseInputStream<GetObjectAnnotationResponse> getObjectAnnotation(
            S3Client client, GetObjectAnnotationRequest request) {
        return client.getObjectAnnotation(request);
    }

    public static ResponseBytes<GetObjectAnnotationResponse> getObjectAnnotationAsBytes(
            S3Client client, GetObjectAnnotationRequest request) {
        return client.getObjectAnnotationAsBytes(request);
    }

    public static ResponseBytes<GetObjectResponse> getObjectAsBytes(S3Client client, GetObjectRequest request) {
        return client.getObjectAsBytes(request);
    }

    public static GetObjectAttributesResponse getObjectAttributes(S3Client client, GetObjectAttributesRequest request) {
        return client.getObjectAttributes(request);
    }

    public static GetObjectLegalHoldResponse getObjectLegalHold(S3Client client, GetObjectLegalHoldRequest request) {
        return client.getObjectLegalHold(request);
    }

    public static GetObjectLockConfigurationResponse getObjectLockConfiguration(
            S3Client client, GetObjectLockConfigurationRequest request) {
        return client.getObjectLockConfiguration(request);
    }

    public static GetObjectRetentionResponse getObjectRetention(S3Client client, GetObjectRetentionRequest request) {
        return client.getObjectRetention(request);
    }

    public static ResponseInputStream<GetObjectTorrentResponse> getObjectTorrent(
            S3Client client, GetObjectTorrentRequest request) {
        return client.getObjectTorrent(request);
    }

    public static ResponseBytes<GetObjectTorrentResponse> getObjectTorrentAsBytes(
            S3Client client, GetObjectTorrentRequest request) {
        return client.getObjectTorrentAsBytes(request);
    }

    public static HeadObjectResponse headObject(S3Client client, HeadObjectRequest request) {
        return client.headObject(request);
    }

    public static ListObjectAnnotationsResponse listObjectAnnotations(
            S3Client client, ListObjectAnnotationsRequest request) {
        return client.listObjectAnnotations(request);
    }

    public static List<ObjectVersion> listObjectVersions(S3Client client, ListObjectVersionsRequest request) {
        return client.listObjectVersionsPaginator(request).stream()
                .flatMap(r -> r.versions().stream())
                .collect(Collectors.toList());
    }

    public static ListObjectsResponse listObjects(S3Client client, ListObjectsRequest request) {
        return client.listObjects(request);
    }

    public static List<S3Object> listObjectsV2(S3Client client, ListObjectsV2Request request) {
        return client.listObjectsV2Paginator(request).stream()
                .flatMap(r -> r.contents().stream())
                .collect(Collectors.toList());
    }

    public static PutObjectAclResponse putObjectAcl(S3Client client, PutObjectAclRequest request) {
        return client.putObjectAcl(request);
    }

    public static PutObjectLegalHoldResponse putObjectLegalHold(S3Client client, PutObjectLegalHoldRequest request) {
        return client.putObjectLegalHold(request);
    }

    public static PutObjectLockConfigurationResponse putObjectLockConfiguration(
            S3Client client, PutObjectLockConfigurationRequest request) {
        return client.putObjectLockConfiguration(request);
    }

    public static PutObjectRetentionResponse putObjectRetention(S3Client client, PutObjectRetentionRequest request) {
        return client.putObjectRetention(request);
    }

    public static RenameObjectResponse renameObject(S3Client client, RenameObjectRequest request) {
        return client.renameObject(request);
    }

    public static RestoreObjectResponse restoreObject(S3Client client, RestoreObjectRequest request) {
        return client.restoreObject(request);
    }

    public static UpdateObjectEncryptionResponse updateObjectEncryption(
            S3Client client, UpdateObjectEncryptionRequest request) {
        return client.updateObjectEncryption(request);
    }

    public static PutObjectResponse putObject(S3Client client, PutObjectRequest request, RequestBody requestBody) {
        return client.putObject(request, requestBody);
    }

    public static <T> T getObject(
            S3Client client, GetObjectRequest request, ResponseTransformer<GetObjectResponse, T> responseTransformer) {
        return client.getObject(request, responseTransformer);
    }

    public static GetObjectResponse getObject(S3Client client, GetObjectRequest request, Path destinationPath) {
        return client.getObject(request, destinationPath);
    }

    public static PutObjectResponse putObject(S3Client client, PutObjectRequest request, Path sourcePath) {
        return client.putObject(request, sourcePath);
    }

    public static <T> T getObjectAnnotation(
            S3Client client,
            GetObjectAnnotationRequest request,
            ResponseTransformer<GetObjectAnnotationResponse, T> responseTransformer) {
        return client.getObjectAnnotation(request, responseTransformer);
    }

    public static PutObjectAnnotationResponse putObjectAnnotation(
            S3Client client, PutObjectAnnotationRequest request, RequestBody requestBody) {
        return client.putObjectAnnotation(request, requestBody);
    }

    public static <T> T getObjectTorrent(
            S3Client client,
            GetObjectTorrentRequest request,
            ResponseTransformer<GetObjectTorrentResponse, T> responseTransformer) {
        return client.getObjectTorrent(request, responseTransformer);
    }

    public static WriteGetObjectResponseResponse writeGetObjectResponse(
            S3Client client, WriteGetObjectResponseRequest request, RequestBody requestBody) {
        return client.writeGetObjectResponse(request, requestBody);
    }
}
