package TAUI;

import javax.swing.*;

/**
 * Application entry point
 * Responsible for instantiating the Control and binding it to the Boundary.
 */
public class TATest {
    public static void main(String[] args) {
        // Ensure GUI creation runs on the Event Dispatch Thread (EDT) to avoid thread safety issues
        SwingUtilities.invokeLater(() -> {
            // 1. Create the controller (the brain)
            TAController controller = new TAController();
            LoginPage.User mockUser = new LoginPage.User("10086", "TA");

            // 2. Create the boundary interface and pass in the controller (the shell)
            TADashboardUI dashboard = new TADashboardUI(mockUser, controller);

            // 3. Show the interface
            dashboard.setVisible(true);
        });
    }
}