package com.github.frimtec.teamcity.plugin.inspectionnotification;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.users.SUser;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public final class NotificationMessage {
  private final String teamCityRootUrl;
  private final SRunningBuild build;
  private final List<InspectionViolation> newViolations;
  private final Set<SUser> committers;
  private final BiFunction<SRunningBuild, InspectionViolation, String> bitbucketUrlGenerator;
  private final String subject;
  private final String subjectNoChanges;

  public NotificationMessage(
      String teamCityRootUrl,
      SRunningBuild build,
      List<InspectionViolation> newViolations,
      Set<SUser> committers,
      BiFunction<SRunningBuild, InspectionViolation, String> bitbucketUrlGenerator,
      String subject,
      String subjectNoChanges) {
    this.teamCityRootUrl = teamCityRootUrl;
    this.build = build;
    this.newViolations = newViolations.stream()
        .sorted(comparing(
            InspectionViolation::getLevel)
            .thenComparing(InspectionViolation::getInspectionName)
            .thenComparing(InspectionViolation::getFileName)
            .thenComparing(InspectionViolation::getLine))
        .collect(toList());
    this.committers = new LinkedHashSet<>(committers);
    this.bitbucketUrlGenerator = bitbucketUrlGenerator;
    this.subject = subject;
    this.subjectNoChanges = subjectNoChanges;
  }

  public String getSubject() {
    return this.committers.isEmpty() ? this.subjectNoChanges : this.subject;
  }

  public String getTeamCityRootUrl() {
    return this.teamCityRootUrl;
  }

  public SRunningBuild getBuild() {
    return this.build;
  }

  public boolean useBitbucket() {
    return this.bitbucketUrlGenerator != null;
  }

  public String generateBitbucketUrl(InspectionViolation violation) {
    return useBitbucket() ? this.bitbucketUrlGenerator.apply(this.build, violation) : "";
  }

  public List<InspectionViolation> getNewViolations() {
    return unmodifiableList(this.newViolations);
  }

  public Set<SUser> getCommitters() {
    return unmodifiableSet(this.committers);
  }
}
