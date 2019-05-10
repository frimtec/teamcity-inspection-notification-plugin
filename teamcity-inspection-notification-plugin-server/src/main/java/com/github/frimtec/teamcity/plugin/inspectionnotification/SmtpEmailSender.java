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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

public class SmtpEmailSender implements EmailSender {
  private final InspectionNotificationConfiguration pluginConfiguration;
  private final JavaMailSender mailSender;
  private final NotificationMailGenerator mailGenerator;

  public SmtpEmailSender(InspectionNotificationConfiguration pluginConfiguration) {
    this.pluginConfiguration = pluginConfiguration;
    this.mailSender = createMailSender();
    this.mailGenerator = new NotificationMailGenerator();
  }

  @Override
  public void sendNotification(NotificationMessage message, String[] toAddresses) {
    try {
      MimeMessageHelper helper = new MimeMessageHelper(this.mailSender.createMimeMessage(), false, "utf-8");
      String text = this.mailGenerator.generate(message, this.pluginConfiguration.getEmailTemplate());
      helper.setText(text, true);
      helper.setTo(toAddresses);
      helper.setSubject(message.getSubject());
      helper.setFrom(this.pluginConfiguration.getEmailFromAddress());
      this.mailSender.send(helper.getMimeMessage());
    } catch (Exception e) {
      throw new RuntimeException(String.format("InspectionNotificationPlugin: Can not send email for %s to %s",
          message.getBuild(), String.join(";", Arrays.asList(toAddresses))), e);
    }
  }

  private JavaMailSender createMailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost(this.pluginConfiguration.getEmailSmtpHost());
    mailSender.setPort(this.pluginConfiguration.getEmailSmtpPort());
    return mailSender;
  }


}
