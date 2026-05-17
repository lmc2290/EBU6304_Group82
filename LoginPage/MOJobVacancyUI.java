package LoginPage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MOJobVacancyUI extends JPanel {
    private final User currentUser;

    private JTextField moduleNameField;
    private JTextArea responsibilitiesArea;
    private JTextArea requirementsArea;
    private JTextField positionsField;
    private JTextField deadlineField;

    // Color palette
    private static final Color BG = new Color(241, 245, 249);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color PRIMARY = new Color(79, 70, 229);
    private static final Color PRIMARY_HOVER = new Color(55, 48, 163);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color INPUT_BG = new Color(248, 250, 252);

    public MOJobVacancyUI(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(10, 10));
        setBackground(BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        initializeUI();
    }

    private void initializeUI() {
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createFormPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(16, 24, 16, 24)
        ));

        JLabel titleLabel = new JLabel("\uD83D\uDCCB  Create TA Vacancy");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel moduleHint = new JLabel("Module: " + currentUser.getModuleName());
        moduleHint.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        moduleHint.setForeground(TEXT_SECONDARY);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setBackground(CARD_BG);
        rightPanel.add(moduleHint);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(BG);

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(CARD_BG);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(24, 28, 24, 28)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        moduleNameField = createStyledTextField(currentUser.getModuleName());
        responsibilitiesArea = createStyledTextArea(4);
        requirementsArea = createStyledTextArea(4);
        positionsField = createStyledTextField("");
        deadlineField = createStyledTextField("");

        int row = 0;

        // Module Name
        gbc.gridx = 0;
        gbc.gridy = row;
        formCard.add(createFormLabel("Module Name"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formCard.add(moduleNameField, gbc);
        gbc.gridwidth = 1;

        // Responsibilities
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTH;
        formCard.add(createFormLabel("Responsibilities"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formCard.add(new JScrollPane(responsibilitiesArea), gbc);
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;

        // Requirements
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTH;
        formCard.add(createFormLabel("Requirements"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formCard.add(new JScrollPane(requirementsArea), gbc);
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;

        // Positions
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formCard.add(createFormLabel("Positions"), gbc);

        gbc.gridx = 1;
        formCard.add(positionsField, gbc);

        // Deadline
        gbc.gridx = 2;
        formCard.add(createFormLabel("Deadline (YYYY-MM-DD):"), gbc);

        gbc.gridx = 3;
        row++;
        gbc.gridy = row;
        formCard.add(deadlineField, gbc);

        wrapper.add(formCard, new GridBagConstraints());
        return wrapper;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 12));
        panel.setBackground(BG);

        JButton saveButton = createStyledButton("\u2713  Save Vacancy", PRIMARY);
        JButton clearButton = createStyledButton("\u21BA  Clear", TEXT_SECONDARY);

        saveButton.addActionListener(e -> saveModule());
        clearButton.addActionListener(e -> clearForm());

        panel.add(clearButton);
        panel.add(saveButton);
        return panel;
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    private JTextField createStyledTextField(String text) {
        JTextField field = new JTextField(text, 20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(INPUT_BG);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    private JTextArea createStyledTextArea(int rows) {
        JTextArea area = new JTextArea(rows, 20);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setBackground(INPUT_BG);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(8, 12, 8, 12)
        ));
        return area;
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

    private void saveModule() {
        String moduleName = moduleNameField.getText().trim();
        String responsibilities = responsibilitiesArea.getText().trim();
        String requirements = requirementsArea.getText().trim();
        String positionsText = positionsField.getText().trim();
        String deadline = deadlineField.getText().trim();

        if (moduleName.isEmpty() || responsibilities.isEmpty() || requirements.isEmpty()
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

        Module module = new Module(
                moduleName,
                responsibilities,
                requirements,
                positions,
                deadline,
                "Pending Review"
        );

        MODataStore.addModule(module);

        JOptionPane.showMessageDialog(this, "Job vacancy submitted successfully.");
        clearForm();
    }

    private void clearForm() {
        moduleNameField.setText(currentUser.getModuleName());
        responsibilitiesArea.setText("");
        requirementsArea.setText("");
        positionsField.setText("");
        deadlineField.setText("");
    }
}
