import com.sun.javafx.application.LauncherImpl;

    /**
     * This is the true main class of GrizzlyTime, it launches GrizzlyTime
     * through some fancy black magic to get JFX working on Java 11
     */
public class Main {
    public static void main(String[] args){
        LauncherImpl.launchApplication(GrizzlyTime.class, args);
    }
}
