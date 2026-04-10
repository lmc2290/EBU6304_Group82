package AdminPage;

import LoginPage.DashBoardUI;
import LoginPage.User;
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
        getContentPane().setBackground(new Color(245, 247, 250));

        // Card panel for switching between sub-panels
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        Admin_TAWorkLoadControlUI taPanel = new Admin_TAWorkLoadControlUI(currentUser);
        Admin_CourseApplicationControlUI coursePanel = new Admin_CourseApplicationControlUI(currentUser);

        cardPanel.add(taPanel, "WORKLOAD");
        cardPanel.add(coursePanel, "REQUEST");

        add(cardPanel, BorderLayout.CENTER);
        // Bottom navigation bar
        JPanel bottomNav = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        bottomNav.setBackground(new Color(230, 233, 237)); 

        // fix :ensure the words are visible
        JButton workloadBtn = createNavBtn("Workload Dashboard", new Color(41, 128, 185));
        JButton requestBtn = createNavBtn("Approve Requests", new Color(41, 128, 185));
        
        JButton mailboxBtn = new JButton();
        mailboxBtn.setIcon(UIManager.getIcon("OptionPane.informationIcon"));
        mailboxBtn.setPreferredSize(new Dimension(60, 45));
        mailboxBtn.setBackground(Color.WHITE);
        mailboxBtn.setFocusPainted(false);

        bottomNav.add(workloadBtn);
        bottomNav.add(requestBtn);
        bottomNav.add(mailboxBtn);
        add(bottomNav, BorderLayout.SOUTH);

        workloadBtn.addActionListener(e -> cardLayout.show(cardPanel, "WORKLOAD"));
        requestBtn.addActionListener(e -> cardLayout.show(cardPanel, "REQUEST"));
    }

    private JButton createNavBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(200, 45));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE); // 确保文字白色可见
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
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