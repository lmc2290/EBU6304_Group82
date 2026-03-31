package LoginPage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MOApplicantManagementUI extends JPanel {
    private User currentUser;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> courseFilter;
    private JComboBox<String> englishFilter;
    private JTextField nameFilter;
    private TableRowSorter<DefaultTableModel> sorter;

    public MOApplicantManagementUI(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        initializeUI();
    }

    private void initializeUI() {
        JLabel title = new JLabel("MO Applicant Management - " + currentUser.getModuleName(), SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        courseFilter = new JComboBox<>(new String[]{"All", "Java", "Database", "OOP", "Python", "ML"});
        englishFilter = new JComboBox<>(new String[]{"All", "IELTS 6.5", "IELTS 7.0", "IELTS 7.5"});
        nameFilter = new JTextField(10);

        JButton filterBtn = new JButton("Filter");
        JButton clearBtn = new JButton("Clear");

        topPanel.add(new JLabel("Completed Course:"));
        topPanel.add(courseFilter);
        topPanel.add(new JLabel("English Level:"));
        topPanel.add(englishFilter);
        topPanel.add(new JLabel("Name:"));
        topPanel.add(nameFilter);
        topPanel.add(filterBtn);
        topPanel.add(clearBtn);

        add(topPanel, BorderLayout.SOUTH);

        String[] columns = {"ID", "Name", "Course", "English", "Completed Courses", "CV", "Status", "Action"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return column == 5 || column == 7;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(35);

        loadApplicants();

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        table.getColumn("CV").setCellRenderer(new ButtonRenderer("View CV"));
        table.getColumn("CV").setCellEditor(new CVButtonEditor(new JCheckBox()));

        table.getColumn("Action").setCellRenderer(new ButtonRenderer("Update Status"));
        table.getColumn("Action").setCellEditor(new ActionButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        filterBtn.addActionListener(e -> applyFilters());
        clearBtn.addActionListener(e -> clearFilters());
    }

    private void loadApplicants() {
        tableModel.setRowCount(0);
        for (Applicant a : MockDatabase.getApplicants()) {
            if (a.getModuleName().equals(currentUser.getModuleName())) {
                tableModel.addRow(new Object[]{
                        a.getApplicantId(),
                        a.getName(),
                        a.getCourse(),
                        a.getEnglishLevel(),
                        a.getCompletedCourses(),
                        "View CV",
                        a.getStatus(),
                        "Update Status"
                });
            }
        }
    }

    private void applyFilters() {
        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        String course = (String) courseFilter.getSelectedItem();
        String english = (String) englishFilter.getSelectedItem();
        String name = nameFilter.getText().trim();

        if (!"All".equals(course)) {
            filters.add(RowFilter.regexFilter("(?i)" + course, 4));
        }

        if (!"All".equals(english)) {
            filters.add(RowFilter.regexFilter("(?i)" + english, 3));
        }

        if (!name.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + name, 1));
        }

        sorter.setRowFilter(filters.isEmpty() ? null : RowFilter.andFilter(filters));
    }

    private void clearFilters() {
        courseFilter.setSelectedIndex(0);
        englishFilter.setSelectedIndex(0);
        nameFilter.setText("");
        sorter.setRowFilter(null);
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer(String text) {
            setText(text);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value == null ? "Button" : value.toString());
            return this;
        }
    }

    class CVButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int currentRow;

        public CVButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("View CV");
            button.addActionListener(e -> {
                String name = table.getValueAt(currentRow, 1).toString();
                String course = table.getValueAt(currentRow, 2).toString();
                JOptionPane.showMessageDialog(button,
                        "Name: " + name +
                                "\nEducation: " + course +
                                "\nSkills: Java, SQL, Communication" +
                                "\nExperience: Tutoring / Lab support",
                        "CV Details",
                        JOptionPane.INFORMATION_MESSAGE);
                fireEditingStopped();
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            currentRow = table.convertRowIndexToModel(row);
            return button;
        }

        public Object getCellEditorValue() {
            return "View CV";
        }
    }

    class ActionButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int currentRow;

        public ActionButtonEditor(JCheckBox checkBox) {
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

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            currentRow = table.convertRowIndexToModel(row);
            return button;
        }

        public Object getCellEditorValue() {
            return "Update Status";
        }
    }
}