package tungnn.tutor.java.aws.service.kms;

import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.*;

public final class KmsCryptoUtil {
    private KmsCryptoUtil() {}

    public static EncryptResponse encrypt(KmsClient client, EncryptRequest request) {
        return client.encrypt(request);
    }

    public static DecryptResponse decrypt(KmsClient client, DecryptRequest request) {
        return client.decrypt(request);
    }

    public static ReEncryptResponse reEncrypt(KmsClient client, ReEncryptRequest request) {
        return client.reEncrypt(request);
    }

    public static GenerateDataKeyResponse generateDataKey(KmsClient client, GenerateDataKeyRequest request) {
        return client.generateDataKey(request);
    }

    public static GenerateDataKeyWithoutPlaintextResponse generateDataKeyWithoutPlaintext(
            KmsClient client, GenerateDataKeyWithoutPlaintextRequest request) {
        return client.generateDataKeyWithoutPlaintext(request);
    }

    public static GenerateDataKeyPairResponse generateDataKeyPair(
            KmsClient client, GenerateDataKeyPairRequest request) {
        return client.generateDataKeyPair(request);
    }

    public static GenerateDataKeyPairWithoutPlaintextResponse generateDataKeyPairWithoutPlaintext(
            KmsClient client, GenerateDataKeyPairWithoutPlaintextRequest request) {
        return client.generateDataKeyPairWithoutPlaintext(request);
    }

    public static GenerateRandomResponse generateRandom(KmsClient client, GenerateRandomRequest request) {
        return client.generateRandom(request);
    }

    public static SignResponse sign(KmsClient client, SignRequest request) {
        return client.sign(request);
    }

    public static VerifyResponse verify(KmsClient client, VerifyRequest request) {
        return client.verify(request);
    }

    public static GenerateMacResponse generateMac(KmsClient client, GenerateMacRequest request) {
        return client.generateMac(request);
    }

    public static VerifyMacResponse verifyMac(KmsClient client, VerifyMacRequest request) {
        return client.verifyMac(request);
    }

    public static DeriveSharedSecretResponse deriveSharedSecret(KmsClient client, DeriveSharedSecretRequest request) {
        return client.deriveSharedSecret(request);
    }
}
