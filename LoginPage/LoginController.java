package LoginPage;

import javax.swing.*;

import TAUI.TADashboardUI;
import TAUI.TAController;

/**
 * Control Class
 * Encapsulates the coordination and authentication logic.
 */
public class LoginController {

    private LoginUI loginUI;

    public void setLoginUI(LoginUI loginUI) {
        this.loginUI = loginUI;
    }

    /**
     * Processes the login attempt.
     */
    public void authenticate(String idStr, String password) {
        if (idStr.isEmpty() || password.isEmpty()) {
            loginUI.showError("Please enter both ID and Password!");
            return;
        }

        try {
            int idNum = Integer.parseInt(idStr);
            User authenticatedUser;

            if (idNum == 0) {
                authenticatedUser = new User(idStr, "Admin");
                loginUI.showMessage("Admin identity detected. Routing to Admin Dashboard...");
                routeToDashboard(authenticatedUser);

            } else if (idNum >= 1 && idNum <= 100) {
                // 给 MO 分配 moduleName，避免 applicant list 为空
                String moduleName;
                if (idNum <= 50) {
                    moduleName = "CS101";
                } else {
                    moduleName = "CS202";
                }

                authenticatedUser = new User(idStr, "MO", moduleName);
                loginUI.showMessage("MO identity detected. Routing to MO Dashboard...");
                routeToDashboard(authenticatedUser);

            } else {
                authenticatedUser = new User(idStr, "TA");
                loginUI.showMessage("TA (Student) identity detected. Routing to TA Dashboard...");
                routeToDashboard(authenticatedUser);
            }

        } catch (NumberFormatException ex) {
            loginUI.showError("ID must be a valid number!");
        }
    }

    /**
     * Routes the user to the appropriate dashboard based on their role.
     */
    private void routeToDashboard(User user) {
        loginUI.setVisible(false);

        DashBoardUI dashboard = null;

        switch (user.getRole()) {
            case "Admin":
                dashboard = new AdminDashboardUI(user);
                break;
            case "MO":
                dashboard = new MODashboardUI(user);
                break;
            case "TA":
                TAController taController = new TAController();
                dashboard = new TADashboardUI(user, taController);
                break;
            default:
                loginUI.showError("Unknown role detected.");
                return;
        }

        if (dashboard != null) {
            dashboard.setVisible(true);
            System.out.println("Routing complete for: " + user.getRole());
            System.out.println("User module: " + user.getModuleName());
        }
    }
}