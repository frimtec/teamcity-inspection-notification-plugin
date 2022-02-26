package com.github.frimtec.teamcity.plugin.inspectionnotification;

import org.junit.jupiter.api.Test;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.users.SUser;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.Builders.inspectionViolation;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.Builders.notificationMessage;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.Builders.runningBuild;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.Builders.user;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionViolation.Level.ERROR;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionViolation.Level.WARNING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class NotificationMessageTest {

  @Test
  void getSubjectWithChanges() {
    NotificationMessage notificationMessage = notificationMessage()
        .subject("subject")
        .addCommitters(Builders.user().build())
        .build();
    String subject = notificationMessage.getSubject();
    assertThat(subject).isEqualTo("subject");
  }

  @Test
  void getSubjectWithNoChanges() {
    NotificationMessage notificationMessage = notificationMessage().subjectNoChanges("subjectNoChange").build();
    String subject = notificationMessage.getSubject();
    assertThat(subject).isEqualTo("subjectNoChange");
  }

  @Test
  void getTeamCityRootUrl() {
    NotificationMessage notificationMessage = notificationMessage().teamCityRootUrl("rootUrl").build();
    String teamCityRootUrl = notificationMessage.getTeamCityRootUrl();
    assertThat(teamCityRootUrl).isEqualTo("rootUrl");
  }

  @Test
  void getBuild() {
    SRunningBuild build = runningBuild().build();
    NotificationMessage notificationMessage = notificationMessage().build(build).build();
    assertThat(notificationMessage.getBuild()).isSameAs(build);
  }

  @Test
  void useBitbucketWithBitbucketUrlGenerator() {
    NotificationMessage notificationMessage = notificationMessage().bitbucketUrlGenerator((sRunningBuild, inspectionViolation) -> "url").build();
    assertThat(notificationMessage.useBitbucket()).isTrue();
  }

  @Test
  void useBitbucketWithoutBitbucketUrlGenerator() {
    NotificationMessage notificationMessage = notificationMessage().build();
    assertThat(notificationMessage.useBitbucket()).isFalse();
  }

  @Test
  void generateBitbucketUrl() {
    SRunningBuild build = mock(SRunningBuild.class);
    InspectionViolation violation = Builders.inspectionViolation().build();
    NotificationMessage notificationMessage = notificationMessage()
        .build(build)
        .bitbucketUrlGenerator((SRunningBuild theBuild, InspectionViolation theViolation) -> {
          assertThat(theBuild).isSameAs(build);
          assertThat(theViolation).isSameAs(violation);
          return "url";
        })
        .build();
    String url = notificationMessage.generateBitbucketUrl(violation);
    assertThat(url).isEqualTo("url");
  }

  @Test
  void getNewViolations() {
    InspectionViolation violation1 = inspectionViolation().level(WARNING).inspectionName("b").build();
    InspectionViolation violation2 = inspectionViolation().level(WARNING).inspectionName("a").fileName("f").line(100).build();
    InspectionViolation violation3 = inspectionViolation().level(WARNING).inspectionName("a").fileName("f").line(99).build();
    InspectionViolation violation4 = inspectionViolation().level(ERROR).inspectionName("c").fileName("2").build();
    InspectionViolation violation5 = inspectionViolation().level(ERROR).inspectionName("c").fileName("1").build();
    NotificationMessage notificationMessage = notificationMessage().addNewViolations(violation1, violation2, violation3, violation4, violation5).build();
    assertThat(notificationMessage.getNewViolations()).containsExactly(violation5, violation4, violation3, violation2, violation1);
  }

  @Test
  void getCommitters() {
    SUser user1 = user().build();
    SUser user2 = user().build();
    NotificationMessage notificationMessage = notificationMessage().addCommitters(user1, user2).build();
    assertThat(notificationMessage.getCommitters()).containsExactlyInAnyOrder(user1, user2);
  }
}
