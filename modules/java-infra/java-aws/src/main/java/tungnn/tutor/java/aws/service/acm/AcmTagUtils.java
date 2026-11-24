package tungnn.tutor.java.aws.service.acm;

import software.amazon.awssdk.services.acm.AcmClient;
import software.amazon.awssdk.services.acm.model.*;

public final class AcmTagUtils {
    private AcmTagUtils() {}

    public static AddTagsToCertificateResponse addTagsToCertificate(
            AcmClient client, AddTagsToCertificateRequest request) {
        return client.addTagsToCertificate(request);
    }

    public static ListTagsForCertificateResponse listTagsForCertificate(
            AcmClient client, ListTagsForCertificateRequest request) {
        return client.listTagsForCertificate(request);
    }

    public static RemoveTagsFromCertificateResponse removeTagsFromCertificate(
            AcmClient client, RemoveTagsFromCertificateRequest request) {
        return client.removeTagsFromCertificate(request);
    }
}
