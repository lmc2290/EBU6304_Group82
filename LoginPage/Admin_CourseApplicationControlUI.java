package LoginPage;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

public class Admin_CourseApplicationControlUI extends JPanel {
    private JTable requestTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    // 配色与样式保持一致
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

    private void initializeUI() {
        // --- 顶部标题 ---
        JLabel title = new JLabel("Module Posting Approval");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        // --- 表格设置 ---
        // 列：ID, Name, MO, Content, Status/Actions (关键)
        String[] columnNames = {"ID", "Module Name", "Organiser", "Content", "Status & Actions"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int r, int c) { 
                // 只有当状态是 "Pending" 且是最后一列时才可点
                String status = getValueAt(r, 4).toString();
                return c == 4 && status.equals("Pending"); 
            }
        };
        
        requestTable = new JTable(tableModel);
        setupTableLogic();

        JScrollPane scrollPane = new JScrollPane(requestTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupTableLogic() {
        requestTable.setRowHeight(60);
        requestTable.setShowVerticalLines(false);
        requestTable.setIntercellSpacing(new Dimension(0, 0));

        // 1. 设置排序规则：Pending 在上，Approved/Rejected 在下
        sorter = new TableRowSorter<>(tableModel);
        requestTable.setRowSorter(sorter);
        
        // 自定义比较器：让 "Pending" 永远排在最前面
        sorter.setComparator(4, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                if (s1.equals(s2)) return 0;
                if (s1.equals("Pending")) return -1; // s1排前面
                if (s2.equals("Pending")) return 1;  // s2排前面
                return s1.compareTo(s2);
            }
        });
        
        // 默认按状态列升序（即Pending排第一）
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(4, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);

        // 2. 设置渲染器：根据状态显示按钮或文字
        requestTable.getColumnModel().getColumn(4).setCellRenderer(new StatusActionRenderer());
        requestTable.getColumnModel().getColumn(4).setCellEditor(new StatusActionEditor());
    }

    private void addMockData() {
        List<Module> modules = MockDataManager.getAllModules();
        for (Module module : modules) {
            tableModel.addRow(new Object[]{module.getId(), module.getName(), module.getOrganiserId(), "VIEW", module.getStatus()});
        }
    }

    // ===================== 核心组件：状态渲染器 =====================
    
    class StatusActionRenderer extends JPanel implements TableCellRenderer {
        private JLabel statusLabel = new JLabel("", SwingConstants.CENTER);
        private JPanel btnPanel = new JPanel(new GridLayout(1, 2, 8, 0)); // 解决黑块/换行：GridLayout

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

    // ===================== 核心组件：状态编辑器（处理点击） =====================

    class StatusActionEditor extends DefaultCellEditor {
        private JPanel panel;
        private String currentStatus;

        public StatusActionEditor() {
            super(new JCheckBox());
            panel = new JPanel(new GridLayout(1, 2, 8, 0));
            panel.setBorder(new EmptyBorder(12, 10, 12, 10));

            JButton appBtn = createSmallButton("Approve", SUCCESS_GREEN);
            JButton rejBtn = createSmallButton("Reject", DANGER_RED);

            appBtn.addActionListener(e -> {
                currentStatus = "Approved";
                // Update module status in MockDataManager
                int row = requestTable.getEditingRow();
                String moduleId = (String) requestTable.getValueAt(row, 0);
                MockDataManager.updateModuleStatus(moduleId, "Approved");
                fireEditingStopped(); // 停止编辑并触发模型更新
            });

            rejBtn.addActionListener(e -> {
                String reason = JOptionPane.showInputDialog(null, "Rejection Reason:", "Feedback", JOptionPane.PLAIN_MESSAGE);
                if (reason != null && !reason.trim().isEmpty()) {
                    currentStatus = "Rejected: " + reason;
                    // Update module status in MockDataManager
                    int row = requestTable.getEditingRow();
                    String moduleId = (String) requestTable.getValueAt(row, 0);
                    MockDataManager.updateModuleStatus(moduleId, "Rejected");
                    fireEditingStopped();
                } else {
                    cancelCellEditing(); // 没写理由不让拒绝
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

    private JButton createSmallButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBorderPainted(false);
        return b;
    }
}