package scenes;

import databases.DatabaseUtils;
import helpers.Constants;
import helpers.LoggingUtil;

import java.util.ArrayList;
import java.util.logging.Level;

public class LoginNotifier {

    private ArrayList<String> firstNamesListReg;
    private ArrayList<String> lastNamesListReg;

    private String firstName;
    private String lastName;

    public boolean checkNotifier(int studentIDRow, DatabaseUtils dbUtils) {
        firstName = dbUtils.getCellData(studentIDRow, Constants.FIRSTNAMECOLUMN, Constants.mainSheet);
        lastName = dbUtils.getCellData(studentIDRow, Constants.LASTNAMECOLUMN, Constants.mainSheet);

        firstNamesListReg = dbUtils.getColumnData(0, Constants.registrationSheet);
        lastNamesListReg = dbUtils.getColumnData(1, Constants.registrationSheet);

        if (matchName(firstName, firstNamesListReg)){
            LoggingUtil.log(Level.INFO, firstName + " was detected in user registration");
            if (matchName(lastName, lastNamesListReg)){
                LoggingUtil.log(Level.INFO, lastName + " was detected in user registration");
                return true;
            }

            LoggingUtil.log(Level.INFO, lastName + " was not detected in user registration");

            return false;
        }

        LoggingUtil.log(Level.INFO, firstName + " was not detected in user registration");

        return false;
    }

    private boolean matchName(String name, ArrayList<String> nameList) {
        name = name.toLowerCase();

        for (String nameInList : nameList) {
            if (nameInList.toLowerCase().equals(name)) {
                return true;
            }
        }

        return false;
    }

}
