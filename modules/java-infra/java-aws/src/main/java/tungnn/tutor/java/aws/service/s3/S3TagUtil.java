package tungnn.tutor.java.aws.service.s3;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

public final class S3TagUtil {
    private S3TagUtil() {}

    public static DeleteBucketTaggingResponse deleteBucketTagging(S3Client client, DeleteBucketTaggingRequest request) {
        return client.deleteBucketTagging(request);
    }

    public static DeleteObjectTaggingResponse deleteObjectTagging(S3Client client, DeleteObjectTaggingRequest request) {
        return client.deleteObjectTagging(request);
    }

    public static GetBucketTaggingResponse getBucketTagging(S3Client client, GetBucketTaggingRequest request) {
        return client.getBucketTagging(request);
    }

    public static GetObjectTaggingResponse getObjectTagging(S3Client client, GetObjectTaggingRequest request) {
        return client.getObjectTagging(request);
    }

    public static PutBucketTaggingResponse putBucketTagging(S3Client client, PutBucketTaggingRequest request) {
        return client.putBucketTagging(request);
    }

    public static PutObjectTaggingResponse putObjectTagging(S3Client client, PutObjectTaggingRequest request) {
        return client.putObjectTagging(request);
    }
}
