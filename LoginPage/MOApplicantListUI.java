package LoginPage;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MOApplicantListUI extends JFrame {
    private JComboBox<String> moduleComboBox;
    private JComboBox<String> englishLevelComboBox;
    private JTextField courseFilterField;
    private JTable applicantTable;
    private DefaultTableModel tableModel;
    private String selectedModuleId;
    
    public MOApplicantListUI(User user) {
        setTitle("Module-Specific Applicant List");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(247, 247, 247));
        
        // Header
        JLabel headerLabel = new JLabel("Applicant List", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        // Filter panel
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new GridLayout(1, 4, 10, 10));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        filterPanel.setBackground(new Color(240, 240, 240));
        
        // Module selection
        filterPanel.add(new JLabel("Select Module:"));
        moduleComboBox = new JComboBox<>();
        List<Module> modules = MockDataManager.getModulesByOrganiser(user.getId());
        for (Module module : modules) {
            moduleComboBox.addItem(module.getName() + " (" + module.getId() + ")");
        }
        if (!modules.isEmpty()) {
            selectedModuleId = modules.get(0).getId();
        }
        moduleComboBox.addActionListener(e -> {
            String selected = (String) moduleComboBox.getSelectedItem();
            if (selected != null) {
                selectedModuleId = selected.substring(selected.indexOf('(') + 1, selected.indexOf(')'));
                refreshApplicantList();
            }
        });
        filterPanel.add(moduleComboBox);
        
        // English level filter
        filterPanel.add(new JLabel("English Level:"));
        englishLevelComboBox = new JComboBox<>(new String[]{"All", "Beginner", "Intermediate", "Advanced"});
        englishLevelComboBox.addActionListener(e -> refreshApplicantList());
        filterPanel.add(englishLevelComboBox);
        
        // Course filter
        JPanel courseFilterPanel = new JPanel();
        courseFilterPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        courseFilterPanel.add(new JLabel("Previous Course:"));
        courseFilterField = new JTextField(15);
        courseFilterField.addActionListener(e -> refreshApplicantList());
        courseFilterPanel.add(courseFilterField);
        mainPanel.add(courseFilterPanel, BorderLayout.CENTER);
        
        // Table panel
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        // Table model
        String[] columnNames = {"ID", "Name", "Course", "English Level", "Status", "Action"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only action column is editable
            }
        };
        
        applicantTable = new JTable(tableModel);
        applicantTable.setRowHeight(30);
        applicantTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 4) { // Status column
                    String status = (String) value;
                    if (status.equals("Shortlisted")) {
                        c.setBackground(new Color(220, 255, 220));
                    } else if (status.equals("Rejected")) {
                        c.setBackground(new Color(255, 220, 220));
                    } else {
                        c.setBackground(table.getBackground());
                    }
                } else {
                    c.setBackground(table.getBackground());
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(applicantTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(filterPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        
        add(mainPanel);
        refreshApplicantList();
    }
    
    private void refreshApplicantList() {
        tableModel.setRowCount(0);
        
        List<Applicant> applicants = MockDataManager.getApplicantsByModule(selectedModuleId);
        String englishLevelFilter = (String) englishLevelComboBox.getSelectedItem();
        String courseFilter = courseFilterField.getText().trim().toLowerCase();
        
        for (Applicant applicant : applicants) {
            // Apply filters
            if (!englishLevelFilter.equals("All") && !applicant.getEnglishLevel().equals(englishLevelFilter)) {
                continue;
            }
            
            if (!courseFilter.isEmpty()) {
                boolean found = false;
                for (String course : applicant.getPreviousCourses()) {
                    if (course.toLowerCase().contains(courseFilter)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    continue;
                }
            }
            
            // Add to table
            Object[] row = new Object[6];
            row[0] = applicant.getId();
            row[1] = applicant.getName();
            row[2] = applicant.getCourse();
            row[3] = applicant.getEnglishLevel();
            row[4] = applicant.getStatus();
            row[5] = "View CV | Shortlist | Reject";
            tableModel.addRow(row);
        }
        
        // Add action listeners for buttons
        applicantTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = applicantTable.rowAtPoint(evt.getPoint());
                int col = applicantTable.columnAtPoint(evt.getPoint());
                if (row >= 0 && col == 5) {
                    String applicantId = (String) applicantTable.getValueAt(row, 0);
                    Applicant applicant = MockDataManager.getApplicantById(applicantId);
                    if (applicant != null) {
                        showActionMenu(applicant, row);
                    }
                }
            }
        });
    }
    
    private void showActionMenu(Applicant applicant, int row) {
        JPopupMenu menu = new JPopupMenu();
        
        JMenuItem viewCVItem = new JMenuItem("View CV");
        viewCVItem.addActionListener(e -> showCV(applicant));
        menu.add(viewCVItem);
        
        JMenuItem shortlistItem = new JMenuItem("Shortlist");
        shortlistItem.addActionListener(e -> {
            MockDataManager.updateApplicantStatus(applicant.getId(), "Shortlisted");
            tableModel.setValueAt("Shortlisted", row, 4);
            refreshApplicantList();
        });
        menu.add(shortlistItem);
        
        JMenuItem rejectItem = new JMenuItem("Reject");
        rejectItem.addActionListener(e -> {
            MockDataManager.updateApplicantStatus(applicant.getId(), "Rejected");
            tableModel.setValueAt("Rejected", row, 4);
            refreshApplicantList();
        });
        menu.add(rejectItem);
        
        menu.show(applicantTable, 100, 100);
    }
    
    private void showCV(Applicant applicant) {
        JFrame cvFrame = new JFrame("CV: " + applicant.getName());
        cvFrame.setSize(500, 400);
        cvFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        cvFrame.setLocationRelativeTo(this);
        
        JTextArea cvTextArea = new JTextArea(applicant.getCv());
        cvTextArea.setEditable(false);
        cvTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        cvTextArea.setLineWrap(true);
        cvTextArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(cvTextArea);
        cvFrame.add(scrollPane);
        cvFrame.setVisible(true);
    }
}