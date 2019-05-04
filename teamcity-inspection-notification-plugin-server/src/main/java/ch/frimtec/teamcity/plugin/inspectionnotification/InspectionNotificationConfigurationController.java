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

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.ServerPaths;
import jetbrains.buildServer.web.openapi.WebControllerManager;

import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.ModelAndView;

import com.thoughtworks.xstream.XStream;

public class InspectionNotificationConfigurationController extends BaseController {

  private static final Object ACTION_ENABLE = "enable";
  private static final String ACTION_PARAMETER = "action";
  private static final String CONTROLLER_PATH = "/configureInspectionNotification.html";
  public static final String EDIT_PARAMETER = "edit";
  private static final String CONFIG_FILE = "inspection-notification-plugin.xml";
  private static final String SAVED_ID = "configurationSaved";
  private static final String NOT_SAVED_ID = "configurationNotSaved";
  private static final String SAVED_MESSAGE = "Settings Saved.";
  private static final String NOT_SAVED_TEMPLATE_VALIDATION_FAILED = "Template validation failed. Check the FreeMarker documentation for syntax.";
  private static Logger logger = Logger.getLogger("ch.frimtec.teamcity.plugin.inspectionnotification");
  private String configFilePath;

  private InspectionNotificationConfiguration configuration;

  public InspectionNotificationConfigurationController(@NotNull SBuildServer server,
                                        @NotNull ServerPaths serverPaths,
                                        @NotNull WebControllerManager manager,
                                        @NotNull InspectionNotificationConfiguration configuration) throws IOException {
    manager.registerController(CONTROLLER_PATH, this);
    this.configuration = configuration;
    this.configFilePath = (new File(serverPaths.getConfigDir(), CONFIG_FILE)).getCanonicalPath();
    logger.debug(String.format("Config file path: %s", this.configFilePath));
    logger.info("Controller created");
  }

  private void handleConfigurationChange(HttpServletRequest request) throws IOException {
    logger.debug("Changing configuration");
    logger.debug(String.format("Query string: '%s'", request.getQueryString()));

    // Get parameters
    String inspectionAdminGroupName = request.getParameter(InspectionNotificationConfiguration.INSPECTION_ADMIN_GROUP_NAME_KEY);
    String bitbucketRootUrl = request.getParameter(InspectionNotificationConfiguration.BITBUCKET_ROOT_URL_KEY);
    String emailFromAddress = request.getParameter(InspectionNotificationConfiguration.EMAIL_FROM_ADDRESS_KEY);
    String emailSmtpHost = request.getParameter(InspectionNotificationConfiguration.EMAIL_SMTP_HOST_KEY);
    int emailSmtpPort = Integer.parseInt(request.getParameter(InspectionNotificationConfiguration.EMAIL_SMTP_PORT_KEY));

    // Save the configuration
    this.configuration.setInspectionAdminGroupName(inspectionAdminGroupName);
    this.configuration.setBitbucketRootUrl(bitbucketRootUrl);
    this.configuration.setEmailFromAddress(emailFromAddress);
    this.configuration.setEmailSmtpHost(emailSmtpHost);
    this.configuration.setEmailSmtpPort(emailSmtpPort);
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
      logger.error("Could not handle request", e);
    }
    return null;
  }

  public void initialise() {
    try {
      File file = new File(this.configFilePath);
      if (file.exists()) {
        this.loadConfiguration();
      } else {
        this.saveConfiguration();
      }
    } catch (Exception e) {
      logger.error("Could not load configuration", e);
    }
  }

  public void loadConfiguration() throws IOException {
    XStream xstream = new XStream();
    xstream.setClassLoader(this.configuration.getClass().getClassLoader());
    xstream.setClassLoader(InspectionNotificationConfiguration.class.getClassLoader());
    xstream.processAnnotations(InspectionNotificationConfiguration.class);
    FileReader fileReader = new FileReader(this.configFilePath);
    InspectionNotificationConfiguration configuration = (InspectionNotificationConfiguration) xstream.fromXML(fileReader);
    fileReader.close();

    // Copy the values, because we need it on the original shared (bean),
    // which is a singleton
    this.configuration.setInspectionAdminGroupName(configuration.getInspectionAdminGroupName());
    this.configuration.setBitbucketRootUrl(configuration.getBitbucketRootUrl());
    this.configuration.setEmailFromAddress(configuration.getEmailFromAddress());
    this.configuration.setEmailSmtpHost(configuration.getEmailSmtpHost());
    this.configuration.setEmailSmtpPort(configuration.getEmailSmtpPort());
  }

  public void saveConfiguration() throws IOException {
    XStream xstream = new XStream();
    xstream.processAnnotations(this.configuration.getClass());
    File file = new File(this.configFilePath);
    file.createNewFile();
    FileOutputStream fileOutputStream = new FileOutputStream(file);
    xstream.toXML(this.configuration, fileOutputStream);
    fileOutputStream.flush();
    fileOutputStream.close();
  }

}