import helpers.CVHelper;
import helpers.Constants;
import helpers.Utils;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import modules.CameraStream;
import modules.KeyHandlers;
import modules.SplashScene;
import modules.UserInterface;

import java.io.File;

public class GrizzlyTime extends Application {
    /**
     * @author Dalton Smith
     * GrizzlyTime main application class
     * This class calls our various modules and starts the JavaFX application
     */

    //only initializations that don't have freezing constructor instances should be placed here
    private SplashScene splash = new SplashScene();
    private KeyHandlers keyHandlers = new KeyHandlers();

    @Override
    public void start(Stage primaryStage) {
        //grab our application icon from stream

        //check if custom icon
        File file = new File(Utils.getCurrentDir() + "\\images\\icon.png");

        if (file.exists()) {
            primaryStage.getIcons().add(new Image(file.toURI().toString()));

        } else {
            primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("images/icon.png")));

        }

        GridPane root = new GridPane();

        Scene scene = new Scene(root, Constants.splashWidth, Constants.splashHeight);
        scene.getStylesheets().add("styles/root.css");

        root.setId("main");
        root.setAlignment(Pos.CENTER);

        primaryStage.setTitle("GrizzlyTime JavaFX Edition");
        primaryStage.setScene(scene);
        primaryStage.setResizable(Constants.windowResizable);

        //show our splash
        splash.showSplash(root);
        primaryStage.show();
        primaryStage.requestFocus();

        //copy OpenCV dlls outside jar
        CVHelper.loadLibrary();

        //initialize our modules and interface objects AFTER
        //we display application
        CameraStream processor = new CameraStream();
        UserInterface userInterface = new UserInterface();

        //remove splash screen on load
        root.getChildren().clear();
        primaryStage.setWidth(608);
        primaryStage.setHeight(610);
        primaryStage.centerOnScreen();

        //add our global key handlers
        keyHandlers.setKeyHandlers(scene, primaryStage);

        //process camera frames and read barcode images
        processor.displayImage(root);

        //create UI and logic
        userInterface.updateInterface(root);

    }
}
