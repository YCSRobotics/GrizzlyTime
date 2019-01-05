package helpers;

import com.google.api.client.util.IOUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

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
            System.out.println("Detected OS: " + osName);

            String os = osName.toLowerCase();

            //check OS version
            if (os.indexOf("win") >= 0) {

                //check architecture type
                int bitness = Integer.parseInt(System.getProperty("sun.arch.data.model"));
                if (bitness == 32) {
                    System.out.println("32 bit detected");
                    in = CVHelper.class.getResourceAsStream("/opencv/x86/opencv_java343.dll");
                    fileOut = File.createTempFile("lib", ".dll");

                } else if (bitness == 64) {
                    System.out.println("64 bit detected");
                    in = CVHelper.class.getResourceAsStream("/opencv/x64/opencv_java343.dll");
                    fileOut = File.createTempFile("lib", ".dll");

                } else {
                    System.out.println("Unknown bit detected - trying with 32 bit");
                    in = CVHelper.class.getResourceAsStream("/opencv/x86/opencv_java343.dll");
                    fileOut = File.createTempFile("lib", ".dll");

                }
            } else if (os.indexOf("mac") >= 0) {
                in = CVHelper.class.getResourceAsStream("/opencv/mac/libopencv_java343.dylib");
                fileOut = File.createTempFile("lib", ".dylib");

            }


            OutputStream out = FileUtils.openOutputStream(fileOut);
            IOUtils.copy(in, out);
            in.close();
            out.close();
            System.load(fileOut.toString());

        } catch (Exception e) {
            throw new RuntimeException("Failed to load opencv native library", e);

        }

    }
}
