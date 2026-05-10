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
        setSize(450, 400);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new BorderLayout(5, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ==========================================
        // 1. Profile confirmation
        // ==========================================
        JPanel profilePanel = new JPanel(new BorderLayout(5, 5));
        profilePanel.add(new JLabel("Your Profile for this Application:"), BorderLayout.NORTH);

        UserProfile currentProfile = controller.getUserProfile(userId);

        // 提取一个布尔值，判断档案是否为空
        boolean isProfileIncomplete = (currentProfile.getName() == null || currentProfile.getName().isEmpty());

        String profileStatus = !isProfileIncomplete
                ? "Profile Ready: " + currentProfile.getName() + " (" + currentProfile.getGrade() + ")"
                : "Profile Incomplete! Please update your profile first.";

        JLabel statusLabel = new JLabel(profileStatus);
        statusLabel.setForeground(!isProfileIncomplete ? new Color(0, 153, 76) : Color.RED);
        profilePanel.add(statusLabel, BorderLayout.CENTER);

        // ==========================================
        // 2. Cover letter text area
        // ==========================================
        JPanel clPanel = new JPanel(new BorderLayout(0, 5));
        clPanel.add(new JLabel("Cover Letter (Optional, Max 1000 chars):"), BorderLayout.NORTH);

        JTextArea coverLetterArea = new JTextArea();
        coverLetterArea.setLineWrap(true);
        coverLetterArea.setWrapStyleWord(true);

        if (currentProfile.getCoverLetterTemplate() != null && !currentProfile.getCoverLetterTemplate().isEmpty()) {
            coverLetterArea.setText(currentProfile.getCoverLetterTemplate());
            clPanel.add(new JLabel("💡 Template auto-loaded from your profile"), BorderLayout.SOUTH);
        }

        clPanel.add(new JScrollPane(coverLetterArea), BorderLayout.CENTER);

        formPanel.add(profilePanel, BorderLayout.NORTH);
        formPanel.add(clPanel, BorderLayout.CENTER);

        // ==========================================
        // 3. Action Button (动态变化的底部按钮)
        // ==========================================
        JButton actionBtn = new JButton();
        actionBtn.setPreferredSize(new Dimension(100, 40));
        actionBtn.setFont(new Font("Arial", Font.BOLD, 14));
        actionBtn.setFocusPainted(false);

        if (isProfileIncomplete) {
            // [新增逻辑]：如果没填档案，把按钮变成橙色的跳转快捷键
            actionBtn.setText("Go to Edit Profile");
            actionBtn.setBackground(new Color(255, 140, 0)); // 醒目的橘色
            actionBtn.setForeground(Color.WHITE);

            actionBtn.addActionListener(e -> {
                dispose(); // 1. 先关闭当前的申请窗口
                // 2. 调出 ProfileManagerDialog
                ProfileManagerDialog profileDialog = new ProfileManagerDialog((JFrame) getParent(), controller, userId);
                profileDialog.setVisible(true);
            });

        } else {
            // [原正常逻辑]：如果填了档案，显示蓝色的提交按钮
            actionBtn.setText("Confirm & Submit");
            actionBtn.setBackground(new Color(0, 102, 204));
            actionBtn.setForeground(Color.WHITE);

            actionBtn.addActionListener(e -> {
                String coverLetter = coverLetterArea.getText();

                if (coverLetter.length() > 1000) {
                    JOptionPane.showMessageDialog(this,
                            "Cover letter is too long (Max 1000 characters).",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                boolean success = controller.submitApplication(targetJob, userId, currentProfile, coverLetter);
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Application submitted successfully!\nAn email confirmation has been sent.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                }
            });
        }

        add(formPanel, BorderLayout.CENTER);
        add(actionBtn, BorderLayout.SOUTH); // 把按钮加到底部
    }
}