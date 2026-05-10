package LoginPage;

import javax.swing.*;
import java.awt.*;

public class MOJobVacancyUI extends JPanel {
    private final User currentUser;
    private JTextField moduleCodeField;
    private JTextField moduleNameField;
    private JTextArea responsibilitiesArea;
    private JTextArea requirementsArea;
    private JTextField positionsField;
    private JTextField deadlineField;

    public MOJobVacancyUI(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(10, 10));
        initializeUI();
    }

    private void initializeUI() {
        add(createTitlePanel(), BorderLayout.NORTH);
        add(createFormPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("MO Job Vacancy Creation", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(titleLabel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        moduleCodeField = new JTextField(20);
        moduleNameField = new JTextField(currentUser.getModuleName(), 20);
        responsibilitiesArea = new JTextArea(4, 20);
        requirementsArea = new JTextArea(4, 20);
        positionsField = new JTextField(20);
        deadlineField = new JTextField(20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Module Code:"), gbc);

        gbc.gridx = 1;
        formPanel.add(moduleCodeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Module Name:"), gbc);

        gbc.gridx = 1;
        formPanel.add(moduleNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Responsibilities:"), gbc);

        gbc.gridx = 1;
        formPanel.add(new JScrollPane(responsibilitiesArea), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Requirements:"), gbc);

        gbc.gridx = 1;
        formPanel.add(new JScrollPane(requirementsArea), gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Positions:"), gbc);

        gbc.gridx = 1;
        formPanel.add(positionsField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Deadline (YYYY-MM-DD):"), gbc);

        gbc.gridx = 1;
        formPanel.add(deadlineField, gbc);

        return formPanel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton saveButton = new JButton("Save");
        JButton clearButton = new JButton("Clear");

        saveButton.addActionListener(e -> saveModule());
        clearButton.addActionListener(e -> clearForm());

        panel.add(saveButton);
        panel.add(clearButton);

        return panel;
    }

    private void saveModule() {
        String moduleCode = moduleCodeField.getText().trim();
        String moduleName = moduleNameField.getText().trim();
        String responsibilities = responsibilitiesArea.getText().trim();
        String requirements = requirementsArea.getText().trim();
        String positionsText = positionsField.getText().trim();
        String deadline = deadlineField.getText().trim();

        if (moduleCode.isEmpty() || moduleName.isEmpty() || responsibilities.isEmpty()
                || requirements.isEmpty() || positionsText.isEmpty() || deadline.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        int positions;
        try {
            positions = Integer.parseInt(positionsText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Positions must be a number.");
            return;
        }

        UnifiedDataStore.addModule(moduleCode, moduleName, currentUser.getId(),
                positions, currentUser.getId());

        JOptionPane.showMessageDialog(this, "Job vacancy submitted successfully. Status: Pending");
        clearForm();
    }

    private void clearForm() {
        moduleCodeField.setText("");
        moduleNameField.setText(currentUser.getModuleName());
        responsibilitiesArea.setText("");
        requirementsArea.setText("");
        positionsField.setText("");
        deadlineField.setText("");
    }
}