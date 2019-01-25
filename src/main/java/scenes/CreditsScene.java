package scenes;

import helpers.Constants;
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
    private GridPane root = new GridPane();
    private Scene scene = new Scene(root);

    private Stage stage = new Stage();

    //credits panes
    private GridPane upperPaneMain = new GridPane();
    private GridPane upperPaneRight = new GridPane();
    private GridPane upperPaneLeft = new GridPane();
    private GridPane bottomPaneMain = new GridPane();

    //grizzly image
    private Image image = new Image(Constants.kErrorImage);
    private ImageView imageView = new ImageView(image);

    //upperPaneLeft
    private Text summaryTitle = new Text("Summary");
    private Text summaryText = new Text(Constants.kCreditsSummary);

    private Text credits = new Text(Constants.kCreditsList);

    public void showCredits() {
        stage.setWidth(Constants.kCreditsStageWidth);
        stage.setHeight(Constants.kCreditsStageHeight);
        stage.setScene(scene);

        scene.getStylesheets().add(Constants.kRootStylesheet);
        stage.getIcons().add(new Image(Constants.kApplicationIcon));

        root.setId("creditsRoot");
        root.setVgap(25);

        upperPaneMain.setAlignment(Pos.CENTER);
        upperPaneMain.setPrefWidth(root.getWidth());

        root.add(upperPaneMain, 0, 0);
        root.add(bottomPaneMain, 0, 1);

        createCreditsUI(root);
        stage.show();
    }

    private void createCreditsUI(GridPane root) {

        root.setAlignment(Pos.TOP_CENTER);
        GridPane.setHalignment(root, HPos.CENTER);

        imageView.setPreserveRatio(Constants.kCreditsBearPreserveRatio);
        imageView.setFitHeight(Constants.kCreditsBearHeight);

        summaryTitle.setId("title");
        summaryTitle.setTextAlignment(TextAlignment.CENTER);

        //forcibly center
        GridPane.setHalignment(summaryTitle, HPos.CENTER);

        summaryText.setId("summaryText");
        summaryText.setWrappingWidth(Constants.kCreditsWrapTextWidth);

        upperPaneLeft.setAlignment(Pos.TOP_CENTER);
        upperPaneLeft.setMinWidth(300);
        upperPaneLeft.setId("upperPaneLeft");
        GridPane.setHalignment(upperPaneLeft, HPos.RIGHT);

        upperPaneRight.setAlignment(Pos.TOP_CENTER);
        upperPaneRight.setMinWidth(260);

        upperPaneLeft.add(summaryTitle, 0, 0);
        upperPaneLeft.add(summaryText, 0, 1);

        upperPaneRight.add(imageView, 0, 0);

        upperPaneMain.add(upperPaneLeft, 0, 0);
        upperPaneMain.add(upperPaneRight, 1, 0);

        bottomPaneMain.setMaxWidth(500);
        bottomPaneMain.setAlignment(Pos.CENTER);
        bottomPaneMain.setId("creditsMain");
        GridPane.setHalignment(bottomPaneMain, HPos.CENTER);
        bottomPaneMain.add(credits, 0, 0);
    }

}
