package helpers;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/** @author Dalton Smith LoggingUtils Handles interaction with the log file. */
public class LoggingUtils {
  private static Logger logger = Logger.getLogger("");

  private static boolean isHandlerSet = false;

  public static void log(Level severity, String text) {
    setHandler();
    logger.log(severity, text);
  }

  // log full stacktrace
  public static void log(Level severity, Throwable error) {
    ByteArrayOutputStream out1 = new ByteArrayOutputStream();
    PrintStream out2 = new PrintStream(out1);
    error.printStackTrace(out2);

    String message;
    try {
      message = out1.toString("UTF8");
    } catch (UnsupportedEncodingException e) {
      message = error.getMessage();
    }

    setHandler();
    logger.log(severity, message);
  }

  private static void setHandler() {
    if (!isHandlerSet) {
      System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s %n");

      FileHandler fh;

      try {
        String currentDate =
            new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());

        File file =
            new File(CommonUtils.getCurrentDir() + File.separator + "logs" + File.separator);

        if (!file.exists()) {
          boolean success = file.mkdir();
          LoggingUtils.log(Level.INFO, "Directory creation success? " + success);
        }

        File logFile =
            new File(
                CommonUtils.getCurrentDir()
                    + File.separator
                    + "logs"
                    + File.separator
                    + "log_"
                    + currentDate
                    + ".log");

        int i = 0;
        while (logFile.exists()) {
          logFile =
              new File(
                  CommonUtils.getCurrentDir()
                      + File.separator
                      + "logs"
                      + File.separator
                      + "log_"
                      + i
                      + "_"
                      + currentDate
                      + ".log");
          i++;
        }

        String logFilePath = logFile.getAbsolutePath();

        fh = new FileHandler(logFilePath);

      } catch (IOException e) {
        e.printStackTrace();
        return;
      }

      logger.addHandler(fh);
      SimpleFormatter format = new SimpleFormatter();
      fh.setFormatter(format);
    }

    isHandlerSet = true;
  }
}
