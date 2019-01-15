package modules;

import databases.JSONHelper;
import helpers.Constants;
import helpers.Utils;
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
import java.io.File;

/***
 * @author Dalton Smith
 * CameraStream
 * Manages grabbing frames from the camera, and reading the bar codes.
 */

public class CameraStream {
    private VideoCapture capture = new VideoCapture(0);
    private boolean stopCamera = false;
    private UserProcess process = new UserProcess();
    private JSONHelper parser = new JSONHelper();

    public void displayImage(GridPane root) {
        startWebCamStream(root);

    }

    //grab frames from camera
    private void startWebCamStream(GridPane root) {
        GridPane subRoot = new GridPane();
        Size sz = new Size(Constants.cameraWidth, Constants.cameraHeight);
        Mat frame = new Mat();
        ImageView currentFrame = new ImageView();

        File errorImage = new File(Utils.getCurrentDir() + "\\images\\error.png");

        System.out.println("Path to fallback: " + Utils.getCurrentDir() + "\\images\\error.png");
        //open the camera
        capture.retrieve(frame);

        //check if camera opened successfully, or is disabled
        if (!capture.isOpened() || parser.getKey("enableCamera").equals("false")) {
            System.out.println("Error opening camera");

            Image image;

            System.out.println("Does custom fallback exist? " +  errorImage.exists());
            //custom error image
            if (errorImage.exists()){
                image = new Image(errorImage.toURI().toString());
            } else {
                image = new Image("images/error.png");
            }

            //set image properties
            currentFrame.setImage(image);
            currentFrame.setFitHeight(Constants.cameraHeight);
            currentFrame.setFitWidth(Constants.cameraWidth);

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
                            if (!(Integer.parseInt(data) == prevID)) {
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
                    e.printStackTrace();

                    //attempt to reconnect the camera
                    capture.release();
                    capture.retrieve(frame);

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                        break;
                    }
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
