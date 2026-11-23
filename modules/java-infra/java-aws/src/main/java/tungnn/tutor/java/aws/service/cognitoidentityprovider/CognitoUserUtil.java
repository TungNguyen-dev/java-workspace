package tungnn.tutor.java.aws.service.cognitoidentityprovider;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

public final class CognitoUserUtil {
  private CognitoUserUtil() {}

  public static AdminConfirmSignUpResponse adminConfirmSignUp(
      CognitoIdentityProviderClient client, AdminConfirmSignUpRequest request) {
    return client.adminConfirmSignUp(request);
  }

  public static AdminCreateUserResponse adminCreateUser(
      CognitoIdentityProviderClient client, AdminCreateUserRequest request) {
    return client.adminCreateUser(request);
  }

  public static AdminDeleteUserResponse adminDeleteUser(
      CognitoIdentityProviderClient client, AdminDeleteUserRequest request) {
    return client.adminDeleteUser(request);
  }

  public static AdminDeleteUserAttributesResponse adminDeleteUserAttributes(
      CognitoIdentityProviderClient client, AdminDeleteUserAttributesRequest request) {
    return client.adminDeleteUserAttributes(request);
  }

  public static AdminDisableUserResponse adminDisableUser(
      CognitoIdentityProviderClient client, AdminDisableUserRequest request) {
    return client.adminDisableUser(request);
  }

  public static AdminEnableUserResponse adminEnableUser(
      CognitoIdentityProviderClient client, AdminEnableUserRequest request) {
    return client.adminEnableUser(request);
  }

  public static AdminForgetDeviceResponse adminForgetDevice(
      CognitoIdentityProviderClient client, AdminForgetDeviceRequest request) {
    return client.adminForgetDevice(request);
  }

  public static AdminGetDeviceResponse adminGetDevice(
      CognitoIdentityProviderClient client, AdminGetDeviceRequest request) {
    return client.adminGetDevice(request);
  }

  public static AdminGetUserResponse adminGetUser(
      CognitoIdentityProviderClient client, AdminGetUserRequest request) {
    return client.adminGetUser(request);
  }

  public static AdminInitiateAuthResponse adminInitiateAuth(
      CognitoIdentityProviderClient client, AdminInitiateAuthRequest request) {
    return client.adminInitiateAuth(request);
  }

  public static AdminListDevicesResponse adminListDevices(
      CognitoIdentityProviderClient client, AdminListDevicesRequest request) {
    return client.adminListDevices(request);
  }

  public static List<AuthEventType> adminListUserAuthEvents(
      CognitoIdentityProviderClient client, AdminListUserAuthEventsRequest request) {
    return client.adminListUserAuthEventsPaginator(request).stream()
        .flatMap(r -> r.authEvents().stream())
        .collect(Collectors.toList());
  }

  public static AdminResetUserPasswordResponse adminResetUserPassword(
      CognitoIdentityProviderClient client, AdminResetUserPasswordRequest request) {
    return client.adminResetUserPassword(request);
  }

  public static AdminRespondToAuthChallengeResponse adminRespondToAuthChallenge(
      CognitoIdentityProviderClient client, AdminRespondToAuthChallengeRequest request) {
    return client.adminRespondToAuthChallenge(request);
  }

  public static AdminSetUserMfaPreferenceResponse adminSetUserMFAPreference(
      CognitoIdentityProviderClient client, AdminSetUserMfaPreferenceRequest request) {
    return client.adminSetUserMFAPreference(request);
  }

  public static AdminSetUserPasswordResponse adminSetUserPassword(
      CognitoIdentityProviderClient client, AdminSetUserPasswordRequest request) {
    return client.adminSetUserPassword(request);
  }

  public static AdminSetUserSettingsResponse adminSetUserSettings(
      CognitoIdentityProviderClient client, AdminSetUserSettingsRequest request) {
    return client.adminSetUserSettings(request);
  }

  public static AdminUpdateAuthEventFeedbackResponse adminUpdateAuthEventFeedback(
      CognitoIdentityProviderClient client, AdminUpdateAuthEventFeedbackRequest request) {
    return client.adminUpdateAuthEventFeedback(request);
  }

  public static AdminUpdateDeviceStatusResponse adminUpdateDeviceStatus(
      CognitoIdentityProviderClient client, AdminUpdateDeviceStatusRequest request) {
    return client.adminUpdateDeviceStatus(request);
  }

  public static AdminUpdateUserAttributesResponse adminUpdateUserAttributes(
      CognitoIdentityProviderClient client, AdminUpdateUserAttributesRequest request) {
    return client.adminUpdateUserAttributes(request);
  }

  public static AdminUserGlobalSignOutResponse adminUserGlobalSignOut(
      CognitoIdentityProviderClient client, AdminUserGlobalSignOutRequest request) {
    return client.adminUserGlobalSignOut(request);
  }

  public static ChangePasswordResponse changePassword(
      CognitoIdentityProviderClient client, ChangePasswordRequest request) {
    return client.changePassword(request);
  }

  public static ConfirmDeviceResponse confirmDevice(
      CognitoIdentityProviderClient client, ConfirmDeviceRequest request) {
    return client.confirmDevice(request);
  }

  public static ConfirmForgotPasswordResponse confirmForgotPassword(
      CognitoIdentityProviderClient client, ConfirmForgotPasswordRequest request) {
    return client.confirmForgotPassword(request);
  }

  public static ConfirmSignUpResponse confirmSignUp(
      CognitoIdentityProviderClient client, ConfirmSignUpRequest request) {
    return client.confirmSignUp(request);
  }

  public static DeleteUserResponse deleteUser(
      CognitoIdentityProviderClient client, DeleteUserRequest request) {
    return client.deleteUser(request);
  }

  public static DeleteUserAttributesResponse deleteUserAttributes(
      CognitoIdentityProviderClient client, DeleteUserAttributesRequest request) {
    return client.deleteUserAttributes(request);
  }

  public static ForgetDeviceResponse forgetDevice(
      CognitoIdentityProviderClient client, ForgetDeviceRequest request) {
    return client.forgetDevice(request);
  }

  public static ForgotPasswordResponse forgotPassword(
      CognitoIdentityProviderClient client, ForgotPasswordRequest request) {
    return client.forgotPassword(request);
  }

  public static GetDeviceResponse getDevice(
      CognitoIdentityProviderClient client, GetDeviceRequest request) {
    return client.getDevice(request);
  }

  public static GetTokensFromRefreshTokenResponse getTokensFromRefreshToken(
      CognitoIdentityProviderClient client, GetTokensFromRefreshTokenRequest request) {
    return client.getTokensFromRefreshToken(request);
  }

  public static GetUserResponse getUser(
      CognitoIdentityProviderClient client, GetUserRequest request) {
    return client.getUser(request);
  }

  public static GetUserAttributeVerificationCodeResponse getUserAttributeVerificationCode(
      CognitoIdentityProviderClient client, GetUserAttributeVerificationCodeRequest request) {
    return client.getUserAttributeVerificationCode(request);
  }

  public static GlobalSignOutResponse globalSignOut(
      CognitoIdentityProviderClient client, GlobalSignOutRequest request) {
    return client.globalSignOut(request);
  }

  public static InitiateAuthResponse initiateAuth(
      CognitoIdentityProviderClient client, InitiateAuthRequest request) {
    return client.initiateAuth(request);
  }

  public static ListDevicesResponse listDevices(
      CognitoIdentityProviderClient client, ListDevicesRequest request) {
    return client.listDevices(request);
  }

  public static List<UserType> listUsers(
      CognitoIdentityProviderClient client, ListUsersRequest request) {
    return client.listUsersPaginator(request).stream()
        .flatMap(r -> r.users().stream())
        .collect(Collectors.toList());
  }

  public static ResendConfirmationCodeResponse resendConfirmationCode(
      CognitoIdentityProviderClient client, ResendConfirmationCodeRequest request) {
    return client.resendConfirmationCode(request);
  }

  public static RespondToAuthChallengeResponse respondToAuthChallenge(
      CognitoIdentityProviderClient client, RespondToAuthChallengeRequest request) {
    return client.respondToAuthChallenge(request);
  }

  public static RevokeTokenResponse revokeToken(
      CognitoIdentityProviderClient client, RevokeTokenRequest request) {
    return client.revokeToken(request);
  }

  public static SetUserMfaPreferenceResponse setUserMFAPreference(
      CognitoIdentityProviderClient client, SetUserMfaPreferenceRequest request) {
    return client.setUserMFAPreference(request);
  }

  public static SetUserSettingsResponse setUserSettings(
      CognitoIdentityProviderClient client, SetUserSettingsRequest request) {
    return client.setUserSettings(request);
  }

  public static SignUpResponse signUp(CognitoIdentityProviderClient client, SignUpRequest request) {
    return client.signUp(request);
  }

  public static UpdateAuthEventFeedbackResponse updateAuthEventFeedback(
      CognitoIdentityProviderClient client, UpdateAuthEventFeedbackRequest request) {
    return client.updateAuthEventFeedback(request);
  }

  public static UpdateDeviceStatusResponse updateDeviceStatus(
      CognitoIdentityProviderClient client, UpdateDeviceStatusRequest request) {
    return client.updateDeviceStatus(request);
  }

  public static UpdateUserAttributesResponse updateUserAttributes(
      CognitoIdentityProviderClient client, UpdateUserAttributesRequest request) {
    return client.updateUserAttributes(request);
  }

  public static VerifyUserAttributeResponse verifyUserAttribute(
      CognitoIdentityProviderClient client, VerifyUserAttributeRequest request) {
    return client.verifyUserAttribute(request);
  }
}
