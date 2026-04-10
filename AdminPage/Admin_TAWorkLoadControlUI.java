package AdminPage;

import LoginPage.User;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

// TA Workload Management Panel for Admin
public class Admin_TAWorkLoadControlUI extends JPanel {
    private final User currentUser;
    private JLabel currentLimitLabel;
    private JLabel hourLimitLabel;
    private JTable taTable;
    private DefaultTableModel tableModel;

    private int currentLimit = 3;
    private int warningHourLimit = 5;

    private final File configFile = new File("limit_config.txt");
    private final File hourConfigFile = new File("hour_limit_config.txt");

    // UI Style Constants
    private final Color PRIMARY_BLUE = new Color(41, 128, 185);
    private final Color DANGER_RED = new Color(231, 76, 60);
    private final Color BG_LIGHT = new Color(245, 247, 250);
    private final Color TEXT_DARK = new Color(44, 62, 80);
    private final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);

    public Admin_TAWorkLoadControlUI(User user) {
        this.currentUser = user;
        this.setLayout(new BorderLayout(0, 20));
        this.setBackground(BG_LIGHT);
        this.setBorder(new EmptyBorder(30, 40, 30, 40));

        loadLimitFromFile();
        loadHourLimitFromFile();
        initializeUI();
        addMockData();
    }

    private void loadLimitFromFile() {
        if (!configFile.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
            String line = br.readLine();
            if (line != null) currentLimit = Integer.parseInt(line.trim());
        } catch (Exception e) { currentLimit = 3; }
    }

    private void saveLimitToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(configFile))) {
            pw.println(currentLimit);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Save failed: " + e.getMessage());
        }
    }

    private void loadHourLimitFromFile() {
        if (!hourConfigFile.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(hourConfigFile))) {
            String line = br.readLine();
            if (line != null) warningHourLimit = Integer.parseInt(line.trim());
        } catch (Exception e) { warningHourLimit = 5; }
    }

    private void saveHourLimitToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(hourConfigFile))) {
            pw.println(warningHourLimit);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Save failed: " + e.getMessage());
        }
    }

    // Initialize all UI components
    private void initializeUI() {
        // Header panel with title
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

        // Table setup
        String[] columnNames = {"ID", "Name", "Workload (Hrs)", "Contact Email"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        taTable = new JTable(tableModel);

        // 1. Enable Sorting: Workload (Index 2) High to Low
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        taTable.setRowSorter(sorter);

        // Set default sort to column 2 (Workload), Descending
        java.util.List<RowSorter.SortKey> sortKeys = new java.util.ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(2, SortOrder.DESCENDING));
        sorter.setSortKeys(sortKeys);

        // 2. Highlighting: Red color for entries over limit
        taTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Convert view row index to model index due to sorting
                int modelRow = table.convertRowIndexToModel(row);
                try {
                    int workload = Integer.parseInt(table.getModel().getValueAt(modelRow, 2).toString());
                    // Highlight red if over limit
                    if (workload > warningHourLimit) {
                        label.setForeground(Color.RED);
                    } else {
                        label.setForeground(Color.BLACK);
                    }
                } catch (Exception e) {
                    label.setForeground(Color.BLACK);
                }
                return label;
            }
        });

        // Table styling
        taTable.setRowHeight(45);
        taTable.setFont(MAIN_FONT);
        taTable.setShowVerticalLines(false);
        taTable.setGridColor(new Color(230, 230, 230));
        taTable.setSelectionBackground(new Color(235, 245, 251));
        taTable.setSelectionForeground(TEXT_DARK);
        taTable.setIntercellSpacing(new Dimension(0, 0));

        // Table header styling
        JTableHeader header = taTable.getTableHeader();
        header.setFont(HEADER_FONT);
        header.setBackground(Color.WHITE);
        header.setForeground(TEXT_DARK);
        header.setPreferredSize(new Dimension(0, 50));
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        setupTableLogic();

        // Scroll pane for table
        JScrollPane scrollPane = new JScrollPane(taTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom control panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setPreferredSize(new Dimension(0, 80));

        // Limit control section
        JPanel limitPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 20));
        limitPanel.setOpaque(false);

        currentLimitLabel = new JLabel("Max Courses per TA: " + currentLimit);
        currentLimitLabel.setFont(MAIN_FONT);
        JTextField limitField = new JTextField(4);
        limitField.setPreferredSize(new Dimension(50, 35));
        limitField.setHorizontalAlignment(JTextField.CENTER);
        JButton setBtn = createStyledButton("Set Course Limit", PRIMARY_BLUE, true);
        setBtn.addActionListener(e -> {
            updateLimit(limitField.getText());
            limitField.setText("");
        });

        hourLimitLabel = new JLabel("Warning Hours: " + warningHourLimit);
        hourLimitLabel.setFont(MAIN_FONT);
        JTextField hourField = new JTextField(4);
        hourField.setPreferredSize(new Dimension(50, 35));
        hourField.setHorizontalAlignment(JTextField.CENTER);
        JButton setHourBtn = createStyledButton("Set Hour Warning", PRIMARY_BLUE, true);
        setHourBtn.addActionListener(e -> {
            updateHourLimit(hourField.getText());
            hourField.setText("");
        });

        limitPanel.add(currentLimitLabel);
        limitPanel.add(limitField);
        limitPanel.add(setBtn);
        limitPanel.add(hourLimitLabel);
        limitPanel.add(hourField);
        limitPanel.add(setHourBtn);

        // Export button section
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 20));
        actionPanel.setOpaque(false);
        JButton exportBtn = createStyledButton("Export Excel (CSV)", new Color(46, 204, 113), true);
        exportBtn.addActionListener(e -> exportDataToCSV());
        actionPanel.add(exportBtn);

        bottomPanel.add(limitPanel, BorderLayout.WEST);
        bottomPanel.add(actionPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Create consistent styled buttons
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

    // Configure table sorting and highlight logic
    private void setupTableLogic() {
        // Sort by workload hours descending
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        taTable.setRowSorter(sorter);
        List<RowSorter.SortKey> keys = new ArrayList<>();
        keys.add(new RowSorter.SortKey(2, SortOrder.DESCENDING));
        sorter.setSortKeys(keys);

        // Highlight overload entries in red
        taTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                label.setBorder(new EmptyBorder(0, 10, 0, 0));

                int modelRow = t.convertRowIndexToModel(r);
                int workload = Integer.parseInt(t.getModel().getValueAt(modelRow, 2).toString());

                if (workload > warningHourLimit) {
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
            currentLimitLabel.setText("Max Courses per TA: " + currentLimit);
            saveLimitToFile();
            taTable.repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number.");
        }
    }

    private void updateHourLimit(String input) {
        try {
            warningHourLimit = Integer.parseInt(input.trim());
            hourLimitLabel.setText("Warning Hours: " + warningHourLimit);
            saveHourLimitToFile();
            taTable.repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number.");
        }
    }

    // Placeholder for CSV export function
    private void exportDataToCSV() {
        JOptionPane.showMessageDialog(this, "Export function triggered!");
    }

    // Add test data to table
    private void addMockData() {
        tableModel.addRow(new Object[]{"ID-901", "Alice Johnson", "5", "alice.j@uni.edu"});
        tableModel.addRow(new Object[]{"ID-722", "Bob Smith", "2", "b.smith@uni.edu"});
        tableModel.addRow(new Object[]{"ID-553", "Charlie Brown", "4", "charlie@uni.edu"});
        tableModel.addRow(new Object[]{"ID-104", "David Wilson", "1", "d.wilson@uni.edu"});
    }
}