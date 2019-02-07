package activities;

import databases.BatchUpdateData;
import databases.DatabaseUtils;
import helpers.AlertUtils;
import helpers.CommonUtils;
import helpers.Constants;
import notifiers.LoginNotifier;

import java.util.ArrayList;

public class LoginActivity {
    private CommonUtils utils = new CommonUtils();
    private AlertUtils alertUtils = new AlertUtils();
    private LoginNotifier notifier = new LoginNotifier();

    private DatabaseUtils dbUtils;

    public final static boolean grizzlyPrompt = LocalDbActivity.kGrizzlyVerification;

    public LoginActivity(DatabaseUtils dbUtils) {
        this.dbUtils = dbUtils;

    }

    public void loginUser(int userRow, String currentTime) {
        ArrayList<BatchUpdateData> data = new ArrayList<>();

        data.add(new BatchUpdateData(userRow, Constants.kLastLoginColumn, currentTime));
        data.add(new BatchUpdateData(userRow, Constants.kLoggedInColumn, "TRUE"));
        data.add(new BatchUpdateData(userRow, Constants.kLastLogoutColumn, "LOGGED IN"));

        dbUtils.setCellDataBatch(data, Constants.kMainSheet);

        if (grizzlyPrompt && !notifier.checkNotifier(userRow, dbUtils)) {
            utils.playDing();

            alertUtils.createAlert("Registration not complete!", "Registration not complete!", "It seems you have not completed your user registration!" +
                    " Please visit https://ycsrobotics.org/registration to finish your registration");

        }
    }
}
