package tungnn.tutor.java.aws.service.s3;

import java.nio.file.Path;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

public final class S3MultipartUtil {
    private S3MultipartUtil() {}

    public static AbortMultipartUploadResponse abortMultipartUpload(
            S3Client client, AbortMultipartUploadRequest request) {
        return client.abortMultipartUpload(request);
    }

    public static CompleteMultipartUploadResponse completeMultipartUpload(
            S3Client client, CompleteMultipartUploadRequest request) {
        return client.completeMultipartUpload(request);
    }

    public static CreateMultipartUploadResponse createMultipartUpload(
            S3Client client, CreateMultipartUploadRequest request) {
        return client.createMultipartUpload(request);
    }

    public static ListMultipartUploadsResponse listMultipartUploads(
            S3Client client, ListMultipartUploadsRequest request) {
        return client.listMultipartUploads(request);
    }

    public static ListPartsResponse listParts(S3Client client, ListPartsRequest request) {
        return client.listParts(request);
    }

    public static UploadPartCopyResponse uploadPartCopy(S3Client client, UploadPartCopyRequest request) {
        return client.uploadPartCopy(request);
    }

    public static UploadPartResponse uploadPart(S3Client client, UploadPartRequest request, RequestBody requestBody) {
        return client.uploadPart(request, requestBody);
    }

    public static UploadPartResponse uploadPart(S3Client client, UploadPartRequest request, Path sourcePath) {
        return client.uploadPart(request, sourcePath);
    }
}
