package helpers;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Scanner;

//methods in this class should not be dependent on anything relative
public class Utils {
    public static String getCurrentDir() throws URISyntaxException {

        return new File(Utils.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
    }

    public static String readFile(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        Scanner sc = new Scanner(file);

        String result = "";

        while (sc.hasNext()) {
            result = result + sc.next();
        }

        return result;
    }

    public void createAlert(String title, String header, String content, Alert.AlertType type) {

        //ensure that we always show the dialog on the main UI thread
        if (Platform.isFxApplicationThread()) {
            showAlert(title, header, content, type);

        } else {
            Platform.runLater(() -> {
                showAlert(title, header, content, type);
            });
        }
    }

    private void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

}
