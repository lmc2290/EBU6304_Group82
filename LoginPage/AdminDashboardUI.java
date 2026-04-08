package LoginPage;

import java.awt.*;
import javax.swing.*;

public class AdminDashboardUI extends DashBoardUI {
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public AdminDashboardUI(User user) {
        super(user);
    }

    @Override
    protected void initializeUI() {
        // Main layout setup
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(247, 247, 247));

        // Card panel for switching between sub-panels
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        Admin_TAWorkLoadControlUI taPanel = new Admin_TAWorkLoadControlUI(currentUser);
        Admin_CourseApplicationControlUI coursePanel = new Admin_CourseApplicationControlUI(currentUser);

        cardPanel.add(taPanel, "WORKLOAD");
        cardPanel.add(coursePanel, "REQUEST");

        add(cardPanel, BorderLayout.CENTER);

        // Bottom navigation bar
        JPanel bottomNav = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 20));
        bottomNav.setBackground(new Color(247, 247, 247));

        // Workload button
        JButton workloadBtn = new JButton("Workload");
        workloadBtn.setPreferredSize(new Dimension(180, 45));
        workloadBtn.setBackground(new Color(0, 102, 204));
        workloadBtn.setForeground(Color.WHITE);
        workloadBtn.setFocusPainted(false);

        // Request button
        JButton requestBtn = new JButton("Request");
        requestBtn.setPreferredSize(new Dimension(180, 45));
        requestBtn.setBackground(new Color(0, 102, 204));
        requestBtn.setForeground(Color.WHITE);
        requestBtn.setFocusPainted(false);

        // Mailbox icon button
        JButton mailboxBtn = new JButton();
        mailboxBtn.setIcon(UIManager.getIcon("OptionPane.informationIcon"));
        mailboxBtn.setPreferredSize(new Dimension(60, 45));
        mailboxBtn.setBackground(Color.WHITE);
        mailboxBtn.setFocusPainted(false);

        bottomNav.add(workloadBtn);
        bottomNav.add(requestBtn);
        bottomNav.add(mailboxBtn);
        add(bottomNav, BorderLayout.SOUTH);

        // Switch panel actions
        workloadBtn.addActionListener(e -> cardLayout.show(cardPanel, "WORKLOAD"));
        requestBtn.addActionListener(e -> cardLayout.show(cardPanel, "REQUEST"));
    }
    // =====================  ADMIN MODULE TEST =====================

public static void main(String[] args) {
    // Setting System Look and Feel for modern UI appearance
    try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}

    // Mock User object for Admin role (String username, String password)
    User adminTestUser = new User("Admin_Tester", "test123");
    
    // Launch the Dashboard
    javax.swing.SwingUtilities.invokeLater(() -> {
        AdminDashboardUI dashboard = new AdminDashboardUI(adminTestUser);
        dashboard.setTitle("ADMIN MODULE DEMO - INDIVIDUAL WORK");
        dashboard.setSize(1100, 850);
        dashboard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dashboard.setLocationRelativeTo(null);
        dashboard.setVisible(true);
    });
}
}