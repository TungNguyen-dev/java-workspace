package tungnn.tutor.java.aws.service.cloudwatch;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;

public final class CloudWatchMetricUtil {
  private CloudWatchMetricUtil() {}

  public static AssociateDatasetKmsKeyResponse associateDatasetKmsKey(
      CloudWatchClient client, AssociateDatasetKmsKeyRequest request) {
    return client.associateDatasetKmsKey(request);
  }

  public static DeleteMetricStreamResponse deleteMetricStream(
      CloudWatchClient client, DeleteMetricStreamRequest request) {
    return client.deleteMetricStream(request);
  }

  public static DisassociateDatasetKmsKeyResponse disassociateDatasetKmsKey(
      CloudWatchClient client, DisassociateDatasetKmsKeyRequest request) {
    return client.disassociateDatasetKmsKey(request);
  }

  public static GetDatasetResponse getDataset(CloudWatchClient client, GetDatasetRequest request) {
    return client.getDataset(request);
  }

  public static List<MetricDataResult> getMetricData(
      CloudWatchClient client, GetMetricDataRequest request) {
    return client.getMetricDataPaginator(request).stream()
        .flatMap(r -> r.metricDataResults().stream())
        .collect(Collectors.toList());
  }

  public static GetMetricStatisticsResponse getMetricStatistics(
      CloudWatchClient client, GetMetricStatisticsRequest request) {
    return client.getMetricStatistics(request);
  }

  public static GetMetricStreamResponse getMetricStream(
      CloudWatchClient client, GetMetricStreamRequest request) {
    return client.getMetricStream(request);
  }

  public static GetMetricWidgetImageResponse getMetricWidgetImage(
      CloudWatchClient client, GetMetricWidgetImageRequest request) {
    return client.getMetricWidgetImage(request);
  }

  public static GetOTelEnrichmentResponse getOTelEnrichment(
      CloudWatchClient client, GetOTelEnrichmentRequest request) {
    return client.getOTelEnrichment(request);
  }

  public static ListMetricStreamsResponse listMetricStreams(
      CloudWatchClient client, ListMetricStreamsRequest request) {
    return client.listMetricStreams(request);
  }

  public static ListMetricsResponse listMetrics(
      CloudWatchClient client, ListMetricsRequest request) {
    return client.listMetrics(request);
  }

  public static PutMetricDataResponse putMetricData(
      CloudWatchClient client, PutMetricDataRequest request) {
    return client.putMetricData(request);
  }

  public static PutMetricStreamResponse putMetricStream(
      CloudWatchClient client, PutMetricStreamRequest request) {
    return client.putMetricStream(request);
  }

  public static StartMetricStreamsResponse startMetricStreams(
      CloudWatchClient client, StartMetricStreamsRequest request) {
    return client.startMetricStreams(request);
  }

  public static StartOTelEnrichmentResponse startOTelEnrichment(
      CloudWatchClient client, StartOTelEnrichmentRequest request) {
    return client.startOTelEnrichment(request);
  }

  public static StopMetricStreamsResponse stopMetricStreams(
      CloudWatchClient client, StopMetricStreamsRequest request) {
    return client.stopMetricStreams(request);
  }

  public static StopOTelEnrichmentResponse stopOTelEnrichment(
      CloudWatchClient client, StopOTelEnrichmentRequest request) {
    return client.stopOTelEnrichment(request);
  }
}
