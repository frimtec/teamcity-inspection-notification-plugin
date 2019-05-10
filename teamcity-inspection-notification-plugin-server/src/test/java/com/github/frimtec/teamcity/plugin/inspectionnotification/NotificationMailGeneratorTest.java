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
import org.junit.jupiter.api.Test;
import freemarker.template.TemplateException;
import jetbrains.buildServer.serverSide.SRunningBuild;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.Builders.inspectionViolation;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.Builders.user;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionViolation.Level.ERROR;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionViolation.Level.WARNING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NotificationMailGeneratorTest {

  @Test
  void generateMailForViolationsWithCommitter() throws IOException {
    NotificationMailGenerator generator = new NotificationMailGenerator();
    SRunningBuild build = Builders.runningBuild()
        .fullName("Build Test")
        .buidId(43)
        .buidNumber("12")
        .build();
    NotificationMessage notificationMessage = Builders.notificationMessage()
        .build(build)
        .bitbucketUrlGenerator((sRunningBuild, inspectionViolation) -> "url")
        .teamCityRootUrl("http://localhost/tc")
        .addNewViolations(
            inspectionViolation()
                .level(ERROR)
                .inspectionName("Declaration has problems in Javadoc references")
                .fileName("src/test/java/com/github/frimtec/teamcity/plugin/inspectionnotification/Builders.java")
                .line(30)
                .build(),
            inspectionViolation()
                .level(WARNING)
                .inspectionName("Declaration has Javadoc problems")
                .fileName("src/test/java/com/github/frimtec/teamcity/plugin/inspectionnotification/Builders.java")
                .line(29)
                .build()
        ).addCommitters(
            user()
                .name("Tester One")
                .email("tester.one@tc.com")
                .build(),
            user()
                .name("Tester Two")
                .email("tester.two@tc.com")
                .build()
        ).build();
    String message = generator.generate(notificationMessage, ResourceHelper.loadDefaultEmailTemplate());
    assertThat(message).isEqualTo("<html>\n"
        + "  <h4>The following new inspection violations has been reported in\n"
        + "    <a href='http://localhost/tc/viewLog.html?buildId=43'>Build Test #12:</a>\n"
        + "  </h4>\n"
        + "  <table border=\"1\">\n"
        + "    <tr style=\"font-weight: bold\">\n"
        + "      <td>Level</td>\n"
        + "      <td>Inspection</td>\n"
        + "      <td>Location</td>\n"
        + "    </tr>\n"
        + "    <tr>\n"
        + "      <td>ERROR</td>\n"
        + "      <td>Declaration has problems in Javadoc references</td>\n"
        + "      <td>\n"
        + "          <a href=\"url\">src/test/java/com/github/frimtec/teamcity/plugin/inspectionnotification/Builders.java:30</a>\n"
        + "      </td>\n"
        + "    </tr>\n"
        + "    <tr>\n"
        + "      <td>WARNING</td>\n"
        + "      <td>Declaration has Javadoc problems</td>\n"
        + "      <td>\n"
        + "          <a href=\"url\">src/test/java/com/github/frimtec/teamcity/plugin/inspectionnotification/Builders.java:29</a>\n"
        + "      </td>\n"
        + "    </tr>\n"
        + "  </table>\n"
        + "\n"
        + "  <h4>The following committers have contributed to this build:</h4>\n"
        + "    <ul>\n"
        + "      <li>\n"
        + "        <a href='mailto:tester.one@tc.com'>Tester One</a>\n"
        + "      </li>\n"
        + "      <li>\n"
        + "        <a href='mailto:tester.two@tc.com'>Tester Two</a>\n"
        + "      </li>\n"
        + "    </ul>\n"
        + "</html>\n");
  }

  @Test
  void generateMailForViolationsWithNoCommitters() throws IOException {
    NotificationMailGenerator generator = new NotificationMailGenerator();
    SRunningBuild build = Builders.runningBuild()
        .fullName("Build Test")
        .buidId(43)
        .buidNumber("12")
        .build();
    NotificationMessage notificationMessage = Builders.notificationMessage()
        .build(build)
        .bitbucketUrlGenerator((sRunningBuild, inspectionViolation) -> "url")
        .teamCityRootUrl("http://localhost/tc")
        .addNewViolations(
            inspectionViolation()
                .level(ERROR)
                .inspectionName("Declaration has problems in Javadoc references")
                .fileName("src/test/java/com/github/frimtec/teamcity/plugin/inspectionnotification/Builders.java")
                .line(30)
                .build(),
            inspectionViolation()
                .level(WARNING)
                .inspectionName("Declaration has Javadoc problems")
                .fileName("src/test/java/com/github/frimtec/teamcity/plugin/inspectionnotification/Builders.java")
                .line(29)
                .build()
        ).build();
    String message = generator.generate(notificationMessage, ResourceHelper.loadDefaultEmailTemplate());
    assertThat(message).isEqualTo("<html>\n"
        + "  <h4>The following new inspection violations has been reported in\n"
        + "    <a href='http://localhost/tc/viewLog.html?buildId=43'>Build Test #12:</a>\n"
        + "  </h4>\n"
        + "  <table border=\"1\">\n"
        + "    <tr style=\"font-weight: bold\">\n"
        + "      <td>Level</td>\n"
        + "      <td>Inspection</td>\n"
        + "      <td>Location</td>\n"
        + "    </tr>\n"
        + "    <tr>\n"
        + "      <td>ERROR</td>\n"
        + "      <td>Declaration has problems in Javadoc references</td>\n"
        + "      <td>\n"
        + "          <a href=\"url\">src/test/java/com/github/frimtec/teamcity/plugin/inspectionnotification/Builders.java:30</a>\n"
        + "      </td>\n"
        + "    </tr>\n"
        + "    <tr>\n"
        + "      <td>WARNING</td>\n"
        + "      <td>Declaration has Javadoc problems</td>\n"
        + "      <td>\n"
        + "          <a href=\"url\">src/test/java/com/github/frimtec/teamcity/plugin/inspectionnotification/Builders.java:29</a>\n"
        + "      </td>\n"
        + "    </tr>\n"
        + "  </table>\n"
        + "\n"
        + "  <h4>Possible reasons for the new inspection violations:</h4>\n"
        + "    <ul>\n"
        + "      <li>Changes in the projects inspection profile.</li>\n"
        + "      <li>New IDEA version with improved or new inspections.</li>\n"
        + "      <li>...</li>\n"
        + "    </ul>\n"
        + "</html>\n");
  }

  @Test
  void generateMailForViolationsWithNoBitbucketUrlGenerator() throws IOException {
    NotificationMailGenerator generator = new NotificationMailGenerator();
    SRunningBuild build = Builders.runningBuild()
        .fullName("Build Test")
        .buidId(43)
        .buidNumber("12")
        .build();
    NotificationMessage notificationMessage = Builders.notificationMessage()
        .build(build)
        .teamCityRootUrl("http://localhost/tc")
        .addNewViolations(
            inspectionViolation()
                .level(ERROR)
                .inspectionName("Declaration has problems in Javadoc references")
                .fileName("src/test/java/com/github/frimtec/teamcity/plugin/inspectionnotification/Builders.java")
                .line(30)
                .build(),
            inspectionViolation()
                .level(WARNING)
                .inspectionName("Declaration has Javadoc problems")
                .fileName("src/test/java/com/github/frimtec/teamcity/plugin/inspectionnotification/Builders.java")
                .line(29)
                .build()
        ).build();
    String message = generator.generate(notificationMessage, ResourceHelper.loadDefaultEmailTemplate());
    assertThat(message).isEqualTo("<html>\n"
        + "  <h4>The following new inspection violations has been reported in\n"
        + "    <a href='http://localhost/tc/viewLog.html?buildId=43'>Build Test #12:</a>\n"
        + "  </h4>\n"
        + "  <table border=\"1\">\n"
        + "    <tr style=\"font-weight: bold\">\n"
        + "      <td>Level</td>\n"
        + "      <td>Inspection</td>\n"
        + "      <td>Location</td>\n"
        + "    </tr>\n"
        + "    <tr>\n"
        + "      <td>ERROR</td>\n"
        + "      <td>Declaration has problems in Javadoc references</td>\n"
        + "      <td>\n"
        + "          src/test/java/com/github/frimtec/teamcity/plugin/inspectionnotification/Builders.java:30\n"
        + "      </td>\n"
        + "    </tr>\n"
        + "    <tr>\n"
        + "      <td>WARNING</td>\n"
        + "      <td>Declaration has Javadoc problems</td>\n"
        + "      <td>\n"
        + "          src/test/java/com/github/frimtec/teamcity/plugin/inspectionnotification/Builders.java:29\n"
        + "      </td>\n"
        + "    </tr>\n"
        + "  </table>\n"
        + "\n"
        + "  <h4>Possible reasons for the new inspection violations:</h4>\n"
        + "    <ul>\n"
        + "      <li>Changes in the projects inspection profile.</li>\n"
        + "      <li>New IDEA version with improved or new inspections.</li>\n"
        + "      <li>...</li>\n"
        + "    </ul>\n"
        + "</html>\n");
  }

  @Test
  void generateMailWithUninitializedNotificationMessage() {
    NotificationMailGenerator generator = new NotificationMailGenerator();
    NotificationMessage notificationMessage = Builders.notificationMessage().build();
    RuntimeException exception = assertThrows(RuntimeException.class, () -> generator.generate(notificationMessage, ResourceHelper.loadDefaultEmailTemplate()));
    assertThat(exception.getCause()).isInstanceOf(TemplateException.class);
  }

}