package tungnn.tutor.java.aws.service.s3;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

public final class S3BucketPolicyUtil {
  private S3BucketPolicyUtil() {}

  public static DeleteBucketPolicyResponse deleteBucketPolicy(
      S3Client client, DeleteBucketPolicyRequest request) {
    return client.deleteBucketPolicy(request);
  }

  public static GetBucketPolicyResponse getBucketPolicy(
      S3Client client, GetBucketPolicyRequest request) {
    return client.getBucketPolicy(request);
  }

  public static GetBucketPolicyStatusResponse getBucketPolicyStatus(
      S3Client client, GetBucketPolicyStatusRequest request) {
    return client.getBucketPolicyStatus(request);
  }

  public static PutBucketPolicyResponse putBucketPolicy(
      S3Client client, PutBucketPolicyRequest request) {
    return client.putBucketPolicy(request);
  }
}
