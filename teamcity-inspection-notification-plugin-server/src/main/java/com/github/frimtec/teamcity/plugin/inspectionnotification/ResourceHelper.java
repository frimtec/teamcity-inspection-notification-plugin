package com.github.frimtec.teamcity.plugin.inspectionnotification;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import org.apache.commons.io.IOUtils;
import static java.nio.charset.StandardCharsets.UTF_8;

final class ResourceHelper {
  private static final String TEMPLATE_NAME = "notification-email.ftl";

  private ResourceHelper() {
  }

  public static String loadDefaultEmailTemplate() throws IOException {
    InputStream defaultEmailTemplate = Objects.requireNonNull(
        InspectionNotificationConfiguration.class.getClassLoader().getResourceAsStream(TEMPLATE_NAME)
    );
    return IOUtils.toString(defaultEmailTemplate, UTF_8.name());
  }
}
