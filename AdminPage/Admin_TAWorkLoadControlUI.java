package AdminPage;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

public class Admin_TAWorkLoadControlUI extends JPanel {
    private final Admin_TAWorkLoadControl controller;

    private JLabel currentLimitLabel;
    private JLabel hourLimitLabel;
    private JTable taTable;
    private DefaultTableModel tableModel;

    // 现代化配色方案
    private final Color PRIMARY_BLUE = new Color(41, 128, 185);
    private final Color DANGER_RED = new Color(231, 76, 60);
    private final Color BG_LIGHT = new Color(245, 247, 250);
    private final Color TEXT_DARK = new Color(44, 62, 80);
    private final Color SUCCESS_GREEN = new Color(46, 204, 113);
    private final Color WARNING_ORANGE = new Color(230, 126, 34); // 用于清理按钮
    
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
        // --- 头部区域 ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("📊 TA Workload Dashboard"); // 增加 Unicode 图标
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_DARK);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JLabel subTitle = new JLabel("Double-click an email to contact TA. Red indicates hour/course overload.");
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subTitle.setForeground(Color.GRAY);
        headerPanel.add(subTitle, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // --- 表格区域 ---
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

        setupTableLogic(); // 初始化排序和渲染
        
        JScrollPane sp = new JScrollPane(taTable);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(Color.WHITE);
        add(sp, BorderLayout.CENTER);

       

        // --- 底部控制区域 ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setPreferredSize(new Dimension(0,120));

        // 1. 设置限制参数面板
        JPanel limitPanel = new JPanel();
        limitPanel.setLayout(new BoxLayout(limitPanel, BoxLayout.Y_AXIS));
        limitPanel.setOpaque(false);
        limitPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

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
        
        // 确保启动时 UI 上的标签数字正确
        refreshLimitLabels();

        // 2. 右下角操作面板
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0));
        actionPanel.setOpaque(false);

        JButton cleanBtn = createStyledButton("🧹 Clean Data", WARNING_ORANGE);
        cleanBtn.addActionListener(e -> {
            int removed = controller.cleanUpInvalidData();
            if (removed > 0) {
                JOptionPane.showMessageDialog(this, "System Cleaned! Removed " + removed + " rejected/withdrawn records.", "Purge Success", JOptionPane.INFORMATION_MESSAGE);
                
            } else {
                JOptionPane.showMessageDialog(this, "Data is already clean.", "Up to date", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton refreshBtn = createStyledButton("🔄 Refresh", PRIMARY_BLUE);
        refreshBtn.addActionListener(e -> {
            controller.loadTADataFromFile();
            taTable.repaint();
            JOptionPane.showMessageDialog(this, "TA workload synced with real database!");
        });

        JButton exportBtn = createStyledButton("📥 Export CSV", SUCCESS_GREEN);
        exportBtn.addActionListener(e -> controller.exportDataToCSV());

        actionPanel.add(cleanBtn);
        actionPanel.add(refreshBtn);
        actionPanel.add(exportBtn);

        bottomPanel.add(limitPanel, BorderLayout.WEST);
        bottomPanel.add(actionPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void refreshLimitLabels() {
        currentLimitLabel.setText("Status: Max Courses per TA: " + controller.currentLimit);
        hourLimitLabel.setText("Warning Hours: " + controller.warningHourLimit);
    }

    private void setupTableLogic() {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        taTable.setRowSorter(sorter);

        // 修复数字排序 Bug
        java.util.Comparator<String> numberComparator = new java.util.Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                try {
                    return Integer.compare(Integer.parseInt(s1.trim()), Integer.parseInt(s2.trim()));
                } catch (NumberFormatException e) {
                    return s1.compareTo(s2);
                }
            }
        };
        sorter.setComparator(2, numberComparator);
        sorter.setComparator(3, numberComparator);

        List<RowSorter.SortKey> keys = new ArrayList<>();
        keys.add(new RowSorter.SortKey(3, SortOrder.DESCENDING));
        sorter.setSortKeys(keys);

        // 渲染邮箱列（蓝色可点击风格）
        taTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setForeground(PRIMARY_BLUE);
                label.setText("✉️ " + value.toString());
                return label;
            }
        });

        // 渲染告警列 (课程超限 或 工时超限)
        taTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                label.setBorder(new EmptyBorder(0,10,0,0));
                
                if (c == 4) return label; // 跳过邮箱列处理

                int mr = t.convertRowIndexToModel(r);
                try {
                    int h = Integer.parseInt(t.getModel().getValueAt(mr,3).toString());
                    int courseCount = Integer.parseInt(t.getModel().getValueAt(mr,2).toString());
                    
                    if (h > controller.warningHourLimit || courseCount > controller.currentLimit) {
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

        // 双击发邮件/复制剪贴板逻辑
        taTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { 
                    int row = taTable.rowAtPoint(e.getPoint());
                    int col = taTable.columnAtPoint(e.getPoint());
                    
                    if (col == 4) { 
                        int modelRow = taTable.convertRowIndexToModel(row);
                        String email = tableModel.getValueAt(modelRow, 4).toString();
                        String name = tableModel.getValueAt(modelRow, 1).toString();
                        
                        try {
                            String subject = "Important Notice Regarding Your TA Workload";
                            String body = "Dear " + name + ",\n\nWe noticed your workload has exceeded the system limits...";
                            String uriStr = String.format("mailto:%s?subject=%s&body=%s", email, 
                                    subject.replace(" ", "%20"), body.replace(" ", "%20").replace("\n", "%0A"));
                            Desktop.getDesktop().mail(new URI(uriStr));
                        } catch (Exception ex) {
                            StringSelection stringSelection = new StringSelection(email);
                            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                            clipboard.setContents(stringSelection, null);
                            JOptionPane.showMessageDialog(null, 
                                "Email client could not be launched.\nEmail copied to clipboard: " + email, 
                                "Email Copied", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
            }
        });
    }

    // 带有悬停变色效果的现代化按钮
    private JButton createStyledButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setPreferredSize(new Dimension(160, 40));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { b.setBackground(bg.brighter()); }
            public void mouseExited(MouseEvent evt) { b.setBackground(bg); }
        });
        return b;
    }
    
    public JTable getTaTable() { return taTable; }
    public DefaultTableModel getTableModel() { return tableModel; }
}