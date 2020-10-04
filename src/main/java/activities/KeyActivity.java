package activities;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

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
                    primaryStage.setResizable(true);
                    stage.setFullScreen(false);
                    isFullscreen = false;
                    primaryStage.setResizable(Constants.kWindowResizable);

                } else {
                    primaryStage.setResizable(true);
                    stage.setFullScreen(true);
                    isFullscreen = true;
                    primaryStage.setResizable(Constants.kWindowResizable);

                }
            }
        });

    }
}
