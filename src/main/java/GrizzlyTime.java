import exceptions.OpenCvLoadFailureException;
import helpers.CVHelper;
import helpers.Constants;
import helpers.LoggingUtil;
import helpers.Utils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import modules.CameraStream;
import modules.KeyHandlers;
import scenes.SplashScene;
import modules.UserInterface;
import scenes.UpdateNotifier;

import java.io.File;
import java.util.logging.Level;

public class GrizzlyTime extends Application {
    /**
     * @author Dalton Smith
     * GrizzlyTime main application class
     * This class calls our various modules and starts the JavaFX application
     */

    //only initializations that don't have freezing constructor instances should be placed here
    private SplashScene splash = new SplashScene();
    private KeyHandlers keyHandlers = new KeyHandlers();
    private Utils utils = new Utils();
    private UpdateNotifier updater = new UpdateNotifier();

    @Override
    public void start(Stage primaryStage) {

        Thread.setDefaultUncaughtExceptionHandler(GrizzlyTime::GlobalExceptionHandler);
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

        //initialize our modules and interface objects AFTER
        //we display application
        UserInterface userInterface = new UserInterface();
        CameraStream processor = null;

        if(System.getProperty("os.name").toLowerCase().contains("mac")){
            utils.createAlert("Unsupported", "Mac OS not supported", "Mac OS is not supported at this time, running in experimental mode", Alert.AlertType.ERROR);
        } else {
            //copy OpenCV dlls outside jar
            try {
                CVHelper.loadLibrary();
                processor = new CameraStream();

            } catch (OpenCvLoadFailureException e) {
                //do nothing

            }
        }

        Utils.stage = primaryStage;

        //remove splash screen on load
        root.getChildren().clear();
        primaryStage.setWidth(608);
        primaryStage.setHeight(630);
        primaryStage.centerOnScreen();

        //add our global key handlers
        keyHandlers.setKeyHandlers(scene, primaryStage);

        //process camera frames and read barcode images
        if (processor == null) {
            //don't load
        } else {
            processor.displayImage(root);
        }

        //check for updates
        updater.checkUpdates();

        //create UI and logic
        userInterface.updateInterface(root);

    }

    private static void GlobalExceptionHandler(Thread thread, Throwable throwable) {
        LoggingUtil.log(Level.SEVERE, throwable);
        Utils.exitApplication();
    }
}
