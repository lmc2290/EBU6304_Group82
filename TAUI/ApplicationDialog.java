package TAUI;

import javax.swing.*;
import java.awt.*;

/**
 * Boundary Class - Submit Application Dialog
 * Uses JDialog to implement a modal dialog without leaving the main interface.
 */
public class ApplicationDialog extends JDialog {

    private TAController controller;
    private Job targetJob;

    public ApplicationDialog(JFrame parent, TAController controller, Job targetJob) {
        super(parent, "Submit Application - " + targetJob.getTitle(), true);
        this.controller = controller;
        this.targetJob = targetJob;

        initUI();
    }

    private void initUI() {
        setSize(450, 350);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new BorderLayout(5, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. Select CV dropdown (Dynamic Loading)
        JPanel cvPanel = new JPanel(new BorderLayout());
        cvPanel.add(new JLabel("Select Uploaded CV: "), BorderLayout.NORTH);

        // [修改] 动态从 Controller 获取简历列表
        java.util.List<String> cvs = controller.getUploadedCVs();
        JComboBox<String> cvDropdown = new JComboBox<>(cvs.toArray(new String[0]));

        // [新增] 如果没有简历的提示与保护逻辑
        if (cvs.isEmpty()) {
            cvDropdown.addItem("No CV found! Please use 'Manage My CVs' first.");
            cvDropdown.setEnabled(false);
        }
        cvPanel.add(cvDropdown, BorderLayout.CENTER);

        // 2. Cover letter text area
        JPanel clPanel = new JPanel(new BorderLayout());
        clPanel.add(new JLabel("Cover Letter (Optional):"), BorderLayout.NORTH);
        JTextArea coverLetterArea = new JTextArea();
        coverLetterArea.setLineWrap(true);
        clPanel.add(new JScrollPane(coverLetterArea), BorderLayout.CENTER);

        formPanel.add(cvPanel, BorderLayout.NORTH);
        formPanel.add(clPanel, BorderLayout.CENTER);

        // 3. Submit button
        JButton submitBtn = new JButton("Confirm & Submit");
        submitBtn.setPreferredSize(new Dimension(100, 40));
        submitBtn.setBackground(new Color(0, 102, 204));
        submitBtn.setForeground(Color.WHITE);

        // [新增] 禁用提交按钮如果没有简历
        if (cvs.isEmpty()) {
            submitBtn.setEnabled(false);
        }

        submitBtn.addActionListener(e -> {
            String selectedCV = (String) cvDropdown.getSelectedItem();
            String coverLetter = coverLetterArea.getText();

            boolean success = controller.submitApplication(targetJob, selectedCV, coverLetter);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Application submitted successfully!\nAn email confirmation has been sent.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        });

        add(formPanel, BorderLayout.CENTER);
        add(submitBtn, BorderLayout.SOUTH);
    }
}