package tungnn.tutor.java.aws.service.cloudformation;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.cloudformation.CloudFormationClient;
import software.amazon.awssdk.services.cloudformation.model.*;

public final class CfnTypeUtils {
    private CfnTypeUtils() {}

    public static ActivateOrganizationsAccessResponse activateOrganizationsAccess(
            CloudFormationClient client, ActivateOrganizationsAccessRequest request) {
        return client.activateOrganizationsAccess(request);
    }

    public static ActivateTypeResponse activateType(CloudFormationClient client, ActivateTypeRequest request) {
        return client.activateType(request);
    }

    public static BatchDescribeTypeConfigurationsResponse batchDescribeTypeConfigurations(
            CloudFormationClient client, BatchDescribeTypeConfigurationsRequest request) {
        return client.batchDescribeTypeConfigurations(request);
    }

    public static DeactivateOrganizationsAccessResponse deactivateOrganizationsAccess(
            CloudFormationClient client, DeactivateOrganizationsAccessRequest request) {
        return client.deactivateOrganizationsAccess(request);
    }

    public static DeactivateTypeResponse deactivateType(CloudFormationClient client, DeactivateTypeRequest request) {
        return client.deactivateType(request);
    }

    public static DeregisterTypeResponse deregisterType(CloudFormationClient client, DeregisterTypeRequest request) {
        return client.deregisterType(request);
    }

    public static DescribeOrganizationsAccessResponse describeOrganizationsAccess(
            CloudFormationClient client, DescribeOrganizationsAccessRequest request) {
        return client.describeOrganizationsAccess(request);
    }

    public static DescribePublisherResponse describePublisher(
            CloudFormationClient client, DescribePublisherRequest request) {
        return client.describePublisher(request);
    }

    public static DescribeTypeResponse describeType(CloudFormationClient client, DescribeTypeRequest request) {
        return client.describeType(request);
    }

    public static DescribeTypeRegistrationResponse describeTypeRegistration(
            CloudFormationClient client, DescribeTypeRegistrationRequest request) {
        return client.describeTypeRegistration(request);
    }

    public static List<String> listTypeRegistrations(
            CloudFormationClient client, ListTypeRegistrationsRequest request) {
        return client.listTypeRegistrationsPaginator(request).stream()
                .flatMap(r -> r.registrationTokenList().stream())
                .collect(Collectors.toList());
    }

    public static List<TypeVersionSummary> listTypeVersions(
            CloudFormationClient client, ListTypeVersionsRequest request) {
        return client.listTypeVersionsPaginator(request).stream()
                .flatMap(r -> r.typeVersionSummaries().stream())
                .collect(Collectors.toList());
    }

    public static List<TypeSummary> listTypes(CloudFormationClient client, ListTypesRequest request) {
        return client.listTypesPaginator(request).stream()
                .flatMap(r -> r.typeSummaries().stream())
                .collect(Collectors.toList());
    }

    public static PublishTypeResponse publishType(CloudFormationClient client, PublishTypeRequest request) {
        return client.publishType(request);
    }

    public static RecordHandlerProgressResponse recordHandlerProgress(
            CloudFormationClient client, RecordHandlerProgressRequest request) {
        return client.recordHandlerProgress(request);
    }

    public static RegisterPublisherResponse registerPublisher(
            CloudFormationClient client, RegisterPublisherRequest request) {
        return client.registerPublisher(request);
    }

    public static RegisterTypeResponse registerType(CloudFormationClient client, RegisterTypeRequest request) {
        return client.registerType(request);
    }

    public static SetTypeConfigurationResponse setTypeConfiguration(
            CloudFormationClient client, SetTypeConfigurationRequest request) {
        return client.setTypeConfiguration(request);
    }

    public static SetTypeDefaultVersionResponse setTypeDefaultVersion(
            CloudFormationClient client, SetTypeDefaultVersionRequest request) {
        return client.setTypeDefaultVersion(request);
    }

    public static TestTypeResponse testType(CloudFormationClient client, TestTypeRequest request) {
        return client.testType(request);
    }
}
