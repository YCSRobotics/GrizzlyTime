package notifiers;

import activities.LocalDbActivity;
import helpers.AlertUtils;
import helpers.CommonUtils;
import helpers.Constants;
import helpers.LoggingUtils;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;

/** @author Dalton Smith UpdateNotifier Displays popup if version is outdated */
public class UpdateNotifier {

  private AlertUtils alertUtils = new AlertUtils();

  private void downloadVersion() throws IOException {

    File file = new File("version.txt");

    if (file.exists()) {
      LoggingUtils.log(Level.WARNING, "version.txt already exists");

      if (file.delete()) {
        LoggingUtils.log(Level.INFO, "Delete was successful");
      } else {
        LoggingUtils.log(Level.WARNING, "Delete was unsuccessful");
      }
    }

    BufferedInputStream inputStream =
        new BufferedInputStream(new URL(Constants.kUpdateUrl).openStream());
    FileOutputStream fileOS =
        new FileOutputStream(CommonUtils.getCurrentDir() + File.separator + "version.txt");

    byte[] data = new byte[1024];
    int byteContent;
    while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
      fileOS.write(data, 0, byteContent);
    }
  }

  public void checkUpdates() {
    if (!LocalDbActivity.kUpdateNotifier) {
      return;
    }

    try {
      downloadVersion();
      LoggingUtils.log(Level.INFO, "Successfully grabbed version");

    } catch (IOException e) {
      LoggingUtils.log(Level.INFO, "Error checking for updates");
      LoggingUtils.log(Level.WARNING, e);
    }

    Scanner scanner;
    try {
      scanner = new Scanner(new File("version.txt"));

    } catch (FileNotFoundException e) {
      LoggingUtils.log(Level.SEVERE, e);
      return;
    }

    String version;

    if (scanner.hasNext()) {
      version = scanner.next();
      LoggingUtils.log(Level.INFO, "Updated version is: " + version);
      LoggingUtils.log(Level.INFO, "Current version is: " + Constants.kVersion);

    } else {
      LoggingUtils.log(Level.WARNING, "No version detected in version.txt");
      return;
    }

    if (!Constants.kVersion.equals(version)) {
      boolean confirm =
          alertUtils.createAlert(
              "New Version Available",
              "An update is available!",
              "Version " + version + " is available!\n" + Constants.kReleaseUrl);

      if (confirm) {
        CommonUtils.application.getHostServices().showDocument(Constants.kReleaseUrl);
      }
    }
  }
}
