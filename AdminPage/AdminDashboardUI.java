package AdminPage;

import LoginPage.DashBoardUI;
import LoginPage.User;
import java.awt.*;
import javax.swing.*;

public class AdminDashboardUI extends DashBoardUI {

    private CardLayout cardLayout;
    private JPanel cardPanel;

    private Admin_TAWorkLoadControl taControl;
    private Admin_CourseApplicationControl courseControl;
    private Admin_MessageControl messageControl;

    public AdminDashboardUI(User user) {
        super(user);
    }

    @Override
    protected void initializeUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 247, 250));

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        taControl = new Admin_TAWorkLoadControl(currentUser);
        Admin_TAWorkLoadControlUI taPanel = taControl.getUi();

        courseControl = new Admin_CourseApplicationControl(currentUser);
        Admin_CourseApplicationControlUI coursePanel = courseControl.getUi();

        messageControl = new Admin_MessageControl();
        Admin_MessageUI messagePanel = messageControl.getUi();

        cardPanel.add(taPanel, "WORKLOAD");
        cardPanel.add(coursePanel, "REQUEST");
        cardPanel.add(messagePanel, "MESSAGE");

        add(cardPanel, BorderLayout.CENTER);

        JPanel bottomNav = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        bottomNav.setBackground(new Color(230, 230, 230));

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

        workloadBtn.addActionListener(e -> {
            cardLayout.show(cardPanel, "WORKLOAD");
            taControl.getUi().refreshLimitLabels();
            taControl.getUi().repaint();
        });

        requestBtn.addActionListener(e -> {
            cardLayout.show(cardPanel, "REQUEST");
            courseControl.loadData();
        });

        mailboxBtn.addActionListener(e -> {
            cardLayout.show(cardPanel, "MESSAGE");
        });
    }

    private JButton createNavBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(200, 45));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}