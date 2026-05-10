package AdminPage;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

public class Admin_CourseApplicationControlUI extends JPanel {

    private final Admin_CourseApplicationControl controller;
    private JTable requestTable;
    private DefaultTableModel tableModel;

    private final Color SUCCESS_GREEN = new Color(46, 204, 113);
    private final Color DANGER_RED = new Color(231, 76, 60);
    private final Color BG_LIGHT = new Color(245, 247, 250);
    private final Color PRIMARY_BLUE = new Color(41, 128, 185);

    public Admin_CourseApplicationControlUI(
            Admin_CourseApplicationControl controller) {

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

        String[] columnNames = {
                "ID",
                "Module Name",
                "Organiser",
                "Content",
                "Status",
                "Action"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 3 || col == 5;
            }
        };

        requestTable = new JTable(tableModel);
        setupTableStyle();

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

    private void setupTableStyle() {
        requestTable.setRowHeight(60);
        requestTable.setShowVerticalLines(false);
        requestTable.setIntercellSpacing(new Dimension(0, 0));
        requestTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JTableHeader header = requestTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setPreferredSize(new Dimension(0, 45));

        requestTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = requestTable.rowAtPoint(e.getPoint());
                int col = requestTable.columnAtPoint(e.getPoint());

                if (row < 0) return;

                if (col == 3) {
                    String id = tableModel.getValueAt(row, 0).toString();
                    String name = tableModel.getValueAt(row, 1).toString();
                    JOptionPane.showMessageDialog(null,
                            "Module ID: " + id + "\nModule Name: " + name + "\n\nTA Support Required",
                            "Module Details", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        requestTable.getColumnModel().getColumn(4).setCellRenderer(new StatusRenderer());

        requestTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        requestTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor());
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
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        return btn;
    }

    class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value,
                    isSelected, hasFocus, row, column);

            String status = value.toString();

            if (status.startsWith("Approved")) {
                label.setForeground(SUCCESS_GREEN);
            } else if (status.startsWith("Rejected")) {
                label.setForeground(DANGER_RED);
            } else {
                label.setForeground(new Color(255, 165, 0));
            }

            label.setHorizontalAlignment(SwingConstants.CENTER);
            return label;
        }
    }

    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private final JButton approveBtn;
        private final JButton rejectBtn;

        public ButtonRenderer() {
            setLayout(new GridLayout(1, 2, 5, 0));
            setOpaque(true);
            approveBtn = createSmallBtn("Approve", SUCCESS_GREEN);
            rejectBtn = createSmallBtn("Reject", DANGER_RED);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            removeAll();
            String status = table.getValueAt(row, 4).toString();
            if ("Pending".equals(status)) {
                add(approveBtn);
            }
            add(rejectBtn);
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private final JPanel panel;
        private final JButton approveBtn;
        private final JButton rejectBtn;

        public ButtonEditor() {
            super(new JCheckBox());
            panel = new JPanel(new GridLayout(1, 2, 5, 0));
            panel.setBorder(new EmptyBorder(12, 10, 12, 10));

            approveBtn = createSmallBtn("Approve", SUCCESS_GREEN);
            rejectBtn = createSmallBtn("Reject", DANGER_RED);

            approveBtn.addActionListener(e -> {
                int viewRow = requestTable.getEditingRow();
                int row = requestTable.convertRowIndexToModel(viewRow);
                String id = tableModel.getValueAt(row, 0).toString();

                controller.approveModule(id);
                tableModel.setValueAt("Approved", row, 4);

                fireEditingStopped();
                JOptionPane.showMessageDialog(null, "Approved successfully.");
            });

            rejectBtn.addActionListener(e -> {
                int viewRow = requestTable.getEditingRow();
                int row = requestTable.convertRowIndexToModel(viewRow);

                String reason = JOptionPane.showInputDialog(null, "Enter reject reason:");
                if (reason == null || reason.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Reject reason required.");
                    fireEditingStopped();
                    return;
                }

                String id = tableModel.getValueAt(row, 0).toString();
                controller.rejectModule(id, reason);
                tableModel.setValueAt("Rejected: " + reason, row, 4);

                fireEditingStopped();
                JOptionPane.showMessageDialog(null, "Rejected successfully.");
            });

            panel.add(approveBtn);
            panel.add(rejectBtn);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            String status = table.getValueAt(row, 4).toString();
            panel.removeAll();
            if ("Pending".equals(status)) {
                panel.add(approveBtn);
            }
            panel.add(rejectBtn);
            panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "Action";
        }
    }
}