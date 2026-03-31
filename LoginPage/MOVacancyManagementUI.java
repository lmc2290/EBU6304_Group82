package LoginPage;

import javax.swing.*;
import java.awt.*;

public class MOVacancyManagementUI extends JPanel {
    private User currentUser;
    private JTextField moduleField;
    private JTextArea responsibilityArea;
    private JTextArea requirementArea;
    private JTextField positionField;
    private JTextField deadlineField;

    public MOVacancyManagementUI(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        initializeUI();
    }

    private void initializeUI() {
        JLabel title = new JLabel("Post Vacancy", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        moduleField = new JTextField(currentUser.getModuleName(), 20);
        responsibilityArea = new JTextArea(4, 20);
        requirementArea = new JTextArea(4, 20);
        positionField = new JTextField(20);
        deadlineField = new JTextField(20);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Module Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(moduleField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Responsibilities:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JScrollPane(responsibilityArea), gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Requirements:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JScrollPane(requirementArea), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Positions:"), gbc);
        gbc.gridx = 1;
        formPanel.add(positionField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Deadline:"), gbc);
        gbc.gridx = 1;
        formPanel.add(deadlineField, gbc);

        JPanel buttonPanel = new JPanel();
        JButton saveBtn = new JButton("Save Vacancy");
        JButton clearBtn = new JButton("Clear");

        buttonPanel.add(saveBtn);
        buttonPanel.add(clearBtn);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        saveBtn.addActionListener(e -> saveVacancy());
        clearBtn.addActionListener(e -> clearForm());
    }

    private void saveVacancy() {
        String module = moduleField.getText().trim();
        String responsibilities = responsibilityArea.getText().trim();
        String requirements = requirementArea.getText().trim();
        String positionsText = positionField.getText().trim();
        String deadline = deadlineField.getText().trim();

        if (module.isEmpty() || responsibilities.isEmpty() || requirements.isEmpty()
                || positionsText.isEmpty() || deadline.isEmpty()) {
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

        Vacancy vacancy = new Vacancy(module, responsibilities, requirements, positions, deadline, "Pending Review");
        MockDatabase.addVacancy(vacancy);

        JOptionPane.showMessageDialog(this, "Vacancy submitted successfully.");
        clearForm();
    }

    private void clearForm() {
        moduleField.setText(currentUser.getModuleName());
        responsibilityArea.setText("");
        requirementArea.setText("");
        positionField.setText("");
        deadlineField.setText("");
    }
}