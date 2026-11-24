package tungnn.tutor.java.aws.service.kinesis;

import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.*;

public final class KinesisEncryptionUtil {
    private KinesisEncryptionUtil() {}

    public static StartStreamEncryptionResponse startStreamEncryption(
            KinesisClient client, StartStreamEncryptionRequest request) {
        return client.startStreamEncryption(request);
    }

    public static StopStreamEncryptionResponse stopStreamEncryption(
            KinesisClient client, StopStreamEncryptionRequest request) {
        return client.stopStreamEncryption(request);
    }
}
