package LoginPage;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MOMessageTAUI extends JFrame {
    private User currentUser;
    private JComboBox<String> moduleComboBox;
    private JTable taTable;
    private DefaultTableModel tableModel;
    private String selectedModuleId;

    public MOMessageTAUI(User user) {
        this.currentUser = user;
        setTitle("Message TA");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(247, 247, 247));

        JLabel headerLabel = new JLabel("Message TA", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        filterPanel.setBackground(new Color(240, 240, 240));

        filterPanel.add(new JLabel("Select Module:"));
        moduleComboBox = new JComboBox<>();

        List<String[]> modules = UnifiedDataStore.getApprovedModules();
        for (String[] module : modules) {
            if (module.length >= 2) {
                moduleComboBox.addItem(module[0]);
            }
        }

        if (modules.isEmpty()) {
            moduleComboBox.addItem("ECS401");
            moduleComboBox.addItem("ECS414");
        }

        if (!modules.isEmpty() && modules.get(0).length >= 1) {
            selectedModuleId = modules.get(0)[0];
        } else {
            selectedModuleId = "ECS401";
        }

        moduleComboBox.addActionListener(e -> {
            String selected = (String) moduleComboBox.getSelectedItem();
            if (selected != null) {
                selectedModuleId = selected;
                refreshTAList();
            }
        });
        filterPanel.add(moduleComboBox);

        mainPanel.add(filterPanel, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        String[] columnNames = {"ID", "Name", "Email", "Module", "Action"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        taTable = new JTable(tableModel);
        taTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(taTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(tablePanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Back to Dashboard");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.addActionListener(e -> {
            dispose();
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            if (parentWindow != null) {
                parentWindow.toFront();
            }
        });
        bottomPanel.add(backButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        refreshTAList();

        taTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = taTable.rowAtPoint(evt.getPoint());
                int col = taTable.columnAtPoint(evt.getPoint());
                if (row >= 0 && col == 4) {
                    String taId = (String) taTable.getValueAt(row, 0);
                    String taName = (String) taTable.getValueAt(row, 1);
                    sendMessageToTA(taId, taName);
                }
            }
        });
    }

    private void refreshTAList() {
        tableModel.setRowCount(0);

        List<String[]> approvedApplicants = UnifiedDataStore.getApprovedApplicants();
        for (String[] applicant : approvedApplicants) {
            if (applicant.length >= 8 && applicant[3].equals(selectedModuleId)) {
                String taId = applicant[1];
                String taName = applicant[2];
                String email = taName.toLowerCase().replace(" ", ".") + "_" + taId.toLowerCase() + "@qmul.ac.uk";

                Object[] row = new Object[5];
                row[0] = taId;
                row[1] = taName;
                row[2] = email;
                row[3] = applicant[3];
                row[4] = "Send Message";
                tableModel.addRow(row);
            }
        }
    }

    private void sendMessageToTA(String taId, String taName) {
        JDialog dialog = new JDialog(this, "Send Message to " + taName, true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new GridLayout(2, 2, 10, 10));
        headerPanel.add(new JLabel("To:"));
        headerPanel.add(new JLabel(taName));
        headerPanel.add(new JLabel("TA ID:"));
        headerPanel.add(new JLabel(taId));
        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout());
        messagePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        messagePanel.add(new JLabel("Message:"), BorderLayout.NORTH);
        JTextArea messageArea = new JTextArea(10, 40);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setText("Dear " + taName + ",\n\n");
        JScrollPane scrollPane = new JScrollPane(messageArea);
        messagePanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(messagePanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> {
            String message = messageArea.getText();
            if (!message.isEmpty()) {
                UnifiedDataStore.addMessage(currentUser.getId(), "MO", taId, taName, selectedModuleId, message);
                JOptionPane.showMessageDialog(dialog, "Message sent to " + taName, "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Message cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(sendButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }
}