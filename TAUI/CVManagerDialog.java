package TAUI;


import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

/**
 * Boundary Class - CV Manager Dialog (US-03)
 * Allows TA to upload, view, and delete their CVs.
 */
public class CVManagerDialog extends JDialog {

    private TAController controller;
    private DefaultListModel<CVRecord> cvListModel;
    private JList<CVRecord> cvList;

    public CVManagerDialog(JFrame parent, TAController controller) {
        super(parent, "Manage My CVs", true); // 'true' makes it a modal dialog
        this.controller = controller;
        initUI();
        refreshCVList();
    }

    private void initUI() {
        setSize(450, 300);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        // Center List Area
        cvListModel = new DefaultListModel<>();
        cvList = new JList<>(cvListModel);
        cvList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cvList.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("My Uploaded CVs"));
        listPanel.add(new JScrollPane(cvList), BorderLayout.CENTER);

        // Right Button Area
        JPanel btnPanel = new JPanel(new GridLayout(3, 1, 5, 10));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(15, 5, 15, 15));

        JButton uploadBtn = new JButton("Upload New CV");
        JButton deleteBtn = new JButton("Delete Selected");
        JButton closeBtn = new JButton("Close");

        btnPanel.add(uploadBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(closeBtn);

        add(listPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.EAST);

        // --- Event Listeners ---
        uploadBtn.addActionListener(e -> handleUpload());

        deleteBtn.addActionListener(e -> {
            CVRecord selectedCV = cvList.getSelectedValue();
            if (selectedCV != null) {
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete '" + selectedCV + "'?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    controller.deleteCV(selectedCV);
                    refreshCVList();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a CV to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        closeBtn.addActionListener(e -> dispose());
    }

    private void handleUpload() {
        JFileChooser fileChooser = new JFileChooser();
        // US-03 AC1: Enforce file formats in the file picker
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CV Documents (*.pdf, *.docx)", "pdf", "docx");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            // Delegate validation to Controller
            String errorMessage = controller.uploadCV(selectedFile);

            if (errorMessage == null) {
                // US-03 AC3: Display uploaded file name and success message
                JOptionPane.showMessageDialog(this, "Successfully uploaded: \n" + selectedFile.getName(), "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshCVList();
            } else {
                // US-03 AC2: Display error if size or format is invalid
                JOptionPane.showMessageDialog(this, errorMessage, "Upload Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshCVList() {
        cvListModel.clear();
        for (CVRecord cv : controller.getUploadedCVs()) {
            cvListModel.addElement(cv);
        }
    }
}
