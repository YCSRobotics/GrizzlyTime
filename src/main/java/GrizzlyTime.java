import helpers.Constants;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class GrizzlyTime extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("images/icon.png")));
        GridPane root = new GridPane();

        Scene scene = new Scene(root, 600, 600);
        scene.getStylesheets().add("styles/root.css");

        root.setId("main");

        primaryStage.setTitle("GrizzlyTime JavaFX Edition");
        primaryStage.setScene(scene);
        primaryStage.setResizable(Constants.windowResizable);

        primaryStage.show();

        CVHelper.loadLibrary();

        ImageProcess processor = new ImageProcess();
        GrizzlyTimeGUI userInterface = new GrizzlyTimeGUI();
        //process camera frames and read barcode images
        processor.displayImage(root);
        //create UI and logic
        userInterface.updateOptions(root);

    }
}
