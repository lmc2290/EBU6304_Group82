package LoginPage;

import javax.swing.*;
import java.awt.*;

public class MODashboardUI extends DashBoardUI {

    public MODashboardUI(User user) {
        super(user); // Call the superclass constructor
    }

    @Override
    protected void initializeUI() {
        // Set the specific layout for the MO (Management Officer) dashboard
        setLayout(new BorderLayout());

        // Add a welcome title
        JLabel welcomeLabel = new JLabel("Welcome to MO Dashboard", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(welcomeLabel, BorderLayout.NORTH);

        // Add MO-specific functional buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(new Color(247, 247, 247));

        JButton reviewAppBtn = new JButton("Review Applications");
        JButton scheduleInterviewBtn = new JButton("Schedule Interviews");

        reviewAppBtn.setPreferredSize(new Dimension(180, 40));
        scheduleInterviewBtn.setPreferredSize(new Dimension(180, 40));

        buttonPanel.add(reviewAppBtn);
        buttonPanel.add(scheduleInterviewBtn);

        add(buttonPanel, BorderLayout.CENTER);
    }
}