package LoginPage;

import javax.swing.*;
import java.awt.*;

public class MOJobVacancyUI extends JFrame {
    private JTextField moduleIdField;
    private JTextField moduleNameField;
    private JTextField positionCountField;
    private JTextArea responsibilitiesArea;
    private JTextArea requirementsArea;
    private User user;
    
    public MOJobVacancyUI(User user) {
        this.user = user;
        setTitle("Create TA Vacancy");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(247, 247, 247));
        
        // Header
        JLabel headerLabel = new JLabel("Create New TA Vacancy", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        formPanel.setBackground(new Color(240, 240, 240));
        
        // Module ID
        formPanel.add(new JLabel("Module ID:"));
        moduleIdField = new JTextField();
        formPanel.add(moduleIdField);
        
        // Module Name
        formPanel.add(new JLabel("Module Name:"));
        moduleNameField = new JTextField();
        formPanel.add(moduleNameField);
        
        // Position Count
        formPanel.add(new JLabel("Number of Positions:"));
        positionCountField = new JTextField();
        formPanel.add(positionCountField);
        
        // Responsibilities
        formPanel.add(new JLabel("Responsibilities:"));
        responsibilitiesArea = new JTextArea();
        responsibilitiesArea.setLineWrap(true);
        responsibilitiesArea.setWrapStyleWord(true);
        JScrollPane responsibilitiesScrollPane = new JScrollPane(responsibilitiesArea);
        formPanel.add(responsibilitiesScrollPane);
        
        // Requirements
        formPanel.add(new JLabel("Requirements:"));
        requirementsArea = new JTextArea();
        requirementsArea.setLineWrap(true);
        requirementsArea.setWrapStyleWord(true);
        JScrollPane requirementsScrollPane = new JScrollPane(requirementsArea);
        formPanel.add(requirementsScrollPane);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JButton createButton = new JButton("Create Vacancy");
        createButton.setPreferredSize(new Dimension(150, 35));
        createButton.addActionListener(e -> createVacancy());
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(150, 35));
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void createVacancy() {
        String moduleId = moduleIdField.getText().trim();
        String moduleName = moduleNameField.getText().trim();
        String positionCountText = positionCountField.getText().trim();
        String responsibilities = responsibilitiesArea.getText().trim();
        String requirements = requirementsArea.getText().trim();
        
        // Validate input
        if (moduleId.isEmpty() || moduleName.isEmpty() || positionCountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int positionCount;
        try {
            positionCount = Integer.parseInt(positionCountText);
            if (positionCount <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number of positions", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create module
        Module module = new Module(moduleId, moduleName, user.getId(), positionCount, responsibilities, requirements);
        MockDataManager.addModule(module);
        
        JOptionPane.showMessageDialog(this, "TA vacancy created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}