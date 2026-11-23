package tungnn.tutor.java.aws.service.elbv2;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.elasticloadbalancingv2.ElasticLoadBalancingV2Client;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.*;

public final class ElbListenerUtil {
  private ElbListenerUtil() {}

  public static AddListenerCertificatesResponse addListenerCertificates(
      ElasticLoadBalancingV2Client client, AddListenerCertificatesRequest request) {
    return client.addListenerCertificates(request);
  }

  public static AddTrustStoreRevocationsResponse addTrustStoreRevocations(
      ElasticLoadBalancingV2Client client, AddTrustStoreRevocationsRequest request) {
    return client.addTrustStoreRevocations(request);
  }

  public static CreateListenerResponse createListener(
      ElasticLoadBalancingV2Client client, CreateListenerRequest request) {
    return client.createListener(request);
  }

  public static CreateTrustStoreResponse createTrustStore(
      ElasticLoadBalancingV2Client client, CreateTrustStoreRequest request) {
    return client.createTrustStore(request);
  }

  public static DeleteListenerResponse deleteListener(
      ElasticLoadBalancingV2Client client, DeleteListenerRequest request) {
    return client.deleteListener(request);
  }

  public static DeleteSharedTrustStoreAssociationResponse deleteSharedTrustStoreAssociation(
      ElasticLoadBalancingV2Client client, DeleteSharedTrustStoreAssociationRequest request) {
    return client.deleteSharedTrustStoreAssociation(request);
  }

  public static DeleteTrustStoreResponse deleteTrustStore(
      ElasticLoadBalancingV2Client client, DeleteTrustStoreRequest request) {
    return client.deleteTrustStore(request);
  }

  public static DescribeListenerAttributesResponse describeListenerAttributes(
      ElasticLoadBalancingV2Client client, DescribeListenerAttributesRequest request) {
    return client.describeListenerAttributes(request);
  }

  public static List<Certificate> describeListenerCertificates(
      ElasticLoadBalancingV2Client client, DescribeListenerCertificatesRequest request) {
    return client.describeListenerCertificatesPaginator(request).stream()
        .flatMap(r -> r.certificates().stream())
        .collect(Collectors.toList());
  }

  public static List<Listener> describeListeners(
      ElasticLoadBalancingV2Client client, DescribeListenersRequest request) {
    return client.describeListenersPaginator(request).stream()
        .flatMap(r -> r.listeners().stream())
        .collect(Collectors.toList());
  }

  public static List<TrustStoreAssociation> describeTrustStoreAssociations(
      ElasticLoadBalancingV2Client client, DescribeTrustStoreAssociationsRequest request) {
    return client.describeTrustStoreAssociationsPaginator(request).stream()
        .flatMap(r -> r.trustStoreAssociations().stream())
        .collect(Collectors.toList());
  }

  public static List<DescribeTrustStoreRevocation> describeTrustStoreRevocations(
      ElasticLoadBalancingV2Client client, DescribeTrustStoreRevocationsRequest request) {
    return client.describeTrustStoreRevocationsPaginator(request).stream()
        .flatMap(r -> r.trustStoreRevocations().stream())
        .collect(Collectors.toList());
  }

  public static List<TrustStore> describeTrustStores(
      ElasticLoadBalancingV2Client client, DescribeTrustStoresRequest request) {
    return client.describeTrustStoresPaginator(request).stream()
        .flatMap(r -> r.trustStores().stream())
        .collect(Collectors.toList());
  }

  public static GetTrustStoreCaCertificatesBundleResponse getTrustStoreCaCertificatesBundle(
      ElasticLoadBalancingV2Client client, GetTrustStoreCaCertificatesBundleRequest request) {
    return client.getTrustStoreCaCertificatesBundle(request);
  }

  public static GetTrustStoreRevocationContentResponse getTrustStoreRevocationContent(
      ElasticLoadBalancingV2Client client, GetTrustStoreRevocationContentRequest request) {
    return client.getTrustStoreRevocationContent(request);
  }

  public static ModifyListenerResponse modifyListener(
      ElasticLoadBalancingV2Client client, ModifyListenerRequest request) {
    return client.modifyListener(request);
  }

  public static ModifyListenerAttributesResponse modifyListenerAttributes(
      ElasticLoadBalancingV2Client client, ModifyListenerAttributesRequest request) {
    return client.modifyListenerAttributes(request);
  }

  public static ModifyTrustStoreResponse modifyTrustStore(
      ElasticLoadBalancingV2Client client, ModifyTrustStoreRequest request) {
    return client.modifyTrustStore(request);
  }

  public static RemoveListenerCertificatesResponse removeListenerCertificates(
      ElasticLoadBalancingV2Client client, RemoveListenerCertificatesRequest request) {
    return client.removeListenerCertificates(request);
  }

  public static RemoveTrustStoreRevocationsResponse removeTrustStoreRevocations(
      ElasticLoadBalancingV2Client client, RemoveTrustStoreRevocationsRequest request) {
    return client.removeTrustStoreRevocations(request);
  }
}
