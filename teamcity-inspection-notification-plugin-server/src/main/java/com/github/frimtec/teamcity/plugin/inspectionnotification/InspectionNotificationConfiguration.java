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

import java.io.InputStream;
import java.util.Objects;
import org.apache.commons.io.IOUtils;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import static java.nio.charset.StandardCharsets.UTF_8;

@XStreamAlias("inspection-notification")
public final class InspectionNotificationConfiguration {

  public static final String INSPECTION_ADMIN_GROUP_NAME_KEY = "inspectionAdminGroupName";
  public static final String BITBUCKET_ROOT_URL_KEY = "bitbucketRootUrl";
  public static final String EMAIL_FROM_ADDRESS_KEY = "emailFromAddress";
  public static final String EMAIL_SMTP_HOST_KEY = "emailSmtpHost";
  public static final String EMAIL_SMTP_PORT_KEY = "emailSmtpPort";
  public static final String EMAIL_SUBJECT = "emailSubject";
  public static final String EMAIL_SUBJECT_NO_CHANGES = "emailSubjectNoChanges";
  public static final String EMAIL_TEMPLATE_KEY = "emailTemplate";

  private static final String TEMPLATE_NAME = "notification-email.ftl";

  @XStreamAlias(INSPECTION_ADMIN_GROUP_NAME_KEY)
  private String inspectionAdminGroupName = "inspection-admin";
  @XStreamAlias(BITBUCKET_ROOT_URL_KEY)
  private String bitbucketRootUrl = "";
  @XStreamAlias(EMAIL_FROM_ADDRESS_KEY)
  private String emailFromAddress = "teamcity@localhost";
  @XStreamAlias(EMAIL_SMTP_HOST_KEY)
  private String emailSmtpHost = "localhost";
  @XStreamAlias(EMAIL_SMTP_PORT_KEY)
  private int emailSmtpPort = 25;
  @XStreamAlias(EMAIL_SUBJECT)
  private String emailSubject = "ACTION-REQUIRED: New inspection violations introduced!";
  @XStreamAlias(EMAIL_SUBJECT_NO_CHANGES)
  private String emailSubjectNoChanges = "WARNING: New inspection violations without code change!";

  @XStreamAlias(EMAIL_TEMPLATE_KEY)
  private String emailTemplate;

  public InspectionNotificationConfiguration() {
    try {
      InputStream defaultEmailTemplate = Objects.requireNonNull(
          InspectionNotificationConfiguration.class.getClassLoader().getResourceAsStream(TEMPLATE_NAME)
      );
      this.emailTemplate = IOUtils.toString(defaultEmailTemplate, UTF_8.name());
    } catch (Exception e) {
      this.emailTemplate = "";
    }
  }

  public String getInspectionAdminGroupName() {
    return this.inspectionAdminGroupName;
  }

  public void setInspectionAdminGroupName(String inspectionAdminGroupName) {
    this.inspectionAdminGroupName = inspectionAdminGroupName;
  }

  public String getBitbucketRootUrl() {
    return this.bitbucketRootUrl;
  }

  public void setBitbucketRootUrl(String bitbucketRootUrl) {
    this.bitbucketRootUrl = bitbucketRootUrl;
  }

  public String getEmailFromAddress() {
    return this.emailFromAddress;
  }

  public void setEmailFromAddress(String emailFromAddress) {
    this.emailFromAddress = emailFromAddress;
  }

  public String getEmailSmtpHost() {
    return this.emailSmtpHost;
  }

  public void setEmailSmtpHost(String emailSmtpHost) {
    this.emailSmtpHost = emailSmtpHost;
  }

  public int getEmailSmtpPort() {
    return this.emailSmtpPort;
  }

  public void setEmailSmtpPort(int emailSmtpPort) {
    this.emailSmtpPort = emailSmtpPort;
  }

  public String getEmailSubject() {
    return this.emailSubject;
  }

  public void setEmailSubject(String emailSubject) {
    this.emailSubject = emailSubject;
  }

  public String getEmailSubjectNoChanges() {
    return this.emailSubjectNoChanges;
  }

  public void setEmailSubjectNoChanges(String emailSubjectNoChanges) {
    this.emailSubjectNoChanges = emailSubjectNoChanges;
  }

  public String getEmailTemplate() {
    return this.emailTemplate;
  }

  public void setEmailTemplate(String emailTemplate) {
    this.emailTemplate = emailTemplate;
  }
}
