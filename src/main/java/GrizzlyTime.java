import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.opencv.core.Core;

public class GrizzlyTime extends Application {
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    private ImageProcess processor = new ImageProcess();
    private GrizzlyTimeGUI userInterface = new GrizzlyTimeGUI();

    @Override
    public void start(Stage primaryStage) {
        GridPane root = new GridPane();
        Scene scene = new Scene(root, 600, 600);

        primaryStage.setTitle("GrizzlyTime JavaFX Edition");
        primaryStage.setScene(scene);

        primaryStage.show();

        processor.displayImage(root);
        userInterface.updateOptions(root);

    }
}
