package com.github.frimtec.teamcity.plugin.inspectionnotification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import jetbrains.buildServer.groups.UserGroup;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.users.UserSet;
import jetbrains.buildServer.vcs.SVcsModification;
import static jetbrains.buildServer.vcs.SelectPrevBuildPolicy.SINCE_LAST_FINISHED_BUILD;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class Builders {

  private Builders() {
  }

  static final class NotificationMessageBuilder {
    private String teamCityRootUrl;
    private SRunningBuild build;
    private final List<InspectionViolation> newViolations = new ArrayList<>();
    private final Set<SUser> committers = new LinkedHashSet<>();
    private BiFunction<SRunningBuild, InspectionViolation, String> bitbucketUrlGenerator;
    private String subject;
    private String subjectNoChanges;

    NotificationMessage build() {
      return new NotificationMessage(
          this.teamCityRootUrl,
          this.build,
          this.newViolations,
          this.committers,
          this.bitbucketUrlGenerator,
          this.subject,
          this.subjectNoChanges);
    }

    public NotificationMessageBuilder teamCityRootUrl(String teamCityRootUrl) {
      this.teamCityRootUrl = teamCityRootUrl;
      return this;
    }

    public NotificationMessageBuilder build(SRunningBuild build) {
      this.build = build;
      return this;
    }

    public NotificationMessageBuilder addNewViolations(InspectionViolation... newViolations) {
      this.newViolations.addAll(Arrays.asList(newViolations));
      return this;
    }

    public NotificationMessageBuilder addCommitters(SUser... committers) {
      this.committers.addAll(Arrays.asList(committers));
      return this;
    }

    public NotificationMessageBuilder bitbucketUrlGenerator(BiFunction<SRunningBuild, InspectionViolation, String> bitbucketUrlGenerator) {
      this.bitbucketUrlGenerator = bitbucketUrlGenerator;
      return this;
    }

    public NotificationMessageBuilder subject(String subject) {
      this.subject = subject;
      return this;
    }

    public NotificationMessageBuilder subjectNoChanges(String subjectNoChanges) {
      this.subjectNoChanges = subjectNoChanges;
      return this;
    }
  }

  static final class InspectionViolationBuilder {
    private InspectionViolation.Level level;
    private String inspectionName;
    private String fileName;
    private int line;

    InspectionViolation build() {
      return new InspectionViolation(this.level, this.inspectionName, this.fileName, this.line);
    }

    public InspectionViolationBuilder level(InspectionViolation.Level level) {
      this.level = level;
      return this;
    }

    public InspectionViolationBuilder inspectionName(String inspectionName) {
      this.inspectionName = inspectionName;
      return this;
    }

    public InspectionViolationBuilder fileName(String fileName) {
      this.fileName = fileName;
      return this;
    }

    public InspectionViolationBuilder line(int line) {
      this.line = line;
      return this;
    }
  }

  static final class UserBuilder {
    private String email;
    private String name;
    private final List<UserGroup> userGroups = new ArrayList<>();

    SUser build() {
      SUser mock = mock(SUser.class);
      when(mock.getName()).thenReturn(this.name);
      when(mock.getEmail()).thenReturn(this.email);
      when(mock.getUserGroups()).thenReturn(this.userGroups);
      return mock;
    }

    public UserBuilder email(String email) {
      this.email = email;
      return this;
    }

    public UserBuilder name(String name) {
      this.name = name;
      return this;
    }

    public UserBuilder addUserGroup(String userGroupName) {
      UserGroup group = mock(UserGroup.class);
      when(group.getName()).thenReturn(userGroupName);
      this.userGroups.add(group);
      return this;
    }
  }

  static final class RunningBuildBuilder {
    private String fullName;
    private long buildId;
    private String projectId = "projectId";
    private String buidNumber;
    private final Set<SUser> committers = new LinkedHashSet<>();
    private boolean hasChanges = false;

    SRunningBuild build() {
      SRunningBuild mock = mock(SRunningBuild.class);
      when(mock.getFullName()).thenReturn(this.fullName);
      when(mock.getBuildId()).thenReturn(this.buildId);
      when(mock.getProjectId()).thenReturn(this.projectId);
      when(mock.getBuildNumber()).thenReturn(this.buidNumber);
      //noinspection unchecked (justification: no runtime type available)
      UserSet<SUser> userSet = mock(UserSet.class);
      when(userSet.getUsers()).thenReturn(this.committers);
      when(mock.getCommitters(SINCE_LAST_FINISHED_BUILD)).thenReturn(userSet);
      if (this.hasChanges) {
        when(mock.getChanges(SINCE_LAST_FINISHED_BUILD, true)).thenReturn(Collections.singletonList(mock(SVcsModification.class)));
      } else {
        when(mock.getChanges(SINCE_LAST_FINISHED_BUILD, true)).thenReturn(Collections.emptyList());
      }
      return mock;
    }

    public RunningBuildBuilder fullName(String fullName) {
      this.fullName = fullName;
      return this;
    }

    public RunningBuildBuilder buidNumber(String buildNumber) {
      this.buidNumber = buildNumber;
      return this;
    }

    public RunningBuildBuilder buidId(long buildId) {
      this.buildId = buildId;
      return this;
    }

    public RunningBuildBuilder projectId(String projectId) {
      this.projectId = projectId;
      return this;
    }

    public RunningBuildBuilder addCommitters(SUser... committers) {
      this.committers.addAll(Arrays.asList(committers));
      return this;
    }

    public RunningBuildBuilder hasChanges(boolean hasChanges) {
      this.hasChanges = hasChanges;
      return this;
    }
  }

  public static NotificationMessageBuilder notificationMessage() {
    return new NotificationMessageBuilder();
  }

  public static InspectionViolationBuilder inspectionViolation() {
    return new InspectionViolationBuilder();
  }

  public static UserBuilder user() {
    return new UserBuilder();
  }

  public static RunningBuildBuilder runningBuild() {
    return new RunningBuildBuilder();
  }

}
