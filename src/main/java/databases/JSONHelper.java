package databases;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import exceptions.JsonKeyHasNoDataException;
import helpers.AlertUtils;
import helpers.CommonUtils;
import helpers.Constants;
import helpers.LoggingUtils;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import org.json.JSONObject;

public class JSONHelper {
  /***
   * @author Dalton Smith
   * JSONHelper
   * Utility methods for retrieving configuration stored in the JSON
   */

  private AlertUtils util = new AlertUtils();

  // grab JSONKey
  public String getKey(String key) throws JsonKeyHasNoDataException, FileNotFoundException {
    String JSONString;

    // attempt to grab JSONString from config file
    JSONString =
        CommonUtils.readFile(CommonUtils.getCurrentDir() + File.separator + Constants.kConfigName);

    // create a JSONObject from our string
    JSONObject json = new JSONObject(JSONString);
    String result;

    // grab the specified key from our json object
    result = json.getString(key);

    // confirm that the key was successfully retrieved
    if (result.isEmpty()) {
      throw new JsonKeyHasNoDataException(key + " has no data!");

    } else {
      // key was successfully retrieved
      LoggingUtils.log(Level.INFO, "Successfully retrieved: " + key + ": " + result);
      return result;
    }
  }

  public void editSpreadsheetId(String sheetID) throws FileNotFoundException {
    String data =
        CommonUtils.readFile(CommonUtils.getCurrentDir() + File.separator + Constants.kConfigName);

    String[] keyData = data.split("\"sheet\":\"");

    String newJson = keyData[0] + "\"sheet\":\"" + sheetID + keyData[1];

    try {
      FileWriter writer =
          new FileWriter(
              new File(CommonUtils.getCurrentDir() + File.separator + Constants.kConfigName));
      writer.write(newJson);
      writer.flush();
      writer.close();
    } catch (IOException e) {
      LoggingUtils.log(Level.SEVERE, "Unable to edit sheet value!");
    }
  }

  // copy our json outside directory
  public void copyTemplateJSON() {
    try {
      InputStream pathToConfig =
          getClass().getClassLoader().getResourceAsStream("templates/config.json");

      if (pathToConfig == null) {
        throw new IOException("Path to config is null!");
      }

      Files.copy(pathToConfig, Paths.get(Constants.kConfigName), REPLACE_EXISTING);

    } catch (IOException e) {
      LoggingUtils.log(Level.SEVERE, e.getMessage());
      util.createAlert(
          "ERROR",
          "Error Copying Json",
          "An unspecified error occured while copying the config.json. \n" + e.getMessage());
    }
  }
}
