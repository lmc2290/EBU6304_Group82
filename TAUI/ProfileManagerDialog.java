package TAUI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileManagerDialog extends JDialog {
    private TAController controller;
    private String userId;
    private UserProfile profile;

    private JTextField nameField, collegeField, otherSkillsField;
    private JComboBox<String> genderCombo, gradeCombo;
    private JTextArea expArea;

    // 技能复选框列表
    private List<JCheckBox> skillCheckBoxes;
    private final String[] SKILL_OPTIONS = {"Python", "Java", "MATLAB", "C", "C++", "JavaScript"};

    public ProfileManagerDialog(JFrame parent, TAController controller, String userId) {
        super(parent, "Personal Profile Management", true);
        this.controller = controller;
        this.userId = userId;
        this.profile = controller.getUserProfile(userId);
        initUI();
    }

    private void initUI() {
        setSize(500, 650);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        // --- Tab 1: Basic Information ---
        JPanel baseInfoPanel = new JPanel(new GridLayout(4, 2, 10, 30));
        baseInfoPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        baseInfoPanel.add(new JLabel("Full Name:"));
        nameField = new JTextField(profile.getName());
        baseInfoPanel.add(nameField);

        baseInfoPanel.add(new JLabel("Gender:"));
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderCombo.setSelectedItem(profile.getGender());
        baseInfoPanel.add(genderCombo);

        baseInfoPanel.add(new JLabel("Academic Grade:"));
        gradeCombo = new JComboBox<>(new String[]{"Undergraduate", "Postgraduate", "PhD"});
        gradeCombo.setSelectedItem(profile.getGrade());
        baseInfoPanel.add(gradeCombo);

        baseInfoPanel.add(new JLabel("College/School:"));
        collegeField = new JTextField(profile.getCollege());
        baseInfoPanel.add(collegeField);

        // --- Tab 2: Skills Selection ---
        JPanel skillTab = new JPanel(new BorderLayout(10, 10));
        skillTab.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel checkPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        checkPanel.setBorder(BorderFactory.createTitledBorder("Core Technical Skills"));
        skillCheckBoxes = new ArrayList<>();

        List<String> savedSkills = profile.getSelectedSkills();
        for (String skill : SKILL_OPTIONS) {
            JCheckBox cb = new JCheckBox(skill);
            if (savedSkills != null && savedSkills.contains(skill)) cb.setSelected(true);
            skillCheckBoxes.add(cb);
            checkPanel.add(cb);
        }

        JPanel otherSkillPanel = new JPanel(new BorderLayout(5, 5));
        otherSkillPanel.add(new JLabel("Other Skills (e.g. AI, Graphic Design):"), BorderLayout.NORTH);
        otherSkillsField = new JTextField(profile.getOtherSkills());
        otherSkillPanel.add(otherSkillsField, BorderLayout.CENTER);

        skillTab.add(checkPanel, BorderLayout.CENTER);
        skillTab.add(otherSkillPanel, BorderLayout.SOUTH);

        // --- Tab 3: Experience ---
        JPanel expPanel = new JPanel(new BorderLayout());
        expPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        expArea = new JTextArea(profile.getExperience());
        expArea.setLineWrap(true);
        expPanel.add(new JLabel("Relevant Experience:"), BorderLayout.NORTH);
        expPanel.add(new JScrollPane(expArea), BorderLayout.CENTER);

        tabbedPane.addTab("Basic Info", baseInfoPanel);
        tabbedPane.addTab("Skills", skillTab);
        tabbedPane.addTab("Experience", expPanel);

        add(tabbedPane, BorderLayout.CENTER);

        JButton saveBtn = new JButton("Save Profile");
        saveBtn.setPreferredSize(new Dimension(0, 50));
        saveBtn.setFont(new Font("Arial", Font.BOLD, 14));
        saveBtn.setBackground(new Color(51, 153, 255));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> handleSave());
        add(saveBtn, BorderLayout.SOUTH);
    }

    private void handleSave() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        profile.setName(nameField.getText());
        profile.setGender((String) genderCombo.getSelectedItem());
        profile.setGrade((String) gradeCombo.getSelectedItem());
        profile.setCollege(collegeField.getText());

        // 保存勾选的技能
        List<String> selected = new ArrayList<>();
        for (JCheckBox cb : skillCheckBoxes) {
            if (cb.isSelected()) selected.add(cb.getText());
        }
        profile.setSelectedSkills(selected);
        profile.setOtherSkills(otherSkillsField.getText());
        profile.setExperience(expArea.getText());

        controller.saveUserProfile(userId, profile);
        JOptionPane.showMessageDialog(this, "Profile updated successfully!");
        dispose();
    }
}