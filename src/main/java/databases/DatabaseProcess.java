package databases;

import activities.LocalDbActivity;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import helpers.AlertUtils;
import helpers.CommonUtils;
import helpers.Constants;
import helpers.LoggingUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import javafx.application.HostServices;

public class DatabaseProcess {
  /**
   * @author Dalton Smith DatabaseProcess Manages low level google sheets handling from API Uses the
   *     power of black magic to manage the google sheet Based on Java QuickStart
   */
  private static final String APPLICATION_NAME = "GrizzlyTime JavaFX Edition";

  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
  private static final String TOKENS_DIRECTORY_PATH = "tokens";

  private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
  private static final String CREDENTIALS_FILE_PATH = "/credentials/credentials.json";

  private AlertUtils util = new AlertUtils();

  private static final String spreadsheet = LocalDbActivity.kSheetId;

  private static final String mainPage = "Current";
  private static final String logPage = "Date Log";
  private static final String regPage = "Student Registration";

  public static boolean worksheetIsEmpty = false;

  // based upon the Java Google Sheets quickstart
  // settings google sheets logging level to SEVERE only
  private static <LocalServerReceiver> Credential getCredentials(
      final NetHttpTransport HTTP_TRANSPORT) throws IOException {
    // set google logging level to severe due to permissions bug, see
    // https://github.com/googleapis/google-http-java-client/issues/315
    java.util.logging.Logger.getLogger(FileDataStoreFactory.class.getName()).setLevel(Level.SEVERE);

    // Load client secrets.
    InputStream in = DatabaseProcess.class.getResourceAsStream(CREDENTIALS_FILE_PATH);

    if (in == null) {
      LoggingUtils.log(
          Level.SEVERE,
          "Credentials file was not loaded, check that the credentials file is in the resources directory!");
      CommonUtils.exitApplication();
    }

    GoogleClientSecrets clientSecrets =
        GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

    // Build flow and trigger user authorization request.
    GoogleAuthorizationCodeFlow flow =
        new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build();

    return new AuthorizationCodeInstalledApp(
            flow,
            new com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver(),
            new AuthorizationCodeInstalledApp.Browser() {
              @Override
              public void browse(String url) {
                LoggingUtils.log(
                    Level.INFO, "Or navigate to " + url + " to authorize the application.");
                // fix for java8 launching on linux because weird shit
                HostServices hostServices = CommonUtils.application.getHostServices();
                try {
                  hostServices.showDocument(url);
                } catch (NullPointerException e) {
                  LoggingUtils.log(
                      Level.INFO,
                      "HostServices failed, please navigate to "
                          + url
                          + " to authorize the application.");
                }
              }
            })
        .authorize("user");
  }

  // return a list of rows and columns of a specified sheet
  public List<List<Object>> returnWorksheetData(int page) {

    try {
      String range = getPage(page);

      final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
      Sheets service =
          new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
              .setApplicationName(APPLICATION_NAME)
              .build();

      return getDataFromSheet(service, range);

    } catch (NoRouteToHostException | UnknownHostException e) {
      LoggingUtils.log(Level.SEVERE, e);
      util.createAlert(
          "ERROR",
          "No Network Connection",
          "Please confirm your network connection and try again.");

      CommonUtils.exitApplication();
      return null;

    } catch (GeneralSecurityException e2) {
      LoggingUtils.log(Level.SEVERE, e2);
      util.createAlert(
          "ERROR", "Invalid Credentials", "Please delete the 'tokens' directory and try again!");

      CommonUtils.exitApplication();
      return null;

    } catch (GoogleJsonResponseException e) {
      LoggingUtils.log(Level.SEVERE, e.getDetails().getMessage());

      try {
        if (e.getDetails().getMessage().contains("Unable to parse range")) {
          LoggingUtils.log(Level.WARNING, "Attempting to create our sheets");

          final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
          Sheets service =
              new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                  .setApplicationName(APPLICATION_NAME)
                  .build();

          ArrayList<Request> requests = new ArrayList<>();

          // add our sheets to the list of sheets to be processed
          if (e.getDetails().getMessage().contains("Current")) {
            requests.add(
                new Request()
                    .setAddSheet(
                        new AddSheetRequest()
                            .setProperties(new SheetProperties().setTitle("Current"))));
            LoggingUtils.log(Level.INFO, "Creating Current Sheet");
          }

          if (e.getDetails().getMessage().contains("Date Log")) {
            requests.add(
                new Request()
                    .setAddSheet(
                        new AddSheetRequest()
                            .setProperties(new SheetProperties().setTitle("Date Log"))));
            LoggingUtils.log(Level.INFO, "Creating Date Log Sheet");
          }

          BatchUpdateSpreadsheetRequest body =
              new BatchUpdateSpreadsheetRequest().setRequests(requests);

          BatchUpdateSpreadsheetResponse updateResponse =
              service.spreadsheets().batchUpdate(spreadsheet, body).execute();

          LoggingUtils.log(Level.INFO, updateResponse.getReplies().toString());

          String range = getPage(page);

          return getDataFromSheet(service, range);
        }

        LoggingUtils.log(Level.SEVERE, "Unknown exception");
        LoggingUtils.log(Level.SEVERE, e);
        CommonUtils.exitApplication();
        return null;

      } catch (Exception e2) {
        LoggingUtils.log(Level.SEVERE, e);
        util.createAlert(
            "ERROR", "Error creating sheets", "Specified sheets do not exist, failed to create");

        CommonUtils.exitApplication();
        return null;
      }

      // attempt to create our sheets

    } catch (IOException e3) {
      LoggingUtils.log(Level.SEVERE, e3);

      util.createAlert(
          "ERROR",
          "Error connecting to database",
          "Please confirm that the URL is valid and that you have internet access.");

      CommonUtils.exitApplication();
      return null;
    }
  }

  private List<List<Object>> getDataFromSheet(Sheets service, String range) throws IOException {
    ValueRange response = null;

    try {
      response = service.spreadsheets().values().get(spreadsheet, range).execute();

    } catch (NoRouteToHostException e) {
      LoggingUtils.log(Level.SEVERE, "No route to host!");
      util.createAlert(
          "ERROR",
          "No Route to Host",
          "Unable to connect to the database, check internet connection and restart the application.");
      CommonUtils.exitApplication();

    } catch (SocketTimeoutException e) {
      LoggingUtils.log(Level.SEVERE, "Connection timed out!");
      util.createAlert(
          "ERROR",
          "Connection timed out",
          "Connecting to database took too long! Please check your internet connection and retry!");
      CommonUtils.exitApplication();
    }

    if (response.getValues() == null) {
      worksheetIsEmpty = true;
    }

    return response.getValues();
  }

  public void updateSpreadSheet(int row, int column, String data, int page) {

    // Build a new authorized API client service.
    String columnLetter = toAlphabetic(column - 1);

    String range = columnLetter + row;
    String sheetPage = getPage(page);
    range = sheetPage + "!" + range;

    ValueRange requestBody = new ValueRange();
    requestBody.setValues(Collections.singletonList(Collections.singletonList(data)));

    try {
      final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
      Sheets service =
          new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
              .setApplicationName(APPLICATION_NAME)
              .build();

      service
          .spreadsheets()
          .values()
          .update(spreadsheet, range, requestBody)
          .setValueInputOption("RAW")
          .execute();

    } catch (GeneralSecurityException e) {
      LoggingUtils.log(Level.SEVERE, "INVALID CREDENTIALS");

    } catch (GoogleJsonResponseException e) {
      if (e.getDetails().getMessage().contains("exceeds grid limits")) {
        LoggingUtils.log(Level.WARNING, "Appending Data");
        try {
          final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
          Sheets service =
              new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                  .setApplicationName(APPLICATION_NAME)
                  .build();

          service
              .spreadsheets()
              .values()
              .append(spreadsheet, range, requestBody)
              .setValueInputOption("RAW")
              .execute();

        } catch (IOException e1) {
          LoggingUtils.log(Level.SEVERE, e);

        } catch (GeneralSecurityException e1) {
          LoggingUtils.log(Level.SEVERE, e);
          util.createAlert(
              "ERROR",
              "Invalid Credentials",
              "You do not have permission to edit this spreadsheet!");
        }

      } else {
        LoggingUtils.log(Level.SEVERE, e);
      }

    } catch (IOException e2) {
      LoggingUtils.log(Level.SEVERE, e2);
      // do nothing

    }
  }

  public void updateSpreadSheetBatch(ArrayList<BatchUpdateData> batchData, int page) {
    try {
      LoggingUtils.log(Level.INFO, "Batch updating sheet");
      final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
      Sheets service =
          new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
              .setApplicationName(APPLICATION_NAME)
              .build();

      List<ValueRange> requestData = new ArrayList<>();

      for (BatchUpdateData data : batchData) {
        // Build a new authorized API client service.
        String columnLetter = toAlphabetic(data.getColumn() - 1);

        String range = columnLetter + data.getRow();

        String sheetPage = getPage(page);
        range = sheetPage + "!" + range;

        requestData.add(
            new ValueRange()
                .setRange(range)
                .setValues(Collections.singletonList(Collections.singletonList(data.getData()))));
      }

      BatchUpdateValuesRequest batchBody =
          new BatchUpdateValuesRequest().setValueInputOption("RAW").setData(requestData);

      // TODO check response
      service.spreadsheets().values().batchUpdate(spreadsheet, batchBody).execute();

    } catch (GeneralSecurityException e) {
      LoggingUtils.log(Level.SEVERE, "INVALID CREDENTIALS");

    } catch (IOException e2) {
      LoggingUtils.log(Level.SEVERE, e2);
      // do nothing

    }
  }

  // update current working page
  private String getPage(int page) {
    switch (page) {
      case Constants.kMainSheet:
        return mainPage;

      case Constants.kLogSheet:
        return logPage;

      case Constants.kRegistrationSheet:
        return regPage;

      default:
        return mainPage;
    }
  }

  public static String toAlphabetic(int i) {
    if (i < 0) {
      return "-" + toAlphabetic(-i - 1);
    }

    int quot = i / 26;
    int rem = i % 26;
    char letter = (char) ((int) 'A' + rem);
    if (quot == 0) {
      return "" + letter;

    } else {
      return toAlphabetic(quot - 1) + letter;
    }
  }
}
