package com.github.frimtec.teamcity.plugin.inspectionnotification;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import jetbrains.buildServer.serverSide.auth.Permission;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.web.openapi.PagePlace;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionNotificationConfiguration.BITBUCKET_ROOT_URL_KEY;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionNotificationConfiguration.EMAIL_FROM_ADDRESS_KEY;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionNotificationConfiguration.EMAIL_SMTP_HOST_KEY;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionNotificationConfiguration.EMAIL_SMTP_PORT_KEY;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionNotificationConfiguration.EMAIL_SUBJECT;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionNotificationConfiguration.EMAIL_SUBJECT_NO_CHANGES;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionNotificationConfiguration.EMAIL_TEMPLATE_KEY;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionNotificationConfiguration.INSPECTION_ADMIN_GROUP_NAME_KEY;
import static jetbrains.buildServer.web.openapi.Groupable.SERVER_RELATED_GROUP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InspectionNotificationConfigurationPageTest {

  @Test
  void fillModel() {
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    configuration.setInspectionAdminGroupName("adminGroupName");
    configuration.setBitbucketRootUrl("bitbucketRootUrl");
    configuration.setEmailFromAddress("emailFromAddress");
    configuration.setEmailSmtpHost("smtpHost");
    configuration.setEmailSmtpPort(100);
    configuration.setEmailSubject("subject");
    configuration.setEmailSubjectNoChanges("subjectNoChanges");
    configuration.setEmailTemplate("emailTemplate");
    InspectionNotificationConfigurationPage page = inspectionNotificationConfigurationPage(configuration);

    HashMap<String, Object> model = new HashMap<>();
    page.fillModel(model, request(null));

    assertThat(model).contains(
        entry(INSPECTION_ADMIN_GROUP_NAME_KEY, "adminGroupName"),
        entry(BITBUCKET_ROOT_URL_KEY, "bitbucketRootUrl"),
        entry(EMAIL_FROM_ADDRESS_KEY, "emailFromAddress"),
        entry(EMAIL_SMTP_HOST_KEY, "smtpHost"),
        entry(EMAIL_SMTP_PORT_KEY, 100),
        entry(EMAIL_SUBJECT, "subject"),
        entry(EMAIL_SUBJECT_NO_CHANGES, "subjectNoChanges"),
        entry(EMAIL_TEMPLATE_KEY, "emailTemplate")
    );
  }

  @Test
  void getGroup() {
    InspectionNotificationConfigurationPage page =
        inspectionNotificationConfigurationPage(new InspectionNotificationConfiguration());

    String group = page.getGroup();
    assertThat(group).isEqualTo(SERVER_RELATED_GROUP);
  }

  @Test
  void isAvailableForNullUserReturnsFalse() {
    InspectionNotificationConfigurationPage page =
        inspectionNotificationConfigurationPage(new InspectionNotificationConfiguration());

    boolean available = page.isAvailable(request(null));
    assertThat(available).isFalse();
  }

  @Test
  void isAvailableForNotAuthorizedUserReturnsFalse() {
    SUser user = mock(SUser.class);
    when(user.isPermissionGrantedGlobally(Permission.CHANGE_SERVER_SETTINGS)).thenReturn(false);
    InspectionNotificationConfigurationPage page =
        inspectionNotificationConfigurationPage(new InspectionNotificationConfiguration());

    boolean available = page.isAvailable(request(user));
    assertThat(available).isFalse();
  }

  @Test
  void isAvailableForAuthorizedUserReturnsTrue() {
    SUser user = mock(SUser.class);
    when(user.isPermissionGrantedGlobally(Permission.CHANGE_SERVER_SETTINGS)).thenReturn(true);
    InspectionNotificationConfigurationPage page =
        inspectionNotificationConfigurationPage(new InspectionNotificationConfiguration());

    boolean available = page.isAvailable(request(user));
    assertThat(available).isTrue();
  }

  private static HttpServletRequest request(SUser user) {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getAttribute("USER_KEY")).thenReturn(user);
    return request;
  }


  private static InspectionNotificationConfigurationPage inspectionNotificationConfigurationPage(
      InspectionNotificationConfiguration configuration) {
    PluginDescriptor pluginDescriptor = mock(PluginDescriptor.class);
    when(pluginDescriptor.getPluginResourcesPath(anyString())).thenReturn("resourcePath");
    PagePlaces pagePlaces = mock(PagePlaces.class);
    when(pagePlaces.getPlaceById(any())).thenReturn(mock(PagePlace.class));
    return new InspectionNotificationConfigurationPage(
        pagePlaces,
        pluginDescriptor,
        configuration);
  }

}
