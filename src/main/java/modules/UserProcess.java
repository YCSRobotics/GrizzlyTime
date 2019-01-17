package modules;

import exceptions.CancelledUserCreationException;
import databases.DatabaseUtils;
import exceptions.ConnectToWorksheetException;
import helpers.Constants;
import helpers.LoggingUtil;
import helpers.Utils;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import scenes.LoginNotifier;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;

class UserProcess {
    /**
     * @author Dalton Smith
     * UserProcess class
     * Contains the various methods for handling user login/logout
     */

    private DatabaseUtils dbUtils = new DatabaseUtils();
    private Utils util = new Utils();
    private LoginNotifier notifier = new LoginNotifier();

    //check if user is logged in
    boolean isUserLoggedIn(String userID, boolean handsFree) throws Exception {
        dbUtils.getUpdatedData();

        ArrayList<String> ids = dbUtils.getColumnData(0, Constants.mainSheet);

        if (ids == null) {
            throw new ConnectToWorksheetException("ids is null");
        }

        //check if the user ID exists
        for (int i = 0; i < ids.size(); i++) {
            //if the user exists, check if logged in or logged out and return state
            if (ids.get(i).equals(userID)) {
                String cellData = dbUtils.getCellData(i, Constants.LOGGEDINCOLUMN, Constants.mainSheet);
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
        LoggingUtil.log(Level.INFO, "New User Detected");
        ArrayList<String> userData = util.getUserInfo();

        //cancel if user cancelled or exited registration dialog
        if (userData.get(0).equals("TRUE")) {
            //create user then login
            int blankRow = dbUtils.nextEmptyCellColumn(Constants.mainSheet);
            dbUtils.setCellData(blankRow, Constants.STUDENTIDCOLUMN, userID, Constants.mainSheet);
            dbUtils.setCellData(blankRow, Constants.FIRSTNAMECOLUMN, userData.get(1), Constants.mainSheet);
            dbUtils.setCellData(blankRow, Constants.LASTNAMECOLUMN, userData.get(2), Constants.mainSheet);

        } else {
            LoggingUtil.log(Level.INFO, "Account Creation Cancelled");
            throw new CancelledUserCreationException("Cancelled");

        }

        return false;

    }

    //login our user
    void loginUser(String userID) {
        Platform.runLater(() -> {
            UserInterface.setMessageBoxText("Logging in user: " + userID);
        });

        //grab the current time from system and format it into string
        String currentTime = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        int userRow = dbUtils.getCellRowFromColumn(userID, Constants.STUDENTIDCOLUMN, Constants.mainSheet);

        //log the user in
        if (userRow != -1) {
            dbUtils.setCellData(userRow, Constants.LASTLOGINCOLUMN, currentTime, Constants.mainSheet);
            dbUtils.setCellData(userRow, Constants.LOGGEDINCOLUMN, "TRUE", Constants.mainSheet);
            dbUtils.setCellData(userRow, Constants.LASTLOGOUTCOLUMN, "LOGGED IN", Constants.mainSheet);

            if (Constants.grizzlyPrompt) {
                if (!notifier.checkNotifier(userRow, dbUtils)){
                    util.playDing();

                    util.createAlert("Registration not complete!", "Registration not complete!", "It seems you have not completed your user registration!" +
                            " Please visit https://ycsrobotics.org/registration to finish your registration", Alert.AlertType.ERROR);


                }
            }

            Platform.runLater(() -> {
                UserInterface.setMessageBoxText("Successfully logged in user: " + userID);
                UserInterface.clearInput();
            });

        }

    }

    //logout the user
    //TODO clean up code
    void logoutUser(String userID) {
        Platform.runLater(() -> {
            UserInterface.setMessageBoxText("Logging out user: " + userID);
        });

        //grab the row the user is on
        int userRow = dbUtils.getCellRowFromColumn(userID, Constants.STUDENTIDCOLUMN, Constants.mainSheet);

        //grab last logged in time
        String logoutTime = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        String loginTime = dbUtils.getCellData(userRow, Constants.LASTLOGINCOLUMN, Constants.mainSheet);

        //ensure it's formatted correctly
        loginTime = loginTime.replaceAll("\\s+","");

        //assuming userRow isn't invalid, calculate difference in time and log hours
        if (userRow != -1) {
            //update the logout time
            dbUtils.setCellData(userRow, Constants.LASTLOGOUTCOLUMN, logoutTime, Constants.mainSheet);

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
                LoggingUtil.log(Level.WARNING, "Error parsing previous time, did they forget to log out? \n" + e.getMessage());
                err = true;
            }


            if (!err) {
                //grab the current total hours
                String totalHours = dbUtils.getCellData(userRow, Constants.TOTALHOURSCOLUMN, Constants.mainSheet);
                String[] prevTotalTime;
                double[] prevTotalTimeNum = new double[3];

                //manually manipulate the time
                try {
                    prevTotalTime = totalHours.split("\\s*:\\s*");

                    prevTotalTimeNum[0] = Double.parseDouble(prevTotalTime[0]);
                    prevTotalTimeNum[1] = Double.parseDouble(prevTotalTime[1]);
                    prevTotalTimeNum[2] = Double.parseDouble(prevTotalTime[2]);

                } catch (DateTimeParseException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    LoggingUtil.log(Level.WARNING, "Error parsing previous total time, using fallback of 0.0");
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
                dbUtils.setCellData(userRow, Constants.HOURSCOLUMN, time, Constants.mainSheet);

                int userTimeColumn = logCurrentDate();

                //add together day times
                LocalTime prevDayTime;
                try {
                    String prevData = dbUtils.getCellData(userRow, userTimeColumn, Constants.logSheet);
                    prevDayTime = LocalTime.parse(prevData);

                } catch (DateTimeParseException | NullPointerException e) {
                    prevDayTime = LocalTime.parse("00:00:01");
                    LoggingUtil.log(Level.WARNING, "There was an issue adding cell data, using fallback. \n" + e.getMessage());

                }

                LocalTime tempTotalDayTime;
                try {
                    tempTotalDayTime = LocalTime.parse(time).plusHours(prevDayTime.getHour()).plusMinutes(prevDayTime.getMinute()).plusSeconds(prevDayTime.getSecond());

                } catch (DateTimeParseException e) {
                    UserInterface.setMessageBoxText("There was an error adding together total time. Using fallback.");
                    tempTotalDayTime = prevDayTime;
                }

                String timeTotalDay = String.format("%02d:%02d:%02d", tempTotalDayTime.getHour(), tempTotalDayTime.getMinute(), tempTotalDayTime.getSecond());

                dbUtils.setCellData(userRow, userTimeColumn, timeTotalDay, Constants.logSheet);
                dbUtils.setCellData(userRow, Constants.TOTALHOURSCOLUMN, timeTotal, Constants.mainSheet);

                //show user logout text
                Platform.runLater(() -> {
                    UserInterface.setMessageBoxText("Logged out user: " + userID);
                    UserInterface.clearInput();
                });

            }

            //if user didn't logout, then logout user and don't log hours
            dbUtils.setCellData(userRow, Constants.LOGGEDINCOLUMN, "FALSE", Constants.mainSheet);

            if(err) {
                Platform.runLater(() -> {
                    UserInterface.setMessageBoxText("You forgot to log out! Yours hours were not counted! See an administrator if this is in error");
                    UserInterface.clearInput();
                });
            }


        }

    }

    //helper method for grabbing the column that contains the current date
    private int logCurrentDate(){
        ArrayList<String> data = dbUtils.getRowData(1, Constants.logSheet);
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
        dbUtils.setCellData(0,  i, currentDate, Constants.logSheet);
        return i;
    }

    //checks if ID is valid integer and 6 digit number
    boolean isValidID(String userID) {
        try {
            Integer.parseInt(userID);

            return userID.length() == 6;

        } catch (NumberFormatException e) {
            //not a valid ID
            return false;
        }
    }

}
