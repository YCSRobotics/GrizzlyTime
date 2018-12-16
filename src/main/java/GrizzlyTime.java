import helpers.CVHelper;
import helpers.Constants;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import modules.CameraStream;
import modules.UserInterface;

public class GrizzlyTime extends Application {
    /**
     * @author Dalton Smith
     * GrizzlyTime main application class
     * This class calls our various modules and starts the JavaFX application
     */

    @Override
    public void start(Stage primaryStage) {
        //grab our application icon from stream
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("images/icon.png")));
        GridPane root = new GridPane();

        Scene scene = new Scene(root, 600, 600);
        scene.getStylesheets().add("styles/root.css");

        root.setId("main");

        primaryStage.setTitle("GrizzlyTime JavaFX Edition");
        primaryStage.setScene(scene);
        primaryStage.setResizable(Constants.windowResizable);

        primaryStage.show();

        //copy OpenCV dlls outside jar
        CVHelper.loadLibrary();

        //initialize our modules and interface objects AFTER
        //we display application
        //TODO Add splash screen and thread listeners
        CameraStream processor = new CameraStream();
        UserInterface userInterface = new UserInterface();

        //process camera frames and read barcode images
        processor.displayImage(root);

        //create UI and logic
        userInterface.updateInterface(root);

    }
}
