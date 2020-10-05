package activities;

import javafx.concurrent.Task;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.Scene;
import javafx.stage.Stage;
import helpers.Constants;
import scenes.GrizzlyScene;



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
                GrizzlyScene.studentIDBox.requestFocus();
            }
            return;
        });
    }
}
