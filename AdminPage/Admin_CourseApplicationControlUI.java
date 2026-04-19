package AdminPage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Admin_CourseApplicationControlUI extends JPanel {
    private final Admin_CourseApplicationControl controller;
    private JTable requestTable;
    private DefaultTableModel tableModel;

    private final Color SUCCESS_GREEN = new Color(46, 204, 113);
    private final Color DANGER_RED = new Color(231, 76, 60);
    private final Color RESET_BLUE = new Color(52, 152, 219);
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
        JLabel title = new JLabel("Module Posting Approval");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Module Name", "Organiser", "Content", "Status & Actions"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 4;
            }
        };

        requestTable = new JTable(tableModel);
        setupTableBehavior();

        JScrollPane sp = new JScrollPane(requestTable);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(Color.WHITE);
        add(sp, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setPreferredSize(new Dimension(0, 80));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 20));
        actionPanel.setOpaque(false);

        JButton refreshBtn = createBtn("Refresh", PRIMARY_BLUE);
        refreshBtn.addActionListener(e -> {
            controller.loadData();
            JOptionPane.showMessageDialog(this, "Course data refreshed successfully!");
        });

        JButton exportBtn = createBtn("Export CSV", SUCCESS_GREEN);
        exportBtn.addActionListener(e -> controller.exportData());

        actionPanel.add(refreshBtn);
        actionPanel.add(exportBtn);
        bottom.add(actionPanel, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);
    }

    private void setupTableBehavior() {
        requestTable.setRowHeight(60);
        requestTable.setShowVerticalLines(false);
        requestTable.setIntercellSpacing(new Dimension(0, 0));

        // Click to edit directly
        requestTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = requestTable.columnAtPoint(e.getPoint());
                int row = requestTable.rowAtPoint(e.getPoint());
                if (col == 4 && !requestTable.isEditing()) {
                    requestTable.editCellAt(row, col);
                }
            }
        });

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        requestTable.setRowSorter(sorter);

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

        requestTable.getColumnModel().getColumn(4).setCellRenderer(new StatusRenderer());
        requestTable.getColumnModel().getColumn(4).setCellEditor(new StatusEditor());
    }

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

    private JButton createSmallBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBorderPainted(false);
        return b;
    }

    // Display correct buttons based on status
    class StatusRenderer extends JPanel implements TableCellRenderer {
        private final JPanel pendingPanel;
        private final JPanel resetPanel;

        public StatusRenderer() {
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(12, 10, 12, 10));
            setOpaque(true);

            pendingPanel = new JPanel(new GridLayout(1, 2, 8, 0));
            pendingPanel.setOpaque(false);
            pendingPanel.add(createSmallBtn("Approve", SUCCESS_GREEN));
            pendingPanel.add(createSmallBtn("Reject", DANGER_RED));

            resetPanel = new JPanel(new GridLayout(1, 1, 0, 0));
            resetPanel.setOpaque(false);
            resetPanel.add(createSmallBtn("Reset", RESET_BLUE));
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean isSel, boolean hasF, int r, int c) {
            String status = String.valueOf(v);
            setBackground(isSel ? t.getSelectionBackground() : Color.WHITE);
            removeAll();

            if (status.contains("Pending")) {
                add(pendingPanel, BorderLayout.CENTER);
            } else {
                add(resetPanel, BorderLayout.CENTER);
            }
            return this;
        }
    }

    // Clickable editor for approval actions
    class StatusEditor extends DefaultCellEditor {
        private final JPanel panel;
        private String currentStatus;
        private final JButton btnApprove;
        private final JButton btnReject;
        private final JButton btnReset;

        public StatusEditor() {
            super(new JCheckBox());
            panel = new JPanel();
            panel.setBorder(new EmptyBorder(12, 10, 12, 10));
            panel.setBackground(Color.WHITE);

            btnApprove = createSmallBtn("Approve", SUCCESS_GREEN);
            btnReject = createSmallBtn("Reject", DANGER_RED);
            btnReset = createSmallBtn("Reset", RESET_BLUE);

            btnApprove.addActionListener(e -> {
                currentStatus = "Approved";
                int row = requestTable.getEditingRow();
                String id = String.valueOf(requestTable.getValueAt(row, 0));
                controller.approveModule(id);
                fireEditingStopped();
            });

            btnReject.addActionListener(e -> {
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

            btnReset.addActionListener(e -> {
                currentStatus = "Pending Review";
                int row = requestTable.getEditingRow();
                String id = String.valueOf(requestTable.getValueAt(row, 0));
                controller.resetModule(id);
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable t, Object v, boolean isSel, int r, int c) {
            currentStatus = String.valueOf(v);
            panel.removeAll();

            if (currentStatus.contains("Pending")) {
                panel.setLayout(new GridLayout(1, 2, 8, 0));
                panel.add(btnApprove);
                panel.add(btnReject);
            } else {
                panel.setLayout(new GridLayout(1, 1, 0, 0));
                panel.add(btnReset);
            }

            panel.setBackground(isSel ? t.getSelectionBackground() : Color.WHITE);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return currentStatus;
        }
    }
}