package LoginPage;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Modernized MOMessageTAUI with indigo colour palette.
 * Loads approved applicants from UnifiedDataStore and persists messages to CSV.
 */
public class MOMessageTAUI extends JPanel {
    private final User currentUser;
    private JComboBox<String> moduleComboBox;
    private JTable taTable;
    private DefaultTableModel tableModel;
    private String selectedModuleId;

    // Indigo colour palette
    private static final Color INDIGO_PRIMARY = new Color(0x4F, 0x46, 0xE5);
    private static final Color INDIGO_LIGHT   = new Color(0xEE, 0xF2, 0xFF);
    private static final Color INDIGO_HOVER   = new Color(0xC7, 0xD2, 0xFE);

    private static final Color PAGE_BG        = new Color(0xF5, 0xF7, 0xFA);
    private static final Color CARD_BG        = Color.WHITE;
    private static final Color TITLE_COLOR    = new Color(0x1E, 0x1B, 0x4B);
    private static final Color SUBTITLE_COLOR = new Color(0x6B, 0x72, 0x80);
    private static final Color BORDER_COLOR   = new Color(0xE2, 0xE8, 0xF0);

    public MOMessageTAUI(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(PAGE_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initializeUI();
    }

    private void initializeUI() {
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createContentPanel(), BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(PAGE_BG);

        // Title bar
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(INDIGO_PRIMARY);
        titleBar.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        JLabel titleLabel = new JLabel("Message TA");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Send messages to approved and shortlisted teaching assistants");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(INDIGO_HOVER);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(INDIGO_PRIMARY);
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(subtitleLabel);

        titleBar.add(textPanel, BorderLayout.WEST);
        header.add(titleBar);

        // Module filter
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        filterPanel.setBackground(CARD_BG);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));

        JLabel filterLabel = new JLabel("Select Module:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        filterLabel.setForeground(TITLE_COLOR);

        moduleComboBox = new JComboBox<>();
        moduleComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        moduleComboBox.setPreferredSize(new Dimension(160, 34));
        moduleComboBox.setBackground(Color.WHITE);

        List<String[]> modules = UnifiedDataStore.getApprovedModules();
        for (String[] module : modules) {
            if (module.length >= 2) {
                moduleComboBox.addItem(module[0]);
            }
        }

        if (!modules.isEmpty() && modules.get(0).length >= 1) {
            selectedModuleId = modules.get(0)[0];
        } else {
            selectedModuleId = "";
        }

        moduleComboBox.addActionListener(e -> {
            String selected = (String) moduleComboBox.getSelectedItem();
            if (selected != null) {
                selectedModuleId = selected;
                refreshTAList();
            }
        });

        JButton refreshBtn = createStyledButton("Refresh", INDIGO_PRIMARY, INDIGO_PRIMARY.darker());
        refreshBtn.addActionListener(e -> refreshTAList());

        filterPanel.add(filterLabel);
        filterPanel.add(moduleComboBox);
        filterPanel.add(Box.createHorizontalStrut(16));
        filterPanel.add(refreshBtn);

        header.add(Box.createVerticalStrut(12));
        header.add(filterPanel);

        return header;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(CARD_BG);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        String[] columnNames = {"ID", "Name", "Email", "Module", "Action"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        taTable = new JTable(tableModel);
        taTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        taTable.setRowHeight(32);
        taTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        taTable.getTableHeader().setBackground(INDIGO_PRIMARY);
        taTable.getTableHeader().setForeground(Color.WHITE);
        taTable.setSelectionBackground(INDIGO_LIGHT);
        taTable.setGridColor(BORDER_COLOR);

        JScrollPane scrollPane = new JScrollPane(taTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        contentPanel.add(scrollPane, BorderLayout.CENTER);

        refreshTAList();

        taTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int row = taTable.rowAtPoint(evt.getPoint());
                int col = taTable.columnAtPoint(evt.getPoint());
                if (row >= 0 && col == 4) {
                    String taId = (String) taTable.getValueAt(row, 0);
                    String taName = (String) taTable.getValueAt(row, 1);
                    sendMessageToTA(taId, taName);
                }
            }
        });

        return contentPanel;
    }

    private void refreshTAList() {
        tableModel.setRowCount(0);

        // Include both Approved and Shortlisted applicants
        List<String[]> approved = UnifiedDataStore.getApprovedApplicants();
        List<String[]> shortlisted = UnifiedDataStore.getShortlistedApplicants();
        List<String[]> allTargets = new ArrayList<>();
        allTargets.addAll(approved);
        allTargets.addAll(shortlisted);

        // Deduplicate by taId
        Set<String> seenTaIds = new HashSet<>();
        for (String[] applicant : allTargets) {
            if (applicant.length >= 8 && applicant[3].equals(selectedModuleId)) {
                String taId = applicant[1];
                if (seenTaIds.contains(taId)) continue;
                seenTaIds.add(taId);

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
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Send Message to " + taName, true);
        dialog.setSize(550, 450);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(PAGE_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Header
        JPanel headerPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        headerPanel.setBackground(INDIGO_LIGHT);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INDIGO_HOVER, 1, true),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        JLabel toLabel = new JLabel("To:");
        toLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        toLabel.setForeground(TITLE_COLOR);
        JLabel toValue = new JLabel(taName);
        toValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel idLabel = new JLabel("TA ID:");
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        idLabel.setForeground(TITLE_COLOR);
        JLabel idValue = new JLabel(taId);
        idValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        headerPanel.add(toLabel);
        headerPanel.add(toValue);
        headerPanel.add(idLabel);
        headerPanel.add(idValue);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Message area
        JPanel messagePanel = new JPanel(new BorderLayout(8, 8));
        messagePanel.setBackground(PAGE_BG);

        JLabel msgLabel = new JLabel("Message:");
        msgLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        msgLabel.setForeground(TITLE_COLOR);
        messagePanel.add(msgLabel, BorderLayout.NORTH);

        JTextArea messageArea = new JTextArea(10, 40);
        messageArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setText("Dear " + taName + ",\n\n");
        messageArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        JScrollPane scrollPane = new JScrollPane(messageArea);
        messagePanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(messagePanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        buttonPanel.setBackground(PAGE_BG);

        JButton sendButton = createStyledButton("Send", INDIGO_PRIMARY, INDIGO_PRIMARY.darker());
        JButton cancelButton = createStyledButton("Cancel", SUBTITLE_COLOR, SUBTITLE_COLOR.darker());

        sendButton.addActionListener(e -> {
            String message = messageArea.getText();
            if (!message.isEmpty()) {
                UnifiedDataStore.addMessage("MO", currentUser.getId(), taId, taName,
                        selectedModuleId, "Message from MO", message);
                JOptionPane.showMessageDialog(dialog,
                        "Message sent to " + taName, "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Message cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(sendButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private JButton createStyledButton(String text, Color baseColor, Color hoverColor) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(baseColor);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                btn.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                btn.setBackground(baseColor);
            }
        });

        return btn;
    }
}
