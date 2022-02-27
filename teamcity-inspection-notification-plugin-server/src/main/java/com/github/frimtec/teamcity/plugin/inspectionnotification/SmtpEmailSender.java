package com.github.frimtec.teamcity.plugin.inspectionnotification;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.Arrays;
import java.util.function.Function;

public class SmtpEmailSender implements EmailSender {

  private final InspectionNotificationConfiguration pluginConfiguration;
  private final NotificationMailGenerator mailGenerator;
  private final Function<InspectionNotificationConfiguration, JavaMailSender> mailSenderFactory;

  public SmtpEmailSender(InspectionNotificationConfiguration pluginConfiguration) {
    this(pluginConfiguration, new NotificationMailGenerator(), new MailSenderFacory());
  }

  SmtpEmailSender(
      InspectionNotificationConfiguration pluginConfiguration,
      NotificationMailGenerator mailGenerator,
      Function<InspectionNotificationConfiguration, JavaMailSender> mailSenderFactory
  ) {
    this.pluginConfiguration = pluginConfiguration;
    this.mailGenerator = mailGenerator;
    this.mailSenderFactory = mailSenderFactory;
  }

  @Override
  public void sendNotification(NotificationMessage message, String[] toAddresses) {
    try {
      JavaMailSender mailSender = this.mailSenderFactory.apply(this.pluginConfiguration);
      MimeMessageHelper helper = new MimeMessageHelper(mailSender.createMimeMessage(), false, "utf-8");
      String text = this.mailGenerator.generate(message, this.pluginConfiguration.getEmailTemplate());
      helper.setText(text, true);
      helper.setTo(toAddresses);
      helper.setSubject(message.getSubject());
      helper.setFrom(this.pluginConfiguration.getEmailFromAddress());
      mailSender.send(helper.getMimeMessage());
    } catch (Exception e) {
      throw new RuntimeException(String.format("InspectionNotificationPlugin: Can not send email for %s to %s",
          message.getBuild(), String.join(";", Arrays.asList(toAddresses))), e);
    }
  }

  public void sendTestMail(String fromAddress, String toAddresse) {
    JavaMailSender mailSender = this.mailSenderFactory.apply(this.pluginConfiguration);
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(fromAddress);
    message.setSubject("Testmail from " + SmtpEmailSender.class.getCanonicalName());
    message.setText("Text");
    message.setTo(toAddresse);
    mailSender.send(message);
  }

}
