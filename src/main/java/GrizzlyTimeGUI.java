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

class GrizzlyTimeGUI {

    private Label scanLabel = new Label("GrizzlyTime Logging System");
    private static Label messageText = new Label("");
    private static TextField studentIDBox = new TextField();
    private Button loginButton = new Button("Login/Logout");
    private UserProcess userProcess = new UserProcess();
    private Utils util = new Utils();
    private Text version = new Text("Version: " + Constants.VERSION);
    private Text credits = new Text(Constants.CREDITS);

    private BorderPane bottomPane = new BorderPane();

    void updateOptions(GridPane root) {
        //update CSS IDS
        scanLabel.setId("title");
        messageText.setId("messageText");
        studentIDBox.setId("textBox");
        loginButton.setId("confirmButton");

        //create our pains
        GridPane subRoot = new GridPane();
        GridPane options = new GridPane();
        GridPane title = new GridPane();
        subRoot.setAlignment(Pos.CENTER);
        options.setAlignment(Pos.CENTER);
        title.setAlignment(Pos.CENTER);

        bottomPane.setId("bottomPane");
        bottomPane.setLeft(version);
        bottomPane.setRight(credits);
        bottomPane.setAlignment(messageText, Pos.CENTER);
        bottomPane.setAlignment(version, Pos.CENTER);
        bottomPane.setAlignment(credits, Pos.CENTER);
        bottomPane.setMinWidth(root.getWidth());
        messageText.setAlignment(Pos.CENTER);
        GridPane.setHalignment(messageText, HPos.CENTER);

        GridPane.setValignment(bottomPane, VPos.BOTTOM);

        //add to root pane
        title.add(scanLabel, 0, 0);
        options.add(studentIDBox, 0, 0);
        options.add(loginButton, 1, 0);
        subRoot.add(title, 0, 0);
        subRoot.add(options, 0, 1);
        subRoot.add(messageText, 0, 2);
        subRoot.setMinHeight(182);
        subRoot.setVgap(10);

        root.add(subRoot, 0, 1);
        root.add(bottomPane, 0, 2);

        setEventHandlers();

    }

    //our event handlers for interactivity
    private void setEventHandlers() {

        loginButton.setOnAction(event -> {
            if (util.confirmInput("Confirm login/logout of user: " + studentIDBox.getText())) {
                Runnable loginUser = () -> {
                    if (studentIDBox.getText().isEmpty()) {
                        util.createAlert(
                                "Invalid ID",
                                "Invalid ID",
                                "The ID you specified is invalid.",
                                Alert.AlertType.ERROR
                        );
                        return;
                    }

                    try {
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

                Thread t = new Thread(loginUser);
                t.setDaemon(true);
                t.start();

            }
        });
    }

    public static void setMessageBoxText(String text) {
        messageText.setText(text);
    }
    public static void clearInput() { studentIDBox.clear(); }

}
