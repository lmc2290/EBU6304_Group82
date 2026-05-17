package LoginPage;

import java.awt.*;
import javax.swing.*;

/**
 * Abstract Boundary Class
 * Base class for all specific dashboard UIs (Admin, MO, TA).
 */
public abstract class DashBoardUI extends JFrame {
    protected User currentUser;

    public DashBoardUI(User user) {
        this.currentUser = user;
        setTitle("Dashboard - " + user.getRole());
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        getContentPane().setBackground(new Color(247, 247, 247));

        // Call the abstract method to let subclasses build their specific UI
        initializeUI();
    }

    /**
     * Abstract method that concrete dashboards must implement
     * to define their specific layout and components.
     */
    protected abstract void initializeUI();

    /**
     * Opens a sub-panel in a maximized JDialog.
     */
    protected void openPanelMaximized(String title, JPanel panel) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentWindow, title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setContentPane(panel);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        dialog.setSize(screenSize.width, screenSize.height);
        dialog.setLocation(0, 0);
        dialog.setVisible(true);
    }
}
