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
import java.io.InputStream;
import java.util.Objects;
import org.apache.commons.io.IOUtils;
import static java.nio.charset.StandardCharsets.UTF_8;

public final class ResourceHelper {
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
