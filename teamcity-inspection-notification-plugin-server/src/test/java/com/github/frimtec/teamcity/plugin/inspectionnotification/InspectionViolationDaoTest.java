package com.github.frimtec.teamcity.plugin.inspectionnotification;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.Builders.inspectionViolation;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionViolation.Level.ERROR;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionViolation.Level.WARNING;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.TestDbHelper.BUILD_WITH_NEW_VIOLATIONS_AND_COMMITTER;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.TestDbHelper.BUILD_WITH_NO_NEW_VIOLATIONS;
import static com.github.frimtec.teamcity.plugin.inspectionnotification.TestDbHelper.createConnection;
import static org.assertj.core.api.Assertions.assertThat;

class InspectionViolationDaoTest {

  @Test
  void findNewInspectionViolationsBuildWithNewViolations() throws SQLException {
    InspectionViolationDao dao = new InspectionViolationDao();

    List<InspectionViolation> expectedViolations = Arrays.asList(
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

    try (Connection connection = createConnection()) {
      List<InspectionViolation> newViolations = dao.findNewInspectionViolations(connection, BUILD_WITH_NEW_VIOLATIONS_AND_COMMITTER);
      assertThat(newViolations).containsAll(expectedViolations);
    }
  }

  @Test
  void findNewInspectionViolationsBuildWithNoNewViolations() throws SQLException {
    InspectionViolationDao dao = new InspectionViolationDao();
    try (Connection connection = createConnection()) {
      List<InspectionViolation> newViolations = dao.findNewInspectionViolations(connection, BUILD_WITH_NO_NEW_VIOLATIONS);
      assertThat(newViolations).isEmpty();
    }
  }


}
