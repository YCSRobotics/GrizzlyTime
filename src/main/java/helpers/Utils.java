package helpers;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

//methods in this class should not be dependent on anything relative
public class Utils {
    /**
     * @author Dalton Smith
     * Utils
     * Various utility methods used throughout the application
     * https://code.makery.ch/blog/javafx-dialogs-official/
     */

    public static Stage stage = null;

    public static String getCurrentDir() {
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
    public boolean createAlert(String title, String header, String content, Alert.AlertType type) {

        //ensure that we always show the dialog on the main UI thread
        if (Platform.isFxApplicationThread()) {
            return showAlert(title, header, content, type);

        } else {

            AtomicBoolean temp = new AtomicBoolean();

            Platform.runLater(() -> {
                temp.set(showAlert(title, header, content, type));
            });

            return temp.get();
        }
    }

    //alert helper
    private boolean showAlert(String title, String header, String content, Alert.AlertType type) {
        return customDialog(title, header, content);
    }

    //confirm new user registration
    public boolean confirmInput(String message) {
        AtomicBoolean tempBoolean = new AtomicBoolean();
        AtomicBoolean isSet = new AtomicBoolean();

        if (Platform.isFxApplicationThread()) {
            return customDialog("Confirm Login/Logout", "Confirm Login/Logout", message);

        } else {
            Platform.runLater(() -> {
                tempBoolean.set(customDialog("Confirm Login/Logout", "Confirm Login/Logout", message));
                isSet.set(true);

            });

            //wait for the user to confirm the dialog
            while(!isSet.get()){}

            Boolean result = tempBoolean.get();
            System.out.println(result);
            return tempBoolean.get();
        }
    }

    private boolean customDialog(String title, String header, String message) {
        // Create the custom dialog.
        Dialog dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.initOwner(stage);

        dialog.getDialogPane().getStylesheets().add("styles/root.css");
        dialog.getDialogPane().getStyleClass().add("myDialog");

        // Set Custom Icon
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("images/icon.png"));

        // Set the button types.
        ButtonType confirmButton = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButton, ButtonType.CANCEL);

        Image image = new Image("images/bear.png");
        ImageView imageView = new ImageView(image);

        imageView.setPreserveRatio(true);
        imageView.setFitWidth(50);

        // Set the icon (must be included in the project).
        dialog.setGraphic(imageView);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setId("customDialog");

        Text text = new Text(message);
        text.setWrappingWidth(400);

        grid.add(text, 0, 0);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        return result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE;
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
        dialog.initOwner(stage);

        // Set Custom Icon
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("images/icon.png"));

        dialog.getDialogPane().getStylesheets().add("styles/root.css");
        dialog.getDialogPane().getStyleClass().add("accountDialog");

        dialog.setTitle("New User Detected");
        dialog.setHeaderText("Please complete user registration!");

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("Create Account", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.setAlignment(Pos.CENTER);
        grid.setId("accountGrid");


        TextField username = new TextField("First Name");
        username.setId("textField");
        username.setPromptText("");
        TextField password = new TextField("Last Name");
        password.setPromptText("");
        password.setId("textField");

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
        try {
            if (data.get(0) != null) {
                LoggingUtil.log(Level.INFO, "User successfully completed all registration fields");
                return data;
            } else {
                data.add("FALSE");
                return data;
            }
        } catch (IndexOutOfBoundsException e) {
            data.add("FALSE");
            LoggingUtil.log(Level.WARNING, e.getMessage());
            return data;
        }
    }

    public void playDing() {

        Media sound = null;
        try {
            sound = new Media(getClass().getResource("/sounds/ding.wav").toURI().toString());
        } catch (URISyntaxException e) {
            LoggingUtil.log(Level.SEVERE, e);
        }
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }

}
