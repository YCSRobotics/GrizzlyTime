package databases;

import helpers.Constants;
import helpers.Utils;
import javafx.scene.control.Alert;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
            //config.json doesn't exist, create
            copyTemplateJSON();

            //show alert dialog
            util.createAlert(
                    "ERROR",
                    "config.json NOT FOUND",
                    "The required configuration was not found. It has been created. Please update the sheet URL!",
                    Alert.AlertType.ERROR
                    );

            //exit the application
            System.exit(1);
            return null;

        }

        //create a JSONObject from our string
        JSONObject json = new JSONObject(JSONString);

        //grab the specified key from our json object
        String result = json.getString(key);

        //confirm that the key was successfully retrieved
        if (result.isEmpty()) {
            util.createAlert(
                    "ERROR",
                    "ERROR LOADING config.json",
                    "Please confirm that the configuration is valid. \n" +
                            "ERROR RETRIEVING: " + key + " EMPTY",
                    Alert.AlertType.ERROR
            );

            //exit application
            System.exit(1);
            return null;

        } else {
            //key was successfully retrieved
            System.out.println(key+ ": " + result);
            return result;

        }

    }

    //copy our json outside directory
    private void copyTemplateJSON(){
        try {
            Files.copy(getClass().getClassLoader().getResourceAsStream("templates/config.json"), Paths.get(Constants.configLocal), REPLACE_EXISTING);

        } catch (IOException e) {
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
