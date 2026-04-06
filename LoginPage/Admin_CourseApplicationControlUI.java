package LoginPage;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

// Course Application Approval Panel for Admin
public class Admin_CourseApplicationControlUI extends JPanel {
    private JTable requestTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    // UI Style Constants
    private final Color SUCCESS_GREEN = new Color(46, 204, 113);
    private final Color DANGER_RED = new Color(231, 76, 60);
    private final Color BG_LIGHT = new Color(245, 247, 250);
    private final Color TEXT_DARK = new Color(44, 62, 80);

    public Admin_CourseApplicationControlUI(User user) {
        this.setLayout(new BorderLayout(0, 20));
        this.setBackground(BG_LIGHT);
        this.setBorder(new EmptyBorder(30, 40, 30, 40));

        initializeUI();
        addMockData();
    }

    // Initialize UI components
    private void initializeUI() {
        // Page title
        JLabel title = new JLabel("Module Posting Approval");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        // Table structure definition
        String[] columnNames = {"ID", "Module Name", "Organiser", "Content", "Status & Actions"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            // Only status column is editable when status is Pending
            @Override public boolean isCellEditable(int r, int c) {
                String status = getValueAt(r, 4).toString();
                return c == 4 && status.equals("Pending");
            }
        };

        // ✅ 修正笔误：J → JTable
        requestTable = new JTable(tableModel);
        setupTableLogic();

        // Table scroll panel
        JScrollPane scrollPane = new JScrollPane(requestTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
    }

    // Configure table appearance, sorting and rendering rules
    private void setupTableLogic() {
        requestTable.setRowHeight(60);
        requestTable.setShowVerticalLines(false);
        requestTable.setIntercellSpacing(new Dimension(0, 0));

        // Custom sorting: Pending items at the top
        sorter = new TableRowSorter<>(tableModel);
        requestTable.setRowSorter(sorter);

        sorter.setComparator(4, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                if (s1.equals(s2)) return 0;
                if (s1.equals("Pending")) return -1;
                if (s2.equals("Pending")) return 1;
                return s1.compareTo(s2);
            }
        });

        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(4, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);

        // Bind custom renderer and editor for status column
        requestTable.getColumnModel().getColumn(4).setCellRenderer(new StatusActionRenderer());
        requestTable.getColumnModel().getColumn(4).setCellEditor(new StatusActionEditor());
    }

    // Add sample test data
    private void addMockData() {
        tableModel.addRow(new Object[]{"CS101", "Java Basics", "Prof. Lee", "VIEW", "Pending"});
        tableModel.addRow(new Object[]{"CS202", "Databases", "Dr. Wong", "VIEW", "Pending"});
        tableModel.addRow(new Object[]{"CS303", "AI Intro", "Dr. Chen", "VIEW", "Approved"});
    }

    // Custom renderer: show buttons for Pending, text for Approved/Rejected
    class StatusActionRenderer extends JPanel implements TableCellRenderer {
        private JLabel statusLabel = new JLabel("", SwingConstants.CENTER);
        private JPanel btnPanel = new JPanel(new GridLayout(1, 2, 8, 0));

        public StatusActionRenderer() {
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(12, 10, 12, 10));
            setOpaque(true);

            btnPanel.setOpaque(false);
            btnPanel.add(createSmallButton("Approve", SUCCESS_GREEN));
            btnPanel.add(createSmallButton("Reject", DANGER_RED));

            statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
            String status = v.toString();
            setBackground(isS ? t.getSelectionBackground() : Color.WHITE);

            removeAll();
            if (status.equals("Pending")) {
                add(btnPanel, BorderLayout.CENTER);
            } else {
                statusLabel.setText(status);
                statusLabel.setForeground(status.startsWith("Approved") ? SUCCESS_GREEN : DANGER_RED);
                add(statusLabel, BorderLayout.CENTER);
            }
            return this;
        }
    }

    // Custom editor: handle Approve/Reject button actions
    class StatusActionEditor extends DefaultCellEditor {
        private JPanel panel;
        private String currentStatus;

        public StatusActionEditor() {
            super(new JCheckBox());
            panel = new JPanel(new GridLayout(1, 2, 8, 0));
            panel.setBorder(new EmptyBorder(12, 10, 12, 10));

            JButton appBtn = createSmallButton("Approve", SUCCESS_GREEN);
            JButton rejBtn = createSmallButton("Reject", DANGER_RED);

            // Approve action
            appBtn.addActionListener(e -> {
                currentStatus = "Approved";
                fireEditingStopped();
            });

            // Reject action with reason input
            rejBtn.addActionListener(e -> {
                String reason = JOptionPane.showInputDialog(null, "Rejection Reason:", "Feedback", JOptionPane.PLAIN_MESSAGE);
                if (reason != null && !reason.trim().isEmpty()) {
                    currentStatus = "Rejected: " + reason;
                    fireEditingStopped();
                } else {
                    cancelCellEditing();
                }
            });

            panel.add(appBtn);
            panel.add(rejBtn);
        }

        @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean isS, int r, int c) {
            panel.setBackground(t.getSelectionBackground());
            return panel;
        }

        @Override public Object getCellEditorValue() { return currentStatus; }
    }

    // Create compact styled action buttons
    private JButton createSmallButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBorderPainted(false);
        return b;
    }
}