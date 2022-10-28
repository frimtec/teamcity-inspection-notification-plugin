package com.github.frimtec.teamcity.plugin.inspectionnotification;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.function.Function;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import jetbrains.buildServer.serverSide.ServerPaths;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionNotificationConfiguration.EMAIL_SMTP_PORT_KEY;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionNotificationConfiguration.EMAIL_TEMPLATE_KEY;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionNotificationConfiguration.INSPECTION_ADMIN_GROUP_NAME_KEY;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionNotificationConfiguration.PROJECT_DISABLED_KEY;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionNotificationConfiguration.PROJECT_ID_KEY;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionNotificationConfigurationController.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class InspectionNotificationConfigurationControllerTest {
  @Test
  public void initialiseForNonExistingFile(@TempDir Path configPath) throws IOException {
    Path configFilePath = configPath.resolve("inspection-notification-plugin.xml");
    assertThat(Files.exists(configFilePath)).isFalse();

    InspectionNotificationConfigurationController controller = controller(configPath);
    controller.initialise();
    assertThat(Files.exists(configFilePath)).isTrue();
    assertThat(Files.readAllLines(configFilePath).size()).isGreaterThan(0);
  }

  @Test
  public void initialiseForExistingFile(@TempDir Path configPath) throws IOException {
    Path configFilePath = configPath.resolve("inspection-notification-plugin.xml");
    String content = "<inspection-notification>\n"
        + "  <inspectionAdminGroupName>inspectionAdminGroupName</inspectionAdminGroupName>\n"
        + "  <bitbucketRootUrl></bitbucketRootUrl>\n"
        + "  <emailFromAddress>emailFromAddress</emailFromAddress>\n"
        + "  <emailSmtpHost>emailSmtpHost</emailSmtpHost>\n"
        + "  <emailSmtpPort>25</emailSmtpPort>\n"
        + "  <emailSmtpLogin></emailSmtpLogin>\n"
        + "  <emailSmtpPassword></emailSmtpPassword>\n"
        + "  <emailSmtpStartTls>false</emailSmtpStartTls>\n"
        + "  <emailSubject>emailSubject</emailSubject>\n"
        + "  <emailSubjectNoChanges>emailSubjectNoChanges</emailSubjectNoChanges>\n"
        + "  <emailTemplate>emailTemplate</emailTemplate>\n"
        + "  <disabledProjectIds/>\n"
        + "</inspection-notification>";
    Files.write(configFilePath, Collections.singletonList(
        content
    ));
    InspectionNotificationConfigurationController controller = controller(configPath);
    controller.initialise();
    controller.saveConfiguration();
    assertThat(String.join("\n", Files.readAllLines(configFilePath))).isEqualTo(content);
  }

  @Test
  public void initialiseWithBadExistingFile(@TempDir Path configPath) throws IOException {
    Path configFilePath = configPath.resolve("inspection-notification-plugin.xml");
    Files.createFile(configFilePath);
    assertThat(Files.readAllLines(configFilePath).size()).isEqualTo(0);

    InspectionNotificationConfigurationController controller = controller(configPath);
    controller.initialise();
    assertThat(Files.exists(configFilePath)).isTrue();
    assertThat(Files.readAllLines(configFilePath).size()).isEqualTo(0);
    controller.saveConfiguration();
    assertThat(Files.readAllLines(configFilePath).size()).isGreaterThan(0);
  }

  @Test
  public void doHandleNoEditAction(@TempDir Path configPath) {
    Path configFilePath = configPath.resolve("inspection-notification-plugin.xml");
    assertThat(Files.exists(configFilePath)).isFalse();

    InspectionNotificationConfigurationController controller = controller(configPath);
    controller.initialise();

    HttpServletRequest request = request();
    when(request.getParameter(INSPECTION_ADMIN_GROUP_NAME_KEY)).thenReturn("NEW_VALUE");
    HttpServletResponse response = mock(HttpServletResponse.class);

    controller.doHandle(request, response);
    assertThat(controller.getConfiguration().getInspectionAdminGroupName()).isEqualTo("inspection-admin");
  }

  @Test
  public void doHandleEditProjectDisableSettingsAction(@TempDir Path configPath) {
    Path configFilePath = configPath.resolve("inspection-notification-plugin.xml");
    assertThat(Files.exists(configFilePath)).isFalse();

    InspectionNotificationConfigurationController controller = controller(configPath);
    controller.initialise();
    controller.getConfiguration().setDisabledProjectIds(Collections.singleton("P0"));

    HttpServletRequest request = request();
    when(request.getParameter(PROJECT_PARAMETER)).thenReturn("");
    when(request.getParameter(PROJECT_DISABLED_KEY)).thenReturn("true");
    when(request.getParameter(PROJECT_ID_KEY)).thenReturn("P1");
    HttpServletResponse response = mock(HttpServletResponse.class);

    controller.doHandle(request, response);
    assertThat(controller.getConfiguration().getDisabledProjectIds()).contains("P0", "P1");
  }

  @Test
  public void doHandleEditProjectEnableSettingsAction(@TempDir Path configPath) {
    Path configFilePath = configPath.resolve("inspection-notification-plugin.xml");
    assertThat(Files.exists(configFilePath)).isFalse();

    InspectionNotificationConfigurationController controller = controller(configPath);
    controller.initialise();
    controller.getConfiguration().setDisabledProjectIds(new HashSet<>(Arrays.asList("P0", "P1")));

    HttpServletRequest request = request();
    when(request.getParameter(PROJECT_PARAMETER)).thenReturn("");
    when(request.getParameter(PROJECT_DISABLED_KEY)).thenReturn("false");
    when(request.getParameter(PROJECT_DISABLED_KEY)).thenReturn("false");
    when(request.getParameter(PROJECT_ID_KEY)).thenReturn("P1");
    HttpServletResponse response = mock(HttpServletResponse.class);

    controller.doHandle(request, response);
    assertThat(controller.getConfiguration().getDisabledProjectIds()).contains("P0");
  }

  @Test
  public void doHandleEditActionForEmptyTemplate(@TempDir Path configPath) {
    Path configFilePath = configPath.resolve("inspection-notification-plugin.xml");
    assertThat(Files.exists(configFilePath)).isFalse();

    InspectionNotificationConfigurationController controller = controller(configPath);
    controller.initialise();

    HttpServletRequest request = request();
    when(request.getParameter(EDIT_PARAMETER)).thenReturn("");
    when(request.getParameter(EMAIL_TEMPLATE_KEY)).thenReturn("");
    HttpServletResponse response = mock(HttpServletResponse.class);

    controller.doHandle(request, response);
    assertThat(controller.getConfiguration().getEmailTemplate()).contains("<html lang=\"en\">");
  }

  @Test
  public void doHandleEditActionForWithSendingTestEmail(@TempDir Path configPath) {
    Path configFilePath = configPath.resolve("inspection-notification-plugin.xml");
    assertThat(Files.exists(configFilePath)).isFalse();

    JavaMailSender mailSender = mock(JavaMailSender.class);
    Function<InspectionNotificationConfiguration, JavaMailSender> mailSenderFactory = inspectionNotificationConfiguration -> mailSender;
    InspectionNotificationConfigurationController controller = controller(configPath, mailSenderFactory);
    controller.initialise();

    HttpServletRequest request = request();
    when(request.getParameter(EDIT_PARAMETER)).thenReturn("");
    when(request.getParameter(TEST_MAIL_TO_ADDRESS)).thenReturn("email@server");
    HttpServletResponse response = mock(HttpServletResponse.class);

    controller.doHandle(request, response);
    verify(mailSender).send(any(SimpleMailMessage.class));
  }

  @Test
  public void doHandleEditActionForWithSendingTestEmailResultingInError(@TempDir Path configPath) {
    Path configFilePath = configPath.resolve("inspection-notification-plugin.xml");
    assertThat(Files.exists(configFilePath)).isFalse();

    JavaMailSender mailSender = mock(JavaMailSender.class);
    doThrow(new RuntimeException("Test")).when(mailSender).send(any(SimpleMailMessage.class));

    Function<InspectionNotificationConfiguration, JavaMailSender> mailSenderFactory = inspectionNotificationConfiguration -> mailSender;
    InspectionNotificationConfigurationController controller = controller(configPath, mailSenderFactory);
    controller.initialise();

    HttpServletRequest request = request();
    when(request.getParameter(EDIT_PARAMETER)).thenReturn("");
    when(request.getParameter(TEST_MAIL_TO_ADDRESS)).thenReturn("email@server");
    HttpServletResponse response = mock(HttpServletResponse.class);

    controller.doHandle(request, response);
  }

  @Test
  public void doHandleEditActionForWithNotSendingTestEmail(@TempDir Path configPath) {
    Path configFilePath = configPath.resolve("inspection-notification-plugin.xml");
    assertThat(Files.exists(configFilePath)).isFalse();

    JavaMailSender mailSender = mock(JavaMailSender.class);
    Function<InspectionNotificationConfiguration, JavaMailSender> mailSenderFactory = inspectionNotificationConfiguration -> mailSender;
    InspectionNotificationConfigurationController controller = controller(configPath, mailSenderFactory);
    controller.initialise();

    HttpServletRequest request = request();
    when(request.getParameter(EDIT_PARAMETER)).thenReturn("");
    when(request.getParameter(TEST_MAIL_TO_ADDRESS)).thenReturn("");
    HttpServletResponse response = mock(HttpServletResponse.class);

    controller.doHandle(request, response);
    verifyNoInteractions(mailSender);
  }

  @Test
  public void doHandleEditActionWithException(@TempDir Path configPath) {
    Path configFilePath = configPath.resolve("inspection-notification-plugin.xml");
    assertThat(Files.exists(configFilePath)).isFalse();

    InspectionNotificationConfigurationController controller = controller(configPath);
    controller.initialise();

    HttpServletRequest request = request();
    when(request.getParameter(EDIT_PARAMETER)).thenReturn("");
    when(request.getParameter(EMAIL_SMTP_PORT_KEY)).thenReturn("NO_INT");
    HttpServletResponse response = mock(HttpServletResponse.class);

    int port = controller.getConfiguration().getEmailSmtpPort();
    controller.doHandle(request, response);
    assertThat(controller.getConfiguration().getEmailSmtpPort()).isEqualTo(port);
  }

  private static HttpServletRequest request() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getSession()).thenReturn(mock(HttpSession.class));
    return request;
  }

  private static InspectionNotificationConfigurationController controller(Path configPath) {
    ServerPaths serverPaths = mock(ServerPaths.class);
    when(serverPaths.getConfigDir()).thenReturn(configPath.toString());
    return new InspectionNotificationConfigurationController(
        serverPaths,
        mock(WebControllerManager.class),
        new InspectionNotificationConfiguration()
    );
  }

  private static InspectionNotificationConfigurationController controller(
      Path configPath,
      Function<InspectionNotificationConfiguration, JavaMailSender> mailSenderFactory
  ) {
    ServerPaths serverPaths = mock(ServerPaths.class);
    when(serverPaths.getConfigDir()).thenReturn(configPath.toString());
    return new InspectionNotificationConfigurationController(
        serverPaths,
        mock(WebControllerManager.class),
        new InspectionNotificationConfiguration(),
        mailSenderFactory
    );
  }

}
