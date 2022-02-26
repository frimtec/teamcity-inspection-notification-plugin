package com.github.frimtec.teamcity.plugin.inspectionnotification;

class SmtpEmailSenderManuelTest {
  /**
   * Sends a test mail via SMTP.
   * @param args host port login password startTls(true|false) fromAddress toAddress
   */
  public static void main(String[] args) {
    if(args.length != 7) {
      throw new IllegalArgumentException("Expected parameters: host port login password startTls(true|false) fromAddress toAddress");
    }
    InspectionNotificationConfiguration pluginConfiguration = new InspectionNotificationConfiguration();
    pluginConfiguration.setEmailSmtpHost(args[0]);
    pluginConfiguration.setEmailSmtpPort(Integer.parseInt(args[1]));
    pluginConfiguration.setEmailSmtpLogin(args[2]);
    pluginConfiguration.setEmailSmtpPassword(args[3]);
    pluginConfiguration.setEmailSmtpStartTls(Boolean.parseBoolean(args[4]));
    new SmtpEmailSender(pluginConfiguration).sendTestMail(args[5], args[6]);
  }
}
