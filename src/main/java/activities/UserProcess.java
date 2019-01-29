package activities;

import databases.DatabaseUtils;
import databases.JSONHelper;
import exceptions.CancelledUserCreationException;
import exceptions.ConnectToWorksheetException;
import helpers.AlertUtils;
import helpers.CommonUtils;
import helpers.Constants;
import helpers.LoggingUtils;
import javafx.application.Platform;
import notifiers.LoginNotifier;
import scenes.GrizzlyScene;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;

public class UserProcess {
    /**
     * @author Dalton Smith
     * UserProcess class
     * Contains the various methods for handling user login/logout
     */

    private DatabaseUtils dbUtils = new DatabaseUtils();
    private AlertUtils alertUtils = new AlertUtils();
    private LoginNotifier notifier = new LoginNotifier();
    private JSONHelper json = new JSONHelper();
    private CommonUtils utils = new CommonUtils();

    private boolean idGrabbed = false;
    private int idLength = 6;

    //check if user is logged in
    public boolean isUserLoggedIn(String userID, boolean handsFree) throws Exception {
        dbUtils.getUpdatedData();

        ArrayList<String> ids = dbUtils.getColumnData(0, Constants.kMainSheet);

        if (ids == null) {
            throw new ConnectToWorksheetException("ids is null");
        }

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

                return cellData.equals("TRUE");
            }
        }

        //request users first name and last name
        LoggingUtils.log(Level.INFO, "New User Detected");
        ArrayList<String> userData = alertUtils.getUserInfo();

        //cancel if user cancelled or exited registration dialog
        if (("TRUE").equalsIgnoreCase(userData.get(0))) {
            //create user then login
            int blankRow = dbUtils.nextEmptyCellColumn(Constants.kMainSheet);
            dbUtils.setCellData(blankRow, Constants.kStudentIdColumn, userID, Constants.kMainSheet);
            dbUtils.setCellData(blankRow, Constants.kFirstNameColumn, userData.get(1), Constants.kMainSheet);
            dbUtils.setCellData(blankRow, Constants.kLastNameColumn, userData.get(2), Constants.kMainSheet);
            dbUtils.getUpdatedData();

        } else {
            LoggingUtils.log(Level.INFO, "Account Creation Cancelled");
            throw new CancelledUserCreationException("Cancelled");

        }

        return false;

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
            dbUtils.setCellData(userRow, Constants.kLastLoginColumn, currentTime, Constants.kMainSheet);
            dbUtils.setCellData(userRow, Constants.kLoggedInColumn, "TRUE", Constants.kMainSheet);
            dbUtils.setCellData(userRow, Constants.kLastLogoutColumn, "LOGGED IN", Constants.kMainSheet);

            if (Constants.kGrizzlyPrompt && !notifier.checkNotifier(userRow, dbUtils)) {
                utils.playDing();

                alertUtils.createAlert("Registration not complete!", "Registration not complete!", "It seems you have not completed your user registration!" +
                        " Please visit https://ycsrobotics.org/registration to finish your registration");
                
            }

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
        String logoutTime = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        String loginTime = dbUtils.getCellData(userRow, Constants.kLastLoginColumn, Constants.kMainSheet);

        //ensure it's formatted correctly
        loginTime = loginTime.replaceAll("\\s+","");

        //assuming userRow isn't invalid, calculate difference in time and log hours
        if (userRow != -1) {
            //update the logout time
            dbUtils.setCellData(userRow, Constants.kLastLogoutColumn, logoutTime, Constants.kMainSheet);

            //create our datetime objects for parsing
            LocalTime loginTimeLocalTime = LocalTime.parse(loginTime);
            LocalTime logoutTimeLocalTime = LocalTime.parse(logoutTime);

            //grab the time different between login/logout
            Duration difference = Duration.between(loginTimeLocalTime, logoutTimeLocalTime);
            long durInSeconds = difference.getSeconds();

            //calculate the total time from difference
            String time = String.format("%02d:%02d:%02d", durInSeconds / 3600, (durInSeconds % 3600) / 60, (durInSeconds % 60));


            boolean err = false;

            //logout the user if in the negative
            LocalTime totalHoursTime = null;
            try {
                totalHoursTime = LocalTime.parse(time);
            } catch(DateTimeParseException e) {
                LoggingUtils.log(Level.WARNING, "Error parsing previous time, did they forget to log out? \n" + e.getMessage());
                err = true;
            }


            if (!err) {
                //grab the current total hours
                String totalHours = dbUtils.getCellData(userRow, Constants.kTotalHoursColumn, Constants.kMainSheet);
                String[] prevTotalTime;
                double[] prevTotalTimeNum = new double[3];

                //manually manipulate the time
                try {
                    prevTotalTime = totalHours.split("\\s*:\\s*");

                    prevTotalTimeNum[0] = Double.parseDouble(prevTotalTime[0]);
                    prevTotalTimeNum[1] = Double.parseDouble(prevTotalTime[1]);
                    prevTotalTimeNum[2] = Double.parseDouble(prevTotalTime[2]);

                } catch (DateTimeParseException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    LoggingUtils.log(Level.WARNING, "Error parsing previous total time, using fallback of 0.0");
                    prevTotalTimeNum[0] = 0.0;
                    prevTotalTimeNum[1] = 0.0;
                    prevTotalTimeNum[2] = 0.0;
                }


                //calculate the new total time
                double totalHour = prevTotalTimeNum[0] + totalHoursTime.getHour();
                double totalMinute = prevTotalTimeNum[1] + totalHoursTime.getMinute();
                double totalSeconds = prevTotalTimeNum[2] + totalHoursTime.getSecond();

                while(totalSeconds > 60) {
                    totalSeconds-=60;
                    totalMinute+=1;
                }

                while(totalMinute > 60) {
                    totalMinute-=60;
                    totalHour+=1;
                }

                String timeTotal = String.format("%02d:%02d:%02d", (int)totalHour, (int)totalMinute, (int)totalSeconds);

                //set cell data
                dbUtils.setCellData(userRow, Constants.kHoursColumn, time, Constants.kMainSheet);

                int userTimeColumn = logCurrentDate();

                //add together day times
                LocalTime prevDayTime;
                try {
                    String prevData = dbUtils.getCellData(userRow, userTimeColumn, Constants.kLogSheet);
                    prevDayTime = LocalTime.parse(prevData);

                } catch (DateTimeParseException | NullPointerException e) {
                    prevDayTime = LocalTime.parse("00:00:01");
                    LoggingUtils.log(Level.WARNING, "There was an issue adding cell data, using fallback. \n" + e.getMessage());

                }

                LocalTime tempTotalDayTime;
                try {
                    tempTotalDayTime = LocalTime.parse(time).plusHours(prevDayTime.getHour()).plusMinutes(prevDayTime.getMinute()).plusSeconds(prevDayTime.getSecond());

                } catch (DateTimeParseException e) {
                    GrizzlyScene.setMessageBoxText("There was an error adding together total time. Using fallback.");
                    tempTotalDayTime = prevDayTime;
                }

                String timeTotalDay = String.format("%02d:%02d:%02d", tempTotalDayTime.getHour(), tempTotalDayTime.getMinute(), tempTotalDayTime.getSecond());

                dbUtils.setCellData(userRow, userTimeColumn, timeTotalDay, Constants.kLogSheet);
                dbUtils.setCellData(userRow, Constants.kTotalHoursColumn, timeTotal, Constants.kMainSheet);

                //show user logout text
                Platform.runLater(() -> {
                    GrizzlyScene.setMessageBoxText("Logged out user: " + userID);
                    GrizzlyScene.clearInput();
                });

            }

            //if user didn't logout, then logout user and don't log hours
            dbUtils.setCellData(userRow, Constants.kLoggedInColumn, "FALSE", Constants.kMainSheet);

            if(err) {
                Platform.runLater(() -> {
                    GrizzlyScene.setMessageBoxText("You forgot to log out! Yours hours were not counted! See an administrator if this is in error");
                    GrizzlyScene.clearInput();
                });
            }


        }

    }

    //helper method for grabbing the column that contains the current date
    private int logCurrentDate(){
        ArrayList<String> data = dbUtils.getRowData(1, Constants.kLogSheet);
        String currentDate = new SimpleDateFormat("yyyy:MM:dd").format(new Date());

        //check if our date already exists
        int i;
        for (i = 0; i < data.size(); i++) {
            String checkDate = data.get(i);
            checkDate = checkDate.replace("'", "");

            //return column containing our date
            if(checkDate.equals(currentDate)){
                return i;
            }
        }

        //log the current date and return column
        dbUtils.setCellData(0, i, currentDate, Constants.kLogSheet);
        return i;
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
