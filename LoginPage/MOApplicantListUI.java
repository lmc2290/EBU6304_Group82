package LoginPage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

public class MOApplicantListUI extends JPanel {
    private final User currentUser;

    private JTable applicantTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> courseFilterBox;
    private JComboBox<String> englishFilterBox;
    private JTextField nameFilterField;
    private TableRowSorter<DefaultTableModel> sorter;

    private JLabel titleLabel;
    private JLabel statsLabel;
    private JProgressBar limitProgressBar;

    private static final Color PAGE_BG = new Color(245, 247, 250);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TITLE_COLOR = new Color(33, 37, 41);
    private static final Color SUBTITLE_COLOR = new Color(108, 117, 125);
    private static final Color HEADER_BG = new Color(52, 58, 64);

    private static final Color PRIMARY_BTN = new Color(0, 123, 255);
    private static final Color PRIMARY_BTN_HOVER = new Color(0, 105, 217);

    private static final Color SECONDARY_BTN = new Color(108, 117, 125);
    private static final Color SECONDARY_BTN_HOVER = new Color(90, 98, 104);

    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color WARNING_COLOR = new Color(255, 193, 7);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);

    private static final Color BORDER_COLOR = new Color(220, 224, 230);

    public MOApplicantListUI(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(15, 15));
        setBackground(PAGE_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initializeUI();
    }

    private void initializeUI() {
        add(createTopPanel(), BorderLayout.NORTH);
        add(createTableWrapperPanel(), BorderLayout.CENTER);
        add(createFilterWrapperPanel(), BorderLayout.SOUTH);
        refreshLimitDisplay();
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(PAGE_BG);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(PAGE_BG);

        titleLabel = new JLabel("", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(TITLE_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 8, 10));

        titlePanel.add(titleLabel, BorderLayout.CENTER);

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(CARD_BG);
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));

        statsLabel = new JLabel("", SwingConstants.CENTER);
        statsLabel.setFont(new Font("Arial", Font.BOLD, 15));
        statsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        limitProgressBar = new JProgressBar();
        limitProgressBar.setStringPainted(true);
        limitProgressBar.setFont(new Font("Arial", Font.BOLD, 13));
        limitProgressBar.setPreferredSize(new Dimension(300, 26));
        limitProgressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        limitProgressBar.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        statsPanel.add(statsLabel);
        statsPanel.add(Box.createVerticalStrut(10));
        statsPanel.add(limitProgressBar);

        topPanel.add(titlePanel);
        topPanel.add(Box.createVerticalStrut(8));
        topPanel.add(statsPanel);

        return topPanel;
    }

    private JPanel createTableWrapperPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(CARD_BG);
        wrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        wrapper.add(createTablePanel(), BorderLayout.CENTER);
        return wrapper;
    }

    private JScrollPane createTablePanel() {
        String[] columns = {
                "Application ID", "TA ID", "TA Name", "Module Code",
                "Module Name", "CV File", "Status", "Action"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5 || column == 7;
            }
        };

        applicantTable = new JTable(tableModel);
        applicantTable.setFont(new Font("Arial", Font.PLAIN, 14));
        applicantTable.setRowHeight(38);
        applicantTable.setGridColor(new Color(230, 233, 238));
        applicantTable.setSelectionBackground(new Color(220, 235, 252));
        applicantTable.setSelectionForeground(Color.BLACK);
        applicantTable.setShowHorizontalLines(true);
        applicantTable.setShowVerticalLines(false);
        applicantTable.setFillsViewportHeight(true);

        applicantTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        applicantTable.getTableHeader().setBackground(HEADER_BG);
        applicantTable.getTableHeader().setForeground(Color.WHITE);
        applicantTable.getTableHeader().setReorderingAllowed(false);

        loadApplicantData();

        sorter = new TableRowSorter<>(tableModel);
        applicantTable.setRowSorter(sorter);

        applicantTable.getColumn("CV File").setCellRenderer(new ButtonRenderer("View CV", PRIMARY_BTN));
        applicantTable.getColumn("CV File").setCellEditor(new CVButtonEditor(new JCheckBox()));

        applicantTable.getColumn("Action").setCellRenderer(new ButtonRenderer("Update Status", SECONDARY_BTN));
        applicantTable.getColumn("Action").setCellEditor(new StatusButtonEditor(new JCheckBox()));

        applicantTable.getColumn("Status").setCellRenderer(new StatusColorRenderer());

        JScrollPane scrollPane = new JScrollPane(applicantTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        return scrollPane;
    }

    private JPanel createFilterWrapperPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(CARD_BG);
        wrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        wrapper.add(createFilterPanel(), BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_BG);

        JPanel firstRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        firstRow.setBackground(CARD_BG);

        JPanel secondRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        secondRow.setBackground(CARD_BG);

        courseFilterBox = new JComboBox<>(new String[]{
                "All", "Pending", "Shortlisted", "Approved", "Rejected"
        });

        englishFilterBox = new JComboBox<>(new String[]{
                "All"
        });

        nameFilterField = new JTextField(12);

        styleComboBox(courseFilterBox);
        styleComboBox(englishFilterBox);
        styleTextField(nameFilterField);

        JButton filterButton = createStyledButton("Filter", PRIMARY_BTN, PRIMARY_BTN_HOVER);
        JButton clearButton = createStyledButton("Clear", SECONDARY_BTN, SECONDARY_BTN_HOVER);
        JButton refreshButton = createStyledButton("Refresh", PRIMARY_BTN, PRIMARY_BTN_HOVER);
        JButton setLimitButton = createStyledButton("Set Limit", SECONDARY_BTN, SECONDARY_BTN_HOVER);

        filterButton.addActionListener(e -> applyFilters());
        clearButton.addActionListener(e -> clearFilters());
        refreshButton.addActionListener(e -> {
            loadApplicantData();
            refreshLimitDisplay();
        });
        setLimitButton.addActionListener(e -> setModuleLimit());

        firstRow.add(createLabel("Status:"));
        firstRow.add(courseFilterBox);
        firstRow.add(createLabel("TA Name:"));
        firstRow.add(nameFilterField);

        secondRow.add(filterButton);
        secondRow.add(clearButton);
        secondRow.add(refreshButton);
        secondRow.add(setLimitButton);

        panel.add(firstRow);
        panel.add(secondRow);

        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(TITLE_COLOR);
        return label;
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        comboBox.setPreferredSize(new Dimension(120, 34));
        comboBox.setBackground(Color.WHITE);
    }

    private void styleTextField(JTextField textField) {
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setPreferredSize(new Dimension(140, 34));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }

    private JButton createStyledButton(String text, Color baseColor, Color hoverColor) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(baseColor);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(baseColor);
            }
        });

        return btn;
    }

    private void loadApplicantData() {
        tableModel.setRowCount(0);

        List<String[]> applicants;
        String moId = currentUser.getMoId();
        if (moId != null && !moId.trim().isEmpty()) {
            applicants = UnifiedDataStore.getApplicationsByMoId(moId);
        } else {
            applicants = UnifiedDataStore.getAllApplicants();
        }

        System.out.println("Current MO ID: " + moId);
        System.out.println("Loaded applicants: " + applicants.size());

        for (String[] applicant : applicants) {
            if (applicant.length >= 8) {
                tableModel.addRow(new Object[]{
                        applicant[0],  // Application ID (column 0)
                        applicant[1],  // TA ID (column 1)
                        applicant[2],  // TA Name (column 2)
                        applicant[3],  // Module Code (column 3)
                        applicant[4],  // Module Name (column 4)
                        "View CV",     // CV File button
                        applicant[7],  // Status (column 7)
                        "Update Status" // Action button
                });
            }
        }

        if (sorter != null) {
            sorter.setRowFilter(null);
        }
    }

    private void refreshLimitDisplay() {
        String moduleText = currentUser.getModuleName();
        if (moduleText == null || moduleText.trim().isEmpty()) {
            moduleText = "Not Assigned";
        }

        if ("Not Assigned".equals(moduleText)) {
            titleLabel.setText("MO Applicant List - Module: Not Assigned");
            statsLabel.setText("No module limit available");
            statsLabel.setForeground(SUBTITLE_COLOR);

            limitProgressBar.setMaximum(1);
            limitProgressBar.setValue(0);
            limitProgressBar.setString("N/A");
            limitProgressBar.setForeground(SECONDARY_BTN);
            return;
        }

        int limit = UnifiedDataStore.getModulePositionLimit(moduleText);
        int approved = UnifiedDataStore.getApprovedCountByModule(moduleText);

        titleLabel.setText("MO Applicant List - Module: " + moduleText);
        statsLabel.setText("Approved: " + approved + " / Limit: " + limit);

        if (limit <= 0) {
            statsLabel.setForeground(DANGER_COLOR);
            limitProgressBar.setMaximum(1);
            limitProgressBar.setValue(0);
            limitProgressBar.setString("No limit set");
            limitProgressBar.setForeground(DANGER_COLOR);
            return;
        }

        limitProgressBar.setMaximum(limit);
        limitProgressBar.setValue(Math.min(approved, limit));
        limitProgressBar.setString(approved + " / " + limit);

        double ratio = (double) approved / limit;

        if (approved >= limit) {
            statsLabel.setForeground(DANGER_COLOR);
            limitProgressBar.setForeground(DANGER_COLOR);
        } else if (ratio >= 0.8) {
            statsLabel.setForeground(WARNING_COLOR.darker());
            limitProgressBar.setForeground(WARNING_COLOR);
        } else {
            statsLabel.setForeground(SUCCESS_COLOR.darker());
            limitProgressBar.setForeground(SUCCESS_COLOR);
        }
    }

    private void applyFilters() {
        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        String selectedStatus = (String) courseFilterBox.getSelectedItem();
        String keyword = nameFilterField.getText().trim();

        if (!"All".equals(selectedStatus)) {
            filters.add(RowFilter.regexFilter("(?i)" + selectedStatus, 6));
        }
        if (!keyword.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + keyword, 2));
        }

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    private void clearFilters() {
        courseFilterBox.setSelectedIndex(0);
        nameFilterField.setText("");
        sorter.setRowFilter(null);
    }

    private void setModuleLimit() {
        String moduleName = currentUser.getModuleName();

        if (moduleName == null || moduleName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No module is assigned to the current MO.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int currentLimit = UnifiedDataStore.getModulePositionLimit(moduleName);

        String input = JOptionPane.showInputDialog(this,
                "Enter new position limit for module " + moduleName + ":\nCurrent limit: " + currentLimit,
                currentLimit);

        if (input == null) return;

        input = input.trim();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Limit cannot be empty.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int newLimit = Integer.parseInt(input);

            if (newLimit <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Limit must be greater than 0.",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            UnifiedDataStore.updateModulePositionLimit(moduleName, newLimit);

            JOptionPane.showMessageDialog(this,
                    "Position limit for module " + moduleName + " updated to " + newLimit + ".",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            refreshLimitDisplay();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid integer.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        private final Color buttonColor;

        public ButtonRenderer(String text, Color buttonColor) {
            setText(text);
            setOpaque(true);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Arial", Font.BOLD, 12));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            this.buttonColor = buttonColor;
            setBackground(buttonColor);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            setText(value == null ? "Button" : value.toString());
            setBackground(buttonColor);
            return this;
        }
    }

    class CVButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private int currentRow;

        public CVButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = createStyledButton("View CV", PRIMARY_BTN, PRIMARY_BTN_HOVER);

            button.addActionListener(e -> {
                String applicationId = tableModel.getValueAt(currentRow, 0).toString();
                String taId = tableModel.getValueAt(currentRow, 1).toString();
                String taName = tableModel.getValueAt(currentRow, 2).toString();
                String moduleCode = tableModel.getValueAt(currentRow, 3).toString();
                String moduleName = tableModel.getValueAt(currentRow, 4).toString();
                String status = tableModel.getValueAt(currentRow, 6).toString();

                JOptionPane.showMessageDialog(button,
                        "Application ID: " + applicationId
                                + "\nTA ID: " + taId
                                + "\nTA Name: " + taName
                                + "\nModule Code: " + moduleCode
                                + "\nModule Name: " + moduleName
                                + "\nStatus: " + status,
                        "Application Details", JOptionPane.INFORMATION_MESSAGE);
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = table.convertRowIndexToModel(row);
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "View CV";
        }
    }

    class StatusButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private int currentRow;

        public StatusButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = createStyledButton("Update Status", SECONDARY_BTN, SECONDARY_BTN_HOVER);

            button.addActionListener(e -> {
                String applicationId = tableModel.getValueAt(currentRow, 0).toString();
                String currentStatus = tableModel.getValueAt(currentRow, 6).toString();
                String moduleCode = tableModel.getValueAt(currentRow, 3).toString();

                String[] options = {"Pending", "Shortlisted", "Rejected", "Approved"};
                String selected = (String) JOptionPane.showInputDialog(button,
                        "Select status:",
                        "Update Status",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        options,
                        currentStatus);

                if (selected != null && !selected.equals(currentStatus)) {

                    if ("Approved".equals(selected)) {
                        int approvedCount = UnifiedDataStore.getApprovedCountByModule(moduleCode);
                        int positionLimit = UnifiedDataStore.getModulePositionLimit(moduleCode);

                        boolean alreadyApproved = "Approved".equals(currentStatus);

                        if (!alreadyApproved && approvedCount >= positionLimit) {
                            JOptionPane.showMessageDialog(button,
                                    "No more positions available for this module.\n"
                                            + "Approved: " + approvedCount + " / Limit: " + positionLimit,
                                    "Approval Limit Reached", JOptionPane.WARNING_MESSAGE);
                            fireEditingStopped();
                            return;
                        }
                    }

                    UnifiedDataStore.updateApplicantStatus(applicationId, moduleCode, selected, currentUser.getId());
                    tableModel.setValueAt(selected, currentRow, 6);

                    refreshLimitDisplay();

                    JOptionPane.showMessageDialog(button,
                            "Status updated successfully.");
                }

                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = table.convertRowIndexToModel(row);
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "Update Status";
        }
    }

    class StatusColorRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (!isSelected) {
                c.setBackground(Color.WHITE);
            }

            String status = value == null ? "" : value.toString();

            if ("Approved".equalsIgnoreCase(status)) {
                c.setForeground(SUCCESS_COLOR.darker());
            } else if ("Shortlisted".equalsIgnoreCase(status)) {
                c.setForeground(new Color(0, 102, 204));
            } else if ("Rejected".equalsIgnoreCase(status)) {
                c.setForeground(DANGER_COLOR);
            } else {
                c.setForeground(SUBTITLE_COLOR);
            }

            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("Arial", Font.BOLD, 13));
            return c;
        }
    }
}