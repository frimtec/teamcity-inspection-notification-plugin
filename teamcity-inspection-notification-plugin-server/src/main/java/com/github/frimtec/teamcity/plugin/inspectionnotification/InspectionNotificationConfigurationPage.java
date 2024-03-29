package com.github.frimtec.teamcity.plugin.inspectionnotification;

import java.util.ArrayList;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import jetbrains.buildServer.controllers.admin.AdminPage;
import jetbrains.buildServer.serverSide.auth.Permission;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.PositionConstraint;

public class InspectionNotificationConfigurationPage extends AdminPage {

  private static final String PAGE = "adminSettings.jsp";

  private static final String AFTER_PAGE_ID = "email";
  private static final String BEFORE_PAGE_ID = "jabber";

  private static final String PLUGIN_NAME = "inspectionNotification";
  private static final String TAB_TITLE = "Inspection Violation Notification";

  private final InspectionNotificationConfiguration configuration;

  public InspectionNotificationConfigurationPage(
      @NotNull PagePlaces pagePlaces,
      @NotNull PluginDescriptor descriptor,
      @NotNull InspectionNotificationConfiguration configuration
  ) {
    super(pagePlaces);
    setPluginName(PLUGIN_NAME);
    setIncludeUrl(descriptor.getPluginResourcesPath(PAGE));
    setTabTitle(TAB_TITLE);
    ArrayList<String> after = new ArrayList<>();
    after.add(AFTER_PAGE_ID);
    ArrayList<String> before = new ArrayList<>();
    before.add(BEFORE_PAGE_ID);
    setPosition(PositionConstraint.between(after, before));

    this.configuration = configuration;
    register();
  }

  @Override
  public void fillModel(@NotNull Map<String, Object> model, @NotNull HttpServletRequest request) {
    super.fillModel(model, request);
    model.put("pluginSettings", this.configuration);
  }

  @Override
  @NotNull
  public String getGroup() {
    return SERVER_RELATED_GROUP;
  }

  @Override
  public boolean isAvailable(@NotNull HttpServletRequest request) {
    return super.isAvailable(request) && checkHasGlobalPermission(request, Permission.CHANGE_SERVER_SETTINGS);
  }

}
