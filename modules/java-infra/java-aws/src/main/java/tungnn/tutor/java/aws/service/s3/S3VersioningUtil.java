package tungnn.tutor.java.aws.service.s3;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

public final class S3VersioningUtil {
    private S3VersioningUtil() {}

    public static GetBucketVersioningResponse getBucketVersioning(S3Client client, GetBucketVersioningRequest request) {
        return client.getBucketVersioning(request);
    }

    public static PutBucketVersioningResponse putBucketVersioning(S3Client client, PutBucketVersioningRequest request) {
        return client.putBucketVersioning(request);
    }
}
