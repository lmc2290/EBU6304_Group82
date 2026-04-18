package AdminPage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Admin_CourseApplicationControlUI extends JPanel {
    private final Admin_CourseApplicationControl controller;
    private JTable requestTable;
    private DefaultTableModel tableModel;

    private final Color SUCCESS_GREEN = new Color(46, 204, 113);
    private final Color DANGER_RED = new Color(231, 76, 60);
    private final Color BG_LIGHT = new Color(245, 247, 250);
    private final Color PRIMARY_BLUE = new Color(41, 128, 185);

    public Admin_CourseApplicationControlUI(Admin_CourseApplicationControl controller) {
        this.controller = controller;
        setLayout(new BorderLayout(0, 20));
        setBackground(BG_LIGHT);
        setBorder(new EmptyBorder(30, 40, 30, 40));
        initUI();
        controller.setTableModel(tableModel);
        controller.loadData();
    }

    private void initUI() {
        // Title label
        JLabel title = new JLabel("Module Posting Approval");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        // Table model with non-editable cells except status column
        String[] columnNames = {"ID", "Module Name", "Organiser", "Content", "Status & Actions"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                if (c != 4) return false;
                String status = String.valueOf(getValueAt(r, 4));
                return status.contains("Pending");
            }
        };

        requestTable = new JTable(tableModel);
        setupTableBehavior();

        // Scroll pane for table
        JScrollPane sp = new JScrollPane(requestTable);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(Color.WHITE);
        add(sp, BorderLayout.CENTER);

        // Bottom action panel
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setPreferredSize(new Dimension(0, 80));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 20));
        actionPanel.setOpaque(false);

        // Refresh button with notification
        JButton refreshBtn = createBtn("Refresh", PRIMARY_BLUE);
        refreshBtn.addActionListener(e -> {
            controller.loadData();
            JOptionPane.showMessageDialog(this, "Course data refreshed successfully!");
        });

        // Export CSV button
        JButton exportBtn = createBtn("Export CSV", SUCCESS_GREEN);
        exportBtn.addActionListener(e -> controller.exportData());

        actionPanel.add(refreshBtn);
        actionPanel.add(exportBtn);
        bottom.add(actionPanel, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);
    }

    // Table appearance and sorting settings
    private void setupTableBehavior() {
        requestTable.setRowHeight(60);
        requestTable.setShowVerticalLines(false);
        requestTable.setIntercellSpacing(new Dimension(0, 0));

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        requestTable.setRowSorter(sorter);

        // Sort to show Pending items first
        sorter.setComparator(4, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                boolean p1 = s1.contains("Pending");
                boolean p2 = s2.contains("Pending");
                if (p1 && !p2) return -1;
                if (!p1 && p2) return 1;
                return s1.compareTo(s2);
            }
        });

        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(4, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);

        // Custom render and editor for status column
        requestTable.getColumnModel().getColumn(4).setCellRenderer(new StatusRenderer());
        requestTable.getColumnModel().getColumn(4).setCellEditor(new StatusEditor());
    }

    // Create styled navigation button
    private JButton createBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(160, 40));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Create small approve/reject buttons
    private JButton createSmallBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBorderPainted(false);
        return b;
    }

    // Custom renderer for status column
    class StatusRenderer extends JPanel implements TableCellRenderer {
        private final JLabel statusLabel = new JLabel("", SwingConstants.CENTER);
        private final JPanel btnPanel;

        public StatusRenderer() {
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(12, 10, 12, 10));
            setOpaque(true);
            btnPanel = new JPanel(new GridLayout(1, 2, 8, 0));
            btnPanel.setOpaque(false);
            btnPanel.add(createSmallBtn("Approve", SUCCESS_GREEN));
            btnPanel.add(createSmallBtn("Reject", DANGER_RED));
            statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean isSel, boolean hasF, int r, int c) {
            String status = String.valueOf(v);
            setBackground(isSel ? t.getSelectionBackground() : Color.WHITE);
            removeAll();
            if (status.contains("Pending")) {
                add(btnPanel, BorderLayout.CENTER);
            } else {
                statusLabel.setText(status);
                statusLabel.setForeground(status.startsWith("Approved") ? SUCCESS_GREEN : DANGER_RED);
                add(statusLabel, BorderLayout.CENTER);
            }
            return this;
        }
    }

    // Custom editor for status operations
    class StatusEditor extends DefaultCellEditor {
        private final JPanel panel;
        private String currentStatus;

        public StatusEditor() {
            super(new JCheckBox());
            panel = new JPanel(new GridLayout(1, 2, 8, 0));
            panel.setBorder(new EmptyBorder(12, 10, 12, 10));

            JButton btnApp = createSmallBtn("Approve", SUCCESS_GREEN);
            JButton btnRej = createSmallBtn("Reject", DANGER_RED);

            // Approve action
            btnApp.addActionListener(e -> {
                currentStatus = "Approved";
                int row = requestTable.getEditingRow();
                String id = String.valueOf(requestTable.getValueAt(row, 0));
                controller.approveModule(id);
                fireEditingStopped();
            });

            // Reject action with reason input
            btnRej.addActionListener(e -> {
                String reason = JOptionPane.showInputDialog(null,
                        "Rejection Reason is REQUIRED:",
                        "Mandatory Feedback",
                        JOptionPane.WARNING_MESSAGE);

                if (reason != null && !reason.isBlank()) {
                    currentStatus = "Rejected: " + reason.replace(",", ";");
                    int row = requestTable.getEditingRow();
                    String id = String.valueOf(requestTable.getValueAt(row, 0));
                    controller.rejectModule(id, reason);
                    fireEditingStopped();
                } else {
                    cancelCellEditing();
                    if (reason != null) {
                        JOptionPane.showMessageDialog(null, "Reason cannot be empty.");
                    }
                }
            });

            panel.add(btnApp);
            panel.add(btnRej);
        }

        @Override
        public Component getTableCellEditorComponent(JTable t, Object v, boolean isSel, int r, int c) {
            panel.setBackground(t.getSelectionBackground());
            currentStatus = String.valueOf(v);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return currentStatus;
        }
    }
}