package modules;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class KeyHandlers {
    /**
     * @author Dalton Smith
     * KeyHandlers
     * Manages global keybinds
     */

    public static boolean isFullscreen = false;

    public void setKeyHandlers(Scene scene, Stage stage) {
        //make application fullscreen on f key press
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F11) {
                if (isFullscreen) {
                    stage.setFullScreen(false);
                    isFullscreen = false;

                } else {
                    stage.setFullScreen(true);
                    isFullscreen = true;

                }
            }
        });

    }
}
