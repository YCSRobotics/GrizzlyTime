package scenes;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * @author Dalton Smith
 * GrizzlyTime Credits class
 * This class constructs the UI for displaying credits
 */
public class CreditsScene {
    GridPane root = new GridPane();
    Scene scene = new Scene(root);

    Stage stage = new Stage();

    //credits panes
    GridPane upperPaneMain = new GridPane();
    GridPane upperPaneRight = new GridPane();
    GridPane upperPaneLeft = new GridPane();
    GridPane bottomPaneMain = new GridPane();

    //grizzly image
    Image image = new Image("images/error.png");
    ImageView imageView = new ImageView(image);

    //upperPaneLeft
    Text summaryTitle = new Text("Summary");
    Text summaryText = new Text("" +
            "GrizzlyTime is a JavaFX application programmed originally for FRC Team 66, Grizzly Robotics. " +
            "GrizzlyTime was programmed by Grizzly Robotics Team Captain of Year 2018/2019, Dalton Smith. " +
            "All rights and permissions are reserved. Content is licensed under MIT. See below for more information.");

    Text creditsText = new Text("" +
            "GrizzlyTime-javaFX (Licensed under MIT): https://github.com/Daltz333/GrizzlyTime-JavaFX\n" +
            "Google Sheets API Client (Licensed under Apache 2.0) https://github.com/googleapis/google-api-java-client\n" +
            "ZXing barcode scanning library (Licensed under Apache 2.0) https://github.com/zxing/zxing\n" +
            "");

    public void showCredits() {
        stage.setWidth(608);
        stage.setHeight(610);
        stage.setScene(scene);

        scene.getStylesheets().add("styles/root.css");
        stage.getIcons().add(new Image("images/icon.png"));

        root.setId("main");

        upperPaneMain.setAlignment(Pos.TOP_CENTER);
        upperPaneMain.setPrefWidth(root.getWidth());

        root.add(upperPaneMain, 0, 0);
        root.add(bottomPaneMain, 0, 1);

        createCreditsUI(root);
        stage.show();
    }

    private void createCreditsUI(GridPane root) {

        imageView.setPreserveRatio(true);
        imageView.setFitWidth(350);

        summaryTitle.setId("title");
        summaryTitle.setTextAlignment(TextAlignment.CENTER);

        //forcibly center
        GridPane.setHalignment(summaryTitle, HPos.CENTER);

        summaryText.setId("summaryText");
        summaryText.setWrappingWidth(285);

        upperPaneLeft.setAlignment(Pos.TOP_CENTER);
        upperPaneLeft.setMinWidth(300);
        upperPaneLeft.setId("upperPaneLeft");

        upperPaneRight.setAlignment(Pos.TOP_CENTER);
        upperPaneRight.setMinWidth(300);

        upperPaneLeft.add(summaryTitle, 0, 0);
        upperPaneLeft.add(summaryText, 0, 1);

        upperPaneRight.add(imageView, 0, 0);

        upperPaneMain.add(upperPaneLeft, 0, 0);
        upperPaneMain.add(upperPaneRight, 1, 0);
    }

}
