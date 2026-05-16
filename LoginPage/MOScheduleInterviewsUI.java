package LoginPage;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MOScheduleInterviewsUI extends JFrame {
    private User currentUser;
    private JComboBox<String> moduleComboBox;
    private JTable shortlistedTable;
    private DefaultTableModel tableModel;
    private String selectedModuleId;

    public MOScheduleInterviewsUI(User user) {
        this.currentUser = user;
        setTitle("Schedule Interviews");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(247, 247, 247));

        JLabel headerLabel = new JLabel("Schedule Interviews", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        filterPanel.setBackground(new Color(240, 240, 240));

        filterPanel.add(new JLabel("Select Module:"));
        moduleComboBox = new JComboBox<>();

        List<String[]> modules = UnifiedDataStore.getApprovedModules();
        for (String[] module : modules) {
            if (module.length >= 2) {
                moduleComboBox.addItem(module[0]);
            }
        }

        if (modules.isEmpty()) {
            moduleComboBox.addItem("ECS401");
            moduleComboBox.addItem("ECS414");
        }

        if (!modules.isEmpty() && modules.get(0).length >= 1) {
            selectedModuleId = modules.get(0)[0];
        } else {
            selectedModuleId = "ECS401";
        }

        moduleComboBox.addActionListener(e -> {
            String selected = (String) moduleComboBox.getSelectedItem();
            if (selected != null) {
                selectedModuleId = selected;
                refreshShortlistedList();
            }
        });
        filterPanel.add(moduleComboBox);

        mainPanel.add(filterPanel, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        String[] columnNames = {"Application ID", "Name", "Module", "Status", "Action"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        shortlistedTable = new JTable(tableModel);
        shortlistedTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(shortlistedTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(tablePanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Back to Dashboard");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.addActionListener(e -> {
            dispose();
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            if (parentWindow != null) {
                parentWindow.toFront();
            }
        });
        bottomPanel.add(backButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        refreshShortlistedList();

        shortlistedTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = shortlistedTable.rowAtPoint(evt.getPoint());
                int col = shortlistedTable.columnAtPoint(evt.getPoint());
                if (row >= 0 && col == 4) {
                    String applicantId = (String) shortlistedTable.getValueAt(row, 0);
                    String applicantName = (String) shortlistedTable.getValueAt(row, 1);
                    showInterviewMenu(applicantId, applicantName);
                }
            }
        });
    }

    private void refreshShortlistedList() {
        tableModel.setRowCount(0);

        // Include both Pending and Shortlisted applicants for interview scheduling
        List<String[]> pending = UnifiedDataStore.getApplicantsByStatus("Pending");
        List<String[]> shortlisted = UnifiedDataStore.getShortlistedApplicants();
        List<String[]> allCandidates = new ArrayList<>(shortlisted);
        allCandidates.addAll(pending);

        for (String[] applicant : allCandidates) {
            if (applicant.length >= 8 && applicant[3].equals(selectedModuleId)) {
                Object[] row = new Object[5];
                row[0] = applicant[0];  // applicationId
                row[1] = applicant[2];  // taName
                row[2] = applicant[4];  // moduleName
                row[3] = applicant[7];  // status
                row[4] = "Schedule Interview";
                tableModel.addRow(row);
            }
        }
    }

    private void showInterviewMenu(String applicationId, String applicantName) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem scheduleItem = new JMenuItem("Schedule Interview");
        scheduleItem.addActionListener(e -> scheduleInterview(applicationId, applicantName));
        menu.add(scheduleItem);

        JMenuItem sendMessageItem = new JMenuItem("Send Message");
        sendMessageItem.addActionListener(e -> sendMessage(applicationId, applicantName));
        menu.add(sendMessageItem);

        menu.show(shortlistedTable, 100, 100);
    }

    private void scheduleInterview(String applicationId, String applicantName) {
        JDialog dialog = new JDialog(this, "Schedule Interview", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Application ID:"));
        panel.add(new JLabel(applicationId));

        panel.add(new JLabel("Date & Time (yyyy-MM-dd HH:mm):"));
        JTextField dateField = new JTextField();
        dateField.setText("2026-05-15 10:00");
        panel.add(dateField);

        panel.add(new JLabel("Location:"));
        JTextField locationField = new JTextField();
        locationField.setText("Room 101");
        panel.add(locationField);

        JButton scheduleButton = new JButton("Schedule");
        scheduleButton.addActionListener(e -> {
            String interviewTime = dateField.getText();
            String location = locationField.getText();

            if (!interviewTime.isEmpty() && !location.isEmpty()) {
                // Find applicant details from the table or applicants data
                List<String[]> applicants = UnifiedDataStore.getAllApplicants();
                String taId = applicantName;
                String moduleName = selectedModuleId;
                for (String[] a : applicants) {
                    if (a.length >= 8 && a[0].equals(applicationId)) {
                        taId = a[1];
                        moduleName = a.length >= 5 ? a[4] : selectedModuleId;
                        break;
                    }
                }

                UnifiedDataStore.addInterview(applicationId, taId, applicantName,
                        selectedModuleId, moduleName, interviewTime, location,
                        currentUser.getId());

                JOptionPane.showMessageDialog(dialog,
                    "Interview scheduled for " + applicantName + " at " + interviewTime + " in " + location,
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(new JLabel());
        panel.add(scheduleButton);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void sendMessage(String applicationId, String applicantName) {
        JDialog dialog = new JDialog(this, "Send Message", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("To: " + applicantName), BorderLayout.NORTH);

        JTextArea messageArea = new JTextArea(8, 30);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setText("Dear " + applicantName + ",\n\nI would like to invite you for an interview...");
        JScrollPane scrollPane = new JScrollPane(messageArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> {
            String message = messageArea.getText();
            if (!message.isEmpty()) {
                String taId = applicantName;
                List<String[]> applicants = UnifiedDataStore.getAllApplicants();
                for (String[] a : applicants) {
                    if (a.length >= 8 && a[0].equals(applicationId)) {
                        taId = a[1];
                        break;
                    }
                }
                UnifiedDataStore.addMessage("MO", currentUser.getId(), taId, applicantName,
                        selectedModuleId, "Interview Invitation", message);
                JOptionPane.showMessageDialog(dialog, "Message sent to " + applicantName, "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Message cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(sendButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }
}