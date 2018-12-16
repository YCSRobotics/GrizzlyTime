import databases.JSONHelper;
import helpers.Constants;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.io.ByteArrayInputStream;

/***
 * Description: Manages grabbing frames from the camera, and reading the bar codes.
 */

class ImageProcess {
    private VideoCapture capture = new VideoCapture(0);
    private boolean stopCamera = false;
    private UserProcess process = new UserProcess();
    private JSONHelper parser = new JSONHelper();

    void displayImage(GridPane root) {
        startWebCamStream(root);

    }

    //grab frames from camera
    private void startWebCamStream(GridPane root) {
        GridPane subRoot = new GridPane();
        stopCamera  = false;
        Size sz = new Size(Constants.cameraWidth, Constants.cameraHeight);

        Mat frame = new Mat();
        capture.read(frame);

        ImageView currentFrame = new ImageView();

        //check if camera opened successfully, or is disabled
        if (!capture.isOpened() || parser.getKey("enableCamera").equals("false")) {
            System.out.println("Error opening camera");
            Image image = new Image("images/error.png");
            currentFrame.setImage(image);
            currentFrame.setFitHeight(Constants.cameraHeight);
            currentFrame.setFitWidth(Constants.cameraWidth);
            subRoot.add(currentFrame, 0, 0);
            root.add(subRoot, 0, 0);

            return;

        }

        //grab frames from the camera
        Runnable frameGrabber = () -> {
            int prevID = 0;

            while (!stopCamera) {
                //read frames from teh camera and display them
                capture.read(frame);
                Imgproc.resize(frame, frame, sz);
                Core.flip(frame, frame, 1);
                MatOfByte buffer = new MatOfByte();
                Imgcodecs.imencode(".png", frame, buffer);
                Image imageToShow = new Image(new ByteArrayInputStream(buffer.toArray()));

                //convert to BufferedReader and decode
                String data = QRReader.decodeQRCode(imageToShow);

                //verify that we have data
                if (data != null) {
                    try {
                        if (!(Integer.parseInt(data) == prevID)) {
                            if (process.isUserLoggedIn(data)) {
                                process.logoutUser(data);
                            } else {
                                process.loginUser(data);
                            }

                            prevID = Integer.valueOf(data);
                        }
                    } catch (Exception e) {
                        //do nothing
                        continue;
                    }
                }

                Platform.runLater(() -> currentFrame.setImage(imageToShow));
            }
        };

        Thread t = new Thread(frameGrabber);
        t.setDaemon(true);
        t.start();

        subRoot.add(currentFrame, 0, 0);
        root.add(subRoot, 0, 0);
    }

}
