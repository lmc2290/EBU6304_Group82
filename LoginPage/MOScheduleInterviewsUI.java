package LoginPage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MOScheduleInterviewsUI extends JFrame {
    private JComboBox<String> moduleComboBox;
    private JTable shortlistedTable;
    private DefaultTableModel tableModel;
    private String selectedModuleId;
    
    public MOScheduleInterviewsUI(User user) {
        setTitle("Schedule Interviews");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(247, 247, 247));
        
        // Header
        JLabel headerLabel = new JLabel("Schedule Interviews", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        // Filter panel
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        filterPanel.setBackground(new Color(240, 240, 240));
        
        // Module selection
        filterPanel.add(new JLabel("Select Module:"));
        moduleComboBox = new JComboBox<>();
        List<Module> modules = MockDataManager.getModules();
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
        filterPanel.add(moduleComboBox);
        
        mainPanel.add(filterPanel, BorderLayout.NORTH);
        
        // Table panel
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        // Table model
        String[] columnNames = {"ID", "Name", "Course", "English Level", "Action"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only action column is editable
            }
        };
        
        shortlistedTable = new JTable(tableModel);
        shortlistedTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(shortlistedTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        
        add(mainPanel);
        refreshShortlistedList();
        
        // Add action listeners for buttons
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
    }
    
    private void refreshShortlistedList() {
        tableModel.setRowCount(0);
        
        List<Applicant> applicants = MockDataManager.getApplicants();
        for (Applicant applicant : applicants) {
            if (applicant.getModuleName().equals(selectedModuleId) && applicant.getStatus().equals("Shortlisted")) {
                // Add to table
                Object[] row = new Object[5];
                row[0] = applicant.getApplicantId();
                row[1] = applicant.getName();
                row[2] = applicant.getCourse();
                row[3] = applicant.getEnglishLevel();
                row[4] = "Schedule Interview";
                tableModel.addRow(row);
            }
        }
    }
    
    private void showInterviewMenu(Applicant applicant) {
        JPopupMenu menu = new JPopupMenu();
        
        JMenuItem scheduleItem = new JMenuItem("Schedule Interview");
        scheduleItem.addActionListener(e -> scheduleInterview(applicant));
        menu.add(scheduleItem);
        
        JMenuItem sendMessageItem = new JMenuItem("Send Message");
        sendMessageItem.addActionListener(e -> sendMessage(applicant));
        menu.add(sendMessageItem);
        
        menu.show(shortlistedTable, 100, 100);
    }
    
    private void scheduleInterview(Applicant applicant) {
        JDialog dialog = new JDialog(this, "Schedule Interview", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        panel.add(new JLabel("Applicant:"));
        panel.add(new JLabel(applicant.getName()));
        
        panel.add(new JLabel("Date:"));
        JTextField dateField = new JTextField();
        dateField.setText("YYYY-MM-DD");
        panel.add(dateField);
        
        panel.add(new JLabel("Time:"));
        JTextField timeField = new JTextField();
        timeField.setText("HH:MM");
        panel.add(timeField);
        
        panel.add(new JLabel("Location:"));
        JTextField locationField = new JTextField();
        panel.add(locationField);
        
        JButton scheduleButton = new JButton("Schedule");
        scheduleButton.addActionListener(e -> {
            String date = dateField.getText();
            String time = timeField.getText();
            String location = locationField.getText();
            
            if (!date.isEmpty() && !time.isEmpty() && !location.isEmpty()) {
                // Save interview details (in a real system, this would be stored in a database)
                JOptionPane.showMessageDialog(dialog, 
                    "Interview scheduled for " + applicant.getName() + " on " + date + " at " + time + " in " + location, 
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
    
    private void sendMessage(Applicant applicant) {
        JDialog dialog = new JDialog(this, "Send Message", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        panel.add(new JLabel("To: " + applicant.getName()), BorderLayout.NORTH);
        
        JTextArea messageArea = new JTextArea(8, 30);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setText("Dear " + applicant.getName() + ",\n\nI would like to invite you for an interview...");
        JScrollPane scrollPane = new JScrollPane(messageArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> {
            String message = messageArea.getText();
            if (!message.isEmpty()) {
                // Save message (in a real system, this would be stored in a database)
                JOptionPane.showMessageDialog(dialog, "Message sent to " + applicant.getName(), "Success", JOptionPane.INFORMATION_MESSAGE);
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
    
    private Applicant getApplicantById(String applicantId) {
        for (Applicant applicant : MockDataManager.getApplicants()) {
            if (applicant.getApplicantId().equals(applicantId)) {
                return applicant;
            }
        }
        return null;
    }
}