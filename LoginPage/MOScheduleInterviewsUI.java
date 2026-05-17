package LoginPage;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Modernized MOScheduleInterviewsUI with indigo colour palette.
 * Connects to UnifiedDataStore for persisting interview data.
 */
public class MOScheduleInterviewsUI extends JPanel {
    private final User currentUser;
    private JComboBox<String> moduleComboBox;
    private JTable shortlistedTable;
    private DefaultTableModel tableModel;
    private String selectedModuleId;

    // Indigo colour palette
    private static final Color INDIGO_PRIMARY = new Color(0x4F, 0x46, 0xE5);
    private static final Color INDIGO_LIGHT   = new Color(0xEE, 0xF2, 0xFF);
    private static final Color INDIGO_HOVER   = new Color(0xC7, 0xD2, 0xFE);

    private static final Color PAGE_BG        = new Color(0xF5, 0xF7, 0xFA);
    private static final Color CARD_BG        = Color.WHITE;
    private static final Color TITLE_COLOR    = new Color(0x1E, 0x1B, 0x4B);
    private static final Color SUBTITLE_COLOR = new Color(0x6B, 0x72, 0x80);
    private static final Color BORDER_COLOR   = new Color(0xE2, 0xE8, 0xF0);

    private static final Color SUCCESS_COLOR  = new Color(0x16, 0xA3, 0x4A);

    public MOScheduleInterviewsUI(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(PAGE_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initializeUI();
    }

    private void initializeUI() {
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createContentPanel(), BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(PAGE_BG);

        // Title bar
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(INDIGO_PRIMARY);
        titleBar.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        JLabel titleLabel = new JLabel("Schedule Interviews");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Manage interview arrangements for selected applicants");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(INDIGO_HOVER);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(INDIGO_PRIMARY);
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(subtitleLabel);

        titleBar.add(textPanel, BorderLayout.WEST);
        header.add(titleBar);

        // Module filter
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        filterPanel.setBackground(CARD_BG);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));

        JLabel filterLabel = new JLabel("Select Module:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        filterLabel.setForeground(TITLE_COLOR);

        moduleComboBox = new JComboBox<>();
        moduleComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        moduleComboBox.setPreferredSize(new Dimension(160, 34));
        moduleComboBox.setBackground(Color.WHITE);

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

        JButton refreshBtn = createStyledButton("Refresh", INDIGO_PRIMARY, INDIGO_PRIMARY.darker());
        refreshBtn.addActionListener(e -> refreshShortlistedList());

        filterPanel.add(filterLabel);
        filterPanel.add(moduleComboBox);
        filterPanel.add(Box.createHorizontalStrut(16));
        filterPanel.add(refreshBtn);

        header.add(Box.createVerticalStrut(12));
        header.add(filterPanel);

        return header;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(CARD_BG);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        String[] columnNames = {"Application ID", "Name", "Module", "Status", "Action"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        shortlistedTable = new JTable(tableModel);
        shortlistedTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        shortlistedTable.setRowHeight(34);
        shortlistedTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        shortlistedTable.getTableHeader().setBackground(INDIGO_PRIMARY);
        shortlistedTable.getTableHeader().setForeground(Color.WHITE);
        shortlistedTable.setSelectionBackground(INDIGO_LIGHT);
        shortlistedTable.setGridColor(BORDER_COLOR);

        JScrollPane scrollPane = new JScrollPane(shortlistedTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        contentPanel.add(scrollPane, BorderLayout.CENTER);

        refreshShortlistedList();

        shortlistedTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int row = shortlistedTable.rowAtPoint(evt.getPoint());
                int col = shortlistedTable.columnAtPoint(evt.getPoint());
                if (row >= 0 && col == 4) {
                    String applicantId = (String) shortlistedTable.getValueAt(row, 0);
                    String applicantName = (String) shortlistedTable.getValueAt(row, 1);
                    showInterviewMenu(applicantId, applicantName);
                }
            }
        });

        return contentPanel;
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
        scheduleItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        scheduleItem.addActionListener(e -> scheduleInterview(applicationId, applicantName));
        menu.add(scheduleItem);

        JMenuItem sendMessageItem = new JMenuItem("Send Message");
        sendMessageItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sendMessageItem.addActionListener(e -> sendMessage(applicationId, applicantName));
        menu.add(sendMessageItem);

        menu.show(shortlistedTable, 100, 100);
    }

    private void scheduleInterview(String applicationId, String applicantName) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Schedule Interview", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(PAGE_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(INDIGO_LIGHT);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INDIGO_HOVER, 1, true),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        JLabel appLabel = new JLabel("Application ID: " + applicationId);
        appLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        appLabel.setForeground(TITLE_COLOR);

        headerPanel.add(appLabel);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 12, 12));
        formPanel.setBackground(CARD_BG);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);

        JLabel dateLabel = new JLabel("Date & Time (yyyy-MM-dd HH:mm):");
        dateLabel.setFont(labelFont);
        dateLabel.setForeground(TITLE_COLOR);
        JTextField dateField = new JTextField("2026-05-20 10:00");
        dateField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));

        JLabel locationLabel = new JLabel("Location:");
        locationLabel.setFont(labelFont);
        locationLabel.setForeground(TITLE_COLOR);
        JTextField locationField = new JTextField("Room 101");
        locationField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        locationField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));

        formPanel.add(dateLabel);
        formPanel.add(dateField);
        formPanel.add(locationLabel);
        formPanel.add(locationField);

        panel.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        buttonPanel.setBackground(PAGE_BG);

        JButton scheduleBtn = createStyledButton("Schedule", INDIGO_PRIMARY, INDIGO_PRIMARY.darker());
        JButton cancelBtn = createStyledButton("Cancel", SUBTITLE_COLOR, SUBTITLE_COLOR.darker());

        scheduleBtn.addActionListener(e -> {
            String interviewTime = dateField.getText().trim();
            String location = locationField.getText().trim();

            if (!interviewTime.isEmpty() && !location.isEmpty()) {
                // Find applicant details
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
                JOptionPane.showMessageDialog(dialog,
                        "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(scheduleBtn);
        buttonPanel.add(cancelBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void sendMessage(String applicationId, String applicantName) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Send Message", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(PAGE_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Header
        JLabel toLabel = new JLabel("To: " + applicantName);
        toLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        toLabel.setForeground(TITLE_COLOR);
        panel.add(toLabel, BorderLayout.NORTH);

        // Message area
        JTextArea messageArea = new JTextArea(8, 30);
        messageArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setText("Dear " + applicantName + ",\n\nI would like to invite you for an interview...");
        messageArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        JScrollPane scrollPane = new JScrollPane(messageArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        buttonPanel.setBackground(PAGE_BG);

        JButton sendBtn = createStyledButton("Send", INDIGO_PRIMARY, INDIGO_PRIMARY.darker());
        sendBtn.addActionListener(e -> {
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
                JOptionPane.showMessageDialog(dialog,
                        "Message sent to " + applicantName, "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Message cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelBtn = createStyledButton("Cancel", SUBTITLE_COLOR, SUBTITLE_COLOR.darker());
        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(sendBtn);
        buttonPanel.add(cancelBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private JButton createStyledButton(String text, Color baseColor, Color hoverColor) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(baseColor);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                btn.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                btn.setBackground(baseColor);
            }
        });

        return btn;
    }
}
