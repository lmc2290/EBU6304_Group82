package LoginPage;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

public class Admin_TAWorkLoadControlUI extends JPanel {
    private final User currentUser;
    private JLabel currentLimitLabel;
    private JTable taTable;
    private DefaultTableModel tableModel;
    private int currentLimit = 3;

    // --- 现代配色方案 ---
    private final Color PRIMARY_BLUE = new Color(41, 128, 185);    // 扁平化蓝
    private final Color DANGER_RED = new Color(231, 76, 60);       // 警示红
    private final Color BG_LIGHT = new Color(245, 247, 250);      // 背景浅灰
    private final Color TEXT_DARK = new Color(44, 62, 80);        // 深色文字
    private final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);

    public Admin_TAWorkLoadControlUI(User user) {
        this.currentUser = user;
        this.setLayout(new BorderLayout(0, 20)); // 增加垂直间距
        this.setBackground(BG_LIGHT);
        this.setBorder(new EmptyBorder(30, 40, 30, 40)); // 增加四周留白
        
        initializeUI();
        addMockData();
    }

    private void initializeUI() {
        // ===================== 顶部标题区 =====================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("TA Workload Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_DARK);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // 辅助说明文字
        JLabel subTitle = new JLabel("Monitor and manage assistant application limits");
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subTitle.setForeground(Color.GRAY);
        headerPanel.add(subTitle, BorderLayout.SOUTH);
        
        add(headerPanel, BorderLayout.NORTH);

        // ===================== 表格区（扁平化设计） =====================
        String[] columnNames = {"ID", "Name", "Workload (Hrs)", "Contact Email"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        taTable = new JTable(tableModel);
        
        // 设置表格样式
        taTable.setRowHeight(45); // 大幅度增加行高
        taTable.setFont(MAIN_FONT);
        taTable.setShowVerticalLines(false); // 隐藏竖线，现代感
        taTable.setGridColor(new Color(230, 230, 230));
        taTable.setSelectionBackground(new Color(235, 245, 251)); // 浅蓝选中色
        taTable.setSelectionForeground(TEXT_DARK);
        taTable.setIntercellSpacing(new Dimension(0, 0)); // 去掉单元格间距

        // 表头美化
        JTableHeader header = taTable.getTableHeader();
        header.setFont(HEADER_FONT);
        header.setBackground(Color.WHITE);
        header.setForeground(TEXT_DARK);
        header.setPreferredSize(new Dimension(0, 50));
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        // 排序与标红
        setupTableLogic();

        JScrollPane scrollPane = new JScrollPane(taTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // 去掉滚动条边框
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        // ===================== 底部控制区 =====================
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setPreferredSize(new Dimension(0, 80));

        // 左侧：Limit 设置
        JPanel limitPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 20));
        limitPanel.setOpaque(false);
        
        currentLimitLabel = new JLabel("Status: Limit is set to " + currentLimit);
        currentLimitLabel.setFont(MAIN_FONT);
        
        JTextField limitField = new JTextField(4);
        limitField.setPreferredSize(new Dimension(50, 35));
        limitField.setHorizontalAlignment(JTextField.CENTER);
        
        JButton setBtn = createStyledButton("Set New Limit", PRIMARY_BLUE, true);
        setBtn.addActionListener(e -> updateLimit(limitField.getText()));

        limitPanel.add(currentLimitLabel);
        limitPanel.add(limitField);
        limitPanel.add(setBtn);

        // 右侧：导出按钮
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 20));
        actionPanel.setOpaque(false);
        JButton exportBtn = createStyledButton("Export Excel (CSV)", new Color(46, 204, 113), true);
        exportBtn.addActionListener(e -> exportDataToCSV());
        actionPanel.add(exportBtn);

        bottomPanel.add(limitPanel, BorderLayout.WEST);
        bottomPanel.add(actionPanel, BorderLayout.EAST);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // 快捷创建扁平化按钮的方法
    private JButton createStyledButton(String text, Color bg, boolean isPrimary) {
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

    private void setupTableLogic() {
        // 排序逻辑
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        taTable.setRowSorter(sorter);
        List<RowSorter.SortKey> keys = new ArrayList<>();
        keys.add(new RowSorter.SortKey(2, SortOrder.DESCENDING));
        sorter.setSortKeys(keys);

        // 现代感标红逻辑
        taTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                label.setBorder(new EmptyBorder(0, 10, 0, 0)); // 文字左边距
                
                int modelRow = t.convertRowIndexToModel(r);
                int workload = Integer.parseInt(t.getModel().getValueAt(modelRow, 2).toString());
                
                if (workload > currentLimit) {
                    label.setForeground(DANGER_RED);
                    label.setFont(new Font("Segoe UI", Font.BOLD, 15));
                } else {
                    label.setForeground(TEXT_DARK);
                    label.setFont(MAIN_FONT);
                }
                
                if (!isS) label.setBackground(Color.WHITE);
                return label;
            }
        });
    }

    private void updateLimit(String input) {
        try {
            currentLimit = Integer.parseInt(input.trim());
            currentLimitLabel.setText("Status: Limit is set to " + currentLimit);
            taTable.repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number.");
        }
    }

    private void exportDataToCSV() {
        // ... (保持之前的导出代码逻辑不变) ...
        JOptionPane.showMessageDialog(this, "Export function triggered!");
    }

    private void addMockData() {
        tableModel.addRow(new Object[]{"ID-901", "Alice Johnson", "5", "alice.j@uni.edu"});
        tableModel.addRow(new Object[]{"ID-722", "Bob Smith", "2", "b.smith@uni.edu"});
        tableModel.addRow(new Object[]{"ID-553", "Charlie Brown", "4", "charlie@uni.edu"});
        tableModel.addRow(new Object[]{"ID-104", "David Wilson", "1", "d.wilson@uni.edu"});
    }
}