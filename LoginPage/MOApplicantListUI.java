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

    public MOApplicantListUI(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(10, 10));
        initializeUI();
    }

    private void initializeUI() {
        add(createTitlePanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createFilterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel(
                "MO Applicant List - Module: " + currentUser.getModuleName(),
                SwingConstants.CENTER
        );
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(titleLabel, BorderLayout.CENTER);
        return panel;
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
        applicantTable.setRowHeight(35);

        loadApplicantData();

        sorter = new TableRowSorter<>(tableModel);
        applicantTable.setRowSorter(sorter);

        applicantTable.getColumn("CV").setCellRenderer(new ButtonRenderer());
        applicantTable.getColumn("CV").setCellEditor(new CVButtonEditor(new JCheckBox()));

        applicantTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        applicantTable.getColumn("Action").setCellEditor(new StatusButtonEditor(new JCheckBox()));

        return new JScrollPane(applicantTable);
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        courseFilterBox = new JComboBox<>(new String[]{
                "All", "Java", "Database", "OOP", "Python", "ML"
        });

        englishFilterBox = new JComboBox<>(new String[]{
                "All", "IELTS 6.5", "IELTS 7.0", "IELTS 7.5"
        });

        nameFilterField = new JTextField(10);

        JButton filterButton = new JButton("Filter");
        JButton clearButton = new JButton("Clear");

        filterButton.addActionListener(e -> applyFilters());
        clearButton.addActionListener(e -> clearFilters());

        panel.add(new JLabel("Completed Course:"));
        panel.add(courseFilterBox);
        panel.add(new JLabel("English Level:"));
        panel.add(englishFilterBox);
        panel.add(new JLabel("Name:"));
        panel.add(nameFilterField);
        panel.add(filterButton);
        panel.add(clearButton);

        return panel;
    }

    private void loadApplicantData() {
        tableModel.setRowCount(0);

        for (Applicant applicant : MockDataManager.getApplicants()) {
            if (applicant.getModuleName().equals(currentUser.getModuleName())) {
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
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            setText(value == null ? "Button" : value.toString());
            return this;
        }
    }

    class CVButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private int currentRow;

        public CVButtonEditor(JCheckBox checkBox) {
            super(checkBox);

            button = new JButton("View CV");
            button.addActionListener(e -> {
                String name = tableModel.getValueAt(currentRow, 1).toString();
                String course = tableModel.getValueAt(currentRow, 2).toString();
                String english = tableModel.getValueAt(currentRow, 3).toString();
                String completed = tableModel.getValueAt(currentRow, 4).toString();

                JOptionPane.showMessageDialog(
                        button,
                        "Name: " + name +
                                "\nCourse: " + course +
                                "\nEnglish Level: " + english +
                                "\nCompleted Courses: " + completed +
                                "\nSkills: Communication, Teamwork, Subject Knowledge" +
                                "\nExperience: Tutoring / Lab Support",
                        "CV Details",
                        JOptionPane.INFORMATION_MESSAGE
                );

                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
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

            button = new JButton("Update Status");
            button.addActionListener(e -> {
                String[] options = {"Shortlisted", "Rejected"};
                String selected = (String) JOptionPane.showInputDialog(
                        button,
                        "Select status:",
                        "Update Status",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        options,
                        options[0]
                );

                if (selected != null) {
                    tableModel.setValueAt(selected, currentRow, 6);
                }

                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            currentRow = table.convertRowIndexToModel(row);
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "Update Status";
        }
    }
}