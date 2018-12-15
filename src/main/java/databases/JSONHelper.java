package databases;

import helpers.Constants;
import helpers.Utils;
import javafx.scene.control.Alert;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/***
 * Description: Utility methods for retrieving configuration stored in the JSON
 */

public class JSONHelper {

    Utils util = new Utils();

    public String getKey(String key) {
        String JSONString;

        try {
            JSONString = Utils.readFile(Utils.getCurrentDir() + "/" + Constants.configLocal);

        } catch (FileNotFoundException e) {
            copyTemplateJSON();

            //show alert dialog
            util.createAlert(
                    "ERROR",
                    "config.json NOT FOUND",
                    "The required configuration was not found. It has been created. Please update the sheet URL!",
                    Alert.AlertType.ERROR
                    );

            System.exit(1);
            return null;

        } catch (URISyntaxException e) {
            e.printStackTrace();

            //show alert dialog
            util.createAlert(
                    "ERROR",
                    "ERROR LOADING config.json",
                    "An unspecified error occured while loading the config.json. Please make sure no other application is currently using it.",
                    Alert.AlertType.ERROR
            );

            //System.exit(1);
            return null;
        }

        JSONObject json = new JSONObject(JSONString);

        String result = json.getString(key);

        if (result.isEmpty()) {
        //show alert dialog
            util.createAlert(
                    "ERROR",
                    "ERROR LOADING config.json",
                    "Please confirm that the configuration is valid.",
                    Alert.AlertType.ERROR
            );

            System.exit(1);
            return null;

        } else {
            System.out.println(key+ ": " + result);
            return result;

        }

    }

    public void copyTemplateJSON(){
        try {
            Files.copy(getClass().getClassLoader().getResourceAsStream("templates/config.json"), Paths.get(Constants.configLocal), REPLACE_EXISTING);

        } catch (IOException e) {
            e.printStackTrace();

            util.createAlert(
                    "ERROR",
                    "ERROR COPYING JSON",
                    "An unspecified error occured while copying the config.json.",
                    Alert.AlertType.ERROR
            );
        }


    }
}
