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

package ch.frimtec.teamcity.plugin.inspectionnotification;

import java.util.HashSet;
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

  public NotificationMessage(
      String teamCityRootUrl,
      SRunningBuild build,
      List<InspectionViolation> newViolations,
      Set<SUser> committers,
      BiFunction<SRunningBuild, InspectionViolation, String> bitbucketUrlGenerator) {
    this.teamCityRootUrl = teamCityRootUrl;
    this.build = build;
    this.newViolations = newViolations.stream()
        .sorted(comparing(
            InspectionViolation::getLevel)
            .thenComparing(InspectionViolation::getInspectionName)
            .thenComparing(InspectionViolation::getFileName)
            .thenComparing(InspectionViolation::getLine))
        .collect(toList());
    this.committers = new HashSet<>(committers);
    this.bitbucketUrlGenerator = bitbucketUrlGenerator;
  }

  public String getSubject() {
    return this.committers.isEmpty() ? "WARNING: New inspection violations without code change!" :
        "ACTION-REQUIRED: New inspection violations introduced!";
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
