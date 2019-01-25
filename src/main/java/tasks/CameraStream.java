package tasks;

import databases.JSONHelper;
import helpers.AlertUtils;
import helpers.CommonUtils;
import helpers.Constants;
import helpers.LoggingUtils;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
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
import java.io.File;
import java.util.logging.Level;

/***
 * @author Dalton Smith
 * CameraStream
 * Manages grabbing frames from the camera, and reading the bar codes.
 */

public class CameraStream {
    // local class instances
    private final VideoCapture capture = new VideoCapture(0);
    private final UserProcess process = new UserProcess();
    private final JSONHelper parser = new JSONHelper();
    private final AlertUtils alertUtils = new AlertUtils();

    // control flow
    private boolean stopCamera = false;

    // ui elements
    private final GridPane subRoot = new GridPane();
    private final Size sz = new Size(Constants.kCameraWidth, Constants.kCameraHeight);
    private final Mat frame = new Mat();
    private final ImageView currentFrame = new ImageView();

    public void displayImage(GridPane root) {
        startWebCamStream(root);

    }

    //grab frames from camera
    private void startWebCamStream(GridPane root) {
        File errorImage = new File(CommonUtils.getCurrentDir() + "\\images\\error.png");

        LoggingUtils.log(Level.INFO, "Path to fallback: " + CommonUtils.getCurrentDir() + "\\images\\error.png");

        //open the camera
        capture.retrieve(frame);

        //check if camera opened successfully, or is disabled
        if (!capture.isOpened() || parser.getKey("enableCamera").equals("false")) {
            LoggingUtils.log(Level.WARNING, "Camera disabled or unable to be opened.");

            Image image;

            LoggingUtils.log(Level.INFO, "Does custom fallback exist? " + errorImage.exists());
            //custom error image
            if (errorImage.exists()){
                image = new Image(errorImage.toURI().toString());
            } else {
                image = new Image("images/error.png");
            }

            //set image properties
            currentFrame.setImage(image);
            currentFrame.setPreserveRatio(true);
            currentFrame.setFitHeight(Constants.kCameraHeight);
            GridPane.setHalignment(currentFrame, HPos.CENTER);

            subRoot.setAlignment(Pos.CENTER);
            subRoot.add(currentFrame, 0, 0);
            root.add(subRoot, 0, 0);

            //close the camera if successfully opened
            capture.release();

            return;

        }

        //grab frames from the camera
        Runnable frameGrabber = () -> {
            int prevID = 0;

            while (!stopCamera) {

                try {
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
                            if (Integer.parseInt(data) != prevID) {
                                if (process.isUserLoggedIn(data, false)) {
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
                } catch (Exception e) {
                    LoggingUtils.log(Level.SEVERE, e);
                    alertUtils.createAlert("Camera disabled", "Camera disabled", "The camera has been disabled, please restart the application to re-enable");
                    stopCamera = true;

                }

            }

        };

        //start camera thread
        Thread t = new Thread(frameGrabber);
        t.setDaemon(true);
        t.start();

        subRoot.add(currentFrame, 0, 0);
        root.add(subRoot, 0, 0);
    }

}
