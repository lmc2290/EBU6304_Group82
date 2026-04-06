package LoginPage;

import javax.swing.*;
import java.awt.*;

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
        getContentPane().setBackground(new Color(247, 247, 247));

        // Call the abstract method to let subclasses build their specific UI
        initializeUI();
    }

    /**
     * Abstract method that concrete dashboards must implement
     * to define their specific layout and components.
     */
    protected abstract void initializeUI();
}