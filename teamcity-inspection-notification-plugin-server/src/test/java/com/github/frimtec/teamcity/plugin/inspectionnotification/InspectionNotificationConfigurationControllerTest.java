package com.github.frimtec.teamcity.plugin.inspectionnotification;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import jetbrains.buildServer.serverSide.ServerPaths;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionNotificationConfiguration.EMAIL_SMTP_PORT_KEY;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionNotificationConfiguration.EMAIL_TEMPLATE_KEY;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionNotificationConfiguration.INSPECTION_ADMIN_GROUP_NAME_KEY;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionNotificationConfigurationController.EDIT_PARAMETER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InspectionNotificationConfigurationControllerTest {
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
        + "  <emailSubject>emailSubject</emailSubject>\n"
        + "  <emailSubjectNoChanges>emailSubjectNoChanges</emailSubjectNoChanges>\n"
        + "  <emailTemplate>emailTemplate</emailTemplate>\n"
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
  public void doHandleEditAction(@TempDir Path configPath) {
    Path configFilePath = configPath.resolve("inspection-notification-plugin.xml");
    assertThat(Files.exists(configFilePath)).isFalse();

    InspectionNotificationConfigurationController controller = controller(configPath);
    controller.initialise();

    HttpServletRequest request = request();
    when(request.getParameter(EDIT_PARAMETER)).thenReturn("");
    when(request.getParameter(INSPECTION_ADMIN_GROUP_NAME_KEY)).thenReturn("NEW_VALUE");
    HttpServletResponse response = mock(HttpServletResponse.class);

    controller.doHandle(request, response);
    assertThat(controller.getConfiguration().getInspectionAdminGroupName()).isEqualTo("NEW_VALUE");
  }

  @Test
  public void doHandleEditActionForNonEmptyTemplate(@TempDir Path configPath) {
    Path configFilePath = configPath.resolve("inspection-notification-plugin.xml");
    assertThat(Files.exists(configFilePath)).isFalse();

    InspectionNotificationConfigurationController controller = controller(configPath);
    controller.initialise();

    HttpServletRequest request = request();
    when(request.getParameter(EDIT_PARAMETER)).thenReturn("");
    when(request.getParameter(EMAIL_TEMPLATE_KEY)).thenReturn("NEW_VALUE");
    HttpServletResponse response = mock(HttpServletResponse.class);

    controller.doHandle(request, response);
    assertThat(controller.getConfiguration().getEmailTemplate()).isEqualTo("NEW_VALUE");
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
    assertThat(controller.getConfiguration().getEmailTemplate()).contains("<html>");
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

}
