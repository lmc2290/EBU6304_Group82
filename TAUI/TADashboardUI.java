package TAUI;

import LoginPage.DashBoardUI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

/**
 * Boundary Class - TA Dashboard
 * Redesigned with a CardLayout to support a Master-Detail workflow:
 * Grid of Job Cards -> Click -> Detailed View.
 */
public class TADashboardUI extends DashBoardUI {

    private TAController controller;

    // UI Components for Filters
    private JComboBox<String> moduleCombo;
    private JComboBox<String> statusCombo;
    private JComboBox<String> jobTypeCombo;
    private JComboBox<String> skillsCombo;
    private JTextField searchField;

    // Right Panel Card Layout Components
    private JPanel rightContainer;
    private CardLayout cardLayout;
    private JPanel gridPanel;

    // Detail View Components
    private JTextArea detailsArea;
    private JButton applyBtn;
    private Job currentSelectedJob; // 追踪当前正在查看的岗位

    public TADashboardUI(LoginPage.User user, TAController controller) {
        super(user);
        this.controller = controller;
        loadInitialData();
    }

    @Override
    protected void initializeUI() {
        setTitle("Teaching Assistant Dashboard - Job Portal (" + currentUser.getId() + ")");
        setSize(1000, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        buildSplitPane();
    }

    private void buildSplitPane() {
        // ==========================================
        // 1. LEFT PANEL (Personal Info & Filters)
        // ==========================================
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setPreferredSize(new Dimension(300, 0));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- 1.1 Personal Info Section ---
        JPanel personalPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        personalPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Personal Hub", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14)));

        JButton myAppsBtn = new JButton("My Applications");
        myAppsBtn.setBackground(new Color(255, 140, 0));
        myAppsBtn.setForeground(Color.WHITE);
        myAppsBtn.setFont(new Font("Arial", Font.BOLD, 14));
        myAppsBtn.setFocusPainted(false);

        JButton manageCVBtn = new JButton("My Profile");
        manageCVBtn.setBackground(new Color(51, 153, 255));
        manageCVBtn.setForeground(Color.WHITE);
        manageCVBtn.setFont(new Font("Arial", Font.BOLD, 14));
        manageCVBtn.setFocusPainted(false);

        personalPanel.add(myAppsBtn);
        personalPanel.add(manageCVBtn);

        // --- 1.2 Filters Section ---
        JPanel filterPanel = new JPanel(new GridLayout(11, 1, 5, 5));
        filterPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Search & Filter", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14)));

        filterPanel.add(new JLabel("Module:"));
        moduleCombo = new JComboBox<>(new String[]{"All", "ECS401", "ECS414", "ECS505"});
        filterPanel.add(moduleCombo);

        filterPanel.add(new JLabel("Status:"));
        statusCombo = new JComboBox<>(new String[]{"All", "Open Only", "Closed"});
        filterPanel.add(statusCombo);

        filterPanel.add(new JLabel("Job Type:"));
        jobTypeCombo = new JComboBox<>(new String[]{"All", "Lab Assistant", "Grader", "Tutor", "Invigilator"});
        filterPanel.add(jobTypeCombo);

        filterPanel.add(new JLabel("Required Skills:"));
        skillsCombo = new JComboBox<>(new String[]{"All", "Java", "Python", "MATLAB"});
        filterPanel.add(skillsCombo);

        filterPanel.add(new JLabel("Keyword:"));
        searchField = new JTextField();
        filterPanel.add(searchField);

        // --- 1.3 Buttons Section ---
        JPanel searchBtnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        JButton searchBtn = new JButton("Search");
        JButton clearBtn = new JButton("Clear Filters");
        searchBtnPanel.add(searchBtn);
        searchBtnPanel.add(clearBtn);
        filterPanel.add(searchBtnPanel);

        // Combine Left Elements
        JPanel leftTopContainer = new JPanel(new BorderLayout(0, 20));
        leftTopContainer.add(personalPanel, BorderLayout.NORTH);
        leftTopContainer.add(filterPanel, BorderLayout.CENTER);
        leftPanel.add(leftTopContainer, BorderLayout.NORTH);


        // ==========================================
        // 2. RIGHT PANEL (CardLayout: Grid <-> Details)
        // ==========================================
        cardLayout = new CardLayout();
        rightContainer = new JPanel(cardLayout);

        // --- Card 1: Job Grid View ---
        // 使用 2 列的网格布局，行数自适应，卡片之间有 15px 间距
        gridPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JScrollPane gridScroll = new JScrollPane(gridPanel);
        gridScroll.getVerticalScrollBar().setUnitIncrement(16);
        gridScroll.setBorder(BorderFactory.createTitledBorder("Available Opportunities"));

        // --- Card 2: Job Details View ---
        JPanel detailPanel = new JPanel(new BorderLayout(10, 10));
        detailPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Back Button (Top)
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backBtn = new JButton("← Back to Jobs");
        backBtn.setFont(new Font("Arial", Font.BOLD, 14));
        backBtn.addActionListener(e -> cardLayout.show(rightContainer, "GRID_VIEW"));
        backPanel.add(backBtn);

        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Arial", Font.PLAIN, 16));
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setBackground(new Color(245, 245, 245));

        // Apply Button (Bottom)
        applyBtn = new JButton("Apply Now");
        applyBtn.setFont(new Font("Arial", Font.BOLD, 24));
        applyBtn.setBackground(new Color(0, 153, 76));
        applyBtn.setForeground(Color.WHITE);
        applyBtn.setPreferredSize(new Dimension(0, 65));
        applyBtn.setOpaque(true);
        applyBtn.setBorderPainted(false);
        applyBtn.setFocusPainted(false);

        detailPanel.add(backPanel, BorderLayout.NORTH);
        detailPanel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        detailPanel.add(applyBtn, BorderLayout.SOUTH);

        // Add both cards to the Right Container
        rightContainer.add(gridScroll, "GRID_VIEW");
        rightContainer.add(detailPanel, "DETAIL_VIEW");

        // Assemble the SplitPane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightContainer);
        splitPane.setDividerLocation(300);
        splitPane.setEnabled(false); // 锁定分割线，保持布局整洁
        add(splitPane, BorderLayout.CENTER);


        // ==========================================
        // 3. EVENT LISTENERS
        // ==========================================
        searchBtn.addActionListener(e -> {
            String mod = (String) moduleCombo.getSelectedItem();
            String status = (String) statusCombo.getSelectedItem();
            String type = (String) jobTypeCombo.getSelectedItem();
            String skills = (String) skillsCombo.getSelectedItem();
            String kw = searchField.getText();
            updateJobCards(controller.filterJobs(mod, status, type, skills, kw));
        });

        clearBtn.addActionListener(e -> {
            moduleCombo.setSelectedIndex(0);
            statusCombo.setSelectedIndex(0);
            jobTypeCombo.setSelectedIndex(0);
            skillsCombo.setSelectedIndex(0);
            searchField.setText("");
            updateJobCards(controller.getAllJobs());
        });

        applyBtn.addActionListener(e -> {
            if (currentSelectedJob != null) {
                ApplicationDialog dialog = new ApplicationDialog(this, controller, currentSelectedJob, currentUser.getId());
                dialog.setVisible(true);
            }
        });

        myAppsBtn.addActionListener(e -> {
            MyApplicationsDialog myAppsDialog = new MyApplicationsDialog(this, controller, currentUser.getId());
            myAppsDialog.setVisible(true);
        });

        // 找到原有的 manageCVBtn.addActionListener(...) 并替换为：
        manageCVBtn.addActionListener(e -> {
            // 跳转到新的个人档案界面
            ProfileManagerDialog profileDialog = new ProfileManagerDialog(this, controller, currentUser.getId());
            profileDialog.setVisible(true);
        });
    }

    /**
     * Loads jobs and populates the grid view on startup.
     */
    private void loadInitialData() {
        updateJobCards(controller.getAllJobs());
    }

    /**
     * Rebuilds the right panel's grid layout with buttons representing each job.
     */
    private void updateJobCards(List<Job> jobs) {
        gridPanel.removeAll();

        if (jobs.isEmpty()) {
            JPanel emptyPanel = new JPanel(new GridBagLayout());
            emptyPanel.add(new JLabel("No jobs match your search criteria."));
            gridPanel.add(emptyPanel);
        } else {
            for (Job job : jobs) {
                gridPanel.add(createJobCard(job));
            }
        }

        gridPanel.revalidate();
        gridPanel.repaint();
        cardLayout.show(rightContainer, "GRID_VIEW");
    }

    /**
     * Creates a large, square-ish button to act as a Job Card.
     */
    private JButton createJobCard(Job job) {
        // 使用 HTML 渲染卡片内部格式
        String cardHtml = String.format(
                "<html><div style='text-align:center; padding: 10px;'>" +
                        "<h2 style='margin: 0 0 10px 0; color: %s;'>%s</h2>" +
                        "<p style='margin: 3px 0; font-size: 11px;'><b>Module:</b> %s</p>" +
                        "<p style='margin: 3px 0; font-size: 11px;'><b>Skills:</b> %s</p>" +
                        "%s" +
                        "</div></html>",
                job.isExpired() ? "#888888" : "#0055cc",
                job.getTitle(),
                job.getModule(),
                job.getRequiredSkill(),
                job.isExpired() ? "<p style='color: red; font-weight: bold;'>[CLOSED]</p>" : ""
        );

        JButton jobBtn = new JButton(cardHtml);
        jobBtn.setPreferredSize(new Dimension(200, 180)); // 大方块尺寸
        jobBtn.setBackground(job.isExpired() ? new Color(240, 240, 240) : Color.WHITE);
        jobBtn.setFocusPainted(false);
        jobBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // 鼠标悬浮反馈 (Hover effect)
        jobBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jobBtn.setBackground(new Color(230, 240, 255));
                jobBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jobBtn.setBackground(job.isExpired() ? new Color(240, 240, 240) : Color.WHITE);
            }
        });

        // 点击事件：切换到详细信息面板
        jobBtn.addActionListener(e -> showJobDetails(job));

        return jobBtn;
    }

    /**
     * Populates the details area and switches the right panel to the details view.
     */
    private void showJobDetails(Job job) {
        this.currentSelectedJob = job;

        StringBuilder sb = new StringBuilder();
        sb.append("Job Title: \t").append(job.getTitle()).append("\n");
        sb.append("Module: \t").append(job.getModule()).append("\n");
        sb.append("Working Hours: \t").append(job.getHours()).append("\n");
        sb.append("Salary: \t").append(job.getSalary()).append("\n");
        sb.append("Competition: \t").append(job.getCompetitionRatio()).append("\n");
        sb.append("Job Type: \t").append(job.getJobType()).append("\n");
        sb.append("Required Skill: \t").append(job.getRequiredSkill()).append("\n");
        sb.append("--------------------------------------------------\n");
        sb.append("Responsibilities:\n").append(job.getResponsibilities()).append("\n");

        detailsArea.setText(sb.toString());

        if (job.isExpired()) {
            applyBtn.setText("Closed");
            applyBtn.setEnabled(false);
            applyBtn.setBackground(Color.GRAY);
        } else {
            applyBtn.setText("Apply Now");
            applyBtn.setEnabled(true);
            applyBtn.setBackground(new Color(0, 153, 76));
        }

        // 切换卡片到详情视图
        cardLayout.show(rightContainer, "DETAIL_VIEW");
    }
}