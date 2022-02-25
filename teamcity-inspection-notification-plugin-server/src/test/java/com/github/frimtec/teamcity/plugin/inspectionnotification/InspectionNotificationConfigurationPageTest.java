package com.github.frimtec.teamcity.plugin.inspectionnotification;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import jetbrains.buildServer.serverSide.auth.Permission;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.web.openapi.PagePlace;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;

import static jetbrains.buildServer.web.openapi.Groupable.SERVER_RELATED_GROUP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InspectionNotificationConfigurationPageTest {

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
