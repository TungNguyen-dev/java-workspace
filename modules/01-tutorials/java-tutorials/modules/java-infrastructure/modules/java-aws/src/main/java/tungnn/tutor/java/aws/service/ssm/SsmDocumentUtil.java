package tungnn.tutor.java.aws.service.ssm;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.*;

public final class SsmDocumentUtil {

  private SsmDocumentUtil() {}

  public static CreateDocumentResponse createDocument(
      SsmClient client, CreateDocumentRequest request) {
    return client.createDocument(request);
  }

  public static DeleteDocumentResponse deleteDocument(
      SsmClient client, DeleteDocumentRequest request) {
    return client.deleteDocument(request);
  }

  public static DescribeDocumentResponse describeDocument(
      SsmClient client, DescribeDocumentRequest request) {
    return client.describeDocument(request);
  }

  public static DescribeDocumentPermissionResponse describeDocumentPermission(
      SsmClient client, DescribeDocumentPermissionRequest request) {
    return client.describeDocumentPermission(request);
  }

  public static GetDocumentResponse getDocument(SsmClient client, GetDocumentRequest request) {
    return client.getDocument(request);
  }

  public static ListDocumentMetadataHistoryResponse listDocumentMetadataHistory(
      SsmClient client, ListDocumentMetadataHistoryRequest request) {
    return client.listDocumentMetadataHistory(request);
  }

  public static List<DocumentIdentifier> listDocuments(
      SsmClient client, ListDocumentsRequest request) {
    return client.listDocumentsPaginator(request).stream()
        .flatMap(r -> r.documentIdentifiers().stream())
        .collect(Collectors.toList());
  }

  public static List<DocumentVersionInfo> listDocumentVersions(
      SsmClient client, ListDocumentVersionsRequest request) {
    return client.listDocumentVersionsPaginator(request).stream()
        .flatMap(r -> r.documentVersions().stream())
        .collect(Collectors.toList());
  }

  public static ModifyDocumentPermissionResponse modifyDocumentPermission(
      SsmClient client, ModifyDocumentPermissionRequest request) {
    return client.modifyDocumentPermission(request);
  }

  public static UpdateDocumentResponse updateDocument(
      SsmClient client, UpdateDocumentRequest request) {
    return client.updateDocument(request);
  }

  public static UpdateDocumentDefaultVersionResponse updateDocumentDefaultVersion(
      SsmClient client, UpdateDocumentDefaultVersionRequest request) {
    return client.updateDocumentDefaultVersion(request);
  }

  public static UpdateDocumentMetadataResponse updateDocumentMetadata(
      SsmClient client, UpdateDocumentMetadataRequest request) {
    return client.updateDocumentMetadata(request);
  }
}
