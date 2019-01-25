package modules;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;

class QRReader {
    /**
     * @author Dalton Smith
     * QRReader
     * Decodes barcodes from OpenCV image
     */

    public static String decodeQRCode(Image qrCodeimage)  {
        BufferedImage image = SwingFXUtils.fromFXImage(qrCodeimage, null);
        LuminanceSource source = new BufferedImageLuminanceSource(image);

        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        //attempt to grab barcode from image
        try {
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();

        } catch (NotFoundException e) {
            return null;

        }

    }

}
