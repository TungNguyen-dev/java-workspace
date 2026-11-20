package tungnn.tutor.java.aws.service.s3;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

public final class S3BucketUtil {
  private S3BucketUtil() {}

  public static CreateBucketResponse createBucket(S3Client client, CreateBucketRequest request) {
    return client.createBucket(request);
  }

  public static CreateBucketMetadataConfigurationResponse createBucketMetadataConfiguration(
      S3Client client, CreateBucketMetadataConfigurationRequest request) {
    return client.createBucketMetadataConfiguration(request);
  }

  public static CreateBucketMetadataTableConfigurationResponse
      createBucketMetadataTableConfiguration(
          S3Client client, CreateBucketMetadataTableConfigurationRequest request) {
    return client.createBucketMetadataTableConfiguration(request);
  }

  public static CreateSessionResponse createSession(S3Client client, CreateSessionRequest request) {
    return client.createSession(request);
  }

  public static DeleteBucketResponse deleteBucket(S3Client client, DeleteBucketRequest request) {
    return client.deleteBucket(request);
  }

  public static DeleteBucketAnalyticsConfigurationResponse deleteBucketAnalyticsConfiguration(
      S3Client client, DeleteBucketAnalyticsConfigurationRequest request) {
    return client.deleteBucketAnalyticsConfiguration(request);
  }

  public static DeleteBucketEncryptionResponse deleteBucketEncryption(
      S3Client client, DeleteBucketEncryptionRequest request) {
    return client.deleteBucketEncryption(request);
  }

  public static DeleteBucketIntelligentTieringConfigurationResponse
      deleteBucketIntelligentTieringConfiguration(
          S3Client client, DeleteBucketIntelligentTieringConfigurationRequest request) {
    return client.deleteBucketIntelligentTieringConfiguration(request);
  }

  public static DeleteBucketInventoryConfigurationResponse deleteBucketInventoryConfiguration(
      S3Client client, DeleteBucketInventoryConfigurationRequest request) {
    return client.deleteBucketInventoryConfiguration(request);
  }

  public static DeleteBucketMetadataConfigurationResponse deleteBucketMetadataConfiguration(
      S3Client client, DeleteBucketMetadataConfigurationRequest request) {
    return client.deleteBucketMetadataConfiguration(request);
  }

  public static DeleteBucketMetadataTableConfigurationResponse
      deleteBucketMetadataTableConfiguration(
          S3Client client, DeleteBucketMetadataTableConfigurationRequest request) {
    return client.deleteBucketMetadataTableConfiguration(request);
  }

  public static DeleteBucketMetricsConfigurationResponse deleteBucketMetricsConfiguration(
      S3Client client, DeleteBucketMetricsConfigurationRequest request) {
    return client.deleteBucketMetricsConfiguration(request);
  }

  public static DeleteBucketOwnershipControlsResponse deleteBucketOwnershipControls(
      S3Client client, DeleteBucketOwnershipControlsRequest request) {
    return client.deleteBucketOwnershipControls(request);
  }

  public static DeleteBucketReplicationResponse deleteBucketReplication(
      S3Client client, DeleteBucketReplicationRequest request) {
    return client.deleteBucketReplication(request);
  }

  public static DeleteBucketWebsiteResponse deleteBucketWebsite(
      S3Client client, DeleteBucketWebsiteRequest request) {
    return client.deleteBucketWebsite(request);
  }

  public static DeletePublicAccessBlockResponse deletePublicAccessBlock(
      S3Client client, DeletePublicAccessBlockRequest request) {
    return client.deletePublicAccessBlock(request);
  }

  public static GetBucketAbacResponse getBucketAbac(S3Client client, GetBucketAbacRequest request) {
    return client.getBucketAbac(request);
  }

  public static GetBucketAccelerateConfigurationResponse getBucketAccelerateConfiguration(
      S3Client client, GetBucketAccelerateConfigurationRequest request) {
    return client.getBucketAccelerateConfiguration(request);
  }

  public static GetBucketAclResponse getBucketAcl(S3Client client, GetBucketAclRequest request) {
    return client.getBucketAcl(request);
  }

  public static GetBucketAnalyticsConfigurationResponse getBucketAnalyticsConfiguration(
      S3Client client, GetBucketAnalyticsConfigurationRequest request) {
    return client.getBucketAnalyticsConfiguration(request);
  }

  public static GetBucketEncryptionResponse getBucketEncryption(
      S3Client client, GetBucketEncryptionRequest request) {
    return client.getBucketEncryption(request);
  }

  public static GetBucketIntelligentTieringConfigurationResponse
      getBucketIntelligentTieringConfiguration(
          S3Client client, GetBucketIntelligentTieringConfigurationRequest request) {
    return client.getBucketIntelligentTieringConfiguration(request);
  }

  public static GetBucketInventoryConfigurationResponse getBucketInventoryConfiguration(
      S3Client client, GetBucketInventoryConfigurationRequest request) {
    return client.getBucketInventoryConfiguration(request);
  }

  public static GetBucketLocationResponse getBucketLocation(
      S3Client client, GetBucketLocationRequest request) {
    return client.getBucketLocation(request);
  }

  public static GetBucketLoggingResponse getBucketLogging(
      S3Client client, GetBucketLoggingRequest request) {
    return client.getBucketLogging(request);
  }

  public static GetBucketMetadataConfigurationResponse getBucketMetadataConfiguration(
      S3Client client, GetBucketMetadataConfigurationRequest request) {
    return client.getBucketMetadataConfiguration(request);
  }

  public static GetBucketMetadataTableConfigurationResponse getBucketMetadataTableConfiguration(
      S3Client client, GetBucketMetadataTableConfigurationRequest request) {
    return client.getBucketMetadataTableConfiguration(request);
  }

  public static GetBucketMetricsConfigurationResponse getBucketMetricsConfiguration(
      S3Client client, GetBucketMetricsConfigurationRequest request) {
    return client.getBucketMetricsConfiguration(request);
  }

  public static GetBucketNotificationConfigurationResponse getBucketNotificationConfiguration(
      S3Client client, GetBucketNotificationConfigurationRequest request) {
    return client.getBucketNotificationConfiguration(request);
  }

  public static GetBucketOwnershipControlsResponse getBucketOwnershipControls(
      S3Client client, GetBucketOwnershipControlsRequest request) {
    return client.getBucketOwnershipControls(request);
  }

  public static GetBucketReplicationResponse getBucketReplication(
      S3Client client, GetBucketReplicationRequest request) {
    return client.getBucketReplication(request);
  }

  public static GetBucketRequestPaymentResponse getBucketRequestPayment(
      S3Client client, GetBucketRequestPaymentRequest request) {
    return client.getBucketRequestPayment(request);
  }

  public static GetBucketWebsiteResponse getBucketWebsite(
      S3Client client, GetBucketWebsiteRequest request) {
    return client.getBucketWebsite(request);
  }

  public static GetPublicAccessBlockResponse getPublicAccessBlock(
      S3Client client, GetPublicAccessBlockRequest request) {
    return client.getPublicAccessBlock(request);
  }

  public static HeadBucketResponse headBucket(S3Client client, HeadBucketRequest request) {
    return client.headBucket(request);
  }

  public static ListBucketAnalyticsConfigurationsResponse listBucketAnalyticsConfigurations(
      S3Client client, ListBucketAnalyticsConfigurationsRequest request) {
    return client.listBucketAnalyticsConfigurations(request);
  }

  public static ListBucketIntelligentTieringConfigurationsResponse
      listBucketIntelligentTieringConfigurations(
          S3Client client, ListBucketIntelligentTieringConfigurationsRequest request) {
    return client.listBucketIntelligentTieringConfigurations(request);
  }

  public static ListBucketInventoryConfigurationsResponse listBucketInventoryConfigurations(
      S3Client client, ListBucketInventoryConfigurationsRequest request) {
    return client.listBucketInventoryConfigurations(request);
  }

  public static ListBucketMetricsConfigurationsResponse listBucketMetricsConfigurations(
      S3Client client, ListBucketMetricsConfigurationsRequest request) {
    return client.listBucketMetricsConfigurations(request);
  }

  public static ListBucketsResponse listBuckets(S3Client client, ListBucketsRequest request) {
    return client.listBuckets(request);
  }

  public static ListDirectoryBucketsResponse listDirectoryBuckets(
      S3Client client, ListDirectoryBucketsRequest request) {
    return client.listDirectoryBuckets(request);
  }

  public static PutBucketAbacResponse putBucketAbac(S3Client client, PutBucketAbacRequest request) {
    return client.putBucketAbac(request);
  }

  public static PutBucketAccelerateConfigurationResponse putBucketAccelerateConfiguration(
      S3Client client, PutBucketAccelerateConfigurationRequest request) {
    return client.putBucketAccelerateConfiguration(request);
  }

  public static PutBucketAclResponse putBucketAcl(S3Client client, PutBucketAclRequest request) {
    return client.putBucketAcl(request);
  }

  public static PutBucketAnalyticsConfigurationResponse putBucketAnalyticsConfiguration(
      S3Client client, PutBucketAnalyticsConfigurationRequest request) {
    return client.putBucketAnalyticsConfiguration(request);
  }

  public static PutBucketEncryptionResponse putBucketEncryption(
      S3Client client, PutBucketEncryptionRequest request) {
    return client.putBucketEncryption(request);
  }

  public static PutBucketIntelligentTieringConfigurationResponse
      putBucketIntelligentTieringConfiguration(
          S3Client client, PutBucketIntelligentTieringConfigurationRequest request) {
    return client.putBucketIntelligentTieringConfiguration(request);
  }

  public static PutBucketInventoryConfigurationResponse putBucketInventoryConfiguration(
      S3Client client, PutBucketInventoryConfigurationRequest request) {
    return client.putBucketInventoryConfiguration(request);
  }

  public static PutBucketLoggingResponse putBucketLogging(
      S3Client client, PutBucketLoggingRequest request) {
    return client.putBucketLogging(request);
  }

  public static PutBucketMetricsConfigurationResponse putBucketMetricsConfiguration(
      S3Client client, PutBucketMetricsConfigurationRequest request) {
    return client.putBucketMetricsConfiguration(request);
  }

  public static PutBucketNotificationConfigurationResponse putBucketNotificationConfiguration(
      S3Client client, PutBucketNotificationConfigurationRequest request) {
    return client.putBucketNotificationConfiguration(request);
  }

  public static PutBucketOwnershipControlsResponse putBucketOwnershipControls(
      S3Client client, PutBucketOwnershipControlsRequest request) {
    return client.putBucketOwnershipControls(request);
  }

  public static PutBucketReplicationResponse putBucketReplication(
      S3Client client, PutBucketReplicationRequest request) {
    return client.putBucketReplication(request);
  }

  public static PutBucketRequestPaymentResponse putBucketRequestPayment(
      S3Client client, PutBucketRequestPaymentRequest request) {
    return client.putBucketRequestPayment(request);
  }

  public static PutBucketWebsiteResponse putBucketWebsite(
      S3Client client, PutBucketWebsiteRequest request) {
    return client.putBucketWebsite(request);
  }

  public static PutPublicAccessBlockResponse putPublicAccessBlock(
      S3Client client, PutPublicAccessBlockRequest request) {
    return client.putPublicAccessBlock(request);
  }

  public static UpdateBucketMetadataAnnotationTableConfigurationResponse
      updateBucketMetadataAnnotationTableConfiguration(
          S3Client client, UpdateBucketMetadataAnnotationTableConfigurationRequest request) {
    return client.updateBucketMetadataAnnotationTableConfiguration(request);
  }

  public static UpdateBucketMetadataInventoryTableConfigurationResponse
      updateBucketMetadataInventoryTableConfiguration(
          S3Client client, UpdateBucketMetadataInventoryTableConfigurationRequest request) {
    return client.updateBucketMetadataInventoryTableConfiguration(request);
  }

  public static UpdateBucketMetadataJournalTableConfigurationResponse
      updateBucketMetadataJournalTableConfiguration(
          S3Client client, UpdateBucketMetadataJournalTableConfigurationRequest request) {
    return client.updateBucketMetadataJournalTableConfiguration(request);
  }
}
