package tungnn.tutor.java.aws.service.kinesis;

import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.*;

public final class KinesisRecordUtil {
  private KinesisRecordUtil() {}

  public static GetRecordsResponse getRecords(KinesisClient client, GetRecordsRequest request) {
    return client.getRecords(request);
  }

  public static GetShardIteratorResponse getShardIterator(
      KinesisClient client, GetShardIteratorRequest request) {
    return client.getShardIterator(request);
  }

  public static PutRecordResponse putRecord(KinesisClient client, PutRecordRequest request) {
    return client.putRecord(request);
  }

  public static PutRecordsResponse putRecords(KinesisClient client, PutRecordsRequest request) {
    return client.putRecords(request);
  }
}
