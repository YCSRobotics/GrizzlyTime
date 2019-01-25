package databases;

import helpers.Constants;
import helpers.LoggingUtil;
import helpers.Utils;
import javafx.scene.control.Alert;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
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

    private Utils util = new Utils();

    //grab JSONKey
    public String getKey(String key) {
        String JSONString;

        //attempt to grab JSONString from config file
        try {
            JSONString = Utils.readFile(Utils.getCurrentDir() + "/" + Constants.configLocal);

        } catch (FileNotFoundException e) {
            LoggingUtil.log(Level.SEVERE, "config.json not found, creating");
            //config.json doesn't exist, create
            copyTemplateJSON();

            //show alert dialog
            util.createAlert(
                    "ERROR",
                    "Configuration file not found",
                    "The required config.json file was not found. It has been created. Please update the sheet URL!",
                    Alert.AlertType.ERROR
                    );

            //exit the application
            return "";

        }

        //create a JSONObject from our string
        JSONObject json = new JSONObject(JSONString);
        String result;

        //grab the specified key from our json object
        try {
            result = json.getString(key);

        } catch (JSONException e) {
            LoggingUtil.log(Level.SEVERE, e);
            util.createAlert(
                    "ERROR",
                    "Error loading configuration file",
                    "Please delete the config.json file and relaunch the application. \n" +
                            "ERROR RETRIEVING: " + key + " EMPTY",
                    Alert.AlertType.ERROR
            );

            return "";
        }

        //confirm that the key was successfully retrieved
        if (result.isEmpty()) {
            LoggingUtil.log(Level.SEVERE, "Specified key: " + key + " has no data");
            util.createAlert(
                    "ERROR",
                    "Invalid Configuration",
                    "Please confirm that the configuration is valid. \n" +
                            "ERROR RETRIEVING: " + key + " EMPTY",
                    Alert.AlertType.ERROR
            );

            //exit application
            return "";

        } else {
            //key was successfully retrieved
            LoggingUtil.log(Level.INFO, "Successfully retrieved: " + key+ ": " + result);
            return result;

        }

    }

    //copy our json outside directory
    private void copyTemplateJSON(){
        try {
            Files.copy(getClass().getClassLoader().getResourceAsStream("templates/config.json"), Paths.get(Constants.configLocal), REPLACE_EXISTING);

        } catch (IOException e) {
            LoggingUtil.log(Level.SEVERE, e.getMessage());
            util.createAlert(
                    "ERROR",
                    "ERROR COPYING JSON",
                    "An unspecified error occured while copying the config.json. \n" +
                    e.getMessage(),
                    Alert.AlertType.ERROR
            );
        }


    }
}
