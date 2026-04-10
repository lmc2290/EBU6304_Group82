package LoginPage;

import javax.swing.*;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MOApplicantListUI extends JPanel {
    private final User currentUser;
    private JTable applicantTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> courseFilterBox;
    private JComboBox<String> englishFilterBox;
    private JTextField nameFilterField;
    private TableRowSorter<DefaultTableModel> sorter;

    private static final Color PAGE_BG = new Color(245, 247, 250);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TITLE_COLOR = new Color(33, 37, 41);
    private static final Color HEADER_BG = new Color(52, 58, 64);
    private static final Color PRIMARY_BTN = new Color(0, 123, 255);
    private static final Color PRIMARY_BTN_HOVER = new Color(0, 105, 217);
    private static final Color SECONDARY_BTN = new Color(108, 117, 125);
    private static final Color SECONDARY_BTN_HOVER = new Color(90, 98, 104);
    private static final Color BORDER_COLOR = new Color(220, 224, 230);

    public MOApplicantListUI(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(15, 15));
        setBackground(PAGE_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initializeUI();
    }

    private void initializeUI() {
        add(createTitlePanel(), BorderLayout.NORTH);
        add(createTableWrapperPanel(), BorderLayout.CENTER);
        add(createFilterWrapperPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PAGE_BG);

        String moduleText = currentUser.getModuleName();
        if (moduleText == null || moduleText.trim().isEmpty()) {
            moduleText = "Not Assigned";
        }

        JLabel titleLabel = new JLabel(
                "MO Applicant List - Module: " + moduleText,
                SwingConstants.CENTER
        );
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(TITLE_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(titleLabel, BorderLayout.CENTER);
        return panel;
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
                "ID", "Name", "Course", "English Level",
                "Completed Courses", "CV", "Status", "Action"
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

        applicantTable.getColumn("CV").setCellRenderer(new ButtonRenderer("View CV", PRIMARY_BTN));
        applicantTable.getColumn("CV").setCellEditor(new CVButtonEditor(new JCheckBox()));

        applicantTable.getColumn("Action").setCellRenderer(new ButtonRenderer("Update Status", SECONDARY_BTN));
        applicantTable.getColumn("Action").setCellEditor(new StatusButtonEditor(new JCheckBox()));

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
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        panel.setBackground(CARD_BG);

        courseFilterBox = new JComboBox<>(new String[]{
                "All", "Java", "Database", "OOP", "Python", "ML"
        });
        englishFilterBox = new JComboBox<>(new String[]{
                "All", "IELTS 6.5", "IELTS 7.0", "IELTS 7.5"
        });
        nameFilterField = new JTextField(12);

        styleComboBox(courseFilterBox);
        styleComboBox(englishFilterBox);
        styleTextField(nameFilterField);

        JButton filterButton = createStyledButton("Filter", PRIMARY_BTN, PRIMARY_BTN_HOVER);
        JButton clearButton = createStyledButton("Clear", SECONDARY_BTN, SECONDARY_BTN_HOVER);
        JButton refreshButton = createStyledButton("Refresh", PRIMARY_BTN, PRIMARY_BTN_HOVER);

        filterButton.addActionListener(e -> applyFilters());
        clearButton.addActionListener(e -> clearFilters());
        refreshButton.addActionListener(e -> loadApplicantData());

        panel.add(createLabel("Completed Course:"));
        panel.add(courseFilterBox);
        panel.add(createLabel("English Level:"));
        panel.add(englishFilterBox);
        panel.add(createLabel("Name:"));
        panel.add(nameFilterField);
        panel.add(filterButton);
        panel.add(clearButton);
        panel.add(refreshButton);

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

        List<Applicant> applicants = MODataStore.loadApplicants();

        System.out.println("Current MO module: " + currentUser.getModuleName());
        System.out.println("Loaded applicants: " + applicants.size());

        for (Applicant applicant : applicants) {
            if (currentUser.getModuleName() == null
                    || currentUser.getModuleName().trim().isEmpty()
                    || applicant.getModuleName().equals(currentUser.getModuleName())) {

                tableModel.addRow(new Object[]{
                        applicant.getApplicantId(),
                        applicant.getName(),
                        applicant.getCourse(),
                        applicant.getEnglishLevel(),
                        applicant.getCompletedCourses(),
                        "View CV",
                        applicant.getStatus(),
                        "Update Status"
                });
            }
        }

        if (sorter != null) {
            sorter.setRowFilter(null);
        }
    }

    private void applyFilters() {
        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        String selectedCourse = (String) courseFilterBox.getSelectedItem();
        String selectedEnglish = (String) englishFilterBox.getSelectedItem();
        String keyword = nameFilterField.getText().trim();

        if (!"All".equals(selectedCourse)) {
            filters.add(RowFilter.regexFilter("(?i)" + selectedCourse, 4));
        }
        if (!"All".equals(selectedEnglish)) {
            filters.add(RowFilter.regexFilter("(?i)" + selectedEnglish, 3));
        }
        if (!keyword.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + keyword, 1));
        }

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    private void clearFilters() {
        courseFilterBox.setSelectedIndex(0);
        englishFilterBox.setSelectedIndex(0);
        nameFilterField.setText("");
        sorter.setRowFilter(null);
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
                boolean hasFocus, int row, int column
        ) {
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
                String name = tableModel.getValueAt(currentRow, 1).toString();
                String course = tableModel.getValueAt(currentRow, 2).toString();
                String english = tableModel.getValueAt(currentRow, 3).toString();
                String completed = tableModel.getValueAt(currentRow, 4).toString();
                String status = tableModel.getValueAt(currentRow, 6).toString();

                JOptionPane.showMessageDialog(
                        button,
                        "Name: " + name
                                + "\nCourse: " + course
                                + "\nEnglish Level: " + english
                                + "\nCompleted Courses: " + completed
                                + "\nStatus: " + status
                                + "\nSkills: Communication, Teamwork, Subject Knowledge"
                                + "\nExperience: Tutoring / Lab Support",
                        "CV Details",
                        JOptionPane.INFORMATION_MESSAGE
                );
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column
        ) {
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
                String applicantId = tableModel.getValueAt(currentRow, 0).toString();
                String currentStatus = tableModel.getValueAt(currentRow, 6).toString();

                String[] options = {"Pending", "Shortlisted", "Rejected", "Approved"};
                String selected = (String) JOptionPane.showInputDialog(
                        button,
                        "Select status:",
                        "Update Status",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        options,
                        currentStatus
                );

                if (selected != null && !selected.equals(currentStatus)) {
                    MODataStore.updateApplicantStatus(
                            applicantId,
                            currentUser.getModuleName(),
                            selected
                    );
                    tableModel.setValueAt(selected, currentRow, 6);

                    JOptionPane.showMessageDialog(
                            button,
                            "Status updated successfully."
                    );
                }

                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column
        ) {
            currentRow = table.convertRowIndexToModel(row);
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "Update Status";
        }
    }
}