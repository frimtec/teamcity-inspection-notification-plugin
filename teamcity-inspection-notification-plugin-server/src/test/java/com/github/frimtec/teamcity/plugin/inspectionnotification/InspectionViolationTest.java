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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.Builders.inspectionViolation;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionViolation.Level.ERROR;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionViolation.Level.WARNING;
import static org.assertj.core.api.Assertions.assertThat;

class InspectionViolationTest {

  @Test
  void getLevel() {
    InspectionViolation violation = inspectionViolation().level(ERROR).build();
    assertThat(violation.getLevel()).isEqualTo(ERROR);
  }

  @Test
  void getInspectionName() {
    InspectionViolation violation = inspectionViolation().inspectionName("inspectionName").build();
    assertThat(violation.getInspectionName()).isEqualTo("inspectionName");
  }

  @Test
  void getFileName() {
    InspectionViolation violation = inspectionViolation().fileName("fileName").build();
    assertThat(violation.getFileName()).isEqualTo("fileName");
  }

  @Test
  void getLine() {
    InspectionViolation violation = inspectionViolation().line(100).build();
    assertThat(violation.getLine()).isEqualTo(100);
  }

  @Test
  void isEqual() {
    InspectionViolation violation1 = inspectionViolation().line(100).build();
    InspectionViolation violation2 = inspectionViolation().line(100).build();
    assertThat(violation1).isEqualTo(violation2);
  }

  @Test
  void isNotEqual() {
    InspectionViolation violation1 = inspectionViolation().line(100).build();
    InspectionViolation violation2 = inspectionViolation().line(101).build();
    assertThat(violation1).isNotEqualTo(violation2);
  }

  @Test
  void isNotEqualWithNull() {
    InspectionViolation violation1 = inspectionViolation().line(100).build();
    InspectionViolation violation2 = null;
    assertThat(violation1).isNotEqualTo(violation2);
  }

  @Test
  void hashCodeMethod() {
    InspectionViolation violation1 = inspectionViolation().line(100).build();
    InspectionViolation violation2 = inspectionViolation().line(100).build();
    assertThat(violation1.hashCode()).isEqualTo(violation2.hashCode());
  }

  @Test
  void toStringMethod() {
    InspectionViolation violation = inspectionViolation().line(100).build();
    assertThat(violation.toString()).isNotBlank();
  }

  @Test
  void levelFromSeverityWarning() {
    assertThat(InspectionViolation.Level.fromSeverity(0)).isEqualTo(ERROR);
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 100})
  void levelFromSeverityError(int value) {
    assertThat(InspectionViolation.Level.fromSeverity(value)).isEqualTo(WARNING);
  }
}