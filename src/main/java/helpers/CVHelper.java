package helpers;

import com.google.api.client.util.IOUtils;
import exceptions.OpenCvLoadFailureException;
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

    public static void loadLibrary() throws OpenCvLoadFailureException {
        try {
            InputStream in;
            File fileOut;

            String osName = System.getProperty("os.name");
            LoggingUtils.log(Level.INFO, "Detected OS as " + osName);

            String os = osName.toLowerCase();

            //check OS version
            if (os.contains("win")) {

                //check architecture type
                int bitness = Integer.parseInt(System.getProperty("sun.arch.data.model"));
                if (bitness == 32) {
                    LoggingUtils.log(Level.INFO, "32 bit detected");
                    in = CVHelper.class.getResourceAsStream("/opencv/x86/opencv_java343.dll");
                    fileOut = File.createTempFile("lib", ".dll");

                } else if (bitness == 64) {
                    LoggingUtils.log(Level.INFO, "64 bit detected");
                    in = CVHelper.class.getResourceAsStream("/opencv/x64/opencv_java343.dll");
                    fileOut = File.createTempFile("lib", ".dll");

                } else {
                    LoggingUtils.log(Level.INFO, "Unknown bit detected");
                    in = CVHelper.class.getResourceAsStream("/opencv/x86/opencv_java343.dll");
                    fileOut = File.createTempFile("lib", ".dll");

                }
            } else if (os.contains("mac")) {
                LoggingUtils.log(Level.SEVERE, "MAC NOT SUPPORTED YET");
                in = CVHelper.class.getResourceAsStream("/opencv/mac/libopencv_java343.dylib");
                fileOut = File.createTempFile("lib", ".dylib");

            } else {
                throw new OpenCvLoadFailureException("Unsupported OS!");

            }


            OutputStream out = FileUtils.openOutputStream(fileOut);
            IOUtils.copy(in, out);
            in.close();
            out.close();
            System.load(fileOut.toString());

        } catch (Exception e) {
            LoggingUtils.log(Level.SEVERE, e);
            throw new OpenCvLoadFailureException("Failed to load");

        }

    }
}
