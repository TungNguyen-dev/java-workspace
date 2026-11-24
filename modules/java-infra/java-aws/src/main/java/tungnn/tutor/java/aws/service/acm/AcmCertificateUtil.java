package tungnn.tutor.java.aws.service.acm;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.acm.AcmClient;
import software.amazon.awssdk.services.acm.model.*;

public final class AcmCertificateUtil {
    private AcmCertificateUtil() {}

    public static DeleteCertificateResponse deleteCertificate(AcmClient client, DeleteCertificateRequest request) {
        return client.deleteCertificate(request);
    }

    public static DescribeCertificateResponse describeCertificate(
            AcmClient client, DescribeCertificateRequest request) {
        return client.describeCertificate(request);
    }

    public static ExportCertificateResponse exportCertificate(AcmClient client, ExportCertificateRequest request) {
        return client.exportCertificate(request);
    }

    public static GetAccountConfigurationResponse getAccountConfiguration(
            AcmClient client, GetAccountConfigurationRequest request) {
        return client.getAccountConfiguration(request);
    }

    public static GetCertificateResponse getCertificate(AcmClient client, GetCertificateRequest request) {
        return client.getCertificate(request);
    }

    public static ImportCertificateResponse importCertificate(AcmClient client, ImportCertificateRequest request) {
        return client.importCertificate(request);
    }

    public static List<CertificateSummary> listCertificates(AcmClient client, ListCertificatesRequest request) {
        return client.listCertificatesPaginator(request).stream()
                .flatMap(r -> r.certificateSummaryList().stream())
                .collect(Collectors.toList());
    }

    public static PutAccountConfigurationResponse putAccountConfiguration(
            AcmClient client, PutAccountConfigurationRequest request) {
        return client.putAccountConfiguration(request);
    }

    public static RenewCertificateResponse renewCertificate(AcmClient client, RenewCertificateRequest request) {
        return client.renewCertificate(request);
    }

    public static RequestCertificateResponse requestCertificate(AcmClient client, RequestCertificateRequest request) {
        return client.requestCertificate(request);
    }

    public static ResendValidationEmailResponse resendValidationEmail(
            AcmClient client, ResendValidationEmailRequest request) {
        return client.resendValidationEmail(request);
    }

    public static RevokeCertificateResponse revokeCertificate(AcmClient client, RevokeCertificateRequest request) {
        return client.revokeCertificate(request);
    }

    public static List<CertificateSearchResult> searchCertificates(
            AcmClient client, SearchCertificatesRequest request) {
        return client.searchCertificatesPaginator(request).stream()
                .flatMap(r -> r.results().stream())
                .collect(Collectors.toList());
    }

    public static UpdateCertificateOptionsResponse updateCertificateOptions(
            AcmClient client, UpdateCertificateOptionsRequest request) {
        return client.updateCertificateOptions(request);
    }
}
