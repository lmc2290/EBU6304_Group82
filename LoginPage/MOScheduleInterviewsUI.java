package LoginPage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MOScheduleInterviewsUI extends JPanel {
    private final User currentUser;
    private JComboBox<String> moduleComboBox;
    private JTable shortlistedTable;
    private DefaultTableModel tableModel;
    private String selectedModuleId;

    // Color palette
    private static final Color BG = new Color(241, 245, 249);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color PRIMARY = new Color(79, 70, 229);
    private static final Color ACCENT_ORANGE = new Color(245, 158, 11);
    private static final Color TABLE_HEADER = new Color(52, 58, 64);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color INPUT_BG = new Color(248, 250, 252);

    public MOScheduleInterviewsUI(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(10, 10));
        setBackground(BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        initializeUI();
    }

    private void initializeUI() {
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(16, 24, 16, 24)
        ));

        JLabel titleLabel = new JLabel("\uD83D\uDCD5  Schedule Interviews");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_PRIMARY);

        // Module selector in header
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(CARD_BG);

        JLabel filterLabel = new JLabel("Module:");
        filterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filterLabel.setForeground(TEXT_SECONDARY);
        rightPanel.add(filterLabel);

        moduleComboBox = new JComboBox<>();
        moduleComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        moduleComboBox.setPreferredSize(new Dimension(140, 32));

        List<Module> modules = MODataStore.loadModules();
        for (Module module : modules) {
            moduleComboBox.addItem(module.getModuleName());
        }
        if (!modules.isEmpty()) {
            selectedModuleId = modules.get(0).getModuleName();
        }
        moduleComboBox.addActionListener(e -> {
            String selected = (String) moduleComboBox.getSelectedItem();
            if (selected != null) {
                selectedModuleId = selected;
                refreshShortlistedList();
            }
        });
        rightPanel.add(moduleComboBox);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(CARD_BG);
        wrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));

        String[] columnNames = {"ID", "Name", "Course", "English Level", "Action"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        shortlistedTable = new JTable(tableModel);
        shortlistedTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        shortlistedTable.setRowHeight(36);
        shortlistedTable.setGridColor(new Color(230, 233, 238));
        shortlistedTable.setSelectionBackground(new Color(220, 235, 252));
        shortlistedTable.setSelectionForeground(Color.BLACK);
        shortlistedTable.setShowHorizontalLines(true);
        shortlistedTable.setShowVerticalLines(false);
        shortlistedTable.setFillsViewportHeight(true);

        shortlistedTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        shortlistedTable.getTableHeader().setBackground(TABLE_HEADER);
        shortlistedTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(shortlistedTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        wrapper.add(scrollPane, BorderLayout.CENTER);
        refreshShortlistedList();

        // Click handler for action column
        shortlistedTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = shortlistedTable.rowAtPoint(evt.getPoint());
                int col = shortlistedTable.columnAtPoint(evt.getPoint());
                if (row >= 0 && col == 4) {
                    String applicantId = (String) shortlistedTable.getValueAt(row, 0);
                    Applicant applicant = getApplicantById(applicantId);
                    if (applicant != null) {
                        showInterviewMenu(applicant);
                    }
                }
            }
        });

        return wrapper;
    }

    private void refreshShortlistedList() {
        tableModel.setRowCount(0);
        List<Applicant> applicants = MODataStore.loadApplicants();
        for (Applicant applicant : applicants) {
            if (applicant.getModuleName().equals(selectedModuleId) && "Shortlisted".equals(applicant.getStatus())) {
                Object[] row = new Object[5];
                row[0] = applicant.getApplicantId();
                row[1] = applicant.getName();
                row[2] = applicant.getCourse();
                row[3] = applicant.getEnglishLevel();
                row[4] = "\uD83D\uDCC5 Schedule";
                tableModel.addRow(row);
            }
        }
    }

    private void showInterviewMenu(Applicant applicant) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem scheduleItem = new JMenuItem("\uD83D\uDCC5 Schedule Interview");
        scheduleItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        scheduleItem.addActionListener(e -> scheduleInterview(applicant));
        menu.add(scheduleItem);

        JMenuItem sendMessageItem = new JMenuItem("\u2709 Send Message");
        sendMessageItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sendMessageItem.addActionListener(e -> sendMessage(applicant));
        menu.add(sendMessageItem);

        menu.show(shortlistedTable, 100, 100);
    }

    private void scheduleInterview(Applicant applicant) {
        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Schedule Interview - " + applicant.getName(),
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));
        panel.setBackground(CARD_BG);

        // Info header
        JLabel infoLabel = new JLabel("Schedule interview for: " + applicant.getName());
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        infoLabel.setForeground(TEXT_PRIMARY);
        panel.add(infoLabel, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField dateField = createStyledTextField("YYYY-MM-DD");
        JTextField timeField = createStyledTextField("HH:MM");
        JTextField locationField = createStyledTextField("");

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createFormLabel("Date:"), gbc);
        gbc.gridx = 1;
        formPanel.add(dateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(createFormLabel("Time:"), gbc);
        gbc.gridx = 1;
        formPanel.add(timeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(createFormLabel("Location:"), gbc);
        gbc.gridx = 1;
        formPanel.add(locationField, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        // Button
        JButton scheduleButton = createStyledButton("\u2713  Schedule", PRIMARY);
        scheduleButton.addActionListener(e -> {
            String date = dateField.getText();
            String time = timeField.getText();
            String location = locationField.getText();
            if (!date.isEmpty() && !time.isEmpty() && !location.isEmpty()) {
                // Persist interview to CSV
                MODataStore.saveInterview(applicant.getApplicantId(), applicant.getName(),
                        applicant.getModuleName(), date, time, location);
                JOptionPane.showMessageDialog(dialog,
                        "Interview scheduled for " + applicant.getName()
                                + " on " + date + " at " + time + " in " + location,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(CARD_BG);
        btnPanel.add(scheduleButton);
        panel.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void sendMessage(Applicant applicant) {
        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Send Message to " + applicant.getName(),
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));
        panel.setBackground(CARD_BG);

        panel.add(new JLabel("To: " + applicant.getName()), BorderLayout.NORTH);

        JTextArea messageArea = new JTextArea(10, 30);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageArea.setText("Dear " + applicant.getName() + ",\n\nI would like to invite you for an interview...");
        messageArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(10, 12, 10, 12)
        ));
        JScrollPane scrollPane = new JScrollPane(messageArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton sendButton = createStyledButton("\u2713  Send", PRIMARY);
        sendButton.addActionListener(e -> {
            String message = messageArea.getText();
            if (!message.isEmpty()) {
                // Persist message to CSV
                MODataStore.saveMessage("MO", currentUser.getId(),
                        applicant.getApplicantId(), applicant.getName(),
                        applicant.getModuleName(), message);
                JOptionPane.showMessageDialog(dialog, "Message sent to " + applicant.getName(), "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Message cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(CARD_BG);
        btnPanel.add(sendButton);
        panel.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private Applicant getApplicantById(String applicantId) {
        for (Applicant applicant : MODataStore.loadApplicants()) {
            if (applicant.getApplicantId().equals(applicantId)) {
                return applicant;
            }
        }
        return null;
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    private JTextField createStyledTextField(String text) {
        JTextField field = new JTextField(text, 18);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(INPUT_BG);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 24, 10, 24));
        return btn;
    }
}
