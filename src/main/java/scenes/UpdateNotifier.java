package scenes;

import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import com.sun.javafx.application.HostServicesDelegate;
import databases.JSONHelper;
import helpers.Constants;
import helpers.LoggingUtil;
import helpers.Utils;
import javafx.scene.control.Alert;
import modules.KeyHandlers;

import java.awt.*;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;

public class UpdateNotifier {

    private Utils utils = new Utils();
    private JSONHelper jsonHelper = new JSONHelper();

    private void downloadVersion() throws IOException {

        File file = new File("version.txt");

        if (file.exists()){
            LoggingUtil.log(Level.WARNING, "version.txt already exists");

            if (file.delete()) {
                LoggingUtil.log(Level.INFO, "Delete was successful");
            } else {
                LoggingUtil.log(Level.WARNING, "Delete was unsuccessful");
            }
        }

        BufferedInputStream inputStream = new BufferedInputStream(new URL(Constants.updateUrl).openStream());
        FileOutputStream fileOS = new FileOutputStream(Utils.getCurrentDir()+"\\version.txt");

        byte data[] = new byte[1024];
        int byteContent;
        while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
            fileOS.write(data, 0, byteContent);

        }
    }

    public void checkUpdates() {
        if (!jsonHelper.getKey("updateNotifier").equals("true")) {
            return;
        }

        try {
            downloadVersion();
            LoggingUtil.log(Level.INFO, "Successfully grabbed version");

        } catch (IOException e) {
            LoggingUtil.log(Level.INFO, "Error checking for updates");
            LoggingUtil.log(Level.WARNING, e);

        }

        Scanner scanner;
        try {
            scanner = new Scanner(new File("version.txt"));

        } catch (FileNotFoundException e) {
            LoggingUtil.log(Level.SEVERE, e);
            return;

        }

        String version = "";

        if (scanner.hasNext()) {
            version = scanner.next();
            LoggingUtil.log(Level.INFO, "Updated version is: " + version);
            LoggingUtil.log(Level.INFO, "Current version is: " + Constants.VERSION);

        } else {
            LoggingUtil.log(Level.WARNING, "No version detected in version.txt");
            return;
        }

        if (!version.equals(Constants.VERSION)){
            boolean confirm = utils.createAlert("New Version Available", "An update is available!", "Version " + version + " is available!\n" + Constants.releaseUrl, Alert.AlertType.INFORMATION);

            if (confirm) {
                try {
                    Desktop.getDesktop().browse(new URL(Constants.releaseUrl).toURI());

                } catch (IOException | URISyntaxException e) {
                    LoggingUtil.log(Level.SEVERE, e);

                }
            }

        }

    }
}
