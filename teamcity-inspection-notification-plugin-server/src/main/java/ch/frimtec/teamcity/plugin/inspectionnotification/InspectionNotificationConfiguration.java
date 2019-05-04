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
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import jetbrains.buildServer.web.openapi.PluginException;

public final class InspectionNotificationConfiguration {
  public static final String BITBUCKET_ROOT_URL = "bitbucketRootUrl";
  public static final String INSPECTION_ADMIN_GROUP_NAME = "inspectionAdminGroupName";
  public static final String EMAIL_FROM_ADDRESS = "email.fromAddress";
  public static final String EMAIL_SMTP_HOST = "email.smtpHost";
  public static final String EMAIL_SMTP_PORT = "email.smtpPort";

  private final Properties databaseProperties;
  private String bitbucketRootUrl;
  private String inspectionAdminGroupName;
  private String emailFromAddress;
  private String emailSmtpHost;
  private int emailSmtpPort;

  public InspectionNotificationConfiguration(String configDir, String configurationFileName) {
    this.databaseProperties = loadPropertiesFile(configDir, "database.properties");
    initPropertiesFromFile(configDir, configurationFileName);
  }

  private static Properties loadPropertiesFile(String path, String fileName) {
    File propertiesFile = new File(path, fileName);
    Properties properties = new Properties();
    try (FileReader reader = new FileReader(propertiesFile)) {
      properties.load(reader);
      return properties;
    } catch (IOException e) {
      throw new PluginException("Can not read properties file: " + propertiesFile.getAbsolutePath(), e);
    }
  }

  private void initPropertiesFromFile(String path, String fileName) {
    Properties properties = loadPropertiesFile(path, fileName);
    this.bitbucketRootUrl = properties.getProperty(BITBUCKET_ROOT_URL, "UNDEFINED");
    this.inspectionAdminGroupName = properties.getProperty(INSPECTION_ADMIN_GROUP_NAME, "inspection-admin");
    this.emailFromAddress = properties.getProperty(EMAIL_FROM_ADDRESS, "UNDEFINED");
    this.emailSmtpHost = properties.getProperty(EMAIL_SMTP_HOST, "localhost");
    this.emailSmtpPort = Integer.parseInt(properties.getProperty(EMAIL_SMTP_PORT, "25"));
  }

  public String getDatabaseConnectionUrl() {
    return this.databaseProperties.getProperty("connectionUrl");
  }

  public String getDatabaseUser() {
    return this.databaseProperties.getProperty("connectionProperties.user", null);
  }

  public String getDatabasePassword() {
    return this.databaseProperties.getProperty("connectionProperties.password", null);
  }

  public String getBitbucketRootUrl() {
    return this.bitbucketRootUrl;
  }

  public String getInspectionAdminGroupName() {
    return this.inspectionAdminGroupName;
  }

  public String getEmailFromAddress() {
    return this.emailFromAddress;
  }

  public String getEmailSmtpHost() {
    return this.emailSmtpHost;
  }

  public int getEmailSmtpPort() {
    return this.emailSmtpPort;
  }
}
