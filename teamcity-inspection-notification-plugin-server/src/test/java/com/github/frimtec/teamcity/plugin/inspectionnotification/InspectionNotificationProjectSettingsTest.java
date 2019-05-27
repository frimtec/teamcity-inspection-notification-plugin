package com.github.frimtec.teamcity.plugin.inspectionnotification;

import java.util.Collections;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.auth.AuthorityHolder;
import jetbrains.buildServer.serverSide.auth.Permission;
import jetbrains.buildServer.serverSide.auth.SecurityContext;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.web.openapi.PagePlace;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionNotificationConfiguration.PROJECT_DISABLED_KEY;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionNotificationConfiguration.PROJECT_ID_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InspectionNotificationProjectSettingsTest {

  @Test
  void fillModelForNotDisabledProject() {
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    InspectionNotificationProjectSettings projectSettings = inspectionNotificationProjectSettings(configuration);
    HashMap<String, Object> model = new HashMap<>();

    SProject project = mock(SProject.class);
    when(project.getProjectId()).thenReturn("P1");

    projectSettings.fillModel(model, request(project));

    assertThat(model).contains(
        entry(PROJECT_DISABLED_KEY, false),
        entry(PROJECT_ID_KEY, "P1")
    );
  }

  @Test
  void fillModelForDisabledProject() {
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    configuration.setDisabledProjectIds(Collections.singleton("P1"));
    InspectionNotificationProjectSettings projectSettings = inspectionNotificationProjectSettings(configuration);
    HashMap<String, Object> model = new HashMap<>();

    SProject project = mock(SProject.class);
    when(project.getProjectId()).thenReturn("P1");

    projectSettings.fillModel(model, request(project));

    assertThat(model).contains(
        entry(PROJECT_DISABLED_KEY, true),
        entry(PROJECT_ID_KEY, "P1")
    );
  }

  @Test
  void fillModelForNullProject() {
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    configuration.setDisabledProjectIds(Collections.singleton("P1"));
    InspectionNotificationProjectSettings projectSettings = inspectionNotificationProjectSettings(configuration);
    HashMap<String, Object> model = new HashMap<>();

    projectSettings.fillModel(model, request(null));

    assertThat(model).isEmpty();
  }

  @Test
  void isAvailableForNullUserReturnsFalse() {
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    InspectionNotificationProjectSettings projectSettings = inspectionNotificationProjectSettings(configuration, null);

    SProject project = mock(SProject.class);
    when(project.getProjectId()).thenReturn("P1");

    boolean available = projectSettings.isAvailable(request(project));
    assertThat(available).isFalse();
  }

  @Test
  void isAvailableForNotAuthorizedUserReturnsFalse() {
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    SUser user = mock(SUser.class);
    when(user.isPermissionGrantedForProject("P1", Permission.EDIT_PROJECT)).thenReturn(false);
    InspectionNotificationProjectSettings projectSettings = inspectionNotificationProjectSettings(configuration, user);

    SProject project = mock(SProject.class);
    when(project.getProjectId()).thenReturn("P1");

    boolean available = projectSettings.isAvailable(request(project));
    assertThat(available).isFalse();
  }

  @Test
  void isAvailableForAuthorizedUserReturnsTrue() {
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    SUser user = mock(SUser.class);
    when(user.isPermissionGrantedForProject("P1", Permission.EDIT_PROJECT)).thenReturn(true);
    InspectionNotificationProjectSettings projectSettings = inspectionNotificationProjectSettings(configuration, user);

    SProject project = mock(SProject.class);
    when(project.getProjectId()).thenReturn("P1");

    boolean available = projectSettings.isAvailable(request(project));
    assertThat(available).isTrue();
  }

  private static HttpServletRequest request(SProject project) {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getAttribute("currentProject")).thenReturn(project);
    return request;
  }

  private static InspectionNotificationProjectSettings inspectionNotificationProjectSettings(
      InspectionNotificationConfiguration configuration) {
    return inspectionNotificationProjectSettings(configuration, null);
  }

  private static InspectionNotificationProjectSettings inspectionNotificationProjectSettings(
      InspectionNotificationConfiguration configuration,
      SUser user) {
    PluginDescriptor pluginDescriptor = mock(PluginDescriptor.class);
    when(pluginDescriptor.getPluginResourcesPath(anyString())).thenReturn("resourcePath");
    PagePlaces pagePlaces = mock(PagePlaces.class);
    when(pagePlaces.getPlaceById(any())).thenReturn(mock(PagePlace.class));
    SecurityContext securityContext = mock(SecurityContext.class);
    AuthorityHolder authorityHolder = mock(AuthorityHolder.class);
    when(authorityHolder.getAssociatedUser()).thenReturn(user);
    when(securityContext.getAuthorityHolder()).thenReturn(authorityHolder);
    return new InspectionNotificationProjectSettings(
        pagePlaces,
        pluginDescriptor,
        securityContext,
        configuration);
  }
}
