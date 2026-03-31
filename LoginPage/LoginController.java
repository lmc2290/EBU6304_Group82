package LoginPage;

import javax.swing.*;

/**
 * Control Class
 * Encapsulates the coordination and authentication logic.
 */
public class LoginController {

    private LoginUI loginUI;

    public void setLoginUI(LoginUI loginUI) {
        this.loginUI = loginUI;
    }

    public void authenticate(String idStr, String password) {
        if (idStr == null || password == null || idStr.trim().isEmpty() || password.trim().isEmpty()) {
            loginUI.showError("Please enter both ID and Password!");
            return;
        }

        try {
            int idNum = Integer.parseInt(idStr.trim());
            User authenticatedUser;

            if (idNum == 0) {
                authenticatedUser = new User(idStr, "Admin");
                loginUI.showMessage("Admin identity detected. Routing to Admin Dashboard...");
                routeToDashboard(authenticatedUser);

            } else if (idNum >= 1 && idNum <= 100) {
                authenticatedUser = new User(idStr, "MO", "CS101");
                loginUI.showMessage("MO identity detected. Routing to MO Dashboard...");
                routeToDashboard(authenticatedUser);

            } else {
                authenticatedUser = new User(idStr, "TA");
                loginUI.showMessage("TA (Student) identity detected. Routing to TA Dashboard...");
                routeToDashboard(authenticatedUser);
            }

        } catch (NumberFormatException ex) {
            loginUI.showError("ID must be a valid number!");
        } catch (Exception ex) {
            ex.printStackTrace();
            loginUI.showError("Login failed: " + ex.getMessage());
        }
    }

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
                dashboard = new TADashboardUI(user);
                break;
            default:
                loginUI.showError("Unknown role detected.");
                loginUI.setVisible(true);
                return;
        }

        if (dashboard != null) {
            dashboard.setVisible(true);
            System.out.println("Routing complete for: " + user.getRole());
        }
    }
}