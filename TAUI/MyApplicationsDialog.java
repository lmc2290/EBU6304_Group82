package TAUI;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Boundary Class - My Applications Dialog (US-08)
 * A basic UI shell designed to display all application records
 * submitted by the current user.
 */
public class MyApplicationsDialog extends JDialog {

    private TAController controller;
    private String userId;

    private DefaultListModel<ApplicationRecord> listModel;
    private JList<ApplicationRecord> appList;
    private JTextArea detailsArea;

    public MyApplicationsDialog(JFrame parent, TAController controller, String userId) {
        super(parent, "My Applications", true); // Modal dialog
        this.controller = controller;
        this.userId = userId;

        initUI();
        loadApplications();
    }

    private void initUI() {
        setSize(600, 400);
        setLocationRelativeTo(getParent()); // Center the dialog
        setLayout(new BorderLayout());

        // ==========================================
        // 1. Left Panel: Application Record List (JList)
        // ==========================================
        listModel = new DefaultListModel<>();
        appList = new JList<>(listModel);
        appList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appList.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Application History"));
        leftPanel.add(new JScrollPane(appList), BorderLayout.CENTER);

        // ==========================================
        // 2. Right Panel: Empty Text Area (Placeholder)
        // ==========================================
        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Arial", Font.PLAIN, 14));
        detailsArea.setBackground(new Color(245, 245, 245));
        detailsArea.setText("\n\n   (Details will be displayed here in the next sprint)");

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Application Details"));
        rightPanel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);

        // ==========================================
        // 3. Assembly: JSplitPane for Master-Detail layout
        // ==========================================
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(250); // Set left panel width to 250px
        add(splitPane, BorderLayout.CENTER);

        // ==========================================
        // 4. Bottom Panel: Close Button
        // ==========================================
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        bottomPanel.add(closeBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Fetches the current user's application records from the Controller
     * and populates them into the JList.
     */
    private void loadApplications() {
        listModel.clear();
        List<ApplicationRecord> myApps = controller.getUserApplications(userId);

        for (ApplicationRecord app : myApps) {
            // Since we overrode toString() in ApplicationRecord,
            // this will automatically render the formatted display string!
            listModel.addElement(app);
        }
    }
}