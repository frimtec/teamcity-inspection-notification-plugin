package com.github.frimtec.teamcity.plugin.inspectionnotification;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;

final class TestDbHelper {

  public static final int BUILD_WITH_NEW_VIOLATIONS_AND_COMMITTER = 6;
  public static final int BUILD_WITH_NO_NEW_VIOLATIONS = 5;


  private TestDbHelper() {
  }

  public static Connection createConnection() {
    try {
      Class.forName("org.hsqldb.jdbc.JDBCDriver");
      String dbFilePath = Paths.get("src").resolve("test").resolve("resources").resolve("test-db").resolve("buildserver").toAbsolutePath().toString();
      Connection connection = DriverManager.getConnection("jdbc:hsqldb:file://" + dbFilePath, "admin", "admin");
      if (connection == null) {
        throw new RuntimeException("Can not create DB connection, connection is null");
      }
      return connection;
    } catch (Exception e) {
      throw new RuntimeException("Can not create DB connection", e);
    }
  }
}
