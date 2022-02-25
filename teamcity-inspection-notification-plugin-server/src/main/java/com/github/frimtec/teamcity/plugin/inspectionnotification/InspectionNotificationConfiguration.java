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

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.intellij.openapi.util.text.StringUtil;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import jetbrains.buildServer.serverSide.crypt.RSACipher;

@XStreamAlias("inspection-notification")
public final class InspectionNotificationConfiguration {

  public static final String INSPECTION_ADMIN_GROUP_NAME_KEY = "inspectionAdminGroupName";
  public static final String BITBUCKET_ROOT_URL_KEY = "bitbucketRootUrl";
  public static final String EMAIL_FROM_ADDRESS_KEY = "emailFromAddress";
  public static final String EMAIL_SMTP_HOST_KEY = "emailSmtpHost";
  public static final String EMAIL_SMTP_PORT_KEY = "emailSmtpPort";
  public static final String EMAIL_SMTP_LOGIN = "emailSmtpLogin";
  public static final String EMAIL_SMTP_PASSWORD = "emailSmtpPassword";
  public static final String EMAIL_SMTP_STARTTLS = "emailSmtpStartTls";
  public static final String EMAIL_SUBJECT = "emailSubject";
  public static final String EMAIL_SUBJECT_NO_CHANGES = "emailSubjectNoChanges";
  public static final String EMAIL_TEMPLATE_KEY = "emailTemplate";
  public static final String DISABLED_PROJECT_IDS_KEY = "disabledProjectIds";
  public static final String PROJECT_ID_KEY = "projectId";
  public static final String PROJECT_DISABLED_KEY = "projectDisabled";

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
  @XStreamAlias(EMAIL_SMTP_LOGIN)
  private String emailSmtpLogin = "";
  @XStreamAlias(EMAIL_SMTP_PASSWORD)
  private String emailSmtpPassword = "";
  @XStreamAlias(EMAIL_SMTP_STARTTLS)
  private boolean emailSmtpStartTls = false;
  @XStreamAlias(EMAIL_SUBJECT)
  private String emailSubject = "ACTION-REQUIRED: New inspection violations introduced!";
  @XStreamAlias(EMAIL_SUBJECT_NO_CHANGES)
  private String emailSubjectNoChanges = "WARNING: New inspection violations without code change!";

  @XStreamAlias(EMAIL_TEMPLATE_KEY)
  private String emailTemplate;

  @XStreamAlias(DISABLED_PROJECT_IDS_KEY)
  private Set<String> disabledProjectIds = new HashSet<>();

  public InspectionNotificationConfiguration() {
    try {
      this.emailTemplate = ResourceHelper.loadDefaultEmailTemplate();
    } catch (IOException e) {
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

  public String getEmailSmtpLogin() {
    return this.emailSmtpLogin;
  }

  public void setEmailSmtpLogin(String emailSmtpLogin) {
    this.emailSmtpLogin = emailSmtpLogin;
  }

  public String getEmailSmtpPassword() {
    return this.emailSmtpPassword;
  }

  public void setEmailSmtpPassword(String emailSmtpPassword) {
    this.emailSmtpPassword = emailSmtpPassword;
  }

  public String getEncryptedEmailSmtpPassword() {
    return StringUtil.isEmpty(this.emailSmtpPassword) ? "" : RSACipher.encryptDataForWeb(this.emailSmtpPassword);
  }

  @SuppressWarnings("unused")
  public void setEncryptedEmailSmtpPassword(String password) {
    this.emailSmtpPassword = RSACipher.decryptWebRequestData(password);
  }

  public boolean isEmailSmtpStartTls() {
    return this.emailSmtpStartTls;
  }

  public void setEmailSmtpStartTls(boolean emailSmtpStartTls) {
    this.emailSmtpStartTls = emailSmtpStartTls;
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

  public Set<String> getDisabledProjectIds() {
    return Collections.unmodifiableSet(this.disabledProjectIds);
  }

  public void setDisabledProjectIds(Set<String> disabledProjectIds) {
    this.disabledProjectIds = new HashSet<>(disabledProjectIds);
  }

  public String getHexEncodedPublicKey() {
    return RSACipher.getHexEncodedPublicKey();
  }
}
