import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

public class ImageProcess {
    VideoCapture capture = new VideoCapture(0);
    private boolean stopCamera = false;
    ImageView capturedImage = null;

    public void displayImage(Stage primaryStage, GridPane root) {
        startWebCamStream(root);

    }

    private void startWebCamStream(GridPane root) {
        GridPane subRoot = new GridPane();
        stopCamera  = false;
        Size sz = new Size(600,400);

        Mat frame = new Mat();
        capture.read(frame);

        ImageView currentFrame = new ImageView();

        if (!capture.isOpened()) {
            System.out.println("Error opening camera");
        }

        Runnable frameGrabber = new Runnable() {
            @Override
            public void run() {
                while (!stopCamera) {
                    capture.read(frame);
                    Imgproc.resize(frame, frame, sz);
                    Core.flip(frame, frame, 1);
                    MatOfByte buffer = new MatOfByte();
                    Imgcodecs.imencode(".png", frame, buffer);
                    Image imageToShow = new Image(new ByteArrayInputStream(buffer.toArray()));

                    Platform.runLater(new Runnable() {
                        @Override public void run() {

                            currentFrame.setImage(imageToShow);
                        }

                    });
                }
            }
        };

        Thread t = new Thread(frameGrabber);
        t.setDaemon(true);
        t.start();

        subRoot.add(currentFrame, 0, 0);
        root.add(subRoot, 0, 0);
    }

}
