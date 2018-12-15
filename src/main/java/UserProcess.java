import databases.DatabaseUtils;
import helpers.Constants;
import javafx.application.Platform;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;

class UserProcess {

    private DatabaseUtils dbUtils = new DatabaseUtils();
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_TIME;

    boolean isUserLoggedIn(String userID) {
        ArrayList<String> ids = dbUtils.getColumnData(0, Constants.mainSheet);

        int i = 0;
        for (String data : ids) {
            if (data.equals(userID)) {
                String cellData = dbUtils.getCellData(i, Constants.LOGGEDINCOLUMN, Constants.mainSheet);
                try {
                    cellData = cellData.replaceAll("\\s+", "");
                } catch (NullPointerException e) {
                    continue;
                    //do nothing because regex fails if it doesn't detect whitespace
                }

                return cellData.equals("TRUE");
            }
            i++;
        }

        //create user then login
        System.out.println("empty: " + dbUtils.nextEmptyCellColumn(Constants.mainSheet));
        dbUtils.setCellData(dbUtils.nextEmptyCellColumn(Constants.mainSheet), Constants.STUDENTIDCOLUMN, userID, Constants.mainSheet);

        return false;

    }

    void loginUser(String userID) {
        Platform.runLater(() -> {
            GrizzlyTimeGUI.setMessageBoxText("Logging in user: " + userID);
        });

        String currentTime = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        int userRow = dbUtils.getCellRowFromColumn(userID, Constants.STUDENTIDCOLUMN, Constants.mainSheet);

        //log the user in
        if (userRow != -1) {
            dbUtils.setCellData(userRow, Constants.LASTLOGINCOLUMN, currentTime, Constants.mainSheet);
            dbUtils.setCellData(userRow, Constants.LOGGEDINCOLUMN, "TRUE", Constants.mainSheet);
            dbUtils.setCellData(userRow, Constants.LASTLOGOUTCOLUMN, "LOGGED IN", Constants.mainSheet);

            Platform.runLater(() -> {
                GrizzlyTimeGUI.setMessageBoxText("Successfully logged in user: " + userID);
                GrizzlyTimeGUI.clearInput();
            });

        }

    }

    //logout the user
    void logoutUser(String userID) {
        Platform.runLater(() -> {
            GrizzlyTimeGUI.setMessageBoxText("Logging out user: " + userID);
        });

        //grab last logged in time
        String currentTime = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        int userRow = dbUtils.getCellRowFromColumn(userID, Constants.STUDENTIDCOLUMN, Constants.mainSheet);
        String oldTime = dbUtils.getCellData(userRow, Constants.LASTLOGINCOLUMN, Constants.mainSheet);
        oldTime = oldTime.replaceAll("\\s+","");

        //assuming userRow isn't invalid, calculate difference in time and log hours
        if (userRow != -1) {
            dbUtils.setCellData(userRow, Constants.LASTLOGOUTCOLUMN, currentTime, Constants.mainSheet);
            LocalTime date1;
            LocalTime date2;

            date1 = LocalTime.parse(oldTime);
            date2 = LocalTime.parse(currentTime);

            Duration difference = Duration.between(date1, date2);
            long durInSeconds = difference.getSeconds();
            String time = String.format("%02d:%02d:%02d", durInSeconds / 3600, (durInSeconds % 3600) / 60, (durInSeconds % 60));

            //logout the user if in the negative
            int err = time.indexOf('-');

            System.out.println("Time difference is: " + time);
            if (err == -1) {
                LocalTime start;
                LocalTime diffTimeObj;

                String totalHours = dbUtils.getCellData(userRow, Constants.TOTALHOURSCOLUMN, Constants.mainSheet);
                String diffTime = dbUtils.getCellData(userRow, Constants.HOURSCOLUMN, Constants.mainSheet);

                //check if any of the fields are blank
                try {
                    start = LocalTime.parse(totalHours);
                } catch (DateTimeParseException e) {
                    start = LocalTime.parse("00:00:01");
                }

                try {
                    diffTimeObj = LocalTime.parse(diffTime);
                } catch (DateTimeParseException e) {
                    diffTimeObj = LocalTime.parse("00:00:01");
                }

                LocalTime totalTime = start.plusHours(diffTimeObj.getHour()).plusMinutes(diffTimeObj.getMinute()).plusSeconds(diffTimeObj.getSecond());
                String timeTotal = String.format("%02d:%02d:%02d", totalTime.getHour(), totalTime.getMinute(), totalTime.getSecond());

                System.out.println("Set Data:");
                dbUtils.setCellData(userRow, Constants.HOURSCOLUMN, time, Constants.mainSheet);
                dbUtils.setCellData(userRow, Constants.TOTALHOURSCOLUMN, timeTotal, Constants.mainSheet);

                Platform.runLater(() -> {
                    GrizzlyTimeGUI.setMessageBoxText("Logged out user: " + userID);
                    GrizzlyTimeGUI.clearInput();
                });

            }

            dbUtils.setCellData(userRow, Constants.LOGGEDINCOLUMN, "FALSE", Constants.mainSheet);


        }

    }

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
