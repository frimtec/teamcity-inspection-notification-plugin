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

final class NotificationMailGenerator {
  private static final String TEMPLATE_NAME = "email";
  private final InspectionNotificationConfiguration pluginConfiguration;
  private final Configuration configuration;
  private final StringTemplateLoader templateLoader = new StringTemplateLoader();

  public NotificationMailGenerator(InspectionNotificationConfiguration pluginConfiguration) {
    this.pluginConfiguration = pluginConfiguration;
    this.configuration = new Configuration(new Version("2.3.28"));
    this.configuration.setTemplateLoader(this.templateLoader);
    this.configuration.setDefaultEncoding("UTF-8");
    this.configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    this.configuration.setWhitespaceStripping(true);
  }

  public String generate(NotificationMessage message) {
    this.templateLoader.putTemplate(TEMPLATE_NAME, this.pluginConfiguration.getEmailTemplate());
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
