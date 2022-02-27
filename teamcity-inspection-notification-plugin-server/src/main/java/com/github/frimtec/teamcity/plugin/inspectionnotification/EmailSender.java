package com.github.frimtec.teamcity.plugin.inspectionnotification;

@FunctionalInterface
public interface EmailSender {
  void sendNotification(NotificationMessage message, String[] toAddresses);
}
