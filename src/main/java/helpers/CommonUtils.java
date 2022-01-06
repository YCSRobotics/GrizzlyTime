package helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.logging.Level;
import javafx.application.Application;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

// methods in this class should not be dependent on anything relative
public class CommonUtils {
  /**
   * @author Dalton Smith CommonUtils Various utility methods used throughout the application
   *     https://code.makery.ch/blog/javafx-dialogs-official/
   */
  public static Application application;

  public static String getCurrentDir() {
    return System.getProperty("user.dir");
  }

  public static String readFile(String filePath) throws FileNotFoundException {
    File file = new File(filePath);
    Scanner sc = new Scanner(file);

    StringBuilder result = new StringBuilder();

    // read the entire json
    while (sc.hasNext()) {
      result.append(sc.next());
    }

    return result.toString();
  }

  public void playDing() {

    Media sound;
    try {
      sound = new Media(getClass().getResource("/sounds/ding.wav").toURI().toString());

    } catch (URISyntaxException e) {
      LoggingUtils.log(Level.SEVERE, e);
      return;
    }

    MediaPlayer mediaPlayer = new MediaPlayer(sound);
    mediaPlayer.play();
  }

  public static void exitApplication() {
    System.exit(1);
  }
}
