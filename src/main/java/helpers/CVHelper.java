package helpers;

import com.google.api.client.util.IOUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

public class CVHelper {
    /**
     * @author Dalton Smith, alistair
     * CVHelper
     * Copies opencv bindings outside jar
     * https://stackoverflow.com/questions/18269570/how-to-package-opencv-java-in-a-jar
     */

    public static void loadLibrary() {
        try {
            InputStream in = null;
            File fileOut = null;
            String osName = System.getProperty("os.name");
            LoggingUtil.log(Level.INFO, "Detected OS as " +osName);

            String os = osName.toLowerCase();

            //check OS version
            if (os.indexOf("win") >= 0) {

                //check architecture type
                int bitness = Integer.parseInt(System.getProperty("sun.arch.data.model"));
                if (bitness == 32) {
                    LoggingUtil.log(Level.INFO, "32 bit detected");
                    in = CVHelper.class.getResourceAsStream("/opencv/x86/opencv_java343.dll");
                    fileOut = File.createTempFile("lib", ".dll");

                } else if (bitness == 64) {
                    LoggingUtil.log(Level.INFO, "64 bit detected");
                    in = CVHelper.class.getResourceAsStream("/opencv/x64/opencv_java343.dll");
                    fileOut = File.createTempFile("lib", ".dll");

                } else {
                    LoggingUtil.log(Level.INFO, "Unknown bit detected");
                    in = CVHelper.class.getResourceAsStream("/opencv/x86/opencv_java343.dll");
                    fileOut = File.createTempFile("lib", ".dll");

                }
            } else if (os.indexOf("mac") >= 0) {
                LoggingUtil.log(Level.SEVERE, "MAC NOT SUPPORTED YET");
                in = CVHelper.class.getResourceAsStream("/opencv/mac/libopencv_java343.dylib");
                fileOut = File.createTempFile("lib", ".dylib");

            }


            OutputStream out = FileUtils.openOutputStream(fileOut);
            IOUtils.copy(in, out);
            in.close();
            out.close();
            System.load(fileOut.toString());

        } catch (Exception e) {
            LoggingUtil.log(Level.SEVERE, e.getMessage());
            throw new RuntimeException("Failed to load opencv native library", e);

        }

    }
}
