package tungnn.tutor.java.aws.service.cognitoidentityprovider;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

public final class CognitoGroupUtils {
    private CognitoGroupUtils() {}

    public static AdminAddUserToGroupResponse adminAddUserToGroup(
            CognitoIdentityProviderClient client, AdminAddUserToGroupRequest request) {
        return client.adminAddUserToGroup(request);
    }

    public static List<GroupType> adminListGroupsForUser(
            CognitoIdentityProviderClient client, AdminListGroupsForUserRequest request) {
        return client.adminListGroupsForUserPaginator(request).stream()
                .flatMap(r -> r.groups().stream())
                .collect(Collectors.toList());
    }

    public static AdminRemoveUserFromGroupResponse adminRemoveUserFromGroup(
            CognitoIdentityProviderClient client, AdminRemoveUserFromGroupRequest request) {
        return client.adminRemoveUserFromGroup(request);
    }

    public static CreateGroupResponse createGroup(CognitoIdentityProviderClient client, CreateGroupRequest request) {
        return client.createGroup(request);
    }

    public static DeleteGroupResponse deleteGroup(CognitoIdentityProviderClient client, DeleteGroupRequest request) {
        return client.deleteGroup(request);
    }

    public static GetGroupResponse getGroup(CognitoIdentityProviderClient client, GetGroupRequest request) {
        return client.getGroup(request);
    }

    public static List<GroupType> listGroups(CognitoIdentityProviderClient client, ListGroupsRequest request) {
        return client.listGroupsPaginator(request).stream()
                .flatMap(r -> r.groups().stream())
                .collect(Collectors.toList());
    }

    public static List<UserType> listUsersInGroup(
            CognitoIdentityProviderClient client, ListUsersInGroupRequest request) {
        return client.listUsersInGroupPaginator(request).stream()
                .flatMap(r -> r.users().stream())
                .collect(Collectors.toList());
    }

    public static UpdateGroupResponse updateGroup(CognitoIdentityProviderClient client, UpdateGroupRequest request) {
        return client.updateGroup(request);
    }
}
