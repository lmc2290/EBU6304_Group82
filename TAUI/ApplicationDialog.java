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
    private String userId; // [Feature]: Added to fetch user-specific CVs

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

        // 1. Select CV dropdown (Dynamic Loading)
        JPanel cvPanel = new JPanel(new BorderLayout());
        cvPanel.add(new JLabel("Select Uploaded CV: "), BorderLayout.NORTH);

        // Fetch CVs specifically for this user
        java.util.List<CVRecord> cvs = controller.getUploadedCVs(userId);
        JComboBox<CVRecord> cvDropdown = new JComboBox<>(cvs.toArray(new CVRecord[0]));

        if (cvs.isEmpty()) {
            cvDropdown.addItem(new CVRecord("No CV found! Please manage CVs first.", ""));
            cvDropdown.setEnabled(false);
        }
        cvPanel.add(cvDropdown, BorderLayout.CENTER);

        // 2. Cover letter text area
        JPanel clPanel = new JPanel(new BorderLayout());
        clPanel.add(new JLabel("Cover Letter (Optional, Max 1000 chars):"), BorderLayout.NORTH);
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

        if (cvs.isEmpty()) {
            submitBtn.setEnabled(false);
        }

        submitBtn.addActionListener(e -> {
            // [Fix]: Resolve ClassCastException caused by previous refactoring
            CVRecord selectedCVRecord = (CVRecord) cvDropdown.getSelectedItem();

            // Defensive programming: prevent submission with empty or invalid CV
            if (selectedCVRecord == null || selectedCVRecord.getOriginalName().startsWith("No CV")) {
                JOptionPane.showMessageDialog(this, "Please select a valid CV.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String selectedCVName = selectedCVRecord.getOriginalName();
            String coverLetter = coverLetterArea.getText();

            // [Feature]: Robustness check for cover letter length
            if (coverLetter.length() > 1000) {
                JOptionPane.showMessageDialog(this,
                        "Cover letter is too long (Max 1000 characters).",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean success = controller.submitApplication(targetJob, userId, selectedCVRecord, coverLetter);
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