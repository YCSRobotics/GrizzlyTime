import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class GrizzlyTimeGUI {

    Label scanLabel = new Label("Scan Student ID or Type Below!");
    Label studentIDLabel = new Label("Student ID: ");
    TextField studentIDBox = new TextField();
    Button loginButton = new Button("Login/Logout");

    public void updateOptions(GridPane root) {
        GridPane subRoot = new GridPane();
        GridPane options = new GridPane();
        GridPane title = new GridPane();
        subRoot.setAlignment(Pos.CENTER);
        options.setAlignment(Pos.CENTER);
        title.setAlignment(Pos.CENTER);

        scanLabel.setStyle("-fx-font-size: 20");

        title.add(scanLabel, 0, 0);
        options.add(studentIDBox, 0, 0);
        options.add(loginButton, 1, 0);
        subRoot.add(title, 0, 0);
        subRoot.add(options, 0, 1);
        subRoot.setMinHeight(180);
        subRoot.setVgap(10);

        root.add(subRoot, 0, 1);

    }
}
