package helpers;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

/**
 * @author Dalton Smith
 * AlertUtils
 * Various alert utility methods used throughout the application
 * https://code.makery.ch/blog/javafx-dialogs-official/
 */
public class AlertUtils {

    public static Stage stage = null;

    //create alert
    public boolean createAlert(String title, String header, String content) {

        //ensure that we always show the dialog on the main UI thread
        if (Platform.isFxApplicationThread()) {
            return customDialog(title, header, content);

        } else {

            AtomicBoolean temp = new AtomicBoolean();

            Platform.runLater(() -> {
                temp.set(customDialog(title, header, content));
            });

            return temp.get();
        }
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

            while (!isSet.get()) {
                //wait for the user to confirm the dialog
            }

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

        dialog.getDialogPane().getStylesheets().add(Constants.kRootStylesheet);
        dialog.getDialogPane().getStyleClass().add("myDialog");

        // Set Custom Icon
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Constants.kApplicationIcon));

        // Set the button types.
        ButtonType confirmButton = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButton, ButtonType.CANCEL);

        Image image = new Image(Constants.kBearImage);
        ImageView imageView = new ImageView(image);

        imageView.setPreserveRatio(Constants.kBearPreserveRatio);
        imageView.setFitWidth(Constants.kBearImageWidth);

        // Set the icon (must be included in the project).
        dialog.setGraphic(imageView);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setId("customDialog");

        Text text = new Text(message);
        text.setWrappingWidth(Constants.kWordWrapWidth);

        grid.add(text, 0, 0);

        dialog.getDialogPane().setContent(grid);

        Optional result = dialog.showAndWait();

        if (!result.isPresent()) {
            return false;
        }

        ButtonType buttonInfo = (ButtonType) result.get();

        return buttonInfo.getButtonData() == ButtonBar.ButtonData.OK_DONE;
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
            while (!isSet.get()) {
            }

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
        stage.getIcons().add(new Image(Constants.kApplicationIcon));

        dialog.getDialogPane().getStylesheets().add(Constants.kRootStylesheet);
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
                LoggingUtils.log(Level.INFO, "User successfully completed all registration fields");
                return data;
            } else {
                data.add("FALSE");
                return data;
            }
        } catch (IndexOutOfBoundsException e) {
            data.add("FALSE");
            LoggingUtils.log(Level.WARNING, e.getMessage());
            return data;
        }
    }
}
