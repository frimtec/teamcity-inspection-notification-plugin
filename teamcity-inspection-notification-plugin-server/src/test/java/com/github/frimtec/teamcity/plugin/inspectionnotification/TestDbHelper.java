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
