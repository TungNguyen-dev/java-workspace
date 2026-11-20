package tungnn.tutor.java.aws.service.s3;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

public final class S3LifecycleUtil {
  private S3LifecycleUtil() {}

  public static DeleteBucketLifecycleResponse deleteBucketLifecycle(
      S3Client client, DeleteBucketLifecycleRequest request) {
    return client.deleteBucketLifecycle(request);
  }

  public static GetBucketLifecycleConfigurationResponse getBucketLifecycleConfiguration(
      S3Client client, GetBucketLifecycleConfigurationRequest request) {
    return client.getBucketLifecycleConfiguration(request);
  }

  public static PutBucketLifecycleConfigurationResponse putBucketLifecycleConfiguration(
      S3Client client, PutBucketLifecycleConfigurationRequest request) {
    return client.putBucketLifecycleConfiguration(request);
  }
}
