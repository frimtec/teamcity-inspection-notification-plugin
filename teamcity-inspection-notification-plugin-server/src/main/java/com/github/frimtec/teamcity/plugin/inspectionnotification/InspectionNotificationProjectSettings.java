/*
 *  Copyright (c) 2012 - 2019 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.frimtec.teamcity.plugin.inspectionnotification;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import jetbrains.buildServer.controllers.admin.projects.EditProjectTab;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.auth.Permission;
import jetbrains.buildServer.serverSide.auth.SecurityContext;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionNotificationConfiguration.PROJECT_DISABLED_KEY;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionNotificationConfiguration.PROJECT_ID_KEY;

public class InspectionNotificationProjectSettings extends EditProjectTab {

  private static final String PAGE = "projectSettings.jsp";

  @NotNull
  private final SecurityContext securityContext;

  @NotNull
  private final InspectionNotificationConfiguration configuration;

  public InspectionNotificationProjectSettings(@NotNull PagePlaces pagePlaces,
      @NotNull PluginDescriptor descriptor,
      @NotNull SecurityContext securityContext,
      @NotNull InspectionNotificationConfiguration configuration) {
    super(pagePlaces, "inspectionNotification", descriptor.getPluginResourcesPath(PAGE), "Inspection Violation Notification");
    this.securityContext = securityContext;
    this.configuration = configuration;
  }

  @Override
  public void fillModel(@NotNull Map<String, Object> model, @NotNull HttpServletRequest request) {
    super.fillModel(model, request);

    SProject project = getProject(request);
    if (project != null) {
      String projectId = project.getProjectId();
      model.put(PROJECT_ID_KEY, projectId);
      model.put(PROJECT_DISABLED_KEY, this.configuration.getDisabledProjectIds().contains(projectId));
    }
  }


  @Override
  public boolean isAvailable(@NotNull final HttpServletRequest request) {
    final SProject project = getProject(request);
    final SUser user = (SUser) this.securityContext.getAuthorityHolder().getAssociatedUser();
    return user != null && project != null && user.isPermissionGrantedForProject(project.getProjectId(), Permission.EDIT_PROJECT);
  }
}
