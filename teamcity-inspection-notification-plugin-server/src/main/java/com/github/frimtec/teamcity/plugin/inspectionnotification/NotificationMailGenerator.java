package com.github.frimtec.teamcity.plugin.inspectionnotification;


import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

class NotificationMailGenerator {
  private static final String TEMPLATE_NAME = "email";
  private final Configuration configuration;
  private final StringTemplateLoader templateLoader = new StringTemplateLoader();

  public NotificationMailGenerator() {
    this.configuration = new Configuration(new Version("2.3.28"));
    this.configuration.setTemplateLoader(this.templateLoader);
    this.configuration.setDefaultEncoding("UTF-8");
    this.configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    this.configuration.setWhitespaceStripping(true);
  }

  public String generate(NotificationMessage message, String emailTemplate) {
    this.templateLoader.putTemplate(TEMPLATE_NAME, emailTemplate);
    this.configuration.getCacheStorage().clear();
    try {
      Map<String, Object> input = new HashMap<>();
      input.put("message", message);
      Template template = this.configuration.getTemplate(TEMPLATE_NAME);
      StringWriter result = new StringWriter();
      template.process(input, result);
      return result.toString();
    } catch (IOException e) {
      String errorMsg = String.format("Could not load template %s", TEMPLATE_NAME);
      throw new RuntimeException(errorMsg, e);
    } catch (TemplateException e) {
      String errorMsg = String.format("Could not process template %s", TEMPLATE_NAME);
      throw new RuntimeException(errorMsg, e);
    }
  }
}
