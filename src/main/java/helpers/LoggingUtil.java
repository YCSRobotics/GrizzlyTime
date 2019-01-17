package helpers;

import java.io.*;
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

    public static void log(Level severity, Throwable error){
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
