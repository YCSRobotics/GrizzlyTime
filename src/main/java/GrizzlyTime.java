import helpers.Constants;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class GrizzlyTime extends Application {
    static{
        CVHelper.loadLibrary();
    }

    private ImageProcess processor = new ImageProcess();
    private GrizzlyTimeGUI userInterface = new GrizzlyTimeGUI();

    @Override
    public void start(Stage primaryStage) {
        GridPane root = new GridPane();
        Scene scene = new Scene(root, 600, 600);

        primaryStage.setTitle("GrizzlyTime JavaFX Edition");
        primaryStage.setScene(scene);
        primaryStage.setResizable(Constants.windowResizable);

        primaryStage.show();

        //process camera frames and read barcode images
        processor.displayImage(root);
        //create UI and logic
        userInterface.updateOptions(root);

    }
}
