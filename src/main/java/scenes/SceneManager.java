package scenes;

import helpers.Constants;
import helpers.LoggingUtils;
import javafx.scene.layout.GridPane;

import java.util.logging.Level;

public class SceneManager {
    private static GrizzlyScene grizzlyScene;
    private static CreditsScene creditsScene;
    private static SplashScene splashScene;
    private static OptionScene optionScene;

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
                displayMain();
                break;
            case Constants.kCreditsSceneState:
                displayCredits();
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
        splashScene = new SplashScene();
    }

    private static void displaySplash() {
        if (splashScene == null) {
            LoggingUtils.log(Level.SEVERE, "Attempted to display scene without initialization");
            return;
        }

        splashScene.showSplash(root);
    }

    private static void loadMain() {
        grizzlyScene = new GrizzlyScene();
    }

    private static void displayMain(){
        if (grizzlyScene == null) {
            LoggingUtils.log(Level.SEVERE, "Attempted to display scene without initialization");
            return;
        }

        grizzlyScene.updateInterface(root);
    }

    private void loadCredits() {
        creditsScene = new CreditsScene();

    }

    private static void displayCredits() {
        if (creditsScene == null) {
            LoggingUtils.log(Level.SEVERE, "Attempted to display scene without initialization");
            return;
        }

        creditsScene.showCredits();
    }

    public static void setRoot(GridPane root) {
        SceneManager.root = root;
    }
}
