package databases;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import helpers.Utils;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

//ignore google sheets warnings because of google permission bug
class DatabaseProcess {
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
    private Utils util = new Utils();

    private static final String spreadsheet = new JSONHelper().getKey("sheet");

    private static final String mainPage = "Current";
    private static final String logPage = "Date Log";

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

    List<List<Object>> returnWorksheetData(String range) {

        // Build a new authorized API client service.
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            ValueRange response = service.spreadsheets().values()
                    .get(spreadsheet, range)
                    .execute();

            return response.getValues();

        } catch (GeneralSecurityException e) {
            util.createAlert(
                    "ERROR",
                    "INVALID CREDENTIALS",
                    "Please check that you have permission to edit the google sheet and try again.",
                    Alert.AlertType.ERROR

            );

            System.exit(1);
            return null;

        } catch (IOException e2) {
            e2.printStackTrace();

            util.createAlert(
                    "ERROR",
                    "INVALID SHEET",
                    "Please check that the google sheet URL located in the config.json is valid, and try again.",
                    Alert.AlertType.ERROR

            );

            System.exit(1);
            return null;

        }
    }

    void updateSpreadSheet(int row, int column, String data, int page) {

        // Build a new authorized API client service.
        String columnLetter = getCharForNumber(column);

        String range = columnLetter + row;
        String sheetPage = page == 0 ? mainPage : logPage;
        range = sheetPage + "!" + range;

        ValueRange requestBody = new ValueRange();
        requestBody.setValues(Arrays.asList(Arrays.asList(data)));

        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            service.spreadsheets().values().update(spreadsheet, range, requestBody).setValueInputOption("RAW").execute();

        } catch (GeneralSecurityException e) {
            System.out.println("Invalid Credentials");

        } catch (IOException e2) {
            e2.printStackTrace();
            //do nothing

        }

    }

    private String getCharForNumber(int i) {
        return i > 0 && i < 27 ? String.valueOf((char)(i + 64)) : null;
    }

}
