package LoginPage;

import javax.swing.*;
import java.awt.*;

public class MODashboardUI extends DashBoardUI {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JButton applicantBtn;
    private JButton vacancyBtn;
    private JLabel welcomeLabel;
    private JLabel moduleLabel;

    public MODashboardUI(User user) {
        super(user);
    }

    @Override
    protected void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createNavigationPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        welcomeLabel = new JLabel("Welcome, MO " + currentUser.getId(), SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        moduleLabel = new JLabel("Module: " + currentUser.getModuleName(), SwingConstants.CENTER);
        moduleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        moduleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(welcomeLabel);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(moduleLabel);

        return headerPanel;
    }

    private JPanel createCenterPanel() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JPanel applicantPage = new MOApplicantManagementUI(currentUser);
        JPanel vacancyPage = new MOVacancyManagementUI(currentUser);

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

    private void showVacancyPage() {
        cardLayout.show(cardPanel, "VACANCY");
    }
}