package databases;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
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
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class DatabaseProcess {
    /**
     * @author Dalton Smith
     * DatabaseProcess
     * Manages low level google sheets handling from API
     * Based on Java QuickStart
     */

    private static final String APPLICATION_NAME = "GrizzlyTime JavaFX Edition";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String CREDENTIALS_FILE_PATH = "/credentials/credentials.json";

    private AlertUtils util = new AlertUtils();

    private static final String spreadsheet = new JSONHelper().getKey("sheet");

    private static final String mainPage = "Current";
    private static final String logPage = "Date Log";
    private static final String regPage = "Student Registration";

    public static boolean worksheetIsEmpty = false;

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        //set google logging level to severe due to permissions bug, see https://github.com/googleapis/google-http-java-client/issues/315
        java.util.logging.Logger.getLogger(FileDataStoreFactory.class.getName()).setLevel(Level.SEVERE);

        // Load client secrets.
        InputStream in = DatabaseProcess.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public List<List<Object>> returnWorksheetData(int page) {

        // Build a new authorized API client service.
        try {

            // Build a new authorized API client service.
            String range = getPage(page);

            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();


            return getDataFromSheet(service, range);

        } catch (NoRouteToHostException | UnknownHostException e) {
            LoggingUtils.log(Level.SEVERE, e);
            util.createAlert(
                    "ERROR",
                    "No Network Connection",
                    "Please confirm your network connection and try again."

            );

            CommonUtils.exitApplication();
            return null;

        } catch (GeneralSecurityException e2) {
            LoggingUtils.log(Level.SEVERE, e2);
            util.createAlert(
                    "ERROR",
                    "Invalid Credentials",
                    "Please delete the 'tokens' directory and try again!"

            );

            CommonUtils.exitApplication();
            return null;

        } catch (GoogleJsonResponseException e) {
            LoggingUtils.log(Level.SEVERE, e.getDetails().getMessage());

            try {

                util.createAlert("ERROR", "Sheet does not exist", e.getDetails().getMessage() + "\nAttempting to create!");

                if (e.getDetails().getMessage().contains("Unable to parse range")) {
                    LoggingUtils.log(Level.WARNING, "Attempting to create our sheets");

                    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
                    Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                            .setApplicationName(APPLICATION_NAME)
                            .build();

                    ArrayList<Request> requests = new ArrayList<>();

                    //add our sheets to the list of sheets to be processed
                    if (e.getDetails().getMessage().contains("Current")) {
                        requests.add(new Request().setAddSheet(new AddSheetRequest()
                                .setProperties(new SheetProperties().setTitle("Current"))));
                        LoggingUtils.log(Level.INFO, "Creating Current Sheet");
                    }

                    if (e.getDetails().getMessage().contains("Date Log")) {
                        requests.add(new Request().setAddSheet(new AddSheetRequest()
                                .setProperties(new SheetProperties().setTitle("Date Log"))));
                        LoggingUtils.log(Level.INFO, "Creating Date Log Sheet");
                    }

                    BatchUpdateSpreadsheetRequest body
                            = new BatchUpdateSpreadsheetRequest().setRequests(requests);

                    BatchUpdateSpreadsheetResponse updateResponse = service.spreadsheets().batchUpdate(spreadsheet, body).execute();

                    LoggingUtils.log(Level.INFO, updateResponse.getReplies().toString());

                    String range = getPage(page);

                    return getDataFromSheet(service, range);
                }

                LoggingUtils.log(Level.SEVERE, "Unknown exception");
                CommonUtils.exitApplication();
                return null;

            } catch (Exception e2) {
                LoggingUtils.log(Level.SEVERE, e);
                util.createAlert(
                        "ERROR",
                        "Error creating sheets",
                        "Specified sheets do not exist, failed to create"

                );

                CommonUtils.exitApplication();
                return null;
            }

            //attempt to create our sheets

        } catch (IOException e3) {
            LoggingUtils.log(Level.SEVERE, e3);

            util.createAlert(
                    "ERROR",
                    "Error connecting to database",
                    "Please confirm that the URL is valid and that you have internet access."

            );

            CommonUtils.exitApplication();
            return null;

        }
    }

    private List<List<Object>> getDataFromSheet(Sheets service, String range) throws IOException {
        ValueRange response = service.spreadsheets().values()
                .get(spreadsheet, range)
                .execute();

        if (response.getValues() == null) {
            worksheetIsEmpty = true;
        }

        return response.getValues();
    }

    public void updateSpreadSheet(int row, int column, String data, int page) {

        // Build a new authorized API client service.
        String columnLetter = getCharForNumber(column);

        String range = columnLetter + row;
        String sheetPage = getPage(page);
        range = sheetPage + "!" + range;

        ValueRange requestBody = new ValueRange();
        requestBody.setValues(Collections.singletonList(Collections.singletonList(data)));

        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            service.spreadsheets().values().update(spreadsheet, range, requestBody).setValueInputOption("RAW").execute();

        } catch (GeneralSecurityException e) {
            LoggingUtils.log(Level.SEVERE, "INVALID CREDENTIALS");

        } catch (IOException e2) {
            LoggingUtils.log(Level.SEVERE, e2);
            //do nothing

        }

    }

    //update current working page
    private String getPage(int page){
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

    private String getCharForNumber(int i) {
        return i > 0 && i < 27 ? String.valueOf((char)(i + 64)) : null;
    }

}
