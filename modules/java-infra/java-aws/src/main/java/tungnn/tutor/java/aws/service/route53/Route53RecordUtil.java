package tungnn.tutor.java.aws.service.route53;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.route53.Route53Client;
import software.amazon.awssdk.services.route53.model.*;

public final class Route53RecordUtil {
    private Route53RecordUtil() {}

    public static ChangeCidrCollectionResponse changeCidrCollection(
            Route53Client client, ChangeCidrCollectionRequest request) {
        return client.changeCidrCollection(request);
    }

    public static ChangeResourceRecordSetsResponse changeResourceRecordSets(
            Route53Client client, ChangeResourceRecordSetsRequest request) {
        return client.changeResourceRecordSets(request);
    }

    public static CreateCidrCollectionResponse createCidrCollection(
            Route53Client client, CreateCidrCollectionRequest request) {
        return client.createCidrCollection(request);
    }

    public static DeleteCidrCollectionResponse deleteCidrCollection(
            Route53Client client, DeleteCidrCollectionRequest request) {
        return client.deleteCidrCollection(request);
    }

    public static GetChangeResponse getChange(Route53Client client, GetChangeRequest request) {
        return client.getChange(request);
    }

    public static List<CidrBlockSummary> listCidrBlocks(Route53Client client, ListCidrBlocksRequest request) {
        return client.listCidrBlocksPaginator(request).stream()
                .flatMap(r -> r.cidrBlocks().stream())
                .collect(Collectors.toList());
    }

    public static List<CollectionSummary> listCidrCollections(
            Route53Client client, ListCidrCollectionsRequest request) {
        return client.listCidrCollectionsPaginator(request).stream()
                .flatMap(r -> r.cidrCollections().stream())
                .collect(Collectors.toList());
    }

    public static List<LocationSummary> listCidrLocations(Route53Client client, ListCidrLocationsRequest request) {
        return client.listCidrLocationsPaginator(request).stream()
                .flatMap(r -> r.cidrLocations().stream())
                .collect(Collectors.toList());
    }

    public static List<ResourceRecordSet> listResourceRecordSets(
            Route53Client client, ListResourceRecordSetsRequest request) {
        return client.listResourceRecordSetsPaginator(request).stream()
                .flatMap(r -> r.resourceRecordSets().stream())
                .collect(Collectors.toList());
    }

    public static TestDnsAnswerResponse testDNSAnswer(Route53Client client, TestDnsAnswerRequest request) {
        return client.testDNSAnswer(request);
    }
}
