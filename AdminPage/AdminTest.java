package AdminPage;

import LoginPage.User;
import javax.swing.*;

public class AdminTest {

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception e) {
        }

        User adminTestUser =
                new User("Admin_Tester", "test123");

        SwingUtilities.invokeLater(() -> {

            AdminDashboardUI dashboard =
                    new AdminDashboardUI(adminTestUser);

            dashboard.setTitle(
                    "ADMIN MODULE DEMO - INDIVIDUAL WORK"
            );

            dashboard.setSize(1100, 850);
            dashboard.setDefaultCloseOperation(
                    JFrame.EXIT_ON_CLOSE
            );
            dashboard.setLocationRelativeTo(null);
            dashboard.setVisible(true);
        });
    }
}