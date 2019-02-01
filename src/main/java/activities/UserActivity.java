package activities;

import databases.BatchUpdateData;
import databases.DatabaseUtils;
import databases.JSONHelper;
import exceptions.CancelledUserCreationException;
import helpers.AlertUtils;
import helpers.Constants;
import helpers.LoggingUtils;
import javafx.application.Platform;
import scenes.GrizzlyScene;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;

public class UserActivity {
    /**
     * @author Dalton Smith
     * UserActivity class
     * Contains the various methods for handling user login/logout
     */

    private DatabaseUtils dbUtils = new DatabaseUtils();
    private AlertUtils alertUtils = new AlertUtils();
    private JSONHelper json = new JSONHelper();

    private LogoutActivity logoutActivity = new LogoutActivity(dbUtils);
    private LoginActivity loginActivity = new LoginActivity(dbUtils);

    private boolean idGrabbed = false;
    private int idLength = 6;

    //check if user is logged in
    public boolean isUserLoggedIn(String userID) throws Exception {
        dbUtils.getUpdatedData();

        ArrayList<String> ids = dbUtils.getColumnData(0, Constants.kMainSheet);

        int state = doesIdExist(ids, userID);

        switch (state) {
            case Constants.kIdDoesNotExist:
                break;
            case Constants.kIdLoggedIn:
                return true;
            case Constants.kIdNotLoggedIn:
                return false;
            default:
                LoggingUtils.log(Level.SEVERE, "Uh oh, isUserLoggedIn received an unknown ID of " + state);
                break;
        }

        //request users first name and last name
        LoggingUtils.log(Level.INFO, "New User Detected");
        ArrayList<String> userData = alertUtils.getUserInfo();

        //throws CancelledUserException if registration was cancelled
        createNewUser(userData, userID);

        return false;

    }

    public void createNewUser(ArrayList<String> userData, String userID) throws CancelledUserCreationException {
        //cancel if user cancelled or exited registration dialog
        if (("TRUE").equalsIgnoreCase(userData.get(0))) {
            //create user then login
            ArrayList<BatchUpdateData<Integer, Integer, String>> data = new ArrayList<>();

            int blankRow = dbUtils.nextEmptyCellColumn(Constants.kMainSheet);
            addUserInfoBasic(userData, userID, data, blankRow);
            data.add(new BatchUpdateData<>(blankRow, Constants.kEmailColumn, userData.get(3)));
            data.add(new BatchUpdateData<>(blankRow, Constants.kRoleColumn, userData.get(5)));
            data.add(new BatchUpdateData<>(blankRow, Constants.kGenderColumn, userData.get(4)));

            dbUtils.setCellDataBatch(data, Constants.kMainSheet);
            dbUtils.getUpdatedData();

            ArrayList<String> columnLogged = dbUtils.getColumnData(Constants.kStudentIdColumn, Constants.kLogSheet);

            int i;
            for (i = 1; i < columnLogged.size(); i++) {
                if (columnLogged.get(i).equals("")) {
                    break;
                }
            }

            data.clear();
            addUserInfoBasic(userData, userID, data, i);

            dbUtils.setCellDataBatch(data, Constants.kLogSheet);
            dbUtils.getUpdatedData();

            //ensure there is a date column
            logoutActivity.getCurrentDateColumn();

        } else {
            LoggingUtils.log(Level.INFO, "Account Creation Cancelled");
            throw new CancelledUserCreationException("Cancelled");

        }
    }

    private void addUserInfoBasic(ArrayList<String> userData, String userID, ArrayList<BatchUpdateData<Integer, Integer, String>> data, int i) {
        data.add(new BatchUpdateData<>(i, Constants.kStudentIdColumn, userID));
        data.add(new BatchUpdateData<>(i, Constants.kFirstNameColumn, userData.get(1)));
        data.add(new BatchUpdateData<>(i, Constants.kLastNameColumn, userData.get(2)));
    }

    public int doesIdExist(ArrayList<String> ids, String userID) {
        //check if the user ID exists
        for (int i = 0; i < ids.size(); i++) {
            //if the user exists, check if logged in or logged out and return state
            if (ids.get(i).equals(userID)) {
                String cellData = dbUtils.getCellData(i, Constants.kLoggedInColumn, Constants.kMainSheet);
                try {
                    cellData = cellData.replaceAll("\\s+", "");

                } catch (NullPointerException e) {
                    continue;
                    //do nothing because the cell doesn't exist?
                }

                if (cellData.equals("TRUE")) {
                    return Constants.kIdLoggedIn;

                } else {
                    return Constants.kIdNotLoggedIn;

                }
            }
        }

        return Constants.kIdDoesNotExist;
    }

    //login our user
    public void loginUser(String userID) {
        Platform.runLater(() -> {
            GrizzlyScene.setMessageBoxText("Logging in user: " + userID);
        });

        //grab the current time from system and format it into string
        String currentTime = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        int userRow = dbUtils.getCellRowFromColumn(userID, Constants.kStudentIdColumn, Constants.kMainSheet);

        //log the user in
        if (userRow != -1) {
            loginActivity.loginUser(userRow, currentTime);

            Platform.runLater(() -> {
                GrizzlyScene.setMessageBoxText("Successfully logged in user: " + userID);
                GrizzlyScene.clearInput();
            });

        }

    }

    //logout the user
    public void logoutUser(String userID) {
        Platform.runLater(() -> {
            GrizzlyScene.setMessageBoxText("Logging out user: " + userID);
        });

        //grab the row the user is on
        int userRow = dbUtils.getCellRowFromColumn(userID, Constants.kStudentIdColumn, Constants.kMainSheet);

        //grab last logged in time
        String logoutTimeAsString = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        String loginTimeAsString = dbUtils.getCellData(userRow, Constants.kLastLoginColumn, Constants.kMainSheet);

        //ensure it's formatted correctly
        loginTimeAsString = loginTimeAsString.replaceAll("\\s+", "");

        //assuming userRow isn't invalid, calculate difference in time and log hours
        if (userRow != -1) {
            //update the logout time
            dbUtils.setCellData(userRow, Constants.kLastLogoutColumn, logoutTimeAsString, Constants.kMainSheet);

            //create our datetime objects for parsing
            LocalTime loginTimeLocalTime = LocalTime.parse(loginTimeAsString);
            LocalTime logoutTimeLocalTime = LocalTime.parse(logoutTimeAsString);

            //grab the time different between login/logout
            Duration difference = Duration.between(loginTimeLocalTime, logoutTimeLocalTime);
            long durInSeconds = difference.getSeconds();

            //calculate the total time from difference
            String totalTimeFromDifference = String.format("%02d:%02d:%02d", durInSeconds / 3600, (durInSeconds % 3600) / 60, (durInSeconds % 60));

            boolean err = false;

            //logout the user if in the negative
            LocalTime totalHoursTime = null;

            try {
                totalHoursTime = LocalTime.parse(totalTimeFromDifference);
            } catch(DateTimeParseException e) {
                LoggingUtils.log(Level.WARNING, "Error parsing previous time, did they forget to log out? \n" + e.getMessage());
                err = true;
            }


            if (!err) {
                logoutActivity.logoutUserWithHours(userID, userRow, totalHoursTime, totalTimeFromDifference);
            }

            //logout the user
            dbUtils.setCellData(userRow, Constants.kLoggedInColumn, "FALSE", Constants.kMainSheet);

            if(err) {
                Platform.runLater(() -> {
                    GrizzlyScene.setMessageBoxText("You forgot to log out! Please re-login!");
                    GrizzlyScene.clearInput();
                });
            }


        }

    }

    //checks if ID is valid integer and 6 digit number
    public boolean isValidID(String userID) {
        grabIdLength();

        try {
            Integer.parseInt(userID);

            if (Constants.kMentorFallback) {
                return userID.length() == idLength || userID.length() == 8;

            } else {
                return userID.length() == idLength;

            }

        } catch (NumberFormatException e) {
            //not a valid ID
            return false;
        }
    }

    private int grabIdLength() {
        if (!idGrabbed) {
            try {
                idLength = Integer.parseInt(json.getKey("idLength"));

            } catch (NumberFormatException e) {
                //do nothing
            }

            idGrabbed = true;
        }

        return idLength;
    }
}
