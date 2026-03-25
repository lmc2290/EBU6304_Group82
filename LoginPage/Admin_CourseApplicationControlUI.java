package LoginPage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

// 👇 只改这里：从 JFrame → JPanel
public class Admin_CourseApplicationControlUI extends JPanel {
    private final User currentUser;

    public Admin_CourseApplicationControlUI(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(new Color(247, 247, 247));
        initializeUI();
    }

    private void initializeUI() {
        JLabel titleLabel = new JLabel("Course Application Control", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // 表格：Course | MO | Request
        String[] columnNames = {"Course", "MO Name", "Application"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable courseTable = new JTable(tableModel);
        courseTable.setFont(new Font("Arial", Font.PLAIN, 14));
        courseTable.setRowHeight(30);
        courseTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));

        JScrollPane scrollPane = new JScrollPane(courseTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        add(scrollPane, BorderLayout.CENTER);
    }
}