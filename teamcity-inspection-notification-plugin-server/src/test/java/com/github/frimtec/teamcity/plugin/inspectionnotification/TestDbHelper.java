package com.github.frimtec.teamcity.plugin.inspectionnotification;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

final class TestDbHelper {

  public static final int BUILD_WITH_NEW_VIOLATIONS_AND_COMMITTER = 6;
  public static final int BUILD_WITH_NO_NEW_VIOLATIONS = 5;


  private TestDbHelper() {
  }

  public static Connection createConnection() {
    try {
      Path testDbDirectoryPath = Paths.get("target").resolve("test-db");
      Path testDbFilePath = testDbDirectoryPath.resolve("buildserver").toAbsolutePath();
      if (!Files.exists(testDbFilePath)) {
        Path testDbArchive = Paths.get("src").resolve("test").resolve("resources").resolve("test-db.zip");
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(Files.newInputStream(testDbArchive.toFile().toPath()));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
          File newFile = newFile(testDbDirectoryPath.toFile(), zipEntry);
          if (zipEntry.isDirectory()) {
            if (!newFile.isDirectory() && !newFile.mkdirs()) {
              throw new IOException("Failed to create directory " + newFile);
            }
          } else {
            // fix for Windows-created archives
            File parent = newFile.getParentFile();
            if (!parent.isDirectory() && !parent.mkdirs()) {
              throw new IOException("Failed to create directory " + parent);
            }

            // write file content
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
              fos.write(buffer, 0, len);
            }
            fos.close();
          }
          zipEntry = zis.getNextEntry();        }
        zis.closeEntry();
        zis.close();
      }
      Class.forName("org.hsqldb.jdbc.JDBCDriver");
      Connection connection = DriverManager.getConnection("jdbc:hsqldb:file://" + testDbFilePath, "admin", "admin");
      if (connection == null) {
        throw new RuntimeException("Can not create DB connection, connection is null");
      }
      return connection;
    } catch (Exception e) {
      throw new RuntimeException("Can not create DB connection", e);
    }
  }

  private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
    File destFile = new File(destinationDir, zipEntry.getName());

    String destDirPath = destinationDir.getCanonicalPath();
    String destFilePath = destFile.getCanonicalPath();

    if (!destFilePath.startsWith(destDirPath + File.separator)) {
      throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
    }

    return destFile;
  }
}
