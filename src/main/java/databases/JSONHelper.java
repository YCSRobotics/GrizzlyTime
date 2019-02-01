package databases;

import helpers.AlertUtils;
import helpers.CommonUtils;
import helpers.Constants;
import helpers.LoggingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class JSONHelper {
    /***
     * @author Dalton Smith
     * JSONHelper
     * Utility methods for retrieving configuration stored in the JSON
     */

    private AlertUtils util = new AlertUtils();

    //grab JSONKey
    public String getKey(String key) {
        String JSONString;

        //attempt to grab JSONString from config file
        try {
            JSONString = CommonUtils.readFile(CommonUtils.getCurrentDir() + File.separator + Constants.kConfigName);

        } catch (FileNotFoundException e) {
            LoggingUtils.log(Level.SEVERE, "config.json not found, creating");
            //config.json doesn't exist, create
            copyTemplateJSON();

            //show alert dialog
            util.createAlert(
                    "ERROR",
                    "Configuration file not found",
                    "The required config.json file was not found. It has been created. Please update the sheet URL!"
                    );

            //exit the application
            CommonUtils.exitApplication();
            return "";

        }

        //create a JSONObject from our string
        JSONObject json = new JSONObject(JSONString);
        String result;

        //grab the specified key from our json object
        try {
            result = json.getString(key);

        } catch (JSONException e) {
            LoggingUtils.log(Level.SEVERE, e);
            util.createAlert(
                    "ERROR",
                    "Error loading configuration file",
                    "Please delete the config.json file and relaunch the application."
            );

            CommonUtils.exitApplication();
            return "";
        }

        //confirm that the key was successfully retrieved
        if (result.isEmpty()) {
            LoggingUtils.log(Level.SEVERE, "Specified key: " + key + " has no data");
            util.createAlert(
                    "ERROR",
                    "Invalid Configuration",
                    "Please confirm that the configuration is valid and the sheet identifier is valid."
            );

            //exit application
            CommonUtils.exitApplication();
            return "";

        } else {
            //key was successfully retrieved
            LoggingUtils.log(Level.INFO, "Successfully retrieved: " + key + ": " + result);
            return result;

        }

    }

    //copy our json outside directory
    private void copyTemplateJSON(){
        try {
            InputStream pathToConfig = getClass().getClassLoader().getResourceAsStream("templates/config.json");

            if (pathToConfig == null) {
                throw new IOException("Path to config is null!");
            }

            Files.copy(pathToConfig, Paths.get(Constants.kConfigName), REPLACE_EXISTING);

        } catch (IOException e) {
            LoggingUtils.log(Level.SEVERE, e.getMessage());
            util.createAlert(
                    "ERROR",
                    "Error Copying Json",
                    "An unspecified error occured while copying the config.json. \n" +
                            e.getMessage()
            );
        }


    }
}
