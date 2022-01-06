package helpers;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * @author Dalton Smith AlertUtils Various alert utility methods used throughout the application
 *     https://code.makery.ch/blog/javafx-dialogs-official/
 */
public class AlertUtils {

  public static Stage stage = null;

  // create alert
  public boolean createAlert(String title, String header, String content) {

    // ensure that we always show the dialog on the main UI thread
    if (Platform.isFxApplicationThread()) {
      return customDialog(title, header, content);

    } else {

      AtomicBoolean temp = new AtomicBoolean();

      Platform.runLater(() -> temp.set(customDialog(title, header, content)));

      return temp.get();
    }
  }

  // confirm new user registration
  public boolean confirmInput(String message) {
    AtomicBoolean tempBoolean = new AtomicBoolean();
    AtomicBoolean isSet = new AtomicBoolean();

    if (Platform.isFxApplicationThread()) {
      return customDialog("Confirm Login/Logout", "Confirm Login/Logout", message);

    } else {
      Platform.runLater(
          () -> {
            tempBoolean.set(customDialog("Confirm Login/Logout", "Confirm Login/Logout", message));
            isSet.set(true);
          });

      while (!isSet.get()) {
        // wait for the user to confirm the dialog
      }

      Boolean result = tempBoolean.get();
      System.out.println(result);
      return tempBoolean.get();
    }
  }

  private boolean customDialog(String title, String header, String message) {
    // Create the custom dialog.
    Dialog dialog = new Dialog<>();
    dialog.setTitle(title);
    dialog.setHeaderText(header);
    dialog.initOwner(stage);

    dialog.getDialogPane().getStylesheets().add(Constants.kRootStylesheet);
    dialog.getDialogPane().getStyleClass().add("myDialog");

    // Set Custom Icon
    Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
    stage.getIcons().add(new Image(Constants.kApplicationIcon));

    // Set the button types.
    ButtonType confirmButton = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(confirmButton, ButtonType.CANCEL);

    Image image = new Image(Constants.kBearImage);
    ImageView imageView = new ImageView(image);

    imageView.setPreserveRatio(Constants.kBearPreserveRatio);
    imageView.setFitWidth(Constants.kBearImageWidth);

    // Set the icon (must be included in the project).
    dialog.setGraphic(imageView);

    // Create the username and password labels and fields.
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setId("customDialog");

    Text text = new Text(message);
    text.setWrappingWidth(Constants.kWordWrapWidth);

    grid.add(text, 0, 0);

    dialog.getDialogPane().setContent(grid);

    Optional result = dialog.showAndWait();

    if (!result.isPresent()) {
      return false;
    }

    ButtonType buttonInfo = (ButtonType) result.get();

    return buttonInfo.getButtonData() == ButtonBar.ButtonData.OK_DONE;
  }

  public ArrayList<String> getUserInfo() {

    // run on FX application thread
    if (Platform.isFxApplicationThread()) {
      return showAuthDialog();

    } else {
      AtomicReference<ArrayList<String>> temp = new AtomicReference<>();
      AtomicReference<Boolean> isSet = new AtomicReference<>();

      isSet.set(false);
      Platform.runLater(
          () -> {
            temp.set(showAuthDialog());
            isSet.set(true);
          });

      // wait until the user has finished dialog
      while (!isSet.get()) {}

      return temp.get();
    }
  }

  public String showSpreadsheetDialog() {
    // Create the custom dialog.
    Dialog dialog = new Dialog<>();

    dialog.initOwner(stage);

    // Set Custom Icon
    Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
    stage.getIcons().add(new Image(Constants.kApplicationIcon));

    dialog.getDialogPane().getStylesheets().add(Constants.kRootStylesheet);
    dialog.getDialogPane().getStyleClass().add("accountDialog");

    dialog.setTitle("Spreadsheet Verification!");
    dialog.setHeaderText("Please input your spreadsheet URL\n and press confirm!");

    // Set the button types.
    ButtonType loginButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

    // Create the username and password labels and fields.
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 10, 10, 10));
    grid.setAlignment(Pos.CENTER);
    grid.setId("accountGrid");

    TextField sheet = new TextField("");
    sheet.setMinWidth(400);

    grid.add(sheet, 0, 0);

    dialog.getDialogPane().setContent(grid);

    Node button = dialog.getDialogPane().lookupButton(loginButtonType);

    button.setDisable(true);

    sheet
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue.contains("/d/")) {
                button.setDisable(false);
              } else {
                button.setDisable(true);
              }
            });

    Platform.runLater(sheet::requestFocus);

    Optional result = dialog.showAndWait();

    ButtonType resultButtonType = (ButtonType) result.get();

    String spreadsheet = "";
    if (resultButtonType.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
      spreadsheet = sheet.getText().split("/d/")[1].split("/")[0];
      LoggingUtils.log(Level.INFO, "Using ID of " + spreadsheet);
    } else {
      CommonUtils.exitApplication();
    }

    return spreadsheet;
  }

  private ArrayList<String> showAuthDialog() {
    // Create the custom dialog.
    Dialog dialog = new Dialog<>();

    dialog.initOwner(stage);

    // Set Custom Icon
    Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
    stage.getIcons().add(new Image(Constants.kApplicationIcon));

    dialog.getDialogPane().getStylesheets().add(Constants.kRootStylesheet);
    dialog.getDialogPane().getStyleClass().add("accountDialog");

    dialog.setTitle("New User Detected");
    dialog.setHeaderText("Please complete user registration!");

    // Set the button types.
    ButtonType loginButtonType = new ButtonType("Create Account", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

    // Create the username and password labels and fields.
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 10, 10, 10));
    grid.setAlignment(Pos.CENTER);
    grid.setId("accountGrid");

    TextField firstName = new TextField("");
    // GridPane.setHgrow(firstName, Priority.ALWAYS);
    TextField lastName = new TextField("");

    TextField email = new TextField("");

    firstName.setId("textField");
    lastName.setId("textField");

    firstName.setPromptText("");
    lastName.setPromptText("");

    email.setPromptText("");

    email.setId("textField");

    ToggleGroup studentRadioGroup = new ToggleGroup();

    RadioButton mentorRadio = new RadioButton("Mentor");
    RadioButton studentRadio = new RadioButton("Student");

    ToggleGroup genderRadioGroup = new ToggleGroup();

    RadioButton maleRadio = new RadioButton("Male");
    RadioButton otherRadio = new RadioButton("Other");
    RadioButton femaleRadio = new RadioButton("Female");

    otherRadio.fire();

    studentRadio.fire();

    mentorRadio.setToggleGroup(studentRadioGroup);
    studentRadio.setToggleGroup(studentRadioGroup);

    GridPane genderPane = new GridPane();

    maleRadio.setToggleGroup(genderRadioGroup);
    femaleRadio.setToggleGroup(genderRadioGroup);
    otherRadio.setToggleGroup(genderRadioGroup);

    genderPane.add(maleRadio, 0, 0);
    genderPane.add(femaleRadio, 1, 0);
    genderPane.add(otherRadio, 2, 0);

    grid.add(new Label("First Name:"), 0, 0);
    grid.add(firstName, 1, 0);
    grid.add(new Label("Last Name:"), 0, 1);
    grid.add(lastName, 1, 1);
    grid.add(new Label("Email:"), 0, 2);
    grid.add(email, 1, 2);

    GridPane.setColumnSpan(genderPane, 2);
    grid.add(genderPane, 0, 3);

    GridPane.setHalignment(grid, HPos.CENTER);

    GridPane.setHalignment(studentRadio, HPos.CENTER);
    GridPane.setHalignment(studentRadio, HPos.CENTER);

    grid.add(studentRadio, 0, 4);
    grid.add(mentorRadio, 1, 4);

    dialog.getDialogPane().setContent(grid);

    Node button = dialog.getDialogPane().lookupButton(loginButtonType);

    button.setDisable(true);

    email
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              Pattern regex =
                  Pattern.compile(
                      "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
              Matcher matcher = regex.matcher(newValue);

              if (matcher.matches()) {
                button.setDisable(false);
              }
            });

    // Request focus on the firstname field by default.
    Platform.runLater(firstName::requestFocus);

    Optional result = dialog.showAndWait();

    ArrayList<String> data = new ArrayList<>();

    ButtonType resultButtonType = (ButtonType) result.get();

    if (resultButtonType.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
      data.add("TRUE");
      data.add(firstName.getText());
      data.add(lastName.getText());
      data.add(email.getText());

      if (maleRadio.isSelected()) {
        data.add("MALE");
      } else if (femaleRadio.isSelected()) {
        data.add("FEMALE");
      } else {
        data.add("OTHER");
      }

      data.add(mentorRadio.isSelected() ? "MENTOR" : "STUDENT");

    } else {
      data.add("FALSE");
    }

    return data;
  }
}
