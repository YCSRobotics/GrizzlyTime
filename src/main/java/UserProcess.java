import com.google.api.client.util.DateTime;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import databases.DatabaseUtils;
import helpers.Constants;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;

public class UserProcess {

    private DatabaseUtils dbUtils = new DatabaseUtils();

    public boolean isUserLoggedIn(String userID) {
        ArrayList<String> ids = dbUtils.getColumnData(0);

        int i = 0;
        for (String data : ids) {
            if (data.equals(userID)) {
                String cellData = dbUtils.getCellData(i, Constants.LOGGEDINCOLUMN);
                cellData = cellData.replaceAll("\\s+","");

                return cellData.equals("TRUE");
            }
            i++;
        }

        //TODO Create user if user doesn't exist
        return false;

    }

    public void loginUser(String userID) {
        String currentTime = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        int userRow = dbUtils.getCellRowFromColumn(userID, Constants.STUDENTIDCOLUMN);

        //log the user in
        if (userRow != -1) {
            dbUtils.setCellData(userRow, Constants.LASTLOGINCOLUMN, currentTime);
            dbUtils.setCellData(userRow, Constants.LOGGEDINCOLUMN, "TRUE");
            dbUtils.setCellData(userRow, Constants.LASTLOGOUTCOLUMN, "LOGGED IN");

        }

    }

    //logout the user
    public void logoutUser(String userID) {
        //grab last logged in time
        String currentTime = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        int userRow = dbUtils.getCellRowFromColumn(userID, Constants.STUDENTIDCOLUMN);
        String oldTime = dbUtils.getCellData(userRow, Constants.LASTLOGINCOLUMN);
        oldTime = oldTime.replaceAll("\\s+","");

        //assuming userRow isn't invalid, calculate difference in time and log hours
        if (userRow != -1) {
            dbUtils.setCellData(userRow, Constants.LASTLOGOUTCOLUMN, currentTime);
            LocalTime date1;
            LocalTime date2;

            date1 = LocalTime.parse(oldTime);
            date2 = LocalTime.parse(currentTime);

            Duration difference = Duration.between(date1, date2);
            long durInSeconds = difference.getSeconds();
            String time = String.format("%02d:%02d:%02d", durInSeconds / 3600, (durInSeconds % 3600) / 60, (durInSeconds % 60));

            System.out.println("Time difference is: " + time);
            dbUtils.setCellData(userRow, Constants.TOTALHOURSCOLUMN, time);
            dbUtils.setCellData(userRow, Constants.LOGGEDINCOLUMN, "FALSE");

        }

    }

}
