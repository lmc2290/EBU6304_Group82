package AdminPage;

import LoginPage.DashBoardUI;
import LoginPage.User;
import java.awt.*;
import javax.swing.*;

public class AdminDashboardUI extends DashBoardUI {

    private CardLayout cardLayout;
    private JPanel cardPanel;

    // 必须声明两个控制类成员变量
    private Admin_TAWorkLoadControl taControl;
    private Admin_CourseApplicationControl courseControl;

    public AdminDashboardUI(User user) {
        super(user);
    }

    @Override
    protected void initializeUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 247, 250));

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // ===================== 正确写法 =====================
        // 1. Workload 面板
        taControl = new Admin_TAWorkLoadControl(currentUser);
        Admin_TAWorkLoadControlUI taPanel = taControl.getUi();

        // 2. 课程审批面板（重要：必须这样写！）
        courseControl = new Admin_CourseApplicationControl(currentUser);
        Admin_CourseApplicationControlUI coursePanel = courseControl.getUi();

        cardPanel.add(taPanel, "WORKLOAD");
        cardPanel.add(coursePanel, "REQUEST");

        add(cardPanel, BorderLayout.CENTER);

        // ===================== 底部导航 =====================
        JPanel bottomNav = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        bottomNav.setBackground(new Color(230, 233, 237));

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

        // ===================== 切换面板 =====================
        workloadBtn.addActionListener(e -> {
            cardLayout.show(cardPanel, "WORKLOAD");
            // 优化：刷新TA面板的标签显示（避免切换时标签为空）
            taControl.getUi().refreshLimitLabels();
            taControl.getUi().repaint();
        });

        requestBtn.addActionListener(e -> {
            cardLayout.show(cardPanel, "REQUEST");
            // 可选：切换到课程面板时刷新数据
            courseControl.loadData();
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