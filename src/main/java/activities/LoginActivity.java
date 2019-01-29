package activities;

import databases.DatabaseUtils;
import helpers.AlertUtils;
import helpers.CommonUtils;
import helpers.Constants;
import notifiers.LoginNotifier;

public class LoginActivity {
    private CommonUtils utils = new CommonUtils();
    private AlertUtils alertUtils = new AlertUtils();
    private LoginNotifier notifier = new LoginNotifier();

    private DatabaseUtils dbUtils;

    public LoginActivity(DatabaseUtils dbUtils) {
        this.dbUtils = dbUtils;

    }

    public void loginUser(int userRow, String currentTime) {
        dbUtils.setCellData(userRow, Constants.kLastLoginColumn, currentTime, Constants.kMainSheet);
        dbUtils.setCellData(userRow, Constants.kLoggedInColumn, "TRUE", Constants.kMainSheet);
        dbUtils.setCellData(userRow, Constants.kLastLogoutColumn, "LOGGED IN", Constants.kMainSheet);

        if (Constants.kGrizzlyPrompt && !notifier.checkNotifier(userRow, dbUtils)) {
            utils.playDing();

            alertUtils.createAlert("Registration not complete!", "Registration not complete!", "It seems you have not completed your user registration!" +
                    " Please visit https://ycsrobotics.org/registration to finish your registration");

        }
    }
}
