package TAUI;

import LoginPage.DashBoardUI;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Boundary Class - TA Dashboard
 * Implements a Master-Detail view with advanced filtering capabilities.
 */
public class TADashboardUI extends DashBoardUI {

    private TAController controller;

    // Left panel components
    private JComboBox<String> moduleCombo;
    private JComboBox<String> statusCombo;
    // [新增] 岗位类型和技能要求下拉菜单
    private JComboBox<String> jobTypeCombo;
    private JComboBox<String> skillsCombo;
    private JTextField searchField;

    private DefaultListModel<Job> listModel;
    private JList<Job> jobList;

    // Right panel components
    private JTextArea detailsArea;
    private JButton applyBtn;

    public TADashboardUI(LoginPage.User user, TAController controller) {
        super(user);
        this.controller = controller;
        initializeUI();
    }

    @Override
    protected void initializeUI() {
        setTitle("Teaching Assistant Dashboard - Job Portal (" + currentUser.getId() + ")");
        setSize(950, 650); // 你也可以根据需要把高度稍微调大一点，比如 700，以免左侧太挤
        setLocationRelativeTo(null);

        buildSplitPane();
        loadInitialData();
    }

    private void buildSplitPane() {
        // ==========================================
        // 1. Build the left panel (Job List Area)
        // ==========================================
        JPanel leftPanel = new JPanel(new BorderLayout());

        // [修改] 将 GridLayout 的行数从 4 改为 6，以容纳新增的两个筛选项
        JPanel filterPanel = new JPanel(new GridLayout(6, 2, 5, 8));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Filter 1: Module
        filterPanel.add(new JLabel("Module:"));
        moduleCombo = new JComboBox<>(new String[]{"All", "ECS401", "ECS414", "ECS505"});
        filterPanel.add(moduleCombo);

        // Filter 2: Status (Open or Closed)
        filterPanel.add(new JLabel("Status:"));
        statusCombo = new JComboBox<>(new String[]{"All", "Open Only", "Closed"});
        filterPanel.add(statusCombo);

        // [新增] Filter 3: Job Type (岗位类型)
        filterPanel.add(new JLabel("Job Type:"));
        jobTypeCombo = new JComboBox<>(new String[]{"All", "Lab Assistant", "Grader", "Tutor", "Invigilator"});
        filterPanel.add(jobTypeCombo);

        // [新增] Filter 4: Required Skills (技能要求)
        filterPanel.add(new JLabel("Required Skills:"));
        skillsCombo = new JComboBox<>(new String[]{"All", "Java", "Python", "MATLAB"});
        filterPanel.add(skillsCombo);

        // Keyword Search
        filterPanel.add(new JLabel("Keyword:"));
        searchField = new JTextField();
        filterPanel.add(searchField);

        // Buttons
        JButton searchBtn = new JButton("Search");
        JButton clearBtn = new JButton("Clear Filters");
        filterPanel.add(searchBtn);
        filterPanel.add(clearBtn);

        listModel = new DefaultListModel<>();
        jobList = new JList<>(listModel);
        jobList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jobList.setFont(new Font("Arial", Font.PLAIN, 14));

        leftPanel.add(filterPanel, BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(jobList), BorderLayout.CENTER);

        // ==========================================
        // 2. Build the right panel (Job Details Area)
        // ==========================================
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Default prompt text
        detailsArea = new JTextArea("\n\n\n\n      Please select a job from the left panel to view details.");
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Arial", Font.PLAIN, 16));
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setBackground(new Color(245, 245, 245));

        // Huge "Apply Now" button stretching across the bottom
        applyBtn = new JButton("Apply Now");
        applyBtn.setFont(new Font("Arial", Font.BOLD, 24));
        applyBtn.setBackground(new Color(0, 153, 76));
        applyBtn.setForeground(Color.WHITE);
        applyBtn.setEnabled(false);
        applyBtn.setPreferredSize(new Dimension(0, 65));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(applyBtn, BorderLayout.CENTER);

        rightPanel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        rightPanel.add(bottomPanel, BorderLayout.SOUTH);

        // ==========================================
        // 3. Assemble the split panel (JSplitPane)
        // ==========================================
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(320);
        add(splitPane);

        // ==========================================
        // 4. Event Binding
        // ==========================================
        jobList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Job selectedJob = jobList.getSelectedValue();
                if (selectedJob != null) {
                    refreshDetailsPanel(selectedJob);
                }
            }
        });

        // [修改] 传递新增的过滤参数给 Controller
        searchBtn.addActionListener(e -> {
            String mod = (String) moduleCombo.getSelectedItem();
            String status = (String) statusCombo.getSelectedItem();
            String type = (String) jobTypeCombo.getSelectedItem();     // 获取岗位类型
            String skills = (String) skillsCombo.getSelectedItem();    // 获取技能要求
            String kw = searchField.getText();

            // 注意：这里需要你同步修改 TAController 里的 filterJobs 方法接收 5 个参数
            updateList(controller.filterJobs(mod, status, type, skills, kw));
        });

        // [修改] 重置所有新增的下拉菜单到默认选项
        clearBtn.addActionListener(e -> {
            moduleCombo.setSelectedIndex(0);
            statusCombo.setSelectedIndex(0);
            jobTypeCombo.setSelectedIndex(0);  // 重置岗位类型
            skillsCombo.setSelectedIndex(0);   // 重置技能要求
            searchField.setText("");
            updateList(controller.getAllJobs());
        });

        applyBtn.addActionListener(e -> {
            Job selectedJob = jobList.getSelectedValue();
            if (selectedJob != null) {
                ApplicationDialog dialog = new ApplicationDialog(this, controller, selectedJob);
                dialog.setVisible(true);
            }
        });
    }

    private void refreshDetailsPanel(Job job) {
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

        // Change button state based on job expiration status
        if (job.isExpired()) {
            applyBtn.setText("Closed");
            applyBtn.setEnabled(false);
            applyBtn.setBackground(Color.GRAY);
        } else {
            applyBtn.setText("Apply Now");
            applyBtn.setEnabled(true);
            applyBtn.setBackground(new Color(0, 153, 76));
        }
    }

    private void loadInitialData() {
        updateList(controller.getAllJobs());
    }

    private void updateList(List<Job> jobs) {
        listModel.clear();
        if (jobs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No matches found for the given criteria.", "Information", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (Job job : jobs) {
                listModel.addElement(job);
            }
        }
    }
}