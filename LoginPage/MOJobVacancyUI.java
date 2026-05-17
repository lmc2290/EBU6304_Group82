package LoginPage;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * MO Job Vacancy Creation UI with indigo styling and total work hours support.
 */
public class MOJobVacancyUI extends JPanel {
    private final User currentUser;
    private JTextField moduleCodeField;
    private JTextField moduleNameField;
    private JTextArea responsibilitiesArea;
    private JTextArea requirementsArea;
    private JTextField positionsField;
    private JTextField deadlineField;
    private JTextField totalWorkHoursField;

    // Indigo palette
    private static final Color INDIGO_50 = new Color(238, 242, 255);
    private static final Color INDIGO_100 = new Color(224, 231, 255);
    private static final Color INDIGO_500 = new Color(99, 102, 241);
    private static final Color INDIGO_600 = new Color(79, 70, 229);
    private static final Color INDIGO_700 = new Color(67, 56, 202);
    private static final Color PAGE_BG = new Color(249, 250, 251);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TITLE_COLOR = new Color(17, 24, 39);
    private static final Color SUBTITLE_COLOR = new Color(107, 114, 128);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);

    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_INPUT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);

    public MOJobVacancyUI(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(10, 10));
        setBackground(PAGE_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        initializeUI();
    }

    private void initializeUI() {
        add(createTitlePanel(), BorderLayout.NORTH);
        add(createFormPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PAGE_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        JLabel titleLabel = new JLabel("Create TA Vacancy", SwingConstants.LEFT);
        titleLabel.setFont(FONT_HEADER);
        titleLabel.setForeground(TITLE_COLOR);

        JLabel subtitleLabel = new JLabel("Publish a new teaching assistant position for your module");
        subtitleLabel.setFont(FONT_INPUT);
        subtitleLabel.setForeground(SUBTITLE_COLOR);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(subtitleLabel);

        panel.add(textPanel, BorderLayout.WEST);
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(CARD_BG);
        wrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_BG);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        moduleCodeField = createStyledTextField();
        moduleNameField = createStyledTextField();
        moduleNameField.setText(currentUser.getModuleName());
        responsibilitiesArea = createStyledTextArea(4, 20);
        requirementsArea = createStyledTextArea(4, 20);
        positionsField = createStyledTextField();
        deadlineField = createStyledTextField();
        totalWorkHoursField = createStyledTextField();

        // Row 0: Module Code
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(createStyledLabel("Module Code:"), gbc);
        gbc.gridx = 1;
        formPanel.add(moduleCodeField, gbc);

        // Row 1: Module Name
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(createStyledLabel("Module Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(moduleNameField, gbc);

        // Row 2: Responsibilities
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        formPanel.add(createStyledLabel("Responsibilities:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(new JScrollPane(responsibilitiesArea), gbc);

        // Row 3: Requirements
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTH;
        formPanel.add(createStyledLabel("Requirements:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(new JScrollPane(requirementsArea), gbc);

        // Row 4: Positions
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(createStyledLabel("Positions:"), gbc);
        gbc.gridx = 1;
        formPanel.add(positionsField, gbc);

        // Row 5: Total Work Hours (NEW)
        gbc.gridx = 0; gbc.gridy = 5;
        JPanel hoursLabelPanel = new JPanel(new BorderLayout());
        hoursLabelPanel.setOpaque(false);
        JLabel hoursLabel = createStyledLabel("Total Work Hours:");
        hoursLabelPanel.add(hoursLabel, BorderLayout.CENTER);
        JLabel hoursHint = new JLabel(" (hours per semester, used for Admin workload control)");
        hoursHint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hoursHint.setForeground(SUBTITLE_COLOR);
        hoursLabelPanel.add(hoursHint, BorderLayout.EAST);
        formPanel.add(hoursLabelPanel, gbc);
        gbc.gridx = 1;
        formPanel.add(totalWorkHoursField, gbc);

        // Row 6: Deadline
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(createStyledLabel("Deadline (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        formPanel.add(deadlineField, gbc);

        wrapper.add(formPanel, BorderLayout.CENTER);
        return wrapper;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_LABEL);
        label.setForeground(TITLE_COLOR);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(FONT_INPUT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    private JTextArea createStyledTextArea(int rows, int cols) {
        JTextArea area = new JTextArea(rows, cols);
        area.setFont(FONT_INPUT);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return area;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(PAGE_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        JButton saveButton = createStyledButton("Save Vacancy", INDIGO_600, INDIGO_700);
        JButton clearButton = createStyledButton("Clear Form", new Color(107, 114, 128), new Color(75, 85, 99));

        saveButton.addActionListener(e -> saveModule());
        clearButton.addActionListener(e -> clearForm());

        panel.add(saveButton);
        panel.add(clearButton);

        return panel;
    }

    private JButton createStyledButton(String text, Color baseColor, Color hoverColor) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(FONT_BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setBackground(baseColor);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(baseColor);
            }
        });

        return btn;
    }

    private void saveModule() {
        String moduleCode = moduleCodeField.getText().trim();
        String moduleName = moduleNameField.getText().trim();
        String responsibilities = responsibilitiesArea.getText().trim();
        String requirements = requirementsArea.getText().trim();
        String positionsText = positionsField.getText().trim();
        String deadline = deadlineField.getText().trim();
        String totalWorkHoursText = totalWorkHoursField.getText().trim();

        if (moduleCode.isEmpty() || moduleName.isEmpty() || responsibilities.isEmpty()
                || requirements.isEmpty() || positionsText.isEmpty() || deadline.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
            return;
        }

        int positions;
        try {
            positions = Integer.parseInt(positionsText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Positions must be a number.");
            return;
        }

        int totalWorkHours = 0;
        if (!totalWorkHoursText.isEmpty()) {
            try {
                totalWorkHours = Integer.parseInt(totalWorkHoursText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Total Work Hours must be a number.");
                return;
            }
        }

        boolean success = UnifiedDataStore.addModule(moduleCode, moduleName, currentUser.getId(),
                positions, currentUser.getId(), totalWorkHours);

        if (success) {
            JOptionPane.showMessageDialog(this, "Job vacancy submitted successfully. Status: Pending");
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Module code '" + moduleCode + "' already exists.\nPlease use a different module code.",
                    "Duplicate Module", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        moduleCodeField.setText("");
        moduleNameField.setText(currentUser.getModuleName());
        responsibilitiesArea.setText("");
        requirementsArea.setText("");
        positionsField.setText("");
        deadlineField.setText("");
        totalWorkHoursField.setText("");
    }
}
