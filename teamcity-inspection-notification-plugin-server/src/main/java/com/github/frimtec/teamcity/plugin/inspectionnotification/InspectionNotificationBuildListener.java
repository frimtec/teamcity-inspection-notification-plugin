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

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import jetbrains.buildServer.serverSide.BuildServerAdapter;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.users.User;
import jetbrains.buildServer.util.StringUtil;
import jetbrains.buildServer.vcs.VcsRootInstanceEntry;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;
import static jetbrains.buildServer.log.Loggers.ACTIVITIES;
import static jetbrains.buildServer.vcs.SelectPrevBuildPolicy.SINCE_LAST_FINISHED_BUILD;

public final class InspectionNotificationBuildListener extends BuildServerAdapter {
  private final SBuildServer server;
  private final InspectionNotificationConfiguration pluginConfiguration;
  private final InspectionViolationDao inspectionViolationDao;
  private final EmailSender emailSender;

  public InspectionNotificationBuildListener(
      @NotNull SBuildServer server,
      InspectionNotificationConfiguration pluginConfiguration,
      InspectionViolationDao inspectionViolationDao,
      EmailSender emailSender) {
    server.addListener(this);
    this.server = server;
    this.pluginConfiguration = pluginConfiguration;
    this.inspectionViolationDao = inspectionViolationDao;
    this.emailSender = emailSender;
  }

  @Override
  public void buildFinished(@NotNull SRunningBuild build) {
    super.buildFinished(build);
    if (isDisabledForProject(build)) {
      return;
    }
    List<InspectionViolation> newViolations = loadNewViolations(build);
    if (newViolations.isEmpty()) {
      info("No new violations in build", build);
      return;
    }

    if (hasChanges(build)) {
      notifyNewViolations(build, newViolations, getCommitters(build));
    } else {
      notifyNewViolations(build, newViolations, emptySet());
    }
  }

  private boolean isDisabledForProject(@NotNull SRunningBuild build) {
    return this.pluginConfiguration.getDisabledProjectIds().contains(build.getProjectId());
  }

  private List<InspectionViolation> loadNewViolations(@NotNull SRunningBuild build) {
    //noinspection deprecation (justification: no API for inspection violations are available)
    return this.server.getSQLRunner().runSql(connection -> {
          return this.inspectionViolationDao.findNewInspectionViolations(connection, build.getBuildId());
        }
    );
  }

  private Set<SUser> getAdministrators() {
    return this.server.getUserModel().getAllUsers().getUsers().stream()
        .filter(user -> user.getUserGroups()
            .stream()
            .anyMatch(group -> this.pluginConfiguration.getInspectionAdminGroupName().equals(group.getName())))
        .collect(toSet());
  }

  private void notifyNewViolations(SRunningBuild build, List<InspectionViolation> newViolations, Set<SUser> committers) {
    NotificationMessage message = createNotificationMessage(build, newViolations, committers);
    Set<SUser> receivers = committers;
    if (committers.isEmpty()) {
      receivers = getAdministrators();
    }
    String[] toAddresses = receivers.stream()
        .map(User::getEmail)
        .toArray(String[]::new);
    this.emailSender.sendNotification(message, toAddresses);
    info("New violations mail sent to: " + String.join(";", Arrays.asList(toAddresses)), build);
  }

  private NotificationMessage createNotificationMessage(SRunningBuild build, List<InspectionViolation> newViolations, Set<SUser> committers) {
    return new NotificationMessage(
        this.server.getRootUrl(),
        build,
        newViolations,
        committers,
        StringUtil.isEmpty(this.pluginConfiguration.getBitbucketRootUrl()) ? null : this::generateBitbucketUrl,
        this.pluginConfiguration.getEmailSubject(),
        this.pluginConfiguration.getEmailSubjectNoChanges()
    );
  }

  private String generateBitbucketUrl(SRunningBuild build, InspectionViolation violation) {
    String project = "projects/UNKNOWN";
    String repo = "unknown";
    String gitUrl = build.getVcsRootEntries().stream()
        .map(VcsRootInstanceEntry::getVcsRoot)
        .map(vcs -> vcs.getProperty("url", "UNKNOWN-VCS-REPO"))
        .map(url -> url.replace(".git", ""))
        .map(url -> url.replace("ssh://", ""))
        .findFirst().orElse("NO-VCS-ROOT");

    String[] urlParts = gitUrl.split("/");
    if (urlParts.length == 3) {
      project = urlParts[1].startsWith("~") ? "users/" + urlParts[1].replaceFirst("~", "") : "projects/" + urlParts[1].toUpperCase();
      repo = urlParts[2];
    }
    return String.format("%s/%s/repos/%s/browse/%s#%d", this.pluginConfiguration.getBitbucketRootUrl(), project, repo, violation.getFileName(), violation.getLine());
  }

  private static Set<SUser> getCommitters(SRunningBuild build) {
    return build.getCommitters(SINCE_LAST_FINISHED_BUILD).getUsers();
  }

  private static boolean hasChanges(SRunningBuild build) {
    return !build.getChanges(SINCE_LAST_FINISHED_BUILD, true).isEmpty();
  }

  private static void info(String message, SRunningBuild build) {
    ACTIVITIES.info(String.format("InspectionNotificationPlugin: Build: %s #%s; %s.", build.getFullName(), build.getBuildNumber(), message));
  }
}
