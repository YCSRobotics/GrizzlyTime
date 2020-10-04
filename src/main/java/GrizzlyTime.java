import activities.KeyActivity;
import activities.LocalDbActivity;
import helpers.AlertUtils;
import helpers.CommonUtils;
import helpers.Constants;
import helpers.LoggingUtils;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import notifiers.UpdateNotifier;
import scenes.SceneManager;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import java.io.File;
import java.util.logging.Level;

public class GrizzlyTime extends Application {
    /**
     * @author Dalton Smith
     * GrizzlyTime main application class
     * This class calls our various activities and starts the JavaFX application
     */

    //only initializations that don't have freezing constructor instances should be placed here
    private KeyActivity keyHandlers = new KeyActivity();
    private UpdateNotifier updater = new UpdateNotifier();

    private LocalDbActivity dbActivity = new LocalDbActivity();

    @Override
    public void start(Stage primaryStage) {
        //set application in commonutils to access hostservices
        CommonUtils.application = this;

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> globalExceptionHandler(throwable));

        dbActivity.updateLocalDb();

        //check if custom icon
        File file = new File(CommonUtils.getCurrentDir() + File.separator + "images" + File.separator + "icon.png");
        File stylesheet = new File(CommonUtils.getCurrentDir() + File.separator + "styles" + File.separator + "style.css");
        if (file.exists()) {
            primaryStage.getIcons().add(new Image(file.toURI().toString()));

        } else {
            primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream(Constants.kApplicationIcon)));

        }

        GridPane root = new GridPane();
        SceneManager.setRoot(root);

        Scene scene = new Scene(root, Constants.kSplashWidth, Constants.kSplashHeight);

        scene.getStylesheets().add(Constants.kRootStylesheet);

        if (stylesheet.exists()) {
            scene.getStylesheets().add("file:///" + stylesheet.getAbsolutePath().replace("\\", "/"));
            LoggingUtils.log(Level.INFO, "Loaded custom stylesheet!");
        }

        root.setId("main");
        root.setAlignment(Pos.CENTER);

        String applicationName = LocalDbActivity.kApplicationName;

        if (applicationName.equals("")) {
            primaryStage.setTitle(Constants.kApplicationName);

        } else {
            applicationName = applicationName.replaceAll("_", " ");
            primaryStage.setTitle(applicationName);
        }

        AlertUtils.stage = primaryStage;
        
        primaryStage.setScene(scene);
        primaryStage.setResizable(Constants.kWindowResizable);

        //show our splash
        SceneManager.updateScene(Constants.kSplashSceneState);
        primaryStage.show();
        primaryStage.requestFocus();

        primaryStage.centerOnScreen();

        //add our global key handlers
        keyHandlers.setKeyHandlers(scene, primaryStage);

        

        // delay for splash screen
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
                return null;
            }
        };
        sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {

                // create UI and logic
                primaryStage.setResizable(true);

                //check for updates
                updater.checkUpdates();

                SceneManager.updateScene(Constants.kLoadMainScene);
                primaryStage.setWidth(Constants.kMainStageWidth);
                primaryStage.setHeight(Constants.kMainStageHeight);
                SceneManager.updateScene(Constants.kMainSceneState);
                primaryStage.centerOnScreen();
                primaryStage.setResizable(Constants.kWindowResizable);

            }
        });
        new Thread(sleeper).start();

    }

    //catch uncaught exceptions
    private static void globalExceptionHandler(Throwable throwable) {
        LoggingUtils.log(Level.SEVERE, throwable);
        CommonUtils.exitApplication();
    }
}
