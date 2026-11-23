package tungnn.tutor.java.aws.service.sns;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

public final class SnsPublishUtil {
  private SnsPublishUtil() {}

  public static PublishResponse publish(SnsClient client, PublishRequest request) {
    return client.publish(request);
  }

  public static PublishBatchResponse publishBatch(SnsClient client, PublishBatchRequest request) {
    return client.publishBatch(request);
  }

  public static CheckIfPhoneNumberIsOptedOutResponse checkIfPhoneNumberIsOptedOut(
      SnsClient client, CheckIfPhoneNumberIsOptedOutRequest request) {
    return client.checkIfPhoneNumberIsOptedOut(request);
  }

  public static List<String> listPhoneNumbersOptedOut(
      SnsClient client, ListPhoneNumbersOptedOutRequest request) {
    return client.listPhoneNumbersOptedOutPaginator(request).stream()
        .flatMap(r -> r.phoneNumbers().stream())
        .collect(Collectors.toList());
  }

  public static OptInPhoneNumberResponse optInPhoneNumber(
      SnsClient client, OptInPhoneNumberRequest request) {
    return client.optInPhoneNumber(request);
  }

  public static GetSmsAttributesResponse getSMSAttributes(
      SnsClient client, GetSmsAttributesRequest request) {
    return client.getSMSAttributes(request);
  }

  public static SetSmsAttributesResponse setSMSAttributes(
      SnsClient client, SetSmsAttributesRequest request) {
    return client.setSMSAttributes(request);
  }

  public static GetSmsSandboxAccountStatusResponse getSMSSandboxAccountStatus(
      SnsClient client, GetSmsSandboxAccountStatusRequest request) {
    return client.getSMSSandboxAccountStatus(request);
  }

  public static CreateSmsSandboxPhoneNumberResponse createSMSSandboxPhoneNumber(
      SnsClient client, CreateSmsSandboxPhoneNumberRequest request) {
    return client.createSMSSandboxPhoneNumber(request);
  }

  public static DeleteSmsSandboxPhoneNumberResponse deleteSMSSandboxPhoneNumber(
      SnsClient client, DeleteSmsSandboxPhoneNumberRequest request) {
    return client.deleteSMSSandboxPhoneNumber(request);
  }

  public static VerifySmsSandboxPhoneNumberResponse verifySMSSandboxPhoneNumber(
      SnsClient client, VerifySmsSandboxPhoneNumberRequest request) {
    return client.verifySMSSandboxPhoneNumber(request);
  }

  public static List<SMSSandboxPhoneNumber> listSMSSandboxPhoneNumbers(
      SnsClient client, ListSmsSandboxPhoneNumbersRequest request) {
    return client.listSMSSandboxPhoneNumbersPaginator(request).stream()
        .flatMap(r -> r.phoneNumbers().stream())
        .collect(Collectors.toList());
  }

  public static List<PhoneNumberInformation> listOriginationNumbers(
      SnsClient client, ListOriginationNumbersRequest request) {
    return client.listOriginationNumbersPaginator(request).stream()
        .flatMap(r -> r.phoneNumbers().stream())
        .collect(Collectors.toList());
  }

  public static CreatePlatformApplicationResponse createPlatformApplication(
      SnsClient client, CreatePlatformApplicationRequest request) {
    return client.createPlatformApplication(request);
  }

  public static DeletePlatformApplicationResponse deletePlatformApplication(
      SnsClient client, DeletePlatformApplicationRequest request) {
    return client.deletePlatformApplication(request);
  }

  public static GetPlatformApplicationAttributesResponse getPlatformApplicationAttributes(
      SnsClient client, GetPlatformApplicationAttributesRequest request) {
    return client.getPlatformApplicationAttributes(request);
  }

  public static SetPlatformApplicationAttributesResponse setPlatformApplicationAttributes(
      SnsClient client, SetPlatformApplicationAttributesRequest request) {
    return client.setPlatformApplicationAttributes(request);
  }

  public static List<PlatformApplication> listPlatformApplications(
      SnsClient client, ListPlatformApplicationsRequest request) {
    return client.listPlatformApplicationsPaginator(request).stream()
        .flatMap(r -> r.platformApplications().stream())
        .collect(Collectors.toList());
  }

  public static CreatePlatformEndpointResponse createPlatformEndpoint(
      SnsClient client, CreatePlatformEndpointRequest request) {
    return client.createPlatformEndpoint(request);
  }

  public static DeleteEndpointResponse deleteEndpoint(
      SnsClient client, DeleteEndpointRequest request) {
    return client.deleteEndpoint(request);
  }

  public static GetEndpointAttributesResponse getEndpointAttributes(
      SnsClient client, GetEndpointAttributesRequest request) {
    return client.getEndpointAttributes(request);
  }

  public static SetEndpointAttributesResponse setEndpointAttributes(
      SnsClient client, SetEndpointAttributesRequest request) {
    return client.setEndpointAttributes(request);
  }

  public static List<Endpoint> listEndpointsByPlatformApplication(
      SnsClient client, ListEndpointsByPlatformApplicationRequest request) {
    return client.listEndpointsByPlatformApplicationPaginator(request).stream()
        .flatMap(r -> r.endpoints().stream())
        .collect(Collectors.toList());
  }
}
