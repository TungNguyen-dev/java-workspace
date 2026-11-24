package tungnn.tutor.java.aws.service.cognitoidentityprovider;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

public final class CognitoUserPoolUtils {
    private CognitoUserPoolUtils() {}

    public static AddCustomAttributesResponse addCustomAttributes(
            CognitoIdentityProviderClient client, AddCustomAttributesRequest request) {
        return client.addCustomAttributes(request);
    }

    public static CreateManagedLoginBrandingResponse createManagedLoginBranding(
            CognitoIdentityProviderClient client, CreateManagedLoginBrandingRequest request) {
        return client.createManagedLoginBranding(request);
    }

    public static CreateTermsResponse createTerms(CognitoIdentityProviderClient client, CreateTermsRequest request) {
        return client.createTerms(request);
    }

    public static CreateUserImportJobResponse createUserImportJob(
            CognitoIdentityProviderClient client, CreateUserImportJobRequest request) {
        return client.createUserImportJob(request);
    }

    public static CreateUserPoolResponse createUserPool(
            CognitoIdentityProviderClient client, CreateUserPoolRequest request) {
        return client.createUserPool(request);
    }

    public static CreateUserPoolReplicaResponse createUserPoolReplica(
            CognitoIdentityProviderClient client, CreateUserPoolReplicaRequest request) {
        return client.createUserPoolReplica(request);
    }

    public static DeleteManagedLoginBrandingResponse deleteManagedLoginBranding(
            CognitoIdentityProviderClient client, DeleteManagedLoginBrandingRequest request) {
        return client.deleteManagedLoginBranding(request);
    }

    public static DeleteTermsResponse deleteTerms(CognitoIdentityProviderClient client, DeleteTermsRequest request) {
        return client.deleteTerms(request);
    }

    public static DeleteUserPoolResponse deleteUserPool(
            CognitoIdentityProviderClient client, DeleteUserPoolRequest request) {
        return client.deleteUserPool(request);
    }

    public static DeleteUserPoolReplicaResponse deleteUserPoolReplica(
            CognitoIdentityProviderClient client, DeleteUserPoolReplicaRequest request) {
        return client.deleteUserPoolReplica(request);
    }

    public static DescribeManagedLoginBrandingResponse describeManagedLoginBranding(
            CognitoIdentityProviderClient client, DescribeManagedLoginBrandingRequest request) {
        return client.describeManagedLoginBranding(request);
    }

    public static DescribeManagedLoginBrandingByClientResponse describeManagedLoginBrandingByClient(
            CognitoIdentityProviderClient client, DescribeManagedLoginBrandingByClientRequest request) {
        return client.describeManagedLoginBrandingByClient(request);
    }

    public static DescribeRiskConfigurationResponse describeRiskConfiguration(
            CognitoIdentityProviderClient client, DescribeRiskConfigurationRequest request) {
        return client.describeRiskConfiguration(request);
    }

    public static DescribeTermsResponse describeTerms(
            CognitoIdentityProviderClient client, DescribeTermsRequest request) {
        return client.describeTerms(request);
    }

    public static DescribeUserImportJobResponse describeUserImportJob(
            CognitoIdentityProviderClient client, DescribeUserImportJobRequest request) {
        return client.describeUserImportJob(request);
    }

    public static DescribeUserPoolResponse describeUserPool(
            CognitoIdentityProviderClient client, DescribeUserPoolRequest request) {
        return client.describeUserPool(request);
    }

    public static GetCsvHeaderResponse getCSVHeader(CognitoIdentityProviderClient client, GetCsvHeaderRequest request) {
        return client.getCSVHeader(request);
    }

    public static GetLogDeliveryConfigurationResponse getLogDeliveryConfiguration(
            CognitoIdentityProviderClient client, GetLogDeliveryConfigurationRequest request) {
        return client.getLogDeliveryConfiguration(request);
    }

    public static GetSigningCertificateResponse getSigningCertificate(
            CognitoIdentityProviderClient client, GetSigningCertificateRequest request) {
        return client.getSigningCertificate(request);
    }

    public static GetUiCustomizationResponse getUICustomization(
            CognitoIdentityProviderClient client, GetUiCustomizationRequest request) {
        return client.getUICustomization(request);
    }

    public static ListTermsResponse listTerms(CognitoIdentityProviderClient client, ListTermsRequest request) {
        return client.listTerms(request);
    }

    public static ListUserImportJobsResponse listUserImportJobs(
            CognitoIdentityProviderClient client, ListUserImportJobsRequest request) {
        return client.listUserImportJobs(request);
    }

    public static ListUserPoolReplicasResponse listUserPoolReplicas(
            CognitoIdentityProviderClient client, ListUserPoolReplicasRequest request) {
        return client.listUserPoolReplicas(request);
    }

    public static List<UserPoolDescriptionType> listUserPools(
            CognitoIdentityProviderClient client, ListUserPoolsRequest request) {
        return client.listUserPoolsPaginator(request).stream()
                .flatMap(r -> r.userPools().stream())
                .collect(Collectors.toList());
    }

    public static SetLogDeliveryConfigurationResponse setLogDeliveryConfiguration(
            CognitoIdentityProviderClient client, SetLogDeliveryConfigurationRequest request) {
        return client.setLogDeliveryConfiguration(request);
    }

    public static SetRiskConfigurationResponse setRiskConfiguration(
            CognitoIdentityProviderClient client, SetRiskConfigurationRequest request) {
        return client.setRiskConfiguration(request);
    }

    public static SetUiCustomizationResponse setUICustomization(
            CognitoIdentityProviderClient client, SetUiCustomizationRequest request) {
        return client.setUICustomization(request);
    }

    public static StartUserImportJobResponse startUserImportJob(
            CognitoIdentityProviderClient client, StartUserImportJobRequest request) {
        return client.startUserImportJob(request);
    }

    public static StopUserImportJobResponse stopUserImportJob(
            CognitoIdentityProviderClient client, StopUserImportJobRequest request) {
        return client.stopUserImportJob(request);
    }

    public static TagResourceResponse tagResource(CognitoIdentityProviderClient client, TagResourceRequest request) {
        return client.tagResource(request);
    }

    public static UpdateManagedLoginBrandingResponse updateManagedLoginBranding(
            CognitoIdentityProviderClient client, UpdateManagedLoginBrandingRequest request) {
        return client.updateManagedLoginBranding(request);
    }

    public static UpdateTermsResponse updateTerms(CognitoIdentityProviderClient client, UpdateTermsRequest request) {
        return client.updateTerms(request);
    }

    public static UpdateUserPoolResponse updateUserPool(
            CognitoIdentityProviderClient client, UpdateUserPoolRequest request) {
        return client.updateUserPool(request);
    }

    public static UpdateUserPoolReplicaResponse updateUserPoolReplica(
            CognitoIdentityProviderClient client, UpdateUserPoolReplicaRequest request) {
        return client.updateUserPoolReplica(request);
    }
}
