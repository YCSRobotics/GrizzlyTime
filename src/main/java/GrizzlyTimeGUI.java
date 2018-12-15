import helpers.Utils;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
class GrizzlyTimeGUI {

    private Label scanLabel = new Label("Scan Student ID or Type Below!");
    private static Label messageText = new Label("");
    private static TextField studentIDBox = new TextField();
    private Button loginButton = new Button("Login/Logout");
    private UserProcess userProcess = new UserProcess();
    private Utils util = new Utils();

    void updateOptions(GridPane root) {
        //create our pains
        GridPane subRoot = new GridPane();
        GridPane options = new GridPane();
        GridPane title = new GridPane();
        subRoot.setAlignment(Pos.CENTER);
        options.setAlignment(Pos.CENTER);
        title.setAlignment(Pos.CENTER);

        //set styles
        scanLabel.setStyle("-fx-font-size: 20");

        //add to root pane
        title.add(scanLabel, 0, 0);
        title.add(messageText, 0, 1);
        options.add(studentIDBox, 0, 0);
        options.add(loginButton, 1, 0);
        subRoot.add(title, 0, 0);
        subRoot.add(options, 0, 1);
        subRoot.setMinHeight(180);
        subRoot.setVgap(10);

        root.add(subRoot, 0, 1);

        setEventHandlers();

    }

    //our event handlers for interactivity
    private void setEventHandlers() {

        loginButton.setOnAction(event -> {
            Runnable loginUser = () -> {
                if (!(userProcess.isUserLoggedIn(studentIDBox.getText()))) {
                    if (util.confirmInput("Confirm login of user: " + studentIDBox.getText())) {
                        System.out.println("Logging in");
                        userProcess.loginUser(studentIDBox.getText());
                    }

                } else {
                    if (util.confirmInput("Confirm logout of user: " + studentIDBox.getText())) {
                        System.out.println("Logging out");
                        userProcess.logoutUser(studentIDBox.getText());
                    }

                }
            };

            Thread t = new Thread(loginUser);
            t.setDaemon(true);
            t.start();

        });
    }

    public static void setMessageBoxText(String text) {
        messageText.setText(text);
    }
    public static void clearInput() { studentIDBox.clear(); }

}
