package LoginPage;

import javax.swing.*;
import AdminPage.AdminDashboardUI;
import TAUI.TADashboardUI;
import TAUI.TAController;
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
        if (idStr.isEmpty() || password.isEmpty()) {
            loginUI.showError("Please enter both ID and Password!");
            return;
        }

        // 调用真实数据
        User authenticatedUser = UnifiedDataStore.authenticateUser(idStr, password);

        if (authenticatedUser != null) {
            loginUI.showMessage(authenticatedUser.getRole() + " identity authenticated. Routing to Dashboard...");
            routeToDashboard(authenticatedUser);
        } else {
            loginUI.showError("Invalid ID or Password! Please try again.");
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

    private DashBoardUI createDashboardForUser(User user) {
        switch (user.getRole()) {
            case "Admin":
                return new AdminDashboardUI(user);
            case "MO":
                return new MODashboardUI(user);
            case "TA":
                // 👇 确保这里是这两行代码！
                TAController taController = new TAController();
                return new TADashboardUI(user, taController);
            default:
                loginUI.showError("Unknown role detected.");
                return null;
        }
    }
}
