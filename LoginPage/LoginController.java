package LoginPage;

import AdminPage.AdminDashboardUI;
import TAUI.TADashboardUI;
import TAUI.TAController;
import javax.swing.*;

/**
 * Control Class
 * Encapsulates the coordination and authentication logic.
 */
public class LoginController {

    private LoginUI loginUI;

    // Link the controller to the boundary
    public void setLoginUI(LoginUI loginUI) {
        this.loginUI = loginUI;
    }

    /**
     * Processes the login attempt.
     */
    public void authenticate(String idStr, String password) {
        // Form null/empty check
        if (idStr.isEmpty() || password.isEmpty()) {
            loginUI.showError("Please enter both ID and Password!");
            return;
        }

        try {
            int idNum = Integer.parseInt(idStr);
            User authenticatedUser;

            // Identity verification logic & Entity creation -- detail can be changed
            if (idNum == 0) {
                authenticatedUser = new User(idStr, "Admin");
                loginUI.showMessage("Admin identity detected. Routing to Admin Dashboard...");
                routeToDashboard(authenticatedUser);

            } else if (idNum >= 1 && idNum <= 100) {
                // 给 MO 分配 moduleName，避免 applicant list 为空
                String moduleName = assignModuleToMO(idNum);
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
     * Assigns module to MO based on ID
     */
    private String assignModuleToMO(int idNum) {
        if (idNum <= 50) {
            return "CS101";
        } else {
            return "CS202";
        }
    }

    /**
     * Routes the user to the appropriate dashboard based on their role.
     */
    private void routeToDashboard(User user) {
        // Hide the login screen
        loginUI.setVisible(false);

        // Instantiate the specific dashboard based on the user's role
        DashBoardUI dashboard = createDashboardForUser(user);

        // Display the corresponding dashboard
        if (dashboard != null) {
            dashboard.setVisible(true);
            System.out.println("Routing complete for: " + user.getRole());
            System.out.println("User module: " + user.getModuleName());
        }
    }

    /**
     * Creates the appropriate dashboard based on user role
     */
    private DashBoardUI createDashboardForUser(User user) {
        switch (user.getRole()) {
            case "Admin":
                return new AdminDashboardUI(user);
            case "MO":
                return new MODashboardUI(user);
            case "TA":
                return new TADashboardUI(user, new TAController());
            default:
                loginUI.showError("Unknown role detected.");
                return null;
        }
    }
}
