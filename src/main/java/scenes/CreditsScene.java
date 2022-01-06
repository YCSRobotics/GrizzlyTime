package scenes;

import helpers.Constants;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * @author Dalton Smith GrizzlyTime Credits class This class constructs the UI for displaying
 *     credits
 */
public class CreditsScene {
  // credits panes
  private GridPane upperPaneMain = new GridPane();
  private GridPane upperPaneRight = new GridPane();
  private GridPane upperPaneLeft = new GridPane();
  private GridPane bottomPaneMain = new GridPane();
  private GridPane mainContent = new GridPane();
  private BorderPane navMenu = new BorderPane();

  // nav menu
  Button backButton = new Button("Back");

  // grizzly image
  private Image image = new Image(Constants.kErrorImage);
  private ImageView imageView = new ImageView(image);

  // upperPaneLeft
  private Text summaryTitle = new Text("Summary");
  private Text summaryText = new Text(Constants.kCreditsSummary);

  private Text credits = new Text(Constants.kCreditsList);

  public void showCredits(GridPane root) {
    root.setId("creditsRoot");

    upperPaneMain.setAlignment(Pos.CENTER);
    upperPaneMain.setPrefWidth(root.getWidth());

    navMenu.setId("navMenu");
    backButton.setId("navMenuButton");

    navMenu.setLeft(backButton);
    mainContent.setId("creditsMainContent");

    mainContent.add(upperPaneMain, 0, 0);
    mainContent.add(bottomPaneMain, 0, 1);

    mainContent.setAlignment(Pos.CENTER);
    GridPane.setValignment(mainContent, VPos.CENTER);

    root.add(navMenu, 0, 0);
    root.add(mainContent, 0, 1);

    linkHandlers();

    createCreditsUI(root);
  }

  public void reShowUI(GridPane root) {
    root.setId("creditsRoot");

    root.add(navMenu, 0, 0);
    root.add(mainContent, 0, 1);
  }

  private void createCreditsUI(GridPane root) {
    GridPane.setHalignment(root, HPos.CENTER);
    root.setAlignment(Pos.TOP_CENTER);

    imageView.setPreserveRatio(Constants.kCreditsBearPreserveRatio);
    imageView.setFitHeight(Constants.kCreditsBearHeight);

    summaryTitle.setId("title");
    summaryTitle.setTextAlignment(TextAlignment.CENTER);

    // forcibly center
    GridPane.setHalignment(summaryTitle, HPos.CENTER);

    summaryText.setId("summaryText");
    summaryText.setWrappingWidth(Constants.kCreditsWrapTextWidth);

    upperPaneLeft.setAlignment(Pos.TOP_CENTER);
    upperPaneLeft.setId("upperPaneLeft");
    upperPaneRight.setId("upperPaneRight");

    GridPane.setHalignment(upperPaneLeft, HPos.RIGHT);

    upperPaneRight.setAlignment(Pos.TOP_CENTER);
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

  private void linkHandlers() {
    backButton.setOnAction(
        event -> {
          SceneManager.updateScene(Constants.kMainSceneState);
        });
  }
}
