// SesV2ContactUtil.java
package tungnn.tutor.java.aws.service.ses.v2;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

public final class SesV2ContactUtil {

  private SesV2ContactUtil() {}

  public static CreateContactResponse createContact(
      SesV2Client client, CreateContactRequest request) {
    return client.createContact(request);
  }

  public static CreateContactListResponse createContactList(
      SesV2Client client, CreateContactListRequest request) {
    return client.createContactList(request);
  }

  public static DeleteContactResponse deleteContact(
      SesV2Client client, DeleteContactRequest request) {
    return client.deleteContact(request);
  }

  public static DeleteContactListResponse deleteContactList(
      SesV2Client client, DeleteContactListRequest request) {
    return client.deleteContactList(request);
  }

  public static GetContactResponse getContact(SesV2Client client, GetContactRequest request) {
    return client.getContact(request);
  }

  public static GetContactListResponse getContactList(
      SesV2Client client, GetContactListRequest request) {
    return client.getContactList(request);
  }

  public static List<ContactList> listContactLists(
      SesV2Client client, ListContactListsRequest request) {
    return client.listContactListsPaginator(request).stream()
        .flatMap(r -> r.contactLists().stream())
        .collect(Collectors.toList());
  }

  public static List<Contact> listContacts(SesV2Client client, ListContactsRequest request) {
    return client.listContactsPaginator(request).stream()
        .flatMap(r -> r.contacts().stream())
        .collect(Collectors.toList());
  }

  public static UpdateContactResponse updateContact(
      SesV2Client client, UpdateContactRequest request) {
    return client.updateContact(request);
  }

  public static UpdateContactListResponse updateContactList(
      SesV2Client client, UpdateContactListRequest request) {
    return client.updateContactList(request);
  }
}
