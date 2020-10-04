package activities;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import helpers.Constants;
import javafx.concurrent.Task;

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
            if (event.getCode() == KeyCode.F11) {
                if (isFullscreen) {
                    stage.setResizable(true);
                    stage.setFullScreen(false);
                    isFullscreen = false;
                    stage.setWidth(Constants.kMainStageWidth);
                    stage.setHeight(Constants.kMainStageHeight);
                    stage.centerOnScreen();

                } else {
                    stage.setResizable(true);
                    stage.setFullScreen(true);
                    isFullscreen = true;

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

                }
            }
            Task<Void> wait3 = new Task<Void>() {
                protected Void call() throws Exception {
                    Thread.sleep(500);
                    return null;
                }
            };

            wait3.setOnSucceeded(e -> stage.setResizable(Constants.kWindowResizable));

            new Thread(wait3).start();
            return;
        });

    }
}
