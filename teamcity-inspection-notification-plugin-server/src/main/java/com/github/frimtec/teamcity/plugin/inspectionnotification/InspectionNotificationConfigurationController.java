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

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.ModelAndView;
import com.thoughtworks.xstream.XStream;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.ServerPaths;
import jetbrains.buildServer.util.StringUtil;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionNotificationConfiguration.BITBUCKET_ROOT_URL_KEY;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionNotificationConfiguration.EMAIL_FROM_ADDRESS_KEY;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionNotificationConfiguration.EMAIL_SMTP_HOST_KEY;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionNotificationConfiguration.EMAIL_SMTP_PORT_KEY;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionNotificationConfiguration.EMAIL_SUBJECT;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionNotificationConfiguration.EMAIL_SUBJECT_NO_CHANGES;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionNotificationConfiguration.EMAIL_TEMPLATE_KEY;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionNotificationConfiguration.INSPECTION_ADMIN_GROUP_NAME_KEY;

public class InspectionNotificationConfigurationController extends BaseController {

  private static final String CONTROLLER_PATH = "/configureInspectionNotification.html";
  public static final String EDIT_PARAMETER = "edit";
  private static final String CONFIG_FILE = "inspection-notification-plugin.xml";
  private static final String SAVED_ID = "configurationSaved";
  private static final String SAVED_MESSAGE = "Settings Saved.";
  private final Path configFilePath;

  private final InspectionNotificationConfiguration configuration;

  public InspectionNotificationConfigurationController(
      @NotNull ServerPaths serverPaths,
      @NotNull WebControllerManager manager,
      @NotNull InspectionNotificationConfiguration configuration) {
    manager.registerController(CONTROLLER_PATH, this);
    this.configuration = configuration;
    this.configFilePath = Paths.get(serverPaths.getConfigDir()).resolve(CONFIG_FILE);
    this.logger.debug(String.format("Config file path: %s", this.configFilePath));
    this.logger.info("Controller created");
  }

  private void handleConfigurationChange(HttpServletRequest request) throws IOException {
    this.configuration.setInspectionAdminGroupName(request.getParameter(INSPECTION_ADMIN_GROUP_NAME_KEY));
    this.configuration.setBitbucketRootUrl(request.getParameter(BITBUCKET_ROOT_URL_KEY));
    this.configuration.setEmailFromAddress(request.getParameter(EMAIL_FROM_ADDRESS_KEY));
    this.configuration.setEmailSmtpHost(request.getParameter(EMAIL_SMTP_HOST_KEY));
    String portAsString = request.getParameter(EMAIL_SMTP_PORT_KEY);
    this.configuration.setEmailSmtpPort(!StringUtil.isEmpty(portAsString) ? Integer.parseInt(portAsString) : 0);
    this.configuration.setEmailSubject(request.getParameter(EMAIL_SUBJECT));
    this.configuration.setEmailSubjectNoChanges(request.getParameter(EMAIL_SUBJECT_NO_CHANGES));
    String emailTemplate = request.getParameter(EMAIL_TEMPLATE_KEY);
    if (!StringUtil.isEmpty(emailTemplate)) {
      this.configuration.setEmailTemplate(emailTemplate);
    }
    this.saveConfiguration();

    // Update the page
    this.getOrCreateMessages(request).addMessage(SAVED_ID, SAVED_MESSAGE);
  }

  @Override
  public ModelAndView doHandle(HttpServletRequest request, HttpServletResponse response) {
    try {
      if (request.getParameter(EDIT_PARAMETER) != null) {
        this.handleConfigurationChange(request);
      }
    } catch (Exception e) {
      this.logger.error("Could not handle request", e);
    }
    return null;
  }

  public void initialise() {
    try {
      if (Files.exists(this.configFilePath)) {
        this.loadConfiguration();
      } else {
        this.saveConfiguration();
      }
    } catch (Exception e) {
      this.logger.error("Could not load configuration", e);
    }
  }

  public void loadConfiguration() throws IOException {
    XStream xstream = new XStream();
    xstream.setClassLoader(this.configuration.getClass().getClassLoader());
    xstream.setClassLoader(InspectionNotificationConfiguration.class.getClassLoader());
    xstream.processAnnotations(InspectionNotificationConfiguration.class);
    InspectionNotificationConfiguration configuration;
    try (FileReader fileReader = new FileReader(this.configFilePath.toFile())) {
      configuration = (InspectionNotificationConfiguration) xstream.fromXML(fileReader);
    }

    // Copy the values, because we need it on the original shared (bean),
    // which is a singleton
    this.configuration.setInspectionAdminGroupName(configuration.getInspectionAdminGroupName());
    this.configuration.setBitbucketRootUrl(configuration.getBitbucketRootUrl());
    this.configuration.setEmailFromAddress(configuration.getEmailFromAddress());
    this.configuration.setEmailSmtpHost(configuration.getEmailSmtpHost());
    this.configuration.setEmailSmtpPort(configuration.getEmailSmtpPort());
    this.configuration.setEmailSubject(configuration.getEmailSubject());
    this.configuration.setEmailSubjectNoChanges(configuration.getEmailSubjectNoChanges());
    if (!StringUtil.isEmpty(configuration.getEmailTemplate())) {
      this.configuration.setEmailTemplate(configuration.getEmailTemplate());
    }
  }

  public void saveConfiguration() throws IOException {
    XStream xstream = new XStream();
    xstream.processAnnotations(this.configuration.getClass());
    try (FileOutputStream fileOutputStream = new FileOutputStream(this.configFilePath.toFile())) {
      xstream.toXML(this.configuration, fileOutputStream);
      fileOutputStream.flush();
    }
  }

  final InspectionNotificationConfiguration getConfiguration() {
    return this.configuration;
  }
}
