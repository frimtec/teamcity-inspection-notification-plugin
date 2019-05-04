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

package ch.frimtec.teamcity.plugin.inspectionnotification;

import jetbrains.buildServer.controllers.admin.AdminPage;
import jetbrains.buildServer.serverSide.auth.Permission;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class InspectionNotificationConfigurationPageExtension extends AdminPage {

	private static final String PAGE = "adminSettings.jsp";
	private static final String PLUGIN_NAME = "inspectionNotification";
	private static final String TAB_TITLE = "Inspection Violation Notfification";
	private InspectionNotificationConfiguration configuration;

	public InspectionNotificationConfigurationPageExtension(@NotNull PagePlaces pagePlaces,
                                                            @NotNull PluginDescriptor descriptor,
                                                            @NotNull InspectionNotificationConfiguration configuration) {
		super(pagePlaces);
		setPluginName(PLUGIN_NAME);
		setIncludeUrl(descriptor.getPluginResourcesPath(PAGE));
		setTabTitle(TAB_TITLE);
		this.configuration = configuration;
		register();
	}

	@Override
	public void fillModel(@NotNull Map<String, Object> model, @NotNull HttpServletRequest request) {
		super.fillModel(model, request);
		model.put(InspectionNotificationConfiguration.INSPECTION_ADMIN_GROUP_NAME_KEY, this.configuration.getInspectionAdminGroupName());
		model.put(InspectionNotificationConfiguration.BITBUCKET_ROOT_URL_KEY, this.configuration.getBitbucketRootUrl());
		model.put(InspectionNotificationConfiguration.EMAIL_FROM_ADDRESS_KEY, this.configuration.getEmailFromAddress());
		model.put(InspectionNotificationConfiguration.EMAIL_SMTP_HOST_KEY, this.configuration.getEmailSmtpHost());
		model.put(InspectionNotificationConfiguration.EMAIL_SMTP_PORT_KEY, this.configuration.getEmailSmtpPort());
	}
	
	@Override
	public String getGroup() {
		return SERVER_RELATED_GROUP;
	}

	@Override
	public boolean isAvailable(@NotNull HttpServletRequest request) {
		return super.isAvailable(request) && checkHasGlobalPermission(request, Permission.CHANGE_SERVER_SETTINGS);
	}
	
}
