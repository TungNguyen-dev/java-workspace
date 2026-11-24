package tungnn.tutor.java.aws.service.firehose;

import software.amazon.awssdk.services.firehose.FirehoseClient;
import software.amazon.awssdk.services.firehose.model.*;

public final class FirehoseEncryptionUtils {
    private FirehoseEncryptionUtils() {}

    public static StartDeliveryStreamEncryptionResponse startDeliveryStreamEncryption(
            FirehoseClient client, StartDeliveryStreamEncryptionRequest request) {
        return client.startDeliveryStreamEncryption(request);
    }

    public static StopDeliveryStreamEncryptionResponse stopDeliveryStreamEncryption(
            FirehoseClient client, StopDeliveryStreamEncryptionRequest request) {
        return client.stopDeliveryStreamEncryption(request);
    }
}
