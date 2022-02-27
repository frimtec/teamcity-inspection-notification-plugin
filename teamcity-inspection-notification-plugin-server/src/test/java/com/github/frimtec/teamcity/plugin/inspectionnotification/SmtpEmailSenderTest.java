package com.github.frimtec.teamcity.plugin.inspectionnotification;

import jetbrains.buildServer.serverSide.SRunningBuild;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SmtpEmailSenderTest {

  @Test
  void sendTestMail() {
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    JavaMailSender sender = mock(JavaMailSender.class);
    SmtpEmailSender mailSender = new SmtpEmailSender(
        configuration,
        new NotificationMailGenerator(),
        (config) -> sender
    );
    ArgumentCaptor<SimpleMailMessage> mailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
    mailSender.sendTestMail("from@address", "to@address");
    verify(sender).send(mailCaptor.capture());

    SimpleMailMessage mail = mailCaptor.getValue();
    assertThat(mail.getFrom()).isEqualTo("from@address");
    assertThat(mail.getTo()).isEqualTo(new String[]{"to@address"});
  }

  @Test
  void sendNotification() throws MessagingException {
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    configuration.setEmailFromAddress("from@address");
    JavaMailSender sender = mock(JavaMailSender.class);
    MimeMessage mimeMessage = mock(MimeMessage.class);
    when(sender.createMimeMessage()).thenReturn(mimeMessage);
    NotificationMessage message = new NotificationMessage(
        "url",
        mock(SRunningBuild.class),
        Collections.emptyList(),
        Collections.emptySet(),
        (sRunningBuild, inspectionViolation) -> "",
        "Subject1",
        "Subject2"
    );
    NotificationMailGenerator mailGenerator = mock(NotificationMailGenerator.class);
    when(mailGenerator.generate(eq(message), anyString())).thenReturn("Mailbody");

    SmtpEmailSender mailSender = new SmtpEmailSender(
        configuration,
        mailGenerator,
        (config) -> sender
    );
    mailSender.sendNotification(
        message,
        new String[]{"to1@address", "to2@address"}
    );
    verify(sender).send(eq(mimeMessage));
    verify(mimeMessage).setContent("Mailbody", "text/html;charset=utf-8");
    verify(mimeMessage).setRecipients(Message.RecipientType.TO, new Address[]{
            new InternetAddress("to1@address"),
            new InternetAddress("to2@address")
        }
    );
    verify(mimeMessage).setSubject("Subject2", "utf-8");
    verify(mimeMessage).setFrom(new InternetAddress("from@address"));
  }
}
