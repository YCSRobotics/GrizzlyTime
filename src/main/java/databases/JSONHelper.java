package databases;

import exceptions.JsonKeyHasNoDataException;
import helpers.AlertUtils;
import helpers.CommonUtils;
import helpers.Constants;
import helpers.LoggingUtils;
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
    public String getKey(String key) throws JsonKeyHasNoDataException, FileNotFoundException {
        String JSONString;

        //attempt to grab JSONString from config file
        JSONString = CommonUtils.readFile(CommonUtils.getCurrentDir() + File.separator + Constants.kConfigName);

        //create a JSONObject from our string
        JSONObject json = new JSONObject(JSONString);
        String result;

        //grab the specified key from our json object
        result = json.getString(key);


        //confirm that the key was successfully retrieved
        if (result.isEmpty()) {
            throw new JsonKeyHasNoDataException(key + " has no data!");

        } else {
            //key was successfully retrieved
            LoggingUtils.log(Level.INFO, "Successfully retrieved: " + key + ": " + result);
            return result;

        }

    }

    //copy our json outside directory
    public void copyTemplateJSON(){
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
