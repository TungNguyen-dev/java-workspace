package tungnn.tutor.java.aws.service.cloudfront;

import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.model.*;

public final class CloudFrontFunctionUtil {
  private CloudFrontFunctionUtil() {}

  public static CreateConnectionFunctionResponse createConnectionFunction(
      CloudFrontClient client, CreateConnectionFunctionRequest request) {
    return client.createConnectionFunction(request);
  }

  public static CreateFunctionResponse createFunction(
      CloudFrontClient client, CreateFunctionRequest request) {
    return client.createFunction(request);
  }

  public static CreateKeyValueStoreResponse createKeyValueStore(
      CloudFrontClient client, CreateKeyValueStoreRequest request) {
    return client.createKeyValueStore(request);
  }

  public static DeleteConnectionFunctionResponse deleteConnectionFunction(
      CloudFrontClient client, DeleteConnectionFunctionRequest request) {
    return client.deleteConnectionFunction(request);
  }

  public static DeleteFunctionResponse deleteFunction(
      CloudFrontClient client, DeleteFunctionRequest request) {
    return client.deleteFunction(request);
  }

  public static DeleteKeyValueStoreResponse deleteKeyValueStore(
      CloudFrontClient client, DeleteKeyValueStoreRequest request) {
    return client.deleteKeyValueStore(request);
  }

  public static DescribeConnectionFunctionResponse describeConnectionFunction(
      CloudFrontClient client, DescribeConnectionFunctionRequest request) {
    return client.describeConnectionFunction(request);
  }

  public static DescribeFunctionResponse describeFunction(
      CloudFrontClient client, DescribeFunctionRequest request) {
    return client.describeFunction(request);
  }

  public static DescribeKeyValueStoreResponse describeKeyValueStore(
      CloudFrontClient client, DescribeKeyValueStoreRequest request) {
    return client.describeKeyValueStore(request);
  }

  public static GetConnectionFunctionResponse getConnectionFunction(
      CloudFrontClient client, GetConnectionFunctionRequest request) {
    return client.getConnectionFunction(request);
  }

  public static GetFunctionResponse getFunction(
      CloudFrontClient client, GetFunctionRequest request) {
    return client.getFunction(request);
  }

  public static ListConnectionFunctionsResponse listConnectionFunctions(
      CloudFrontClient client, ListConnectionFunctionsRequest request) {
    return client.listConnectionFunctions(request);
  }

  public static ListFunctionsResponse listFunctions(
      CloudFrontClient client, ListFunctionsRequest request) {
    return client.listFunctions(request);
  }

  public static ListKeyValueStoresResponse listKeyValueStores(
      CloudFrontClient client, ListKeyValueStoresRequest request) {
    return client.listKeyValueStores(request);
  }

  public static PublishConnectionFunctionResponse publishConnectionFunction(
      CloudFrontClient client, PublishConnectionFunctionRequest request) {
    return client.publishConnectionFunction(request);
  }

  public static PublishFunctionResponse publishFunction(
      CloudFrontClient client, PublishFunctionRequest request) {
    return client.publishFunction(request);
  }

  public static TestConnectionFunctionResponse testConnectionFunction(
      CloudFrontClient client, TestConnectionFunctionRequest request) {
    return client.testConnectionFunction(request);
  }

  public static TestFunctionResponse testFunction(
      CloudFrontClient client, TestFunctionRequest request) {
    return client.testFunction(request);
  }

  public static UpdateConnectionFunctionResponse updateConnectionFunction(
      CloudFrontClient client, UpdateConnectionFunctionRequest request) {
    return client.updateConnectionFunction(request);
  }

  public static UpdateFunctionResponse updateFunction(
      CloudFrontClient client, UpdateFunctionRequest request) {
    return client.updateFunction(request);
  }

  public static UpdateKeyValueStoreResponse updateKeyValueStore(
      CloudFrontClient client, UpdateKeyValueStoreRequest request) {
    return client.updateKeyValueStore(request);
  }
}
