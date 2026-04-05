package LoginPage;

import java.awt.*;
import javax.swing.*;

public class AdminDashboardUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    protected User currentUser;

    public AdminDashboardUI(User user) {
        this.currentUser = user;
        initializeUI();
    }

    protected void initializeUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 247, 250));

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        Admin_TAWorkLoadControlUI taPanel = new Admin_TAWorkLoadControlUI(currentUser);
        Admin_CourseApplicationControlUI coursePanel = new Admin_CourseApplicationControlUI(currentUser);

        cardPanel.add(taPanel, "WORKLOAD");
        cardPanel.add(coursePanel, "REQUEST");
        add(cardPanel, BorderLayout.CENTER);

        JPanel bottomNav = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        bottomNav.setBackground(new Color(230, 233, 237)); 

        JButton workloadBtn = createNavButton("Workload Dashboard", new Color(41, 128, 185));
        JButton requestBtn = createNavButton("Approve Requests", new Color(41, 128, 185));
        
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

    private JButton createNavButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(200, 45));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE); // ensure text is visible on colored background
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        User fakeAdmin = new User("admin_test", "123456");
        SwingUtilities.invokeLater(() -> {
            AdminDashboardUI frame = new AdminDashboardUI(fakeAdmin);
            frame.setTitle("Admin Management System");
            frame.setSize(1100, 850);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

   
   
}