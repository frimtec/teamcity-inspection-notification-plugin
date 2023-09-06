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
      "select distinct IDA.severity as SEVERITY,"
          + "      IIN.inspection_name as INSPECTION_NAME,"
          + "      IDA.file_name as FILE_NAME,"
          + "      IRE.line as LINE"
          + " from inspection_diff IDI"
          + "        join inspection_data IDA on (IDA.hash = IDI.hash) "
          + "        join inspection_info IIN on (IIN.id = IDA.inspection_id) "
          + "        join inspection_results IRE on (IRE.hash = IDI.hash) "
          + "where IDI.build_id = ";

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
