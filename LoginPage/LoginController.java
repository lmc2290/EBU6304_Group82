package LoginPage;

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
                authenticatedUser = new User(idStr, "MO");
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
        // Hide the login screen
        loginUI.setVisible(false);

        // TODO: Instantiate the specific concrete DashboardUI based on user.getRole()
        // Example: if (user.getRole().equals("TA")) { new TADashboardUI(user).setVisible(true); }

        System.out.println("Routing complete for: " + user.getRole());
    }
}