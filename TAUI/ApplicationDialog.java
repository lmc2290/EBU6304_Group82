package TAUI;

import javax.swing.*;
import java.awt.*;

public class ApplicationDialog extends JDialog {

    private TAController controller;
    private Job targetJob;
    private String userId;

    public ApplicationDialog(JFrame parent, TAController controller, Job targetJob, String userId) {
        super(parent, "Submit Application - " + targetJob.getTitle(), true);
        this.controller = controller;
        this.targetJob = targetJob;
        this.userId = userId;

        initUI();
    }

    private void initUI() {
        setSize(450, 350);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new BorderLayout(5, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. Profile confirmation (不再使用下拉框)
        JPanel profilePanel = new JPanel(new BorderLayout(5, 5));
        profilePanel.add(new JLabel("Your Profile for this Application:"), BorderLayout.NORTH);

        // 在 ApplicationDialog.java 的 initUI() 中修改：
        UserProfile currentProfile = controller.getUserProfile(userId);
        String profileStatus = (currentProfile.getName() != null && !currentProfile.getName().isEmpty())
                ? "Profile Ready: " + currentProfile.getName() + " (" + currentProfile.getGrade() + ")"
                : "Profile Incomplete! Please update in 'My Profile' first.";

        JLabel statusLabel = new JLabel(profileStatus);
        statusLabel.setForeground(currentProfile.getName() != null ? new Color(0, 153, 76) : Color.RED);
        profilePanel.add(statusLabel, BorderLayout.CENTER);

        // 2. Cover letter text area
        JPanel clPanel = new JPanel(new BorderLayout());
        clPanel.add(new JLabel("Cover Letter (Optional, Max 1000 chars):"), BorderLayout.NORTH);
        JTextArea coverLetterArea = new JTextArea();
        coverLetterArea.setLineWrap(true);
        clPanel.add(new JScrollPane(coverLetterArea), BorderLayout.CENTER);

        formPanel.add(profilePanel, BorderLayout.NORTH);
        formPanel.add(clPanel, BorderLayout.CENTER);

        // 3. Submit button
        JButton submitBtn = new JButton("Confirm & Submit");
        submitBtn.setPreferredSize(new Dimension(100, 40));
        submitBtn.setBackground(new Color(0, 102, 204));
        submitBtn.setForeground(Color.WHITE);

        // 如果没填名字，禁用提交按钮
        if (currentProfile.getName() == null || currentProfile.getName().isEmpty()) {
            submitBtn.setEnabled(false);
        }

        submitBtn.addActionListener(e -> {
            String coverLetter = coverLetterArea.getText();

            if (coverLetter.length() > 1000) {
                JOptionPane.showMessageDialog(this, "Cover letter is too long (Max 1000 characters).", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 修改为传递 currentProfile
            boolean success = controller.submitApplication(targetJob, userId, currentProfile, coverLetter);
            if (success) {
                JOptionPane.showMessageDialog(this, "Application submitted successfully!\nAn email confirmation has been sent.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        });

        add(formPanel, BorderLayout.CENTER);
        add(submitBtn, BorderLayout.SOUTH);
    }
}