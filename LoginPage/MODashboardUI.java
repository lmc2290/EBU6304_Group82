package LoginPage;

import java.awt.*;
import javax.swing.*;

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
        buttonPanel.setLayout(new GridLayout(2, 2, 20, 20));
        buttonPanel.setBackground(new Color(247, 247, 247));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));

        JButton reviewAppBtn = new JButton("Review Applications");
        JButton scheduleInterviewBtn = new JButton("Schedule Interviews");
        JButton createVacancyBtn = new JButton("Create TA Vacancy");
        JButton viewApplicantsBtn = new JButton("View Applicants");

        reviewAppBtn.setPreferredSize(new Dimension(180, 40));
        scheduleInterviewBtn.setPreferredSize(new Dimension(180, 40));
        createVacancyBtn.setPreferredSize(new Dimension(180, 40));
        viewApplicantsBtn.setPreferredSize(new Dimension(180, 40));

        // Add action listeners
        viewApplicantsBtn.addActionListener(e -> new MOApplicantListUI(currentUser).setVisible(true));
        createVacancyBtn.addActionListener(e -> new MOJobVacancyUI(currentUser).setVisible(true));

        buttonPanel.add(viewApplicantsBtn);
        buttonPanel.add(createVacancyBtn);
        buttonPanel.add(reviewAppBtn);
        buttonPanel.add(scheduleInterviewBtn);

        add(buttonPanel, BorderLayout.CENTER);
    }
}