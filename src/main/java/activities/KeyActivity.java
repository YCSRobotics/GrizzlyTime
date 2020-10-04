package activities;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import helpers.Constants;

public class KeyActivity {
    /**
     * @author Dalton Smith
     * KeyActivity
     * Manages global keybinds
     */

    public static boolean isFullscreen = false;

    public void setKeyHandlers(Scene scene, Stage stage) {
        //make application fullscreen on f key press
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F10) {
                if (isFullscreen) {
                    stage.setResizable(true);
                    stage.setFullScreen(false);
                    isFullscreen = false;
                    stage.setWidth(Constants.kMainStageWidth);
                    stage.setHeight(Constants.kMainStageHeight);
                    stage.centerOnScreen();
                    stage.setResizable(Constants.kWindowResizable);

                } else {
                    stage.setResizable(true);
                    stage.setFullScreen(true);
                    isFullscreen = true;
                    stage.setResizable(Constants.kWindowResizable);

                }
            }
            else if (event.getCode() == KeyCode.ESCAPE) {
                if (isFullscreen) {
                    stage.setResizable(true);
                    stage.setFullScreen(false);
                    isFullscreen = false;
                    stage.setWidth(Constants.kMainStageWidth);
                    stage.setHeight(Constants.kMainStageHeight);
                    stage.centerOnScreen();
                    stage.setResizable(Constants.kWindowResizable);

                }
            }
        });

    }
}
