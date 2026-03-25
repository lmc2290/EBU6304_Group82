package LoginPage;

import javax.swing.*;
import java.awt.*;

public class AdminDashboardUI extends DashBoardUI {
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public AdminDashboardUI(User user) {
        super(user);
    }

    @Override
    protected void initializeUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(247, 247, 247));

        // ===================== 中间卡片区域（两个界面完全重合） =====================
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        Admin_TAWorkLoadControlUI taPanel = new Admin_TAWorkLoadControlUI(currentUser);
        Admin_CourseApplicationControlUI coursePanel = new Admin_CourseApplicationControlUI(currentUser);

        cardPanel.add(taPanel, "WORKLOAD");
        cardPanel.add(coursePanel, "REQUEST");

        add(cardPanel, BorderLayout.CENTER);

        // ===================== 底部固定导航栏（完全按你要求） =====================
        JPanel bottomNav = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 20));
        bottomNav.setBackground(new Color(247, 247, 247));

        // -------- 按钮1：Workload（加宽 + 主题蓝） --------
        JButton workloadBtn = new JButton("Workload");
        workloadBtn.setPreferredSize(new Dimension(180, 45));
        workloadBtn.setBackground(new Color(0, 102, 204));
        workloadBtn.setForeground(Color.WHITE);
        workloadBtn.setFocusPainted(false);

        // -------- 按钮2：Request（加宽 + 主题蓝） --------
        JButton requestBtn = new JButton("Request");
        requestBtn.setPreferredSize(new Dimension(180, 45));
        requestBtn.setBackground(new Color(0, 102, 204));
        requestBtn.setForeground(Color.WHITE);
        requestBtn.setFocusPainted(false);

        // -------- 按钮3：Mailbox → 小信封图标（无文字） --------
        JButton mailboxBtn = new JButton();
        mailboxBtn.setIcon(UIManager.getIcon("OptionPane.informationIcon")); // 小信封/消息图标
        mailboxBtn.setPreferredSize(new Dimension(60, 45));
        mailboxBtn.setBackground(Color.WHITE);
        mailboxBtn.setFocusPainted(false);

        // 添加到导航栏
        bottomNav.add(workloadBtn);
        bottomNav.add(requestBtn);
        bottomNav.add(mailboxBtn);
        add(bottomNav, BorderLayout.SOUTH);

        // ===================== 切换功能 =====================
        workloadBtn.addActionListener(e -> cardLayout.show(cardPanel, "WORKLOAD"));
        requestBtn.addActionListener(e -> cardLayout.show(cardPanel, "REQUEST"));
    }
}