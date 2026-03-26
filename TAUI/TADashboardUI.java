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

    // Left panel components (US-02 Browse and Filter)
    // Declared as class members so their values can be retrieved during search
    private JComboBox<String> moduleCombo;
    private JComboBox<String> statusCombo;
    private JTextField searchField;

    private DefaultListModel<Job> listModel;
    private JList<Job> jobList;

    // Right panel components (US-05 Job Details)
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
        setSize(950, 650);
        setLocationRelativeTo(null);

        buildSplitPane();
        loadInitialData();
    }

    private void buildSplitPane() {
        // ==========================================
        // 1. Build the left panel (Job List Area)
        // ==========================================
        JPanel leftPanel = new JPanel(new BorderLayout());

        // 4 rows to accommodate 2 dropdowns, 1 search field, and 1 row for buttons
        JPanel filterPanel = new JPanel(new GridLayout(4, 2, 5, 8));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Filter 1: Module
        filterPanel.add(new JLabel("Module:"));
        moduleCombo = new JComboBox<>(new String[]{"All", "ECS401", "ECS414", "ECS505"});
        filterPanel.add(moduleCombo);

        // Filter 2: Status (Open or Closed)
        filterPanel.add(new JLabel("Status:"));
        statusCombo = new JComboBox<>(new String[]{"All", "Open Only", "Closed"});
        filterPanel.add(statusCombo);

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

        // Pass selected filter values to the controller
        searchBtn.addActionListener(e -> {
            String mod = (String) moduleCombo.getSelectedItem();
            String status = (String) statusCombo.getSelectedItem();
            String kw = searchField.getText();

            updateList(controller.filterJobs(mod, status, kw));
        });

        // Clear all inputs and reset to default
        clearBtn.addActionListener(e -> {
            moduleCombo.setSelectedIndex(0);
            statusCombo.setSelectedIndex(0);
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