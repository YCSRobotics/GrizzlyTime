package modules;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class SplashScene {
    /**
     * @author Dalton Smith
     * SplashScene
     * Creates our splash image
     */

    private Image splash = new Image("images/splash.jpg");
    private ImageView splashViewer = new ImageView(splash);

    public void showSplash(GridPane root) {
        root.add(splashViewer, 0, 0);
    }

}
