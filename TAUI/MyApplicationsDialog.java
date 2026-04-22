package TAUI;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Boundary Class - My Applications Dialog (US-07 & US-08)
 * Upgraded from Sprint 1 basic shell. Now includes full detail rendering,
 * status-aware button states, and complex withdrawal business logic integration.
 */
public class MyApplicationsDialog extends JDialog {

    private TAController controller;
    private String userId;

    private DefaultListModel<ApplicationRecord> listModel;
    private JList<ApplicationRecord> appList;
    private JTextArea detailsArea;
    private JButton withdrawBtn; // [New]: Withdraw feature
    private JButton editBtn;     // [New]: Edit feature (prepared for next round)

    public MyApplicationsDialog(JFrame parent, TAController controller, String userId) {
        super(parent, "My Applications", true);
        this.controller = controller;
        this.userId = userId;

        initUI();
        loadApplications();
    }

    private void initUI() {
        setSize(750, 500); // Expanded size for more content
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        // ==========================================
        // 1. Left Panel: Application Record List
        // ==========================================
        listModel = new DefaultListModel<>();
        appList = new JList<>(listModel);
        appList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appList.setFont(new Font("Arial", Font.PLAIN, 15));

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Application History"));
        leftPanel.add(new JScrollPane(appList), BorderLayout.CENTER);

        // ==========================================
        // 2. Right Panel: Dynamic Details Area
        // ==========================================
        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 14)); // Better alignment
        detailsArea.setBackground(new Color(250, 250, 250));
        detailsArea.setText("\n\n   <-- Please select an application from the list.");

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Detailed Information"));
        rightPanel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);

        // ==========================================
        // 3. Assembly: JSplitPane
        // ==========================================
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(280);
        add(splitPane, BorderLayout.CENTER);

        // ==========================================
        // 4. Bottom Panel: Action Controls (US-07)
        // ==========================================
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));

        editBtn = new JButton("Edit Application");
        editBtn.setEnabled(false); // Disabled by default

        withdrawBtn = new JButton("Withdraw Application");
        withdrawBtn.setForeground(Color.RED);
        withdrawBtn.setEnabled(false); // Disabled by default

        JButton closeBtn = new JButton("Close");

        bottomPanel.add(editBtn);
        bottomPanel.add(withdrawBtn);
        bottomPanel.add(closeBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // ==========================================
        // 5. Complex Event Listeners & State Machine
        // ==========================================

        // Listener 1: Update details and button states dynamically
        appList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                ApplicationRecord selectedRecord = appList.getSelectedValue();
                if (selectedRecord != null) {
                    // Update Text
                    displayApplicationDetails(selectedRecord);

                    // US-07 AC4: Hide/Disable options if already processed
                    String status = selectedRecord.getStatus();
                    if (status.equalsIgnoreCase("Pending") || status.equalsIgnoreCase("Interviewing")) {
                        withdrawBtn.setEnabled(true);
                        editBtn.setEnabled(true);
                    } else {
                        // Status is Hired, Rejected, or already Withdrawn
                        withdrawBtn.setEnabled(false);
                        editBtn.setEnabled(false);
                    }
                }
            }
        });

        // Listener 2: Execute Withdrawal (US-07 AC2 & AC3)
        withdrawBtn.addActionListener(e -> {
            ApplicationRecord selectedRecord = appList.getSelectedValue();
            if (selectedRecord != null) {
                // Require confirmation click to withdraw
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to withdraw your application for: \n" +
                                selectedRecord.getTargetJob().getTitle() + "?\nThis action cannot be undone.",
                        "Confirm Withdrawal",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = controller.withdrawApplication(selectedRecord);
                    if (success) {
                        JOptionPane.showMessageDialog(this, "Application successfully withdrawn.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        // Refresh the UI to reflect the new "Withdrawn" status
                        loadApplications();
                        detailsArea.setText("");
                        withdrawBtn.setEnabled(false);
                        editBtn.setEnabled(false);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to withdraw application. The status may have already changed.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        closeBtn.addActionListener(e -> dispose());
    }

    /**
     * Engine to format the internal ApplicationRecord object into a user-friendly text display.
     */
    private void displayApplicationDetails(ApplicationRecord record) {
        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append(" TARGET POSITION INFO\n");
        sb.append("=========================================\n");
        sb.append(String.format("%-15s: %s\n", "Job Title", record.getTargetJob().getTitle()));
        sb.append(String.format("%-15s: %s\n", "Module", record.getTargetJob().getModule()));
        sb.append(String.format("%-15s: %s\n", "Salary", record.getTargetJob().getSalary()));

        sb.append("\n=========================================\n");
        sb.append(" SUBMISSION STATUS\n");
        sb.append("=========================================\n");
        sb.append(String.format("%-15s: %s\n", "Status", record.getStatus().toUpperCase()));
        sb.append(String.format("%-15s: %s\n", "Submitted On", record.getFormattedSubmissionDate()));
        sb.append(String.format("%-15s: %s\n", "Tracking ID", record.getApplicationId().substring(0, 8) + "..."));

        sb.append("\n=========================================\n");
        sb.append(" ATTACHED DOCUMENTS\n");
        sb.append("=========================================\n");
        sb.append(String.format("%-15s: %s\n", "Resume / CV", record.getSubmittedCV().getOriginalName()));

        sb.append("\nCover Letter:\n");
        String cl = record.getCoverLetter();
        if (cl == null || cl.trim().isEmpty()) {
            sb.append("  (No cover letter provided for this application)\n");
        } else {
            sb.append("-----------------------------------------\n");
            sb.append(cl).append("\n");
            sb.append("-----------------------------------------\n");
        }

        detailsArea.setText(sb.toString());
        // Scroll to top
        detailsArea.setCaretPosition(0);
    }

    private void loadApplications() {
        listModel.clear();
        List<ApplicationRecord> myApps = controller.getUserApplications(userId);

        for (ApplicationRecord app : myApps) {
            listModel.addElement(app);
        }
    }
}