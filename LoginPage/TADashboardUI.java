package LoginPage;

import javax.swing.*;
import java.awt.*;

public class TADashboardUI extends DashBoardUI {

    public TADashboardUI(User user) {
        super(user); // Call the superclass constructor
    }

    @Override
    protected void initializeUI() {
        // Set the specific layout for the TA (Teaching Assistant / Student) dashboard
        setLayout(new BorderLayout());

        // Add a welcome title
        JLabel welcomeLabel = new JLabel("Welcome to TA Portal", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(welcomeLabel, BorderLayout.NORTH);

        // Add TA-specific functional buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(new Color(247, 247, 247));

        JButton submitAppBtn = new JButton("Submit Application");
        JButton viewStatusBtn = new JButton("View Status");

        submitAppBtn.setPreferredSize(new Dimension(160, 40));
        viewStatusBtn.setPreferredSize(new Dimension(160, 40));

        buttonPanel.add(submitAppBtn);
        buttonPanel.add(viewStatusBtn);

        add(buttonPanel, BorderLayout.CENTER);
    }
}
