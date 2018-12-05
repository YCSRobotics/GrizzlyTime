import com.google.api.client.util.IOUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public class CVHelper {
    public static void loadLibrary() {
        try {
            InputStream in = null;
            File fileOut = null;
            String osName = System.getProperty("os.name");
            System.out.println(GrizzlyTime.class + osName);
            if (osName.startsWith("Windows")) {
                int bitness = Integer.parseInt(System.getProperty("sun.arch.data.model"));
                if (bitness == 32) {
                    System.out.println("32 bit detected");
                    in = GrizzlyTime.class.getResourceAsStream("/opencv/x86/opencv_java343.dll");
                    fileOut = File.createTempFile("lib", ".dll");
                } else if (bitness == 64) {
                    System.out.println("64 bit detected");
                    in = GrizzlyTime.class.getResourceAsStream("/opencv/x64/opencv_java343.dll");
                    fileOut = File.createTempFile("lib", ".dll");
                } else {
                    System.out.println("Unknown bit detected - trying with 32 bit");
                    in = GrizzlyTime.class.getResourceAsStream("/opencv/x86/opencv_java343.dll");
                    fileOut = File.createTempFile("lib", ".dll");
                }
            } else if (osName.equals("Mac OS X")) {
                in = GrizzlyTime.class.getResourceAsStream("/opencv/mac/libopencv_java343.dylib");
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
