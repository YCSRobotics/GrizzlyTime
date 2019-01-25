package tasks;

import databases.JSONHelper;
import exceptions.CancelledUserCreationException;
import exceptions.ConnectToWorksheetException;
import helpers.AlertUtils;
import helpers.Constants;
import helpers.LoggingUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import scenes.CreditsScene;

import java.util.logging.Level;

public class UserInterface {
    /**
     * @author Dalton Smith
     * UserInterface
     * Manages the main interface
     */

    private static Label messageText = new Label("");
    private static TextField studentIDBox = new TextField();
    private Button loginButton = new Button("Login/Logout");
    private UserProcess userProcess = new UserProcess();
    private AlertUtils alertUtils = new AlertUtils();
    private Text description = new Text(Constants.kUserTutorial);

    private Hyperlink creditsLink = new Hyperlink("Credits");
    private Text creditsText = new Text("v" + Constants.kVersion);
    private Hyperlink optionsLink = new Hyperlink("Full Screen");

    private BorderPane bottomPane = new BorderPane();

    private JSONHelper parser = new JSONHelper();

    private boolean jsonHandsFreeGrabbed = false;
    private boolean handsFreeMode = false;

    public UserInterface() {
        updateHandsFreeValue();
    }

    public void updateInterface(GridPane root) {
        //update CSS IDS
        messageText.setId("messageText");
        studentIDBox.setId("textBox");
        loginButton.setId("confirmButton");
        creditsLink.setId("hyperlinkBottom");
        optionsLink.setId("hyperlinkBottom");
        creditsText.setId("hyperlinkBottom");

        //create our panes
        GridPane subRoot = new GridPane();
        GridPane options = new GridPane();
        GridPane title = new GridPane();

        subRoot.setId("bottomView");

        //confirm alignments
        subRoot.setAlignment(Pos.CENTER);
        options.setAlignment(Pos.CENTER);
        title.setAlignment(Pos.CENTER);
        messageText.setAlignment(Pos.CENTER);
        description.setTextAlignment(TextAlignment.CENTER);
        description.setId("textDescription");

        //manually align message text because Gridpane is weird
        GridPane.setHalignment(messageText, HPos.CENTER);
        GridPane.setHalignment(description, HPos.CENTER);
        GridPane.setHalignment(subRoot, HPos.CENTER);

        //set bottom pane details
        bottomPane.setId("bottomPane");
        bottomPane.setLeft(optionsLink);
        bottomPane.setCenter(creditsText);
        bottomPane.setRight(creditsLink);
        bottomPane.setMinWidth(subRoot.getWidth());

        //add our various nodes to respective panes
        title.add(description, 0, 1);
        options.add(studentIDBox, 0, 0);
        options.add(loginButton, 1, 0);
        subRoot.add(title, 0, 0);
        subRoot.add(options, 0, 1);
        subRoot.add(messageText, 0, 2);

        //sub root details
        subRoot.setVgap(10);

        //add to root pane
        root.add(subRoot, 0, 1);
        root.add(bottomPane, 0, 2);

        //handle our buttons
        setEventHandlers();

    }

    //our event handlers for interactivity
    private void setEventHandlers() {
        //login on enter key press
        studentIDBox.setOnAction(event -> {
            confirmLogin();
        });

        //login button event handler
        loginButton.setOnAction(event -> {
            confirmLogin();
        });

        creditsLink.setOnAction(event -> {
            showCredits();
        });

        optionsLink.setOnAction(event -> {
            Stage stage = (Stage)optionsLink.getScene().getWindow();
            if (KeyHandlers.isFullscreen){
                stage.setFullScreen(false);
                KeyHandlers.isFullscreen = false;
            } else {
                stage.setFullScreen(true);
                KeyHandlers.isFullscreen = true;
            }
        });
    }

    private void showCredits() {
        CreditsScene scene = new CreditsScene();
        scene.showCredits();
    }

    //helper login method
    private void confirmLogin() {
        setMessageBoxText("Processing...");

        if (!userProcess.isValidID(studentIDBox.getText())) {
            setMessageBoxText("ID " + studentIDBox.getText() + " is an invalid 6 digit number.");

            Task<Void> wait = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Thread.sleep(5000);
                    return null;
                }
            };

            wait.setOnSucceeded(e -> {
                setMessageBoxText("");
            });

            //no need to set as daemon as will end after x seconds.
            new Thread(wait).start();
            return;

        }

        if (handsFreeMode) {
            //confirm that the user wants to login/logout
            if (alertUtils.confirmInput("Confirm login/logout of user: " + studentIDBox.getText())) {
                loginUser(false);
            } else {
                setMessageBoxText("");
            }

        //show no prompts
        } else {
            loginUser(true);
        }

    }

    private void updateHandsFreeValue() {
        if (!jsonHandsFreeGrabbed) {
            handsFreeMode = !parser.getKey("handsFreeMode").equals("false");

            jsonHandsFreeGrabbed = true;

        }
    }

    //login the user, check if hands free or not
    private void loginUser(boolean handsFree) {
        //separate login process on different thread to ensure
        //main application does not freeze
        //also allows in for multiple users login simultaneously
        Runnable loginUser = () -> {
            //ensure that the user typed something in
            if (studentIDBox.getText().isEmpty()) {
                setMessageBoxText("Nothing was entered!");
                return;

            }

            //attempt login/logout and or account creation
            //do nothing if account creation was cancelled
            try {

                //check if the user is logged in, and that user exists
                if (!(userProcess.isUserLoggedIn(studentIDBox.getText(), handsFree))) {
                    LoggingUtils.log(Level.INFO, "Logging in: " + studentIDBox.getText());
                    userProcess.loginUser(studentIDBox.getText());

                } else {
                    LoggingUtils.log(Level.INFO, "Logging out: " + studentIDBox.getText());
                    userProcess.logoutUser(studentIDBox.getText());

                }

            } catch (CancelledUserCreationException e) {
                setMessageBoxText("Cancelled account creation");

            } catch (ConnectToWorksheetException e) {
                setMessageBoxText("There was an error connecting to the database. Please retry.");

            } catch (Exception e) {
                LoggingUtils.log(Level.SEVERE, e);
                setMessageBoxText("An unknown error has occurred, see log file.");
            }

            //refocus the textbox
            Platform.runLater(() -> {
                studentIDBox.requestFocus();
            });
        };

        //start our thread
        Thread t = new Thread(loginUser);
        t.setDaemon(true);
        t.start();
    }

    //helper methods for setting and clearing text box
    public static void setMessageBoxText(String text) {
        if (Platform.isFxApplicationThread()) {
            messageText.setText(text);
        } else {
            Platform.runLater(() -> {
                messageText.setText(text);
            });
        }
    }

    public static void clearInput() { studentIDBox.clear(); }

}
