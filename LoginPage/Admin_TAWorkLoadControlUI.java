package LoginPage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Admin_TAWorkLoadControlUI extends JPanel {
    private final User currentUser;

    // 用于实时显示当前限制值
    private JLabel currentLimitLabel;

    public Admin_TAWorkLoadControlUI(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(new Color(247, 247, 247));
        initializeUI();
    }

    private void initializeUI() {
        // ===================== 标题 =====================
        JLabel titleLabel = new JLabel("TA Workload Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // ===================== 表格 =====================
        String[] columnNames = {"TA Name", "Workload", "Emails"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        JTable taTable = new JTable(tableModel);
        taTable.setFont(new Font("Arial", Font.PLAIN, 14));
        taTable.setRowHeight(30);
        taTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));

        JScrollPane scrollPane = new JScrollPane(taTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        add(scrollPane, BorderLayout.CENTER);

        // ===================== 底部面板 =====================
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        bottomPanel.setBackground(new Color(247, 247, 247));

        // 导出部分
        JLabel exportLabel = new JLabel("Export as");
        exportLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton exportPdfBtn = new JButton("PDF");
        JButton exportXlsxBtn = new JButton("Excel");
        exportPdfBtn.setPreferredSize(new Dimension(80, 35));
        exportXlsxBtn.setPreferredSize(new Dimension(80, 35));

        // ===================== Application Limit 区域（你要的样式） =====================
        currentLimitLabel = new JLabel("Current Limit: 3");
        currentLimitLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JTextField limitField = new JTextField(5);
        limitField.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton editBtn = new JButton("Edit");
        editBtn.setPreferredSize(new Dimension(60, 35));
        editBtn.setFont(new Font("Arial", Font.PLAIN, 14));

        // Edit 按钮功能：修改并显示新的限制值
        editBtn.addActionListener(e -> {
            String input = limitField.getText().trim();
            if (!input.isEmpty()) {
                try {
                    int newLimit = Integer.parseInt(input);
                    currentLimitLabel.setText("Current Limit: " + newLimit);
                    limitField.setText(""); // 清空输入框
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number!", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // ===================== 组装底部 =====================
        bottomPanel.add(exportLabel);
        bottomPanel.add(exportPdfBtn);
        bottomPanel.add(new JLabel("/"));
        bottomPanel.add(exportXlsxBtn);
        bottomPanel.add(Box.createHorizontalStrut(30));

        bottomPanel.add(currentLimitLabel);
        bottomPanel.add(limitField);
        bottomPanel.add(editBtn);

        add(bottomPanel, BorderLayout.SOUTH);
    }
}