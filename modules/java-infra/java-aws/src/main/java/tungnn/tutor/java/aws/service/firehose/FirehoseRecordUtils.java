package tungnn.tutor.java.aws.service.firehose;

import software.amazon.awssdk.services.firehose.FirehoseClient;
import software.amazon.awssdk.services.firehose.model.*;

public final class FirehoseRecordUtils {
    private FirehoseRecordUtils() {}

    public static PutRecordResponse putRecord(FirehoseClient client, PutRecordRequest request) {
        return client.putRecord(request);
    }

    public static PutRecordBatchResponse putRecordBatch(FirehoseClient client, PutRecordBatchRequest request) {
        return client.putRecordBatch(request);
    }
}
