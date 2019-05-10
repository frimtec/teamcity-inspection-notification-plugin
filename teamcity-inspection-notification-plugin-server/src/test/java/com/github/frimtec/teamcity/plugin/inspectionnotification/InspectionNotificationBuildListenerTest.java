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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SQLRunner;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.users.UserModel;
import jetbrains.buildServer.users.UserSet;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.Builders.inspectionViolation;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.Builders.runningBuild;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.Builders.user;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionViolation.Level.ERROR;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionViolation.Level.WARNING;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.TestDbHelper.BUILD_WITH_NEW_VIOLATIONS_AND_COMMITTER;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.TestDbHelper.BUILD_WITH_NO_NEW_VIOLATIONS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InspectionNotificationBuildListenerTest {

  @Test
  void buildFinishedForBuildWithNoNewViolations() {
    TestMailReceiver mailReceiver = new TestMailReceiver();
    InspectionNotificationBuildListener listener = new InspectionNotificationBuildListener(
        server(),
        new InspectionNotificationConfiguration(),
        mailReceiver
    );
    SRunningBuild build = runningBuild()
        .buidId(BUILD_WITH_NO_NEW_VIOLATIONS)
        .build();
    listener.buildFinished(build);
    assertThat(mailReceiver.mailReceived).isFalse();
  }

  @Test
  void buildFinishedForBuildWithNewViolationsAndCommitters() {
    TestMailReceiver mailReceiver = new TestMailReceiver();
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    configuration.setEmailSubject("subject");
    configuration.setBitbucketRootUrl("http://localhost/bitbucket");
    InspectionNotificationBuildListener listener = new InspectionNotificationBuildListener(
        server(),
        configuration,
        mailReceiver
    );
    SRunningBuild build = runningBuild()
        .buidId(BUILD_WITH_NEW_VIOLATIONS_AND_COMMITTER)
        .addCommitters(
            user()
                .email("tester.one@tc.com")
                .build(),
            user()
                .email("tester.two@tc.com")
                .build())
        .hasChanges(true)
        .build();
    listener.buildFinished(build);
    assertThat(mailReceiver.mailReceived).isTrue();
    assertThat(mailReceiver.toAddresses).containsExactly("tester.one@tc.com", "tester.two@tc.com");
    assertThat(mailReceiver.message.getSubject()).isEqualTo("subject");
    assertThat(mailReceiver.message.getCommitters()).hasSize(2);
    assertThat(mailReceiver.message.getNewViolations()).containsExactly(
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
    );
    assertThat(mailReceiver.message.generateBitbucketUrl(inspectionViolation()
        .level(ERROR)
        .inspectionName("Declaration has problems in Javadoc references")
        .fileName("src/test/java/com/github/frimtec/teamcity/plugin/inspectionnotification/Builders.java")
        .line(30)
        .build())).isEqualTo("http://localhost/bitbucket/projects/UNKNOWN/repos/unknown/browse/src/test/java/com/github/frimtec/teamcity/plugin/inspectionnotification/Builders.java#30");
  }

  @Test
  void buildFinishedForBuildWithNewViolationsNoCommitters() {
    TestMailReceiver mailReceiver = new TestMailReceiver();
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    configuration.setEmailSubjectNoChanges("subjectNoChanges");
    configuration.setInspectionAdminGroupName("admin");
    SBuildServer server = server();
    UserModel userModel = mock(UserModel.class);
    //noinspection unchecked (justification: no runtime type available)
    UserSet<SUser> userSet = mock(UserSet.class);
    List<SUser> allUsers = Arrays.asList(user().email("tester@tc.com").addUserGroup("tester").build(), user().email("admin@tc.com").addUserGroup("admin").build());
    when(userSet.getUsers()).thenReturn(new LinkedHashSet<>(allUsers));
    when(userModel.getAllUsers()).thenReturn(userSet);
    when(server.getUserModel()).thenReturn(userModel);
    InspectionNotificationBuildListener listener = new InspectionNotificationBuildListener(
        server,
        configuration,
        mailReceiver
    );
    SRunningBuild build = runningBuild()
        .buidId(BUILD_WITH_NEW_VIOLATIONS_AND_COMMITTER)
        .hasChanges(true)
        .build();
    listener.buildFinished(build);
    assertThat(mailReceiver.mailReceived).isTrue();
    assertThat(mailReceiver.toAddresses).containsExactly("admin@tc.com");
    assertThat(mailReceiver.message.getSubject()).isEqualTo("subjectNoChanges");
    assertThat(mailReceiver.message.getCommitters()).isEmpty();
    assertThat(mailReceiver.message.getNewViolations()).containsExactly(
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
    );
  }

  private static final class TestMailReceiver implements EmailSender {

    private boolean mailReceived = false;
    private NotificationMessage message = null;
    private List<String> toAddresses = null;

    @Override
    public void sendNotification(NotificationMessage message, String[] toAddresses) {
      this.mailReceived = true;
      this.message = message;
      this.toAddresses = Arrays.asList(toAddresses);
    }
  }

  private static SBuildServer server() {
    SBuildServer server = mock(SBuildServer.class);
    //noinspection deprecation (justification: no API for inspection violations are available)
    when(server.getSQLRunner()).thenReturn(new SQLRunner() {
      @Override
      public <T> T runSql(@NotNull SQLAction<T> sqlAction) {
        try {
          try (Connection connection = TestDbHelper.createConnection()) {
            return sqlAction.run(connection);
          }
        } catch (SQLException e) {
          throw new IllegalStateException("Unexpected exception", e);
        }
      }

      @Override
      public void runSql(@NotNull NoResultSQLAction noResultSQLAction) {

      }

      @Override
      public void runSql(@NotNull NoResultSQLAction noResultSQLAction, boolean b) {

      }

      @Override
      public long getNextId(@NotNull String s, String s1) {
        return 0;
      }

      @Override
      public void commitCurrentTransaction() {

      }
    });
    return server;
  }
}