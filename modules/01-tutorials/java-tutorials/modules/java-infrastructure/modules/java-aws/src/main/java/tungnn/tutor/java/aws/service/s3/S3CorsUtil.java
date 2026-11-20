package tungnn.tutor.java.aws.service.s3;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

public final class S3CorsUtil {
  private S3CorsUtil() {}

  public static DeleteBucketCorsResponse deleteBucketCors(
      S3Client client, DeleteBucketCorsRequest request) {
    return client.deleteBucketCors(request);
  }

  public static GetBucketCorsResponse getBucketCors(S3Client client, GetBucketCorsRequest request) {
    return client.getBucketCors(request);
  }

  public static PutBucketCorsResponse putBucketCors(S3Client client, PutBucketCorsRequest request) {
    return client.putBucketCors(request);
  }
}
