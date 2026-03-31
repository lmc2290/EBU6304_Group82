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

        cardPanel.add(applicantPage, "APPLICANTS");
        cardPanel.add(vacancyPage, "VACANCY");

        cardLayout.show(cardPanel, "APPLICANTS");

        return cardPanel;
    }

    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        navPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 15, 10));

        applicantBtn = new JButton("Manage Applicants");
        vacancyBtn = new JButton("Post Vacancy");

        applicantBtn.setPreferredSize(new Dimension(180, 40));
        vacancyBtn.setPreferredSize(new Dimension(180, 40));

        applicantBtn.addActionListener(e -> showApplicantsPage());
        vacancyBtn.addActionListener(e -> showVacancyPage());

        navPanel.add(applicantBtn);
        navPanel.add(vacancyBtn);

        return navPanel;
    }

    private void showApplicantsPage() {
        cardLayout.show(cardPanel, "APPLICANTS");
    }

    private void showVacancyPage() {
        cardLayout.show(cardPanel, "VACANCY");
    }
}