import databases.DatabaseUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

class GrizzlyTimeGUI {

    private Label scanLabel = new Label("Scan Student ID or Type Below!");
    private Label messageText = new Label("");
    private TextField studentIDBox = new TextField();
    private Button loginButton = new Button("Login/Logout");
    private UserProcess userProcess = new UserProcess();

    public void updateOptions(GridPane root) {
        GridPane subRoot = new GridPane();
        GridPane options = new GridPane();
        GridPane title = new GridPane();
        subRoot.setAlignment(Pos.CENTER);
        options.setAlignment(Pos.CENTER);
        title.setAlignment(Pos.CENTER);

        scanLabel.setStyle("-fx-font-size: 20");

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

    private void setEventHandlers() {
        loginButton.setOnAction(event -> {
            if (!(userProcess.isUserLoggedIn(studentIDBox.getText()))) {
                userProcess.loginUser(studentIDBox.getText());

            } else {
                userProcess.logoutUser(studentIDBox.getText());

            }
        });
    }
}
