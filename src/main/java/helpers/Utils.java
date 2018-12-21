package helpers;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

//methods in this class should not be dependent on anything relative
public class Utils {
    /**
     * @author Dalton Smith
     * Utils
     * Various utility methods used throughout the application
     * https://code.makery.ch/blog/javafx-dialogs-official/
     */

    public static String getCurrentDir() {
        System.out.println(System.getProperty("user.dir"));
        return System.getProperty("user.dir");

    }

    public static String readFile(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        Scanner sc = new Scanner(file);

        String result = "";

        //read the entire json
        while (sc.hasNext()) {
            result = result + sc.next();

        }

        return result;
    }

    //create alert
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

    //alert helper
    private void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

    //confirm new user registration
    public boolean confirmInput(String message) {
        AtomicBoolean tempBoolean = new AtomicBoolean();
        AtomicBoolean isSet = new AtomicBoolean();

        if (Platform.isFxApplicationThread()) {
            return confirmInputHelper(message);

        } else {
            Platform.runLater(() -> {
                tempBoolean.set(confirmInputHelper(message));
                isSet.set(true);

            });

            //wait for the user to confirm the dialog
            while(!isSet.get()){}

            Boolean result = tempBoolean.get();
            System.out.println(result);
            return tempBoolean.get();
        }
    }

    //new user registration helper
    private boolean confirmInputHelper(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Confirm login/logout?");
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }

    public ArrayList<String> getUserInfo() {

        //run on FX application thread
        if (Platform.isFxApplicationThread()) {
            return showAuthDialog();

        } else {
            AtomicReference<ArrayList<String>> temp = new AtomicReference<>();
            AtomicReference<Boolean> isSet = new AtomicReference<>();

            isSet.set(false);
            Platform.runLater(() -> {
                temp.set(showAuthDialog());
                isSet.set(true);
            });

            //wait until the user has finished dialog
            while (!isSet.get()) {}

            System.out.println("Run later");
            return temp.get();

        }
    }

    private ArrayList<String> showAuthDialog() {
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("New User Detected");
        dialog.setHeaderText("Please enter your name to complete your registration and login.");

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("Create Account", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField("First Name");
        username.setPromptText("");
        TextField password = new TextField("Last Name");
        password.setPromptText("");

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(password, 1, 1);

        // Enable/Disable login button depending on whether a username was entered.
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        username.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(username::requestFocus);

        ArrayList<String> data = new ArrayList<>();
        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(username.getText(), password.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        //confirm that the user typed in data
        result.ifPresent(usernamePassword -> {
            data.add("TRUE");
            data.add(usernamePassword.getKey());
            data.add(usernamePassword.getValue());

        });

        //return the data
        if (data.get(0) != null){ return data; }

        //the user did not return data
        data.set(0, "FALSE");

        return data;
    }

}
