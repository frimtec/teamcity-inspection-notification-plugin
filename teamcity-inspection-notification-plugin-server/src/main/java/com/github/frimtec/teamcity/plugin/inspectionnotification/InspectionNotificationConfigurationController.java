package com.github.frimtec.teamcity.plugin.inspectionnotification;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jetbrains.buildServer.controllers.FormUtil;
import jetbrains.buildServer.log.Loggers;
import org.jetbrains.annotations.NotNull;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.servlet.ModelAndView;
import com.thoughtworks.xstream.XStream;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.ServerPaths;
import jetbrains.buildServer.util.StringUtil;
import jetbrains.buildServer.web.openapi.WebControllerManager;

import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionNotificationConfiguration.*;

public class InspectionNotificationConfigurationController extends BaseController {

  private static final String CONTROLLER_PATH = "/configureInspectionNotification.html";
  public static final String EDIT_PARAMETER = "edit";
  public static final String TEST_MAIL_TO_ADDRESS = "testMailToAddress";
  public static final String PROJECT_PARAMETER = "project";
  private static final String CONFIG_FILE = "inspection-notification-plugin.xml";
  private static final String SAVED_ID = "configurationSaved";
  private static final String SAVED_MESSAGE = "Your changes have been saved.";

  private final Path configFilePath;

  private final InspectionNotificationConfiguration configuration;
  private final Function<InspectionNotificationConfiguration, JavaMailSender> mailSenderFactory;

  public InspectionNotificationConfigurationController(
      @NotNull ServerPaths serverPaths,
      @NotNull WebControllerManager manager,
      @NotNull InspectionNotificationConfiguration configuration
  ) {
    this(serverPaths, manager, configuration, new MailSenderFacory());
  }

  public InspectionNotificationConfigurationController(
      @NotNull ServerPaths serverPaths,
      @NotNull WebControllerManager manager,
      @NotNull InspectionNotificationConfiguration configuration,
      @NotNull Function<InspectionNotificationConfiguration, JavaMailSender> mailSenderFactory
  ) {
    manager.registerController(CONTROLLER_PATH, this);
    this.configuration = configuration;
    this.configFilePath = Paths.get(serverPaths.getConfigDir()).resolve(CONFIG_FILE);
    this.mailSenderFactory = mailSenderFactory;
    this.logger.debug(String.format("Config file path: %s", this.configFilePath));
    this.logger.info("Controller created");
  }

  private void handleConfigurationChange(HttpServletRequest request) throws IOException {
    FormUtil.bindFromRequest(request, this.configuration);
    this.saveConfiguration();

    // Update the page
    this.getOrCreateMessages(request).addMessage(SAVED_ID, SAVED_MESSAGE);
  }

  private void handleProjectConfigurationChange(HttpServletRequest request) throws IOException {
    String projectId = request.getParameter(PROJECT_ID_KEY);
    boolean disabled = Boolean.parseBoolean(request.getParameter(PROJECT_DISABLED_KEY));
    Set<String> disabledProjectIds = new HashSet<>(this.configuration.getDisabledProjectIds());
    if (disabled) {
      disabledProjectIds.add(projectId);
    } else {
      disabledProjectIds.remove(projectId);
    }
    this.configuration.setDisabledProjectIds(disabledProjectIds);
    this.saveConfiguration();

    // Update the page
    this.getOrCreateMessages(request).addMessage(SAVED_ID, SAVED_MESSAGE);
  }

  @Override
  public ModelAndView doHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) {
    try {
      if (request.getParameter(EDIT_PARAMETER) != null) {
        this.handleConfigurationChange(request);
      }
      String testMailToAddress = request.getParameter(TEST_MAIL_TO_ADDRESS);
      if (!StringUtil.isEmpty(testMailToAddress)) {
        try {
          SmtpEmailSender smtpEmailSender = new SmtpEmailSender(this.configuration, this.mailSenderFactory);
          smtpEmailSender.sendTestMail(this.configuration.getEmailFromAddress(), testMailToAddress);
          Loggers.SERVER.info("Test email sent to " + testMailToAddress);
        } catch (Exception e) {
          Loggers.SERVER.error("Cannot sent test email to " + testMailToAddress, e);
        }
      }
      if (request.getParameter(PROJECT_PARAMETER) != null) {
        this.handleProjectConfigurationChange(request);
      }
    } catch (Exception e) {
      this.logger.error("Could not handle request", e);
    }
    return null;
  }

  public void initialise() {
    try {
      if (Files.exists(this.configFilePath)) {
        this.loadConfiguration();
      } else {
        this.saveConfiguration();
      }
    } catch (Exception e) {
      this.logger.error("Could not load configuration", e);
    }
  }

  public void loadConfiguration() throws IOException {
    XStream xstream = new XStream();
    Class<?>[] classes = new Class[]{InspectionNotificationConfiguration.class};
    xstream.allowTypes(classes);
    xstream.setClassLoader(this.configuration.getClass().getClassLoader());
    xstream.setClassLoader(InspectionNotificationConfiguration.class.getClassLoader());
    xstream.processAnnotations(InspectionNotificationConfiguration.class);
    InspectionNotificationConfiguration configuration;
    try (FileReader fileReader = new FileReader(this.configFilePath.toFile())) {
      configuration = (InspectionNotificationConfiguration) xstream.fromXML(fileReader);
    }

    // Copy the values, because we need it on the original shared (bean),
    // which is a singleton
    this.configuration.setInspectionAdminGroupName(configuration.getInspectionAdminGroupName());
    this.configuration.setBitbucketRootUrl(configuration.getBitbucketRootUrl());
    this.configuration.setEmailFromAddress(configuration.getEmailFromAddress());
    this.configuration.setEmailSmtpHost(configuration.getEmailSmtpHost());
    this.configuration.setEmailSmtpPort(configuration.getEmailSmtpPort());
    this.configuration.setEmailSmtpLogin(configuration.getEmailSmtpLogin());
    this.configuration.setEmailSmtpPassword(configuration.getEmailSmtpPassword());
    this.configuration.setEmailSmtpStartTls(configuration.isEmailSmtpStartTls());
    this.configuration.setEmailSubject(configuration.getEmailSubject());
    this.configuration.setEmailSubjectNoChanges(configuration.getEmailSubjectNoChanges());
    if (!StringUtil.isEmpty(configuration.getEmailTemplate())) {
      this.configuration.setEmailTemplate(configuration.getEmailTemplate());
    }
    this.configuration.setDisabledProjectIds(configuration.getDisabledProjectIds());
  }

  public void saveConfiguration() throws IOException {
    XStream xstream = new XStream();
    xstream.processAnnotations(this.configuration.getClass());
    try (FileOutputStream fileOutputStream = new FileOutputStream(this.configFilePath.toFile())) {
      xstream.toXML(this.configuration, fileOutputStream);
      fileOutputStream.flush();
    }
  }

  final InspectionNotificationConfiguration getConfiguration() {
    return this.configuration;
  }
}
