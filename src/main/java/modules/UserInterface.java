package modules;

import helpers.Constants;
import helpers.Utils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

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
    private Utils util = new Utils();
    private Text description = new Text("Type in your Student ID to login. If you do not have a Student ID," +
            "\nenter your birth date in 6 digits. [MMDDYY]");

    private Button credits = new Button("!");
    private BorderPane bottomPane = new BorderPane();

    public void updateInterface(GridPane root) {
        //update CSS IDS
        messageText.setId("messageText");
        studentIDBox.setId("textBox");
        loginButton.setId("confirmButton");
        credits.setId("creditsButton");

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
        bottomPane.setRight(credits);
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
        //root.add(bottomPane, 0, 2);

        //handle our buttons
        setEventHandlers();

    }

    //our event handlers for interactivity
    private void setEventHandlers() {
        //login on enter key press
        studentIDBox.setOnAction(event -> {
            loginUser();
        });

        //login button event handler
        loginButton.setOnAction(event -> {
            loginUser();
        });

        credits.setOnAction(event -> {
            showCredits();
        });
    }

    private void showCredits() {
        util.createAlert("Credits", "Credits",
                "This application was programmed by a member of Grizzly Robotics (Team 66 & Team 470), Dalton Smith.\n" +
                "This application is licensed under MIT and all restrictions apply.\n" +
                "This application uses the follow external dependencies which have their own licenses.\n" +
                "OpenCV\n" +
                "ZXing Barcode Scanning\n" +
                "Java Google OAuth Client\n" +
                "Java Google Sheets Client\n" +
                "Java Google API Client\n" +
                "Apache Commons-IO\n" +
                "Apache Json",
                Alert.AlertType.INFORMATION);
    }

    //helper login method
    private void loginUser() {
        setMessageBoxText("Processing...");

        if(!userProcess.isValidID(studentIDBox.getText())) {
            setMessageBoxText("ID " + studentIDBox.getText() + " is an invalid 6 digit number.");

            Task<Void> wait = new Task<Void>() {
                @Override
                protected Void call() throws Exception{
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

        //confirm that the user wants to login/logout
        if (util.confirmInput("Confirm login/logout of user: " + studentIDBox.getText())) {

            //separate login process on different thread to ensure
            //main application does not freeze
            //also allows in for multiple users login simultaneously
            Runnable loginUser = () -> {
                //ensure that the user typed something in
                if (studentIDBox.getText().isEmpty()) {
                    util.createAlert(
                            "Invalid ID",
                            "Invalid ID",
                            "The ID you specified is invalid.",
                            Alert.AlertType.ERROR
                    );
                    return;

                }

                //attempt login/logout and or account creation
                //do nothing if account creation was cancelled
                try {

                    //check if the user is logged in, and that user exists
                    if (!(userProcess.isUserLoggedIn(studentIDBox.getText()))) {
                        System.out.println("Logging in");
                        userProcess.loginUser(studentIDBox.getText());

                    } else {
                        System.out.println("Logging out");
                        userProcess.logoutUser(studentIDBox.getText());

                    }
                } catch (Exception e) {
                    //do nothing
                    setMessageBoxText("Cancelled account creation");
                    System.out.println("CANCELLED");

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

        } else {
            setMessageBoxText("");
        }

    }
    //helper methods for setting and clearing text box
    static void setMessageBoxText(String text) {
        messageText.setText(text);
    }

    static void clearInput() { studentIDBox.clear(); }

}
