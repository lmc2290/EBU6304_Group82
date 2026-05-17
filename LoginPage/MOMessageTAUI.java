package LoginPage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MOMessageTAUI extends JPanel {
    private final User currentUser;
    private JComboBox<String> moduleComboBox;
    private JTable taTable;
    private DefaultTableModel tableModel;
    private String selectedModuleId;

    // Color palette
    private static final Color BG = new Color(241, 245, 249);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color PRIMARY = new Color(79, 70, 229);
    private static final Color ACCENT_ROSE = new Color(244, 63, 94);
    private static final Color TABLE_HEADER = new Color(52, 58, 64);
    private static final Color BORDER = new Color(226, 232, 240);

    public MOMessageTAUI(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(10, 10));
        setBackground(BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        initializeUI();
    }

    private void initializeUI() {
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(16, 24, 16, 24)
        ));

        JLabel titleLabel = new JLabel("\u2709\uFE0F  Message TA");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_PRIMARY);

        // Module selector in header
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(CARD_BG);

        JLabel filterLabel = new JLabel("Module:");
        filterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filterLabel.setForeground(TEXT_SECONDARY);
        rightPanel.add(filterLabel);

        moduleComboBox = new JComboBox<>();
        moduleComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        moduleComboBox.setPreferredSize(new Dimension(140, 32));

        List<Module> modules = MODataStore.loadModules();
        for (Module module : modules) {
            moduleComboBox.addItem(module.getModuleName());
        }
        if (!modules.isEmpty()) {
            selectedModuleId = modules.get(0).getModuleName();
        }
        moduleComboBox.addActionListener(e -> {
            String selected = (String) moduleComboBox.getSelectedItem();
            if (selected != null) {
                selectedModuleId = selected;
                refreshTAList();
            }
        });
        rightPanel.add(moduleComboBox);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(CARD_BG);
        wrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));

        String[] columnNames = {"TA ID", "Name", "Email", "Module", "Action"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        taTable = new JTable(tableModel);
        taTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        taTable.setRowHeight(36);
        taTable.setGridColor(new Color(230, 233, 238));
        taTable.setSelectionBackground(new Color(220, 235, 252));
        taTable.setSelectionForeground(Color.BLACK);
        taTable.setShowHorizontalLines(true);
        taTable.setShowVerticalLines(false);
        taTable.setFillsViewportHeight(true);

        taTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        taTable.getTableHeader().setBackground(TABLE_HEADER);
        taTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(taTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        wrapper.add(scrollPane, BorderLayout.CENTER);
        refreshTAList();

        // Click handler for "Send Message" action
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

        return wrapper;
    }

    private void refreshTAList() {
        tableModel.setRowCount(0);
        // Load Approved applicants for the selected module as "TAs"
        List<Applicant> applicants = MODataStore.loadApplicants();
        for (Applicant a : applicants) {
            if (a.getModuleName().equals(selectedModuleId)
                    && "Approved".equalsIgnoreCase(a.getStatus())) {
                String email = a.getApplicantId().toLowerCase() + "@bupt.edu.cn";
                addTA(a.getApplicantId(), a.getName(), email, a.getModuleName());
            }
        }
    }

    private void addTA(String id, String name, String email, String moduleId) {
        Object[] row = new Object[5];
        row[0] = id;
        row[1] = name;
        row[2] = email;
        row[3] = moduleId;
        row[4] = "\u2709 Send Message";
        tableModel.addRow(row);
    }

    private void sendMessageToTA(String taId, String taName) {
        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Send Message to " + taName,
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setSize(550, 450);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));
        panel.setBackground(CARD_BG);

        // Recipient info
        JPanel headerPanel = new JPanel(new GridLayout(2, 2, 10, 6));
        headerPanel.setBackground(CARD_BG);
        headerPanel.add(createInfoLabel("To:"));
        headerPanel.add(createInfoValue(taName));
        headerPanel.add(createInfoLabel("TA ID:"));
        headerPanel.add(createInfoValue(taId));
        panel.add(headerPanel, BorderLayout.NORTH);

        // Message area
        JPanel messagePanel = new JPanel(new BorderLayout(8, 8));
        messagePanel.setBackground(CARD_BG);
        messagePanel.add(new JLabel("Message:"), BorderLayout.NORTH);

        JTextArea messageArea = new JTextArea(10, 40);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageArea.setText("Dear " + taName + ",\n\n");
        messageArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(10, 12, 10, 12)
        ));
        JScrollPane scrollPane = new JScrollPane(messageArea);
        messagePanel.add(scrollPane, BorderLayout.CENTER);
        panel.add(messagePanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setBackground(CARD_BG);

        JButton cancelButton = createStyledButton("Cancel", TEXT_SECONDARY);
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton sendButton = createStyledButton("\u2713  Send", PRIMARY);
        sendButton.addActionListener(e -> {
            String message = messageArea.getText();
            if (!message.isEmpty()) {
                // Persist message to CSV
                MODataStore.saveMessage("MO", currentUser.getId(),
                        taId, taName, selectedModuleId, message);
                JOptionPane.showMessageDialog(dialog, "Message sent to " + taName, "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Message cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(sendButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_SECONDARY);
        return label;
    }

    private JLabel createInfoValue(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 24, 10, 24));
        return btn;
    }
}
