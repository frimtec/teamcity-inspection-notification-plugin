package com.github.frimtec.teamcity.plugin.inspectionnotification;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;
import java.util.function.Function;

import static org.springframework.util.StringUtils.isEmpty;

class MailSenderFacory implements Function<InspectionNotificationConfiguration, JavaMailSender> {
  @Override
  public JavaMailSender apply(InspectionNotificationConfiguration configuration) {
    return createMailSender(
        configuration.getEmailSmtpHost(),
        configuration.getEmailSmtpPort(),
        configuration.getEmailSmtpLogin(),
        configuration.getEmailSmtpPassword(),
        configuration.isEmailSmtpStartTls()
    );
  }

  private static JavaMailSender createMailSender(
      String host,
      int port,
      String login,
      String password,
      boolean startTls
  ) {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost(host);
    mailSender.setPort(port);
    Properties mailProps = new Properties();
    if (!isEmpty(login)) {
      mailSender.setUsername(login);
      mailProps.put("mail.smtps.auth", "true");
      if (!isEmpty(password)) {
        mailSender.setPassword(password);
      }
    }

    if (startTls) {
      mailProps.put("mail.smtp.starttls.enable", "true");
      mailProps.put("mail.smtp.ssl.protocols", "TLSv1.2");
    }
    mailSender.setJavaMailProperties(mailProps);
    return mailSender;
  }

}
