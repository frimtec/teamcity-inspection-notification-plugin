package com.github.frimtec.teamcity.plugin.inspectionnotification;

import java.util.Arrays;
import java.util.HashSet;

import jetbrains.buildServer.serverSide.crypt.RSACipher;
import org.apache.commons.codec.binary.Hex;
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
    assertThat(configuration.getEmailSmtpPasswordScrambled()).isEqualTo("scrambled:bmV3VmFsdWU=");
  }

  @Test
  void setEmailSmtpPasswordScrambled() {
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    configuration.setEmailSmtpPasswordScrambled("scrambled:bmV3VmFsdWU=");
    assertThat(configuration.getEmailSmtpPasswordScrambled()).isEqualTo("scrambled:bmV3VmFsdWU=");
    assertThat(configuration.getEmailSmtpPassword()).isEqualTo("newValue");
  }

  @Test
  void setEmailSmtpPasswordScrambledButWithUnscrambledValue() {
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    configuration.setEmailSmtpPasswordScrambled("newValue");
    assertThat(configuration.getEmailSmtpPasswordScrambled()).isEqualTo("newValue");
    assertThat(configuration.getEmailSmtpPassword()).isEqualTo("newValue");
  }

  @Test
  void setEmailSmtpPasswordNull() {
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    configuration.setEmailSmtpPassword(null);
    assertThat(configuration.getEmailSmtpPassword()).isNull();
    assertThat(configuration.getEmailSmtpPasswordScrambled()).isNull();
  }

  @Test
  void setEmailSmtpPasswordEmpty() {
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    configuration.setEmailSmtpPassword("");
    assertThat(configuration.getEmailSmtpPassword()).isEmpty();
    assertThat(configuration.getEmailSmtpPasswordScrambled()).isEmpty();
  }

  @Test
  void setEncryptedEmailSmtpPassword() {
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    configuration.setEmailSmtpPassword("newValue");
    String encryptedEmailSmtpPassword = configuration.getEncryptedEmailSmtpPassword();
    assertThat(encryptedEmailSmtpPassword).doesNotContain("newWalue");
    assertThat(encryptedEmailSmtpPassword).hasSize(256);
    configuration.setEncryptedEmailSmtpPassword(null);
    assertThat(configuration.getEmailSmtpPassword()).isNull();
    configuration.setEncryptedEmailSmtpPassword(encryptedEmailSmtpPassword);
    assertThat(configuration.getEmailSmtpPassword()).isEqualTo("newValue");
  }

  @Test
  void setEncryptedEmailSmtpPasswordNull() {
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    configuration.setEncryptedEmailSmtpPassword(null);
    assertThat(configuration.getEmailSmtpPassword()).isNull();
    assertThat(configuration.getEmailSmtpPasswordScrambled()).isNull();
  }

  @Test
  void setEncryptedEmailSmtpPasswordEmpty() {
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    configuration.setEncryptedEmailSmtpPassword("");
    assertThat(configuration.getEmailSmtpPassword()).isEmpty();
    assertThat(configuration.getEmailSmtpPasswordScrambled()).isEmpty();
  }

  @Test
  void getHexEncodedPublicKey() {
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    String publicKey = configuration.getHexEncodedPublicKey();
    assertThat(publicKey).isEqualTo(RSACipher.getHexEncodedPublicKey());
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
