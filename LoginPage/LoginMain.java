package LoginPage;
import javax.swing.SwingUtilities;

/**
 * This is a test class for Login page.
 */
public class LoginMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Create the Control
            LoginController controller = new LoginController();

            // 2. Create the Boundary and link it to the Control
            LoginUI loginUI = new LoginUI(controller);

            // 3. Link the Control back to the Boundary
            controller.setLoginUI(loginUI);

            // 4. Show the UI
            loginUI.setVisible(true);
        });
    }
}