package AdminPage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Admin_TAWorkLoadControlUI extends JPanel {
    private final Admin_TAWorkLoadControl controller;

    private JLabel currentLimitLabel;
    private JLabel hourLimitLabel;
    private JTable taTable;
    private DefaultTableModel tableModel;

    private final Color PRIMARY_BLUE = new Color(41, 128, 185);
    private final Color DANGER_RED = new Color(231, 76, 60);
    private final Color BG_LIGHT = new Color(245, 247, 250);
    private final Color TEXT_DARK = new Color(44, 62, 80);
    private final Color SUCCESS_GREEN = new Color(46, 204, 113);
    private final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);

    public Admin_TAWorkLoadControlUI(Admin_TAWorkLoadControl controller) {
        this.controller = controller;
        setLayout(new BorderLayout(0, 20));
        setBackground(BG_LIGHT);
        setBorder(new EmptyBorder(30, 40, 30, 40));
        initializeUI();
    }

    private void initializeUI() {
        // 标题
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("TA Workload Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_DARK);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        JLabel subTitle = new JLabel("Monitor and manage assistant application limits");
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subTitle.setForeground(Color.GRAY);
        headerPanel.add(subTitle, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // 表格
        String[] cols = {"ID", "Name", "Enrolled Courses", "Total Work Hours", "Contact Email"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        taTable = new JTable(tableModel);
        taTable.setRowHeight(45);
        taTable.setFont(MAIN_FONT);
        taTable.setShowVerticalLines(false);
        taTable.setGridColor(new Color(230,230,230));
        taTable.setSelectionBackground(new Color(235,245,251));
        taTable.setSelectionForeground(TEXT_DARK);
        taTable.setIntercellSpacing(new Dimension(0,0));

        JTableHeader header = taTable.getTableHeader();
        header.setFont(HEADER_FONT);
        header.setBackground(Color.WHITE);
        header.setForeground(TEXT_DARK);
        header.setPreferredSize(new Dimension(0,50));
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        setupTableLogic();
        JScrollPane sp = new JScrollPane(taTable);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(Color.WHITE);
        add(sp, BorderLayout.CENTER);

        // 底部面板
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setPreferredSize(new Dimension(0,120));

        // 1. 上下布局的限制设置面板
        JPanel limitPanel = new JPanel();
        limitPanel.setLayout(new BoxLayout(limitPanel, BoxLayout.Y_AXIS));
        limitPanel.setOpaque(false);
        limitPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        // 第一行：课程限制
        JPanel courseLimitRow = new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
        courseLimitRow.setOpaque(false);
        currentLimitLabel = new JLabel();
        currentLimitLabel.setFont(MAIN_FONT);
        JTextField limitField = new JTextField(4);
        limitField.setPreferredSize(new Dimension(50,35));
        limitField.setHorizontalAlignment(JTextField.CENTER);
        JButton setBtn = createStyledButton("Set Course Limit", PRIMARY_BLUE);
        setBtn.addActionListener(e -> {
            controller.updateLimit(limitField.getText());
            limitField.setText("");
        });
        courseLimitRow.add(currentLimitLabel);
        courseLimitRow.add(limitField);
        courseLimitRow.add(setBtn);

        // 第二行：小时警告
        JPanel hourLimitRow = new JPanel(new FlowLayout(FlowLayout.LEFT,10,10));
        hourLimitRow.setOpaque(false);
        hourLimitLabel = new JLabel();
        hourLimitLabel.setFont(MAIN_FONT);
        JTextField hourField = new JTextField(4);
        hourField.setPreferredSize(new Dimension(50,35));
        hourField.setHorizontalAlignment(JTextField.CENTER);
        JButton setHourBtn = createStyledButton("Set Hour Warning", PRIMARY_BLUE);
        setHourBtn.addActionListener(e -> {
            controller.updateHourLimit(hourField.getText());
            hourField.setText("");
        });
        hourLimitRow.add(hourLimitLabel);
        hourLimitRow.add(hourField);
        hourLimitRow.add(setHourBtn);

        limitPanel.add(courseLimitRow);
        limitPanel.add(hourLimitRow);

        // 2. 右侧按钮面板（刷新 + 导出）
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0));
        actionPanel.setOpaque(false);

        // 新增：刷新按钮
        JButton refreshBtn = createStyledButton("Refresh", PRIMARY_BLUE);
        refreshBtn.addActionListener(e -> {
            controller.loadTADataFromFile();
            controller.loadLimitFromFile();
            controller.loadHourLimitFromFile();
            refreshLimitLabels();
            taTable.repaint();
            JOptionPane.showMessageDialog(this, "TA data refreshed successfully!");
        });
        actionPanel.add(refreshBtn);

        JButton exportBtn = createStyledButton("Export CSV", SUCCESS_GREEN);
        exportBtn.addActionListener(e -> controller.exportDataToCSV());
        actionPanel.add(exportBtn);

        bottomPanel.add(limitPanel, BorderLayout.WEST);
        bottomPanel.add(actionPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // 统一刷新文字
    public void refreshLimitLabels() {
        currentLimitLabel.setText("Status: Max Courses per TA: " + controller.currentLimit);
        hourLimitLabel.setText("Warning Hours: " + controller.warningHourLimit);
    }

    private void setupTableLogic() {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        taTable.setRowSorter(sorter);
        List<RowSorter.SortKey> keys = new ArrayList<>();
        keys.add(new RowSorter.SortKey(3, SortOrder.DESCENDING));
        sorter.setSortKeys(keys);

        taTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                label.setBorder(new EmptyBorder(0,10,0,0));
                int mr = t.convertRowIndexToModel(r);
                try {
                    int h = Integer.parseInt(t.getModel().getValueAt(mr,3).toString());
                    if (h > controller.warningHourLimit) {
                        label.setForeground(DANGER_RED);
                        label.setFont(new Font("Segoe UI", Font.BOLD,15));
                    } else {
                        label.setForeground(TEXT_DARK);
                        label.setFont(MAIN_FONT);
                    }
                } catch (Exception ignored) {}
                if (!isS) setBackground(Color.WHITE);
                return label;
            }
        });
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD,14));
        b.setPreferredSize(new Dimension(160,40));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    // Getter
    public JTable getTaTable() { return taTable; }
    public DefaultTableModel getTableModel() { return tableModel; }
}