package com.github.frimtec.teamcity.plugin.inspectionnotification;

import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class MailSenderFacoryTest {
  @Test
  void createNonSecureMailSender() {
    MailSenderFacory factory = new MailSenderFacory();
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    configuration.setEmailSmtpHost("localhost");
    configuration.setEmailSmtpPort(25);
    JavaMailSenderImpl mailSender = (JavaMailSenderImpl) factory.apply(configuration);
    assertThat(mailSender.getHost()).isEqualTo("localhost");
    assertThat(mailSender.getPort()).isEqualTo(25);
    assertThat(mailSender.getUsername()).isNull();
    assertThat(mailSender.getPassword()).isNull();
    assertThat(mailSender.getJavaMailProperties()).isEmpty();
  }

  @Test
  void createAuthMailSenderUnsecure() {
    MailSenderFacory factory = new MailSenderFacory();
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    configuration.setEmailSmtpHost("localhost");
    configuration.setEmailSmtpPort(25);
    configuration.setEmailSmtpLogin("login");
    JavaMailSenderImpl mailSender = (JavaMailSenderImpl) factory.apply(configuration);
    assertThat(mailSender.getHost()).isEqualTo("localhost");
    assertThat(mailSender.getPort()).isEqualTo(25);
    assertThat(mailSender.getUsername()).isEqualTo("login");
    assertThat(mailSender.getPassword()).isNull();
    assertThat(mailSender.getJavaMailProperties()).containsExactlyEntriesOf(
        Collections.singletonMap("mail.smtps.auth", "true")
    );
  }

  @Test
  void createAuthMailSenderStartTls() {
    MailSenderFacory factory = new MailSenderFacory();
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    configuration.setEmailSmtpHost("localhost");
    configuration.setEmailSmtpPort(25);
    configuration.setEmailSmtpLogin("login");
    configuration.setEmailSmtpPassword("password");
    configuration.setEmailSmtpStartTls(true);
    JavaMailSenderImpl mailSender = (JavaMailSenderImpl) factory.apply(configuration);
    assertThat(mailSender.getHost()).isEqualTo("localhost");
    assertThat(mailSender.getPort()).isEqualTo(25);
    assertThat(mailSender.getUsername()).isEqualTo("login");
    assertThat(mailSender.getPassword()).isEqualTo("password");
    assertThat(mailSender.getJavaMailProperties()).containsAnyOf(
        entry("mail.smtps.auth", "true"),
        entry("mail.smtp.starttls.enable", "true")
    );
  }

}
