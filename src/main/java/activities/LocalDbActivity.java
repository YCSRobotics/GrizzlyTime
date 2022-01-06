package activities;

import databases.JSONHelper;
import exceptions.JsonKeyHasNoDataException;
import helpers.AlertUtils;
import helpers.CommonUtils;
import helpers.LoggingUtils;
import java.io.FileNotFoundException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import javafx.application.Platform;
import org.json.JSONException;

public class LocalDbActivity {
  public static String kSheetId = "";
  public static boolean kHandsFreeMode = false;
  public static boolean kUpdateNotifier = true;
  public static int kIdLength = 6;
  public static int kIdLengthFallback = 7;
  public static String kApplicationName = "GrizzlyTime_JavaFX_Edition";
  public static boolean kGrizzlyVerification = false;

  private JSONHelper jsonHelper = new JSONHelper();
  private AlertUtils alertUtils = new AlertUtils();

  public void updateLocalDb() {
    try {
      try {
        kSheetId = jsonHelper.getKey("sheet");
      } catch (JsonKeyHasNoDataException e) {
        LoggingUtils.log(Level.SEVERE, e);
        throw new FileNotFoundException("Blank spreadsheet");
      }

      kHandsFreeMode = jsonHelper.getKey("handsFreeMode").equalsIgnoreCase("true");
      kUpdateNotifier = jsonHelper.getKey("updateNotifier").equalsIgnoreCase("true");
      kGrizzlyVerification = jsonHelper.getKey("grizzlyVerification").equalsIgnoreCase("true");
      kApplicationName = jsonHelper.getKey("applicationName");

      try {
        kIdLength = Integer.parseInt(jsonHelper.getKey("idLength"));
        kIdLengthFallback = Integer.parseInt(jsonHelper.getKey("idLengthFallback"));

      } catch (NumberFormatException e) {
        LoggingUtils.log(Level.INFO, "Error reading from config, using fallback");
        LoggingUtils.log(Level.SEVERE, e);
      }
    } catch (FileNotFoundException e) {
      LoggingUtils.log(Level.SEVERE, e);
      AlertUtils alert = new AlertUtils();
      jsonHelper.copyTemplateJSON();

      AtomicBoolean set = new AtomicBoolean(false);
      if (Platform.isFxApplicationThread()) {
        try {
          jsonHelper.editSpreadsheetId(alert.showSpreadsheetDialog());
          set.set(true);
        } catch (FileNotFoundException ex) {
          LoggingUtils.log(Level.SEVERE, "Error using GUI to set spreadsheet!");
        }
      } else {
        Platform.runLater(
            () -> {
              try {
                jsonHelper.editSpreadsheetId(alert.showSpreadsheetDialog());
                set.set(true);
              } catch (FileNotFoundException ex) {
                LoggingUtils.log(Level.SEVERE, "Error using GUI to set spreadsheet!");
              }
            });
      }

      if (set.get()) {
        updateLocalDb();
      } else {
        alertUtils.createAlert(
            "ERROR",
            "Configuration File does not exist!",
            "The config.json file does not exist and has been created! Please update the sheet ID");

        CommonUtils.exitApplication();
      }

    } catch (JsonKeyHasNoDataException e) {
      LoggingUtils.log(Level.SEVERE, e);
      LoggingUtils.log(Level.WARNING, "Using Fallback");

    } catch (JSONException e) {
      LoggingUtils.log(Level.SEVERE, e);
      alertUtils.createAlert(
          "ERROR",
          "Error reading configuration file!",
          "Please try deleting the config.json file and trying again!\n" + e.getMessage());
      CommonUtils.exitApplication();
    }
  }
}
