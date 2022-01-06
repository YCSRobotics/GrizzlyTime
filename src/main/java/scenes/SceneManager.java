package scenes;

import helpers.Constants;
import helpers.LoggingUtils;
import java.util.logging.Level;
import javafx.scene.layout.GridPane;

public class SceneManager {
  private static GrizzlyScene grizzlyScene;
  private static CreditsScene creditsScene;
  private static SplashScene splashScene;
  private static OptionScene optionScene;

  private static boolean initGrizzlyScene = false;
  private static boolean initCreditsScene = false;
  private static boolean initSplashScene = false;
  private static boolean initOptionsScene = false;

  private static boolean shownGrizzlyScene = false;
  private static boolean shownCreditsScene = false;

  private static GridPane root;

  public static void updateScene(int scene) {
    if (root == null) {
      LoggingUtils.log(Level.SEVERE, "Root was not set for scene manager");
    }

    switch (scene) {
      case Constants.kSplashSceneState:
        loadSplash();
        displaySplash();
        break;
      case Constants.kMainSceneState:
        root.getChildren().clear();
        if (!shownGrizzlyScene) {
          displayMain();
          shownGrizzlyScene = true;
        } else {
          grizzlyScene.reShowUI(root);
        }

        break;
      case Constants.kCreditsSceneState:
        root.getChildren().clear();

        if (!shownCreditsScene) {
          loadCredits();
          displayCredits();
          shownCreditsScene = true;
        } else {
          creditsScene.reShowUI(root);
        }

        break;
      case Constants.kLoadMainScene:
        loadMain();
        break;
      default:
        LoggingUtils.log(Level.WARNING, scene + " is unknown");
        break;
    }
  }

  private static void loadSplash() {
    if (!initSplashScene) {
      splashScene = new SplashScene();
    }
  }

  private static void displaySplash() {
    if (splashScene == null) {
      LoggingUtils.log(Level.SEVERE, "Attempted to display scene without initialization");
      return;
    }

    splashScene.showSplash(root);
  }

  private static void loadMain() {
    if (!initGrizzlyScene) {
      grizzlyScene = new GrizzlyScene();
    }
  }

  private static void displayMain() {
    if (grizzlyScene == null) {
      LoggingUtils.log(Level.SEVERE, "Attempted to display scene without initialization");
      return;
    }

    grizzlyScene.updateInterface(root);
  }

  private static void loadCredits() {
    if (!initCreditsScene) {
      creditsScene = new CreditsScene();
    }
  }

  private static void displayCredits() {
    if (creditsScene == null) {
      LoggingUtils.log(Level.SEVERE, "Attempted to display scene without initialization");
      return;
    }

    creditsScene.showCredits(root);
  }

  public static void setRoot(GridPane root) {
    SceneManager.root = root;
  }
}
