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
import java.util.Collections;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InspectionNotificationBuildListenerTest {

  public static final Connection CONNECTION = mock(Connection.class);

  @Test
  void buildFinishedForBuildWithNoNewViolations() throws SQLException {
    InspectionViolationDao dao = mock(InspectionViolationDao.class);
    int buildId = 1;
    when(dao.findNewInspectionViolations(CONNECTION, buildId)).thenReturn(Collections.emptyList());
    TestMailReceiver mailReceiver = new TestMailReceiver();
    InspectionNotificationBuildListener listener = new InspectionNotificationBuildListener(
        server(),
        new InspectionNotificationConfiguration(),
        dao,
        mailReceiver
    );
    SRunningBuild build = runningBuild()
        .buidId(buildId)
        .build();
    listener.buildFinished(build);
    assertThat(mailReceiver.mailReceived).isFalse();
  }

  @Test
  void buildFinishedForBuildWithNewViolationsAndCommitters() throws SQLException {
    List<InspectionViolation> expectedViolations = Arrays.asList(inspectionViolation()
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

    InspectionViolationDao dao = mock(InspectionViolationDao.class);
    int buildId = 1;
    when(dao.findNewInspectionViolations(CONNECTION, buildId)).thenReturn(expectedViolations);
    TestMailReceiver mailReceiver = new TestMailReceiver();
    InspectionNotificationConfiguration configuration = new InspectionNotificationConfiguration();
    configuration.setEmailSubject("subject");
    configuration.setBitbucketRootUrl("http://localhost/bitbucket");
    InspectionNotificationBuildListener listener = new InspectionNotificationBuildListener(
        server(),
        configuration,
        dao,
        mailReceiver
    );
    SRunningBuild build = runningBuild()
        .buidId(buildId)
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
  void buildFinishedForBuildWithNewViolationsNoCommitters() throws SQLException {
    List<InspectionViolation> expectedViolations = Arrays.asList(inspectionViolation()
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

    InspectionViolationDao dao = mock(InspectionViolationDao.class);
    int buildId = 1;
    when(dao.findNewInspectionViolations(CONNECTION, buildId)).thenReturn(expectedViolations);
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
        dao,
        mailReceiver
    );
    SRunningBuild build = runningBuild()
        .buidId(buildId)
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

  @Test
  void buildFinishedForBuildWithSqlException() throws SQLException {
    InspectionViolationDao dao = mock(InspectionViolationDao.class);
    int buildId = 1;
    when(dao.findNewInspectionViolations(CONNECTION, buildId)).thenThrow(new SQLException("test"));
    TestMailReceiver mailReceiver = new TestMailReceiver();
    InspectionNotificationBuildListener listener = new InspectionNotificationBuildListener(
        server(),
        new InspectionNotificationConfiguration(),
        dao,
        mailReceiver
    );
    SRunningBuild build = runningBuild()
        .buidId(buildId)
        .build();
    RuntimeException exception = assertThrows(RuntimeException.class, () -> listener.buildFinished(build));
    assertThat(exception.getCause().getMessage()).isEqualTo("test");
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
          return sqlAction.run(CONNECTION);
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
      }

      @Override
      public void runSql(@NotNull NoResultSQLAction noResultSQLAction) {
        throw new IllegalStateException("not supported in mock");
      }

      @Override
      public void runSql(@NotNull NoResultSQLAction noResultSQLAction, boolean b) {
        throw new IllegalStateException("not supported in mock");
      }

      @Override
      public long getNextId(@NotNull String s, String s1) {
        throw new IllegalStateException("not supported in mock");
      }

      @Override
      public void commitCurrentTransaction() {
        throw new IllegalStateException("not supported in mock");
      }
    });
    return server;
  }
}
