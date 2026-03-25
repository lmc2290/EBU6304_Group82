package TAUI;

import LoginPage.DashBoardUI;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Boundary Class - TA Dashboard
 * Uses JSplitPane to implement a left-right split Master-Detail view.
 */

public class TADashboardUI extends DashBoardUI {

    private TAController controller;

    // Left panel components (US-02 Browse and Filter)
    private JTextField searchField;
    private DefaultListModel<Job> listModel;
    private JList<Job> jobList;

    // Right panel components (US-05 Job Details)
    private JTextArea detailsArea;
    private JButton applyBtn;

    /**
     * Constructor now requires the User object to satisfy the DashBoardUI base class.
     */
    public TADashboardUI(LoginPage.User user, TAController controller) {
        super(user); // 1. Sets up the base JFrame and saves the currentUser
        this.controller = controller; // 2. Safely assign the controller

        initializeUI(); // 3. NOW it is safe to build the UI
    }

    @Override
    protected void initializeUI() {
        // Overwrite the default 600x500 size and title from the base class for TA specifics
        setTitle("Teaching Assistant Dashboard - Job Portal (" + currentUser.getId() + ")");
        setSize(900, 600);
        setLocationRelativeTo(null); // Re-center after resizing

        buildSplitPane();
        loadInitialData();
    }

    private void buildSplitPane() {
        // ==========================================
        // 1. Build the left panel (Job List Area)
        // ==========================================
        JPanel leftPanel = new JPanel(new BorderLayout());

        JPanel filterPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        filterPanel.add(new JLabel("Module:"));
        filterPanel.add(new JComboBox<>(new String[]{"All", "ECS401", "ECS414", "ECS505"}));
        filterPanel.add(new JLabel("Level:"));
        filterPanel.add(new JComboBox<>(new String[]{"All", "Undergraduate", "Postgraduate"}));
        filterPanel.add(new JLabel("Keyword:"));
        searchField = new JTextField();
        filterPanel.add(searchField);

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

        detailsArea = new JTextArea("\n\n\n      Welcome, " + currentUser.getId() + ". Please select a job from the left list.");
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Arial", Font.PLAIN, 15));
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setBackground(new Color(245, 245, 245));

        applyBtn = new JButton("Apply Now");
        applyBtn.setFont(new Font("Arial", Font.BOLD, 18));
        applyBtn.setBackground(new Color(0, 153, 76));
        applyBtn.setForeground(Color.WHITE);
        applyBtn.setEnabled(false);
        applyBtn.setPreferredSize(new Dimension(200, 50));

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(applyBtn);

        rightPanel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        rightPanel.add(bottomPanel, BorderLayout.SOUTH);

        // ==========================================
        // 3. Assemble the split panel (JSplitPane)
        // ==========================================
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(300);
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

        searchBtn.addActionListener(e -> updateList(controller.filterJobs(searchField.getText())));
        clearBtn.addActionListener(e -> {
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
            JOptionPane.showMessageDialog(this, "No matches found for the given criteria.");
        } else {
            for (Job job : jobs) {
                listModel.addElement(job);
            }
        }
    }
}