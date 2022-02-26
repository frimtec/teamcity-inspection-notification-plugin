package com.github.frimtec.teamcity.plugin.inspectionnotification;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import static java.lang.String.format;

public class InspectionViolationDao {
  private static final String SELECT_NEW_INSPECTION_VIOLATION_STATEMENT =
      "select distinct IDA.SEVERITY as SEVERITY,"
          + "      IIN.INSPECTION_NAME as INSPECTION_NAME,"
          + "      IDA.FILE_NAME as FILE_NAME,"
          + "      IRE.LINE as LINE"
          + " from INSPECTION_DIFF IDI "
          + "        join INSPECTION_DATA IDA on (IDA.HASH = IDI.HASH) "
          + "        join INSPECTION_INFO IIN on (IIN.ID = IDA.INSPECTION_ID) "
          + "        join INSPECTION_RESULTS IRE on (IRE.HASH = IDI.HASH) "
          + "where IDI.BUILD_ID = ";

  public List<InspectionViolation> findNewInspectionViolations(Connection connection, long buildId) throws SQLException {
    List<InspectionViolation> newViolations = new ArrayList<>();
    try (Statement statement = connection.createStatement()) {
      statement.execute(SELECT_NEW_INSPECTION_VIOLATION_STATEMENT + buildId);
      try (ResultSet resultSet = statement.getResultSet()) {
        int rowNumber = 0;
        while (resultSet.next()) {
          newViolations.add(inspectionViolation(resultSet, rowNumber++));
        }
        return newViolations;
      }
    }
  }

  private static InspectionViolation inspectionViolation(ResultSet rs, int i) {
    try {
      return new InspectionViolation(
          InspectionViolation.Level.fromSeverity(rs.getInt("SEVERITY")),
          rs.getString("INSPECTION_NAME"),
          rs.getString("FILE_NAME"),
          rs.getInt("LINE"));
    } catch (SQLException e) {
      return new InspectionViolation(InspectionViolation.Level.UNPARSABLE, format("Can not parse violation with index %d from DB", i), "no-file", 0);
    }
  }
}
