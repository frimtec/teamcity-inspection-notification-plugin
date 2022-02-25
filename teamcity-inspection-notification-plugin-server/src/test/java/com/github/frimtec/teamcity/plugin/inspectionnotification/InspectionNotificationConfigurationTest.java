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

import java.util.Arrays;
import java.util.HashSet;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class InspectionNotificationConfigurationTest {

  @Test
  void defaultConstructor() {
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    assertThat(configuration.getInspectionAdminGroupName()).isEqualTo("inspection-admin");
    assertThat(configuration.getBitbucketRootUrl()).isEmpty();
    assertThat(configuration.getEmailFromAddress()).isEqualTo("teamcity@localhost");
    assertThat(configuration.getEmailSmtpHost()).isEqualTo("localhost");
    assertThat(configuration.getEmailSmtpPort()).isEqualTo(25);
    assertThat(configuration.getEmailSmtpLogin()).isEmpty();
    assertThat(configuration.getEmailSmtpPassword()).isEmpty();
    assertThat(configuration.isEmailSmtpStartTls()).isFalse();
    assertThat(configuration.getEmailSubject()).isEqualTo("ACTION-REQUIRED: New inspection violations introduced!");
    assertThat(configuration.getEmailSubjectNoChanges()).isEqualTo("WARNING: New inspection violations without code change!");
    assertThat(configuration.getDisabledProjectIds()).isEmpty();
  }

  @Test
  void setInspectionAdminGroupName() {
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    configuration.setInspectionAdminGroupName("newValue");
    assertThat(configuration.getInspectionAdminGroupName()).isEqualTo("newValue");
  }

  @Test
  void setBitbucketRootUrl() {
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    configuration.setBitbucketRootUrl("newValue");
    assertThat(configuration.getBitbucketRootUrl()).isEqualTo("newValue");
  }

  @Test
  void setEmailFromAddress() {
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    configuration.setEmailFromAddress("newValue");
    assertThat(configuration.getEmailFromAddress()).isEqualTo("newValue");
  }

  @Test
  void setEmailSmtpHost() {
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    configuration.setEmailSmtpHost("newValue");
    assertThat(configuration.getEmailSmtpHost()).isEqualTo("newValue");
  }

  @Test
  void setEmailSmtpPort() {
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    configuration.setEmailSmtpPort(12);
    assertThat(configuration.getEmailSmtpPort()).isEqualTo(12);
  }

  @Test
  void setEmailSmtpLogin() {
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    configuration.setEmailSmtpLogin("newValue");
    assertThat(configuration.getEmailSmtpLogin()).isEqualTo("newValue");
  }

  @Test
  void setEmailSmtpPassword() {
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    configuration.setEmailSmtpPassword("newValue");
    assertThat(configuration.getEmailSmtpPassword()).isEqualTo("newValue");
  }

  @Test
  void setEmailSmtpStartTls() {
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    configuration.setEmailSmtpStartTls(true);
    assertThat(configuration.isEmailSmtpStartTls()).isTrue();
  }

  @Test
  void setEmailSubject() {
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    configuration.setEmailSubject("newValue");
    assertThat(configuration.getEmailSubject()).isEqualTo("newValue");
  }

  @Test
  void setEmailSubjectNoChanges() {
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    configuration.setEmailSubjectNoChanges("newValue");
    assertThat(configuration.getEmailSubjectNoChanges()).isEqualTo("newValue");
  }

  @Test
  void setEmailTemplate() {
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    configuration.setEmailTemplate("newValue");
    assertThat(configuration.getEmailTemplate()).isEqualTo("newValue");
  }

  @Test
  void setDisabledProjectIds() {
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    configuration.setDisabledProjectIds(new HashSet<>(Arrays.asList("P1", "P2")));
    assertThat(configuration.getDisabledProjectIds()).contains("P1", "P2");
  }
}
