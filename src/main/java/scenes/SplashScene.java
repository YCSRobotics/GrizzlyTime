package scenes;

import helpers.CommonUtils;
import helpers.Constants;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.File;

public class SplashScene {
    /**
     * @author Dalton Smith
     * SplashScene
     * Creates our splash image
     */

    public void showSplash(GridPane root) {

        Image splash;
        File file = new File(CommonUtils.getCurrentDir() + File.separator + "images" + File.separator + "splash.png");

        //check for custom splash
        if (file.exists()) {
            splash = new Image(file.toURI().toString());

        } else {
            splash = new Image(Constants.kSplashImage);
        }

        ImageView splashViewer = new ImageView(splash);

        root.add(splashViewer, 0, 0);
    }

}
