package modules;

import helpers.Constants;
import helpers.Utils;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class UserInterface {
    /**
     * @author Dalton Smith
     * UserInterface
     * Manages the main interface
     */

    private Label scanLabel = new Label("GrizzlyTime Logging System");
    private static Label messageText = new Label("");
    private static TextField studentIDBox = new TextField();
    private Button loginButton = new Button("Login/Logout");
    private UserProcess userProcess = new UserProcess();
    private Utils util = new Utils();
    private Text version = new Text("Version: " + Constants.VERSION);
    private Text credits = new Text(Constants.CREDITS);

    private BorderPane bottomPane = new BorderPane();

    public void updateInterface(GridPane root) {
        //update CSS IDS
        scanLabel.setId("title");
        messageText.setId("messageText");
        studentIDBox.setId("textBox");
        loginButton.setId("confirmButton");

        //create our panes
        GridPane subRoot = new GridPane();
        GridPane options = new GridPane();
        GridPane title = new GridPane();

        //confirm alignments
        subRoot.setAlignment(Pos.CENTER);
        options.setAlignment(Pos.CENTER);
        title.setAlignment(Pos.CENTER);
        messageText.setAlignment(Pos.CENTER);

        //manually align message text because Gridpane is weird
        GridPane.setHalignment(messageText, HPos.CENTER);

        //set bottom pane details
        bottomPane.setId("bottomPane");
        bottomPane.setLeft(version);
        bottomPane.setRight(credits);
        bottomPane.setMinWidth(root.getWidth());

        //add our various nodes to respective panes
        title.add(scanLabel, 0, 0);
        options.add(studentIDBox, 0, 0);
        options.add(loginButton, 1, 0);
        subRoot.add(title, 0, 0);
        subRoot.add(options, 0, 1);
        subRoot.add(messageText, 0, 2);

        //sub root details
        subRoot.setMinHeight(182);
        subRoot.setVgap(10);

        //add to root pane
        root.add(subRoot, 0, 1);
        root.add(bottomPane, 0, 2);

        //handle our buttons
        setEventHandlers();

    }

    //our event handlers for interactivity
    private void setEventHandlers() {

        //login button event handler
        loginButton.setOnAction(event -> {

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
                        System.out.println("CANCELLED");

                    }
                };

                //start our thread
                Thread t = new Thread(loginUser);
                t.setDaemon(true);
                t.start();

            }
        });
    }

    //helper methods for setting and clearing text box
    static void setMessageBoxText(String text) {
        messageText.setText(text);
    }

    static void clearInput() { studentIDBox.clear(); }

}
