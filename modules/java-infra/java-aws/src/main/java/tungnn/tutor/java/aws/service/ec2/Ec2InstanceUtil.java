package tungnn.tutor.java.aws.service.ec2;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

public final class Ec2InstanceUtil {
    private Ec2InstanceUtil() {}

    public static AcceptReservedInstancesExchangeQuoteResponse acceptReservedInstancesExchangeQuote(
            Ec2Client client, AcceptReservedInstancesExchangeQuoteRequest request) {
        return client.acceptReservedInstancesExchangeQuote(request);
    }

    public static AllocateHostsResponse allocateHosts(Ec2Client client, AllocateHostsRequest request) {
        return client.allocateHosts(request);
    }

    public static AssociateIamInstanceProfileResponse associateIamInstanceProfile(
            Ec2Client client, AssociateIamInstanceProfileRequest request) {
        return client.associateIamInstanceProfile(request);
    }

    public static AssociateInstanceEventWindowResponse associateInstanceEventWindow(
            Ec2Client client, AssociateInstanceEventWindowRequest request) {
        return client.associateInstanceEventWindow(request);
    }

    public static BundleInstanceResponse bundleInstance(Ec2Client client, BundleInstanceRequest request) {
        return client.bundleInstance(request);
    }

    public static CancelCapacityReservationFleetsResponse cancelCapacityReservationFleets(
            Ec2Client client, CancelCapacityReservationFleetsRequest request) {
        return client.cancelCapacityReservationFleets(request);
    }

    public static CancelReservedInstancesListingResponse cancelReservedInstancesListing(
            Ec2Client client, CancelReservedInstancesListingRequest request) {
        return client.cancelReservedInstancesListing(request);
    }

    public static CancelSpotFleetRequestsResponse cancelSpotFleetRequests(
            Ec2Client client, CancelSpotFleetRequestsRequest request) {
        return client.cancelSpotFleetRequests(request);
    }

    public static CancelSpotInstanceRequestsResponse cancelSpotInstanceRequests(
            Ec2Client client, CancelSpotInstanceRequestsRequest request) {
        return client.cancelSpotInstanceRequests(request);
    }

    public static ConfirmProductInstanceResponse confirmProductInstance(
            Ec2Client client, ConfirmProductInstanceRequest request) {
        return client.confirmProductInstance(request);
    }

    public static CreateCapacityReservationFleetResponse createCapacityReservationFleet(
            Ec2Client client, CreateCapacityReservationFleetRequest request) {
        return client.createCapacityReservationFleet(request);
    }

    public static CreateFleetResponse createFleet(Ec2Client client, CreateFleetRequest request) {
        return client.createFleet(request);
    }

    public static CreateInstanceConnectEndpointResponse createInstanceConnectEndpoint(
            Ec2Client client, CreateInstanceConnectEndpointRequest request) {
        return client.createInstanceConnectEndpoint(request);
    }

    public static CreateInstanceEventWindowResponse createInstanceEventWindow(
            Ec2Client client, CreateInstanceEventWindowRequest request) {
        return client.createInstanceEventWindow(request);
    }

    public static CreateInstanceExportTaskResponse createInstanceExportTask(
            Ec2Client client, CreateInstanceExportTaskRequest request) {
        return client.createInstanceExportTask(request);
    }

    public static CreateLaunchTemplateResponse createLaunchTemplate(
            Ec2Client client, CreateLaunchTemplateRequest request) {
        return client.createLaunchTemplate(request);
    }

    public static CreateLaunchTemplateVersionResponse createLaunchTemplateVersion(
            Ec2Client client, CreateLaunchTemplateVersionRequest request) {
        return client.createLaunchTemplateVersion(request);
    }

    public static CreatePlacementGroupResponse createPlacementGroup(
            Ec2Client client, CreatePlacementGroupRequest request) {
        return client.createPlacementGroup(request);
    }

    public static CreateReservedInstancesListingResponse createReservedInstancesListing(
            Ec2Client client, CreateReservedInstancesListingRequest request) {
        return client.createReservedInstancesListing(request);
    }

    public static CreateSpotDatafeedSubscriptionResponse createSpotDatafeedSubscription(
            Ec2Client client, CreateSpotDatafeedSubscriptionRequest request) {
        return client.createSpotDatafeedSubscription(request);
    }

    public static DeleteFleetsResponse deleteFleets(Ec2Client client, DeleteFleetsRequest request) {
        return client.deleteFleets(request);
    }

    public static DeleteInstanceConnectEndpointResponse deleteInstanceConnectEndpoint(
            Ec2Client client, DeleteInstanceConnectEndpointRequest request) {
        return client.deleteInstanceConnectEndpoint(request);
    }

    public static DeleteInstanceEventWindowResponse deleteInstanceEventWindow(
            Ec2Client client, DeleteInstanceEventWindowRequest request) {
        return client.deleteInstanceEventWindow(request);
    }

    public static DeleteLaunchTemplateResponse deleteLaunchTemplate(
            Ec2Client client, DeleteLaunchTemplateRequest request) {
        return client.deleteLaunchTemplate(request);
    }

    public static DeleteLaunchTemplateVersionsResponse deleteLaunchTemplateVersions(
            Ec2Client client, DeleteLaunchTemplateVersionsRequest request) {
        return client.deleteLaunchTemplateVersions(request);
    }

    public static DeletePlacementGroupResponse deletePlacementGroup(
            Ec2Client client, DeletePlacementGroupRequest request) {
        return client.deletePlacementGroup(request);
    }

    public static DeleteQueuedReservedInstancesResponse deleteQueuedReservedInstances(
            Ec2Client client, DeleteQueuedReservedInstancesRequest request) {
        return client.deleteQueuedReservedInstances(request);
    }

    public static DeleteSpotDatafeedSubscriptionResponse deleteSpotDatafeedSubscription(
            Ec2Client client, DeleteSpotDatafeedSubscriptionRequest request) {
        return client.deleteSpotDatafeedSubscription(request);
    }

    public static DeregisterInstanceEventNotificationAttributesResponse deregisterInstanceEventNotificationAttributes(
            Ec2Client client, DeregisterInstanceEventNotificationAttributesRequest request) {
        return client.deregisterInstanceEventNotificationAttributes(request);
    }

    public static List<CapacityReservationFleet> describeCapacityReservationFleets(
            Ec2Client client, DescribeCapacityReservationFleetsRequest request) {
        return client.describeCapacityReservationFleetsPaginator(request).stream()
                .flatMap(r -> r.capacityReservationFleets().stream())
                .collect(Collectors.toList());
    }

    public static DescribeFleetHistoryResponse describeFleetHistory(
            Ec2Client client, DescribeFleetHistoryRequest request) {
        return client.describeFleetHistory(request);
    }

    public static DescribeFleetInstancesResponse describeFleetInstances(
            Ec2Client client, DescribeFleetInstancesRequest request) {
        return client.describeFleetInstances(request);
    }

    public static List<FleetData> describeFleets(Ec2Client client, DescribeFleetsRequest request) {
        return client.describeFleetsPaginator(request).stream()
                .flatMap(r -> r.fleets().stream())
                .collect(Collectors.toList());
    }

    public static List<Host> describeHosts(Ec2Client client, DescribeHostsRequest request) {
        return client.describeHostsPaginator(request).stream()
                .flatMap(r -> r.hosts().stream())
                .collect(Collectors.toList());
    }

    public static List<IamInstanceProfileAssociation> describeIamInstanceProfileAssociations(
            Ec2Client client, DescribeIamInstanceProfileAssociationsRequest request) {
        return client.describeIamInstanceProfileAssociationsPaginator(request).stream()
                .flatMap(r -> r.iamInstanceProfileAssociations().stream())
                .collect(Collectors.toList());
    }

    public static DescribeInstanceAttributeResponse describeInstanceAttribute(
            Ec2Client client, DescribeInstanceAttributeRequest request) {
        return client.describeInstanceAttribute(request);
    }

    public static List<Ec2InstanceConnectEndpoint> describeInstanceConnectEndpoints(
            Ec2Client client, DescribeInstanceConnectEndpointsRequest request) {
        return client.describeInstanceConnectEndpointsPaginator(request).stream()
                .flatMap(r -> r.instanceConnectEndpoints().stream())
                .collect(Collectors.toList());
    }

    public static List<InstanceCreditSpecification> describeInstanceCreditSpecifications(
            Ec2Client client, DescribeInstanceCreditSpecificationsRequest request) {
        return client.describeInstanceCreditSpecificationsPaginator(request).stream()
                .flatMap(r -> r.instanceCreditSpecifications().stream())
                .collect(Collectors.toList());
    }

    public static DescribeInstanceEventNotificationAttributesResponse describeInstanceEventNotificationAttributes(
            Ec2Client client, DescribeInstanceEventNotificationAttributesRequest request) {
        return client.describeInstanceEventNotificationAttributes(request);
    }

    public static List<InstanceEventWindow> describeInstanceEventWindows(
            Ec2Client client, DescribeInstanceEventWindowsRequest request) {
        return client.describeInstanceEventWindowsPaginator(request).stream()
                .flatMap(r -> r.instanceEventWindows().stream())
                .collect(Collectors.toList());
    }

    public static DescribeInstanceSqlHaHistoryStatesResponse describeInstanceSqlHaHistoryStates(
            Ec2Client client, DescribeInstanceSqlHaHistoryStatesRequest request) {
        return client.describeInstanceSqlHaHistoryStates(request);
    }

    public static DescribeInstanceSqlHaStatesResponse describeInstanceSqlHaStates(
            Ec2Client client, DescribeInstanceSqlHaStatesRequest request) {
        return client.describeInstanceSqlHaStates(request);
    }

    public static List<InstanceStatus> describeInstanceStatus(Ec2Client client, DescribeInstanceStatusRequest request) {
        return client.describeInstanceStatusPaginator(request).stream()
                .flatMap(r -> r.instanceStatuses().stream())
                .collect(Collectors.toList());
    }

    public static List<InstanceTopology> describeInstanceTopology(
            Ec2Client client, DescribeInstanceTopologyRequest request) {
        return client.describeInstanceTopologyPaginator(request).stream()
                .flatMap(r -> r.instances().stream())
                .collect(Collectors.toList());
    }

    public static List<InstanceTypeOffering> describeInstanceTypeOfferings(
            Ec2Client client, DescribeInstanceTypeOfferingsRequest request) {
        return client.describeInstanceTypeOfferingsPaginator(request).stream()
                .flatMap(r -> r.instanceTypeOfferings().stream())
                .collect(Collectors.toList());
    }

    public static List<InstanceTypeInfo> describeInstanceTypes(Ec2Client client, DescribeInstanceTypesRequest request) {
        return client.describeInstanceTypesPaginator(request).stream()
                .flatMap(r -> r.instanceTypes().stream())
                .collect(Collectors.toList());
    }

    public static List<Reservation> describeInstances(Ec2Client client, DescribeInstancesRequest request) {
        return client.describeInstancesPaginator(request).stream()
                .flatMap(r -> r.reservations().stream())
                .collect(Collectors.toList());
    }

    public static List<LaunchTemplateVersion> describeLaunchTemplateVersions(
            Ec2Client client, DescribeLaunchTemplateVersionsRequest request) {
        return client.describeLaunchTemplateVersionsPaginator(request).stream()
                .flatMap(r -> r.launchTemplateVersions().stream())
                .collect(Collectors.toList());
    }

    public static List<LaunchTemplate> describeLaunchTemplates(
            Ec2Client client, DescribeLaunchTemplatesRequest request) {
        return client.describeLaunchTemplatesPaginator(request).stream()
                .flatMap(r -> r.launchTemplates().stream())
                .collect(Collectors.toList());
    }

    public static List<MacHost> describeMacHosts(Ec2Client client, DescribeMacHostsRequest request) {
        return client.describeMacHostsPaginator(request).stream()
                .flatMap(r -> r.macHosts().stream())
                .collect(Collectors.toList());
    }

    public static DescribePlacementGroupsResponse describePlacementGroups(
            Ec2Client client, DescribePlacementGroupsRequest request) {
        return client.describePlacementGroups(request);
    }

    public static DescribeReservedInstancesResponse describeReservedInstances(
            Ec2Client client, DescribeReservedInstancesRequest request) {
        return client.describeReservedInstances(request);
    }

    public static DescribeReservedInstancesListingsResponse describeReservedInstancesListings(
            Ec2Client client, DescribeReservedInstancesListingsRequest request) {
        return client.describeReservedInstancesListings(request);
    }

    public static List<ReservedInstancesModification> describeReservedInstancesModifications(
            Ec2Client client, DescribeReservedInstancesModificationsRequest request) {
        return client.describeReservedInstancesModificationsPaginator(request).stream()
                .flatMap(r -> r.reservedInstancesModifications().stream())
                .collect(Collectors.toList());
    }

    public static List<ReservedInstancesOffering> describeReservedInstancesOfferings(
            Ec2Client client, DescribeReservedInstancesOfferingsRequest request) {
        return client.describeReservedInstancesOfferingsPaginator(request).stream()
                .flatMap(r -> r.reservedInstancesOfferings().stream())
                .collect(Collectors.toList());
    }

    public static List<ScheduledInstanceAvailability> describeScheduledInstanceAvailability(
            Ec2Client client, DescribeScheduledInstanceAvailabilityRequest request) {
        return client.describeScheduledInstanceAvailabilityPaginator(request).stream()
                .flatMap(r -> r.scheduledInstanceAvailabilitySet().stream())
                .collect(Collectors.toList());
    }

    public static List<ScheduledInstance> describeScheduledInstances(
            Ec2Client client, DescribeScheduledInstancesRequest request) {
        return client.describeScheduledInstancesPaginator(request).stream()
                .flatMap(r -> r.scheduledInstanceSet().stream())
                .collect(Collectors.toList());
    }

    public static DescribeSpotDatafeedSubscriptionResponse describeSpotDatafeedSubscription(
            Ec2Client client, DescribeSpotDatafeedSubscriptionRequest request) {
        return client.describeSpotDatafeedSubscription(request);
    }

    public static DescribeSpotFleetInstancesResponse describeSpotFleetInstances(
            Ec2Client client, DescribeSpotFleetInstancesRequest request) {
        return client.describeSpotFleetInstances(request);
    }

    public static DescribeSpotFleetRequestHistoryResponse describeSpotFleetRequestHistory(
            Ec2Client client, DescribeSpotFleetRequestHistoryRequest request) {
        return client.describeSpotFleetRequestHistory(request);
    }

    public static List<SpotFleetRequestConfig> describeSpotFleetRequests(
            Ec2Client client, DescribeSpotFleetRequestsRequest request) {
        return client.describeSpotFleetRequestsPaginator(request).stream()
                .flatMap(r -> r.spotFleetRequestConfigs().stream())
                .collect(Collectors.toList());
    }

    public static List<SpotInstanceRequest> describeSpotInstanceRequests(
            Ec2Client client, DescribeSpotInstanceRequestsRequest request) {
        return client.describeSpotInstanceRequestsPaginator(request).stream()
                .flatMap(r -> r.spotInstanceRequests().stream())
                .collect(Collectors.toList());
    }

    public static List<SpotPrice> describeSpotPriceHistory(Ec2Client client, DescribeSpotPriceHistoryRequest request) {
        return client.describeSpotPriceHistoryPaginator(request).stream()
                .flatMap(r -> r.spotPriceHistory().stream())
                .collect(Collectors.toList());
    }

    public static DisableInstanceSqlHaStandbyDetectionsResponse disableInstanceSqlHaStandbyDetections(
            Ec2Client client, DisableInstanceSqlHaStandbyDetectionsRequest request) {
        return client.disableInstanceSqlHaStandbyDetections(request);
    }

    public static DisassociateIamInstanceProfileResponse disassociateIamInstanceProfile(
            Ec2Client client, DisassociateIamInstanceProfileRequest request) {
        return client.disassociateIamInstanceProfile(request);
    }

    public static DisassociateInstanceEventWindowResponse disassociateInstanceEventWindow(
            Ec2Client client, DisassociateInstanceEventWindowRequest request) {
        return client.disassociateInstanceEventWindow(request);
    }

    public static EnableInstanceSqlHaStandbyDetectionsResponse enableInstanceSqlHaStandbyDetections(
            Ec2Client client, EnableInstanceSqlHaStandbyDetectionsRequest request) {
        return client.enableInstanceSqlHaStandbyDetections(request);
    }

    public static GetConsoleOutputResponse getConsoleOutput(Ec2Client client, GetConsoleOutputRequest request) {
        return client.getConsoleOutput(request);
    }

    public static GetConsoleScreenshotResponse getConsoleScreenshot(
            Ec2Client client, GetConsoleScreenshotRequest request) {
        return client.getConsoleScreenshot(request);
    }

    public static GetInstanceMetadataDefaultsResponse getInstanceMetadataDefaults(
            Ec2Client client, GetInstanceMetadataDefaultsRequest request) {
        return client.getInstanceMetadataDefaults(request);
    }

    public static GetInstanceTpmEkPubResponse getInstanceTpmEkPub(
            Ec2Client client, GetInstanceTpmEkPubRequest request) {
        return client.getInstanceTpmEkPub(request);
    }

    public static List<InstanceTypeInfoFromInstanceRequirements> getInstanceTypesFromInstanceRequirements(
            Ec2Client client, GetInstanceTypesFromInstanceRequirementsRequest request) {
        return client.getInstanceTypesFromInstanceRequirementsPaginator(request).stream()
                .flatMap(r -> r.instanceTypes().stream())
                .collect(Collectors.toList());
    }

    public static GetInstanceUefiDataResponse getInstanceUefiData(
            Ec2Client client, GetInstanceUefiDataRequest request) {
        return client.getInstanceUefiData(request);
    }

    public static GetLaunchTemplateDataResponse getLaunchTemplateData(
            Ec2Client client, GetLaunchTemplateDataRequest request) {
        return client.getLaunchTemplateData(request);
    }

    public static GetPasswordDataResponse getPasswordData(Ec2Client client, GetPasswordDataRequest request) {
        return client.getPasswordData(request);
    }

    public static GetReservedInstancesExchangeQuoteResponse getReservedInstancesExchangeQuote(
            Ec2Client client, GetReservedInstancesExchangeQuoteRequest request) {
        return client.getReservedInstancesExchangeQuote(request);
    }

    public static List<SpotPlacementScore> getSpotPlacementScores(
            Ec2Client client, GetSpotPlacementScoresRequest request) {
        return client.getSpotPlacementScoresPaginator(request).stream()
                .flatMap(r -> r.spotPlacementScores().stream())
                .collect(Collectors.toList());
    }

    public static ImportInstanceResponse importInstance(Ec2Client client, ImportInstanceRequest request) {
        return client.importInstance(request);
    }

    public static ModifyCapacityReservationFleetResponse modifyCapacityReservationFleet(
            Ec2Client client, ModifyCapacityReservationFleetRequest request) {
        return client.modifyCapacityReservationFleet(request);
    }

    public static ModifyFleetResponse modifyFleet(Ec2Client client, ModifyFleetRequest request) {
        return client.modifyFleet(request);
    }

    public static ModifyHostsResponse modifyHosts(Ec2Client client, ModifyHostsRequest request) {
        return client.modifyHosts(request);
    }

    public static ModifyInstanceAttributeResponse modifyInstanceAttribute(
            Ec2Client client, ModifyInstanceAttributeRequest request) {
        return client.modifyInstanceAttribute(request);
    }

    public static ModifyInstanceCapacityReservationAttributesResponse modifyInstanceCapacityReservationAttributes(
            Ec2Client client, ModifyInstanceCapacityReservationAttributesRequest request) {
        return client.modifyInstanceCapacityReservationAttributes(request);
    }

    public static ModifyInstanceConnectEndpointResponse modifyInstanceConnectEndpoint(
            Ec2Client client, ModifyInstanceConnectEndpointRequest request) {
        return client.modifyInstanceConnectEndpoint(request);
    }

    public static ModifyInstanceCpuOptionsResponse modifyInstanceCpuOptions(
            Ec2Client client, ModifyInstanceCpuOptionsRequest request) {
        return client.modifyInstanceCpuOptions(request);
    }

    public static ModifyInstanceCreditSpecificationResponse modifyInstanceCreditSpecification(
            Ec2Client client, ModifyInstanceCreditSpecificationRequest request) {
        return client.modifyInstanceCreditSpecification(request);
    }

    public static ModifyInstanceEventStartTimeResponse modifyInstanceEventStartTime(
            Ec2Client client, ModifyInstanceEventStartTimeRequest request) {
        return client.modifyInstanceEventStartTime(request);
    }

    public static ModifyInstanceEventWindowResponse modifyInstanceEventWindow(
            Ec2Client client, ModifyInstanceEventWindowRequest request) {
        return client.modifyInstanceEventWindow(request);
    }

    public static ModifyInstanceMaintenanceOptionsResponse modifyInstanceMaintenanceOptions(
            Ec2Client client, ModifyInstanceMaintenanceOptionsRequest request) {
        return client.modifyInstanceMaintenanceOptions(request);
    }

    public static ModifyInstanceMetadataDefaultsResponse modifyInstanceMetadataDefaults(
            Ec2Client client, ModifyInstanceMetadataDefaultsRequest request) {
        return client.modifyInstanceMetadataDefaults(request);
    }

    public static ModifyInstanceMetadataOptionsResponse modifyInstanceMetadataOptions(
            Ec2Client client, ModifyInstanceMetadataOptionsRequest request) {
        return client.modifyInstanceMetadataOptions(request);
    }

    public static ModifyInstanceNetworkPerformanceOptionsResponse modifyInstanceNetworkPerformanceOptions(
            Ec2Client client, ModifyInstanceNetworkPerformanceOptionsRequest request) {
        return client.modifyInstanceNetworkPerformanceOptions(request);
    }

    public static ModifyInstancePlacementResponse modifyInstancePlacement(
            Ec2Client client, ModifyInstancePlacementRequest request) {
        return client.modifyInstancePlacement(request);
    }

    public static ModifyLaunchTemplateResponse modifyLaunchTemplate(
            Ec2Client client, ModifyLaunchTemplateRequest request) {
        return client.modifyLaunchTemplate(request);
    }

    public static ModifyReservedInstancesResponse modifyReservedInstances(
            Ec2Client client, ModifyReservedInstancesRequest request) {
        return client.modifyReservedInstances(request);
    }

    public static ModifySpotFleetRequestResponse modifySpotFleetRequest(
            Ec2Client client, ModifySpotFleetRequestRequest request) {
        return client.modifySpotFleetRequest(request);
    }

    public static MonitorInstancesResponse monitorInstances(Ec2Client client, MonitorInstancesRequest request) {
        return client.monitorInstances(request);
    }

    public static MoveCapacityReservationInstancesResponse moveCapacityReservationInstances(
            Ec2Client client, MoveCapacityReservationInstancesRequest request) {
        return client.moveCapacityReservationInstances(request);
    }

    public static PurchaseReservedInstancesOfferingResponse purchaseReservedInstancesOffering(
            Ec2Client client, PurchaseReservedInstancesOfferingRequest request) {
        return client.purchaseReservedInstancesOffering(request);
    }

    public static PurchaseScheduledInstancesResponse purchaseScheduledInstances(
            Ec2Client client, PurchaseScheduledInstancesRequest request) {
        return client.purchaseScheduledInstances(request);
    }

    public static RebootInstancesResponse rebootInstances(Ec2Client client, RebootInstancesRequest request) {
        return client.rebootInstances(request);
    }

    public static RegisterInstanceEventNotificationAttributesResponse registerInstanceEventNotificationAttributes(
            Ec2Client client, RegisterInstanceEventNotificationAttributesRequest request) {
        return client.registerInstanceEventNotificationAttributes(request);
    }

    public static ReleaseHostsResponse releaseHosts(Ec2Client client, ReleaseHostsRequest request) {
        return client.releaseHosts(request);
    }

    public static ReplaceIamInstanceProfileAssociationResponse replaceIamInstanceProfileAssociation(
            Ec2Client client, ReplaceIamInstanceProfileAssociationRequest request) {
        return client.replaceIamInstanceProfileAssociation(request);
    }

    public static ReportInstanceStatusResponse reportInstanceStatus(
            Ec2Client client, ReportInstanceStatusRequest request) {
        return client.reportInstanceStatus(request);
    }

    public static RequestSpotFleetResponse requestSpotFleet(Ec2Client client, RequestSpotFleetRequest request) {
        return client.requestSpotFleet(request);
    }

    public static RequestSpotInstancesResponse requestSpotInstances(
            Ec2Client client, RequestSpotInstancesRequest request) {
        return client.requestSpotInstances(request);
    }

    public static ResetInstanceAttributeResponse resetInstanceAttribute(
            Ec2Client client, ResetInstanceAttributeRequest request) {
        return client.resetInstanceAttribute(request);
    }

    public static RunInstancesResponse runInstances(Ec2Client client, RunInstancesRequest request) {
        return client.runInstances(request);
    }

    public static RunScheduledInstancesResponse runScheduledInstances(
            Ec2Client client, RunScheduledInstancesRequest request) {
        return client.runScheduledInstances(request);
    }

    public static SendDiagnosticInterruptResponse sendDiagnosticInterrupt(
            Ec2Client client, SendDiagnosticInterruptRequest request) {
        return client.sendDiagnosticInterrupt(request);
    }

    public static StartInstancesResponse startInstances(Ec2Client client, StartInstancesRequest request) {
        return client.startInstances(request);
    }

    public static StopInstancesResponse stopInstances(Ec2Client client, StopInstancesRequest request) {
        return client.stopInstances(request);
    }

    public static TerminateInstancesResponse terminateInstances(Ec2Client client, TerminateInstancesRequest request) {
        return client.terminateInstances(request);
    }

    public static UnmonitorInstancesResponse unmonitorInstances(Ec2Client client, UnmonitorInstancesRequest request) {
        return client.unmonitorInstances(request);
    }
}
