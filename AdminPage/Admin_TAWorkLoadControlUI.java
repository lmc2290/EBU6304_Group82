package AdminPage;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
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
    private final Color WARNING_ORANGE = new Color(230, 126, 34); 
    private final Color PURPLE_COLOR = new Color(142, 68, 173); 
    
    private final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);

    public Admin_TAWorkLoadControlUI(Admin_TAWorkLoadControl controller) {
        this.controller = controller;
        setLayout(new BorderLayout(0, 20));
        setBackground(BG_LIGHT);
        setBorder(new EmptyBorder(25, 35, 25, 35)); // 增加四周呼吸空间
        initializeUI();
        refreshTableData();
    }

    private void initializeUI() {
        // ==========================================
        // 1. 顶部标题区
        // ==========================================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("📊 TA Workload Dashboard"); 
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_DARK);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JLabel subTitle = new JLabel("Single-click an email to copy. Red indicates hour/course overload.");
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subTitle.setForeground(Color.GRAY);
        headerPanel.add(subTitle, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // ==========================================
        // 2. 中间数据表格区
        // ==========================================
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

        // ==========================================
        // 3. 底部控制台 (UI 布局大幅优化，告别拥挤)
        // ==========================================
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        // [上层]：阈值设置区域 (带标题边框，左对齐)
        JPanel settingsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        settingsPanel.setOpaque(false);
        TitledBorder tb = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200,200,200)), "⚙️ Threshold Settings");
        tb.setTitleFont(new Font("Segoe UI", Font.BOLD, 13));
        tb.setTitleColor(Color.GRAY);
        settingsPanel.setBorder(BorderFactory.createCompoundBorder(tb, new EmptyBorder(5, 5, 5, 5)));

        currentLimitLabel = new JLabel();
        currentLimitLabel.setFont(MAIN_FONT);
        JTextField limitField = new JTextField(4);
        limitField.setPreferredSize(new Dimension(50, 35));
        limitField.setHorizontalAlignment(JTextField.CENTER);
        JButton setBtn = createStyledButton("Set Course Limit", PRIMARY_BLUE);
        setBtn.addActionListener(e -> {
            controller.updateLimit(limitField.getText());
            limitField.setText("");
            refreshLimitLabels();
            taTable.repaint();
        });

        hourLimitLabel = new JLabel();
        hourLimitLabel.setFont(MAIN_FONT);
        JTextField hourField = new JTextField(4);
        hourField.setPreferredSize(new Dimension(50, 35));
        hourField.setHorizontalAlignment(JTextField.CENTER);
        JButton setHourBtn = createStyledButton("Set Hour Warning", PRIMARY_BLUE);
        setHourBtn.addActionListener(e -> {
            controller.updateHourLimit(hourField.getText());
            hourField.setText("");
            refreshLimitLabels();
            taTable.repaint();
        });

        // 组装上层设置区
        settingsPanel.add(currentLimitLabel);
        settingsPanel.add(limitField);
        settingsPanel.add(setBtn);
        settingsPanel.add(Box.createHorizontalStrut(30)); 
        settingsPanel.add(hourLimitLabel);
        settingsPanel.add(hourField);
        settingsPanel.add(setHourBtn);
        refreshLimitLabels();

        // [下层]：系统操作按钮区域 (右对齐)
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        actionPanel.setOpaque(false);

        JButton purgeBtn = createStyledButton("🎓 Remove TA", PURPLE_COLOR);
        purgeBtn.setToolTipText("Remove graduated or resigned TAs by their ID.");
        purgeBtn.addActionListener(e -> {
            String taId = JOptionPane.showInputDialog(this, "Enter the Student ID of the TA to remove:", "Purge TA", JOptionPane.WARNING_MESSAGE);
            if (taId != null && !taId.trim().isEmpty()) {
                int removed = controller.purgeTAById(taId.trim());
                if (removed > 0) {
                    JOptionPane.showMessageDialog(this, "Success! Purged " + removed + " records belonging to TA: " + taId);
                    refreshTableData();
                } else {
                    JOptionPane.showMessageDialog(this, "No records found for TA ID: " + taId, "Not Found", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        JButton cleanBtn = createStyledButton("🧹 Clean Junk", WARNING_ORANGE);
        cleanBtn.addActionListener(e -> {
            int removed = controller.cleanUpInvalidData();
            if (removed > 0) {
                JOptionPane.showMessageDialog(this, "Cleaned up " + removed + " rejected/withdrawn junk records.");
                refreshTableData();
            } else {
                JOptionPane.showMessageDialog(this, "Data is already clean.");
            }
        });

        JButton refreshBtn = createStyledButton("🔄 Refresh", PRIMARY_BLUE);
        refreshBtn.addActionListener(e -> {
            refreshTableData();
            JOptionPane.showMessageDialog(this, "TA workload synced with real database!");
        });

        JButton exportBtn = createStyledButton("📥 Export CSV", SUCCESS_GREEN);
        exportBtn.addActionListener(e -> exportDataToCSV());

        // 组装下层操作区
        actionPanel.add(purgeBtn);
        actionPanel.add(cleanBtn);
        actionPanel.add(refreshBtn);
        actionPanel.add(exportBtn);

        // 将两层加入底部面板
        bottomPanel.add(settingsPanel);
        bottomPanel.add(Box.createVerticalStrut(5)); 
        bottomPanel.add(actionPanel);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void refreshTableData() {
        tableModel.setRowCount(0);
        List<Object[]> data = controller.getRealTAWorkloadData();
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
        taTable.repaint();
    }

    public void refreshLimitLabels() {
        currentLimitLabel.setText("Status: Max Courses per TA: " + controller.currentLimit);
        hourLimitLabel.setText("Warning Hours: " + controller.warningHourLimit);
    }

    private void setupTableLogic() {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        taTable.setRowSorter(sorter);

        // 数字比较器（修复排序Bug）
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

        // 渲染邮箱列（增加 Tooltip 提示用户可点击）
        taTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setForeground(PRIMARY_BLUE);
                label.setText("<html><u>" + value.toString() + "</u></html>"); // 加上下划线更像超链接
                label.setToolTipText("👆 Click to copy email address"); // 鼠标悬停提示
                label.setCursor(new Cursor(Cursor.HAND_CURSOR));
                return label;
            }
        });

        taTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                label.setBorder(new EmptyBorder(0,10,0,0));
                
                if (c == 4) return label; 

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

        // ==========================================
        // 【核心优化】一键（单击）复制邮箱，零学习成本
        // ==========================================
        taTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 监听鼠标左键单击
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 1) { 
                    int row = taTable.rowAtPoint(e.getPoint());
                    int col = taTable.columnAtPoint(e.getPoint());
                    
                    if (col == 4 && row >= 0) { 
                        int modelRow = taTable.convertRowIndexToModel(row);
                        String email = tableModel.getValueAt(modelRow, 4).toString();
                        
                        // 写入系统剪贴板
                        StringSelection selection = new StringSelection(email);
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(selection, null);
                        
                        // 快速提示
                        JOptionPane.showMessageDialog(Admin_TAWorkLoadControlUI.this, 
                            "📋 Email copied to clipboard:\n" + email, 
                            "Copied!", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });
    }

    private void exportDataToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("TA_Workload.csv"));
        int result = fileChooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File file = fileChooser.getSelectedFile();
        if (!file.getName().endsWith(".csv"))
            file = new File(file.getParent(), file.getName() + ".csv");

        try (BufferedWriter w = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                w.write(escape(tableModel.getColumnName(i)));
                if (i < tableModel.getColumnCount()-1) w.write(",");
            }
            w.newLine();

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    Object v = tableModel.getValueAt(i, j);
                    w.write(escape(v == null ? "" : v.toString()));
                    if (j < tableModel.getColumnCount()-1) w.write(",");
                }
                w.newLine();
            }
            JOptionPane.showMessageDialog(this, "Export success!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Export failed: "+e.getMessage());
        }
    }

    private String escape(String s) {
        if (s.startsWith("=")||s.startsWith("+")||s.startsWith("-")||s.startsWith("@")) s = "'"+s;
        if (s.contains(",")||s.contains("\"")||s.contains("\n"))
            s = "\"" + s.replace("\"","\"\"") + "\"";
        return s;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setPreferredSize(new Dimension(160, 42)); // 稍微增高一点按钮，显得更大气
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
}