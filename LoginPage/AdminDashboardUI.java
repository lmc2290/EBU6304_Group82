package LoginPage;

import javax.swing.*;
import java.awt.*;

public class AdminDashboardUI extends DashBoardUI {

    public AdminDashboardUI(User user) {
        super(user); // Call the superclass constructor to set up the base UI (size, centering, etc.)
    }

    @Override
    protected void initializeUI() {
        // Set the specific layout for the Admin dashboard
        setLayout(new BorderLayout());

        // Add a welcome title
        JLabel welcomeLabel = new JLabel("Welcome to Admin Control Panel", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(welcomeLabel, BorderLayout.NORTH);

        // Add admin-specific functional buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20)); // Center buttons with 20px gaps
        buttonPanel.setBackground(new Color(247, 247, 247)); // Maintain the same background color as the base class

        JButton manageUsersBtn = new JButton("Manage Users");
        JButton viewReportsBtn = new JButton("View Reports");

        // Briefly set the button sizes
        manageUsersBtn.setPreferredSize(new Dimension(150, 40));
        viewReportsBtn.setPreferredSize(new Dimension(150, 40));

        buttonPanel.add(manageUsersBtn);
        buttonPanel.add(viewReportsBtn);

        add(buttonPanel, BorderLayout.CENTER);
    }
}