// SesV2DeliverabilityUtil.java
package tungnn.tutor.java.aws.service.ses.v2;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

public final class SesV2DeliverabilityUtil {

  private SesV2DeliverabilityUtil() {}

  public static CreateDeliverabilityTestReportResponse createDeliverabilityTestReport(
      SesV2Client client, CreateDeliverabilityTestReportRequest request) {
    return client.createDeliverabilityTestReport(request);
  }

  public static GetDeliverabilityDashboardOptionsResponse getDeliverabilityDashboardOptions(
      SesV2Client client, GetDeliverabilityDashboardOptionsRequest request) {
    return client.getDeliverabilityDashboardOptions(request);
  }

  public static GetDeliverabilityTestReportResponse getDeliverabilityTestReport(
      SesV2Client client, GetDeliverabilityTestReportRequest request) {
    return client.getDeliverabilityTestReport(request);
  }

  public static GetDomainDeliverabilityCampaignResponse getDomainDeliverabilityCampaign(
      SesV2Client client, GetDomainDeliverabilityCampaignRequest request) {
    return client.getDomainDeliverabilityCampaign(request);
  }

  public static GetDomainStatisticsReportResponse getDomainStatisticsReport(
      SesV2Client client, GetDomainStatisticsReportRequest request) {
    return client.getDomainStatisticsReport(request);
  }

  public static List<DeliverabilityTestReport> listDeliverabilityTestReports(
      SesV2Client client, ListDeliverabilityTestReportsRequest request) {
    return client.listDeliverabilityTestReportsPaginator(request).stream()
        .flatMap(r -> r.deliverabilityTestReports().stream())
        .collect(Collectors.toList());
  }

  public static List<DomainDeliverabilityCampaign> listDomainDeliverabilityCampaigns(
      SesV2Client client, ListDomainDeliverabilityCampaignsRequest request) {
    return client.listDomainDeliverabilityCampaignsPaginator(request).stream()
        .flatMap(r -> r.domainDeliverabilityCampaigns().stream())
        .collect(Collectors.toList());
  }

  public static PutDeliverabilityDashboardOptionResponse putDeliverabilityDashboardOption(
      SesV2Client client, PutDeliverabilityDashboardOptionRequest request) {
    return client.putDeliverabilityDashboardOption(request);
  }
}
