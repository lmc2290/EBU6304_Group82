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
        // The 'true' parameter indicates this is a modal window;
        // the main interface cannot be clicked until this is closed
        super(parent, "Submit Application - " + targetJob.getTitle(), true);
        this.controller = controller;
        this.targetJob = targetJob;

        initUI();
    }

    private void initUI() {
        setSize(450, 350);
        setLocationRelativeTo(getParent()); // Center relative to the parent window
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new BorderLayout(5, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. Select CV dropdown (US-06 Requirement 1)
        JPanel cvPanel = new JPanel(new BorderLayout());
        cvPanel.add(new JLabel("Select Uploaded CV: "), BorderLayout.NORTH);
        JComboBox<String> cvDropdown = new JComboBox<>(new String[]{
                "CV_Software_Engineering_v1.pdf",
                "CV_Updated_2026.pdf"
        });
        cvPanel.add(cvDropdown, BorderLayout.CENTER);

        // 2. Cover letter text area (US-06 Requirement 1)
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

        submitBtn.addActionListener(e -> {
            String selectedCV = (String) cvDropdown.getSelectedItem();
            String coverLetter = coverLetterArea.getText();

            // Pass the input data to the Controller for processing
            boolean success = controller.submitApplication(targetJob, selectedCV, coverLetter);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Application submitted successfully!\nAn email confirmation has been sent.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Automatically close the dialog after a successful submission
            }
        });

        add(formPanel, BorderLayout.CENTER);
        add(submitBtn, BorderLayout.SOUTH);
    }
}