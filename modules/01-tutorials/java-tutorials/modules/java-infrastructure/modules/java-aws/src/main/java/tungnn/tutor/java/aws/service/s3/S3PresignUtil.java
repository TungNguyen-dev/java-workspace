package tungnn.tutor.java.aws.service.s3;

import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.*;

public final class S3PresignUtil {
  private S3PresignUtil() {}

  public static PresignedAbortMultipartUploadRequest presignAbortMultipartUpload(
      S3Presigner presigner, AbortMultipartUploadPresignRequest request) {
    return presigner.presignAbortMultipartUpload(request);
  }

  public static PresignedCompleteMultipartUploadRequest presignCompleteMultipartUpload(
      S3Presigner presigner, CompleteMultipartUploadPresignRequest request) {
    return presigner.presignCompleteMultipartUpload(request);
  }

  public static PresignedCreateMultipartUploadRequest presignCreateMultipartUpload(
      S3Presigner presigner, CreateMultipartUploadPresignRequest request) {
    return presigner.presignCreateMultipartUpload(request);
  }

  public static PresignedDeleteObjectRequest presignDeleteObject(
      S3Presigner presigner, DeleteObjectPresignRequest request) {
    return presigner.presignDeleteObject(request);
  }

  public static PresignedGetObjectRequest presignGetObject(
      S3Presigner presigner, GetObjectPresignRequest request) {
    return presigner.presignGetObject(request);
  }

  public static PresignedHeadBucketRequest presignHeadBucket(
      S3Presigner presigner, HeadBucketPresignRequest request) {
    return presigner.presignHeadBucket(request);
  }

  public static PresignedHeadObjectRequest presignHeadObject(
      S3Presigner presigner, HeadObjectPresignRequest request) {
    return presigner.presignHeadObject(request);
  }

  public static PresignedPutObjectRequest presignPutObject(
      S3Presigner presigner, PutObjectPresignRequest request) {
    return presigner.presignPutObject(request);
  }

  public static PresignedUploadPartRequest presignUploadPart(
      S3Presigner presigner, UploadPartPresignRequest request) {
    return presigner.presignUploadPart(request);
  }
}
