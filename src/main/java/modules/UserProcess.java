package modules;

import databases.DatabaseUtils;
import helpers.Constants;
import helpers.Utils;
import javafx.application.Platform;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

class UserProcess {
    /**
     * @author Dalton Smith
     * UserProcess class
     * Contains the various methods for handling user login/logout
     */

    private DatabaseUtils dbUtils = new DatabaseUtils();
    private Utils util = new Utils();

    //check if user is logged in
    boolean isUserLoggedIn(String userID) throws Exception {
        ArrayList<String> ids = dbUtils.getColumnData(0, Constants.mainSheet);

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
        System.out.println("NEW USER DETECTED");
        ArrayList<String> userData = util.getUserInfo();

        //cancel if user cancelled or exited registration dialog
        if (userData.get(0).equals("TRUE")) {
            //create user then login
            int blankRow = dbUtils.nextEmptyCellColumn(Constants.mainSheet);
            dbUtils.setCellData(blankRow, Constants.STUDENTIDCOLUMN, userID, Constants.mainSheet);
            dbUtils.setCellData(blankRow, Constants.FIRSTNAMECOLUMN, userData.get(1), Constants.mainSheet);
            dbUtils.setCellData(blankRow, Constants.LASTNAMECOLUMN, userData.get(2), Constants.mainSheet);

        } else {
            throw new Exception("Cancelled");

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

            Platform.runLater(() -> {
                UserInterface.setMessageBoxText("Successfully logged in user: " + userID);
                UserInterface.clearInput();
            });

        }

    }

    //logout the user
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
            LocalTime totalHoursTime = LocalTime.parse(time);

            //logout the user if in the negative
            //err returns -1 if index doesn't exist
            int err = time.indexOf('-');

            if (err == -1) {
                LocalTime prevTotalHours;

                //grab the current total hours
                String totalHours = dbUtils.getCellData(userRow, Constants.TOTALHOURSCOLUMN, Constants.mainSheet);

                //check if totalHours already exists
                try {
                    prevTotalHours = LocalTime.parse(totalHours);

                } catch (DateTimeParseException e) {
                    prevTotalHours = LocalTime.parse("00:00:01");

                }


                //calculate the new total time
                LocalTime totalTime = totalHoursTime.plusHours(prevTotalHours.getHour()).plusMinutes(prevTotalHours.getMinute()).plusSeconds(prevTotalHours.getSecond());
                String timeTotal = String.format("%02d:%02d:%02d", totalTime.getHour(), totalTime.getMinute(), totalTime.getSecond());

                //set cell data
                dbUtils.setCellData(userRow, Constants.HOURSCOLUMN, time, Constants.mainSheet);
                dbUtils.setCellData(userRow, logCurrentDate(), time, Constants.logSheet);
                dbUtils.setCellData(userRow, Constants.TOTALHOURSCOLUMN, timeTotal, Constants.mainSheet);

                //show user logout text
                Platform.runLater(() -> {
                    UserInterface.setMessageBoxText("Logged out user: " + userID);
                    UserInterface.clearInput();
                });

            }

            //if user didn't logout, then logout user and don't log hours
            dbUtils.setCellData(userRow, Constants.LOGGEDINCOLUMN, "FALSE", Constants.mainSheet);


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
        return i+1;
    }

    //checks if ID is valid integer and 6 digit number
    //TODO Implement checkers in various locations
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
