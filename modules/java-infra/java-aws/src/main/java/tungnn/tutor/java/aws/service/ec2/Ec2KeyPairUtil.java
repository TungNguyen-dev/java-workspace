package tungnn.tutor.java.aws.service.ec2;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

public final class Ec2KeyPairUtil {
    private Ec2KeyPairUtil() {}

    public static CreateKeyPairResponse createKeyPair(Ec2Client client, CreateKeyPairRequest request) {
        return client.createKeyPair(request);
    }

    public static DeleteKeyPairResponse deleteKeyPair(Ec2Client client, DeleteKeyPairRequest request) {
        return client.deleteKeyPair(request);
    }

    public static DescribeKeyPairsResponse describeKeyPairs(Ec2Client client, DescribeKeyPairsRequest request) {
        return client.describeKeyPairs(request);
    }

    public static ImportKeyPairResponse importKeyPair(Ec2Client client, ImportKeyPairRequest request) {
        return client.importKeyPair(request);
    }
}
