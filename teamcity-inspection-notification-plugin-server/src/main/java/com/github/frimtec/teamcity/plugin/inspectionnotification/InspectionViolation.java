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

import java.util.Objects;

public final class InspectionViolation {
  enum Level {
    ERROR,
    WARNING,
    UNPARSABLE;

    static Level fromSeverity(int severity) {
      return severity == 0 ? ERROR : WARNING;
    }
  }

  private final Level level;
  private final String inspectionName;
  private final String fileName;
  private final int line;

  public InspectionViolation(Level level, String inspectionName, String fileName, int line) {
    this.level = level;
    this.inspectionName = inspectionName;
    this.fileName = fileName;
    this.line = line;
  }

  public Level getLevel() {
    return this.level;
  }

  public String getInspectionName() {
    return this.inspectionName;
  }

  public String getFileName() {
    return this.fileName;
  }

  public int getLine() {
    return this.line;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || !getClass().equals(o.getClass())) {
      return false;
    }
    InspectionViolation violation = (InspectionViolation) o;
    return this.line == violation.line &&
        this.level == violation.level &&
        Objects.equals(this.inspectionName, violation.inspectionName) &&
        Objects.equals(this.fileName, violation.fileName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.level, this.inspectionName, this.fileName, this.line);
  }

  @Override
  public String toString() {
    return "InspectionViolation{" +
        "level=" + this.level +
        ", inspectionName='" + this.inspectionName + '\'' +
        ", fileName='" + this.fileName + '\'' +
        ", line=" + this.line +
        '}';
  }
}
