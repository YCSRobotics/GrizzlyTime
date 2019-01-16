package helpers;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.logging.*;

public class LoggingUtil {
    public static Logger logger = Logger.getLogger("");

    private static boolean isHandlerSet = false;

    private static FileHandler fh = null;

    public static void log(Level severity, String text) {
        setHandler();
        logger.log(severity, text);
    }

    private static void setHandler() {
        if (!isHandlerSet) {
            System.setProperty("java.util.logging.SimpleFormatter.format",
                    "%4$s: %5$s %n");

            try {
                String currentDate = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());

                File file = new File(Utils.getCurrentDir() + "\\logs\\");

                if (!file.exists()) {
                    file.mkdir();
                }

                fh = new FileHandler(Utils.getCurrentDir() + "\\logs\\log_" + currentDate + ".log");

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
