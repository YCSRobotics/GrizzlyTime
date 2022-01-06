import javafx.application.Application;
import javafx.application.Preloader;
import javafx.stage.Stage;

/**
     * This is the true main class of GrizzlyTime, it launches GrizzlyTime
     * through some fancy black magic to get JFX working on Java 11
     */
public class Main extends Preloader {
    public static void main(String[] args){
        launch(GrizzlyTime.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO Auto-generated method stub
        
    }
}
