package LoginPage;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

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

    private void initializeUI() {
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

        String[] columnNames = {"ID", "Name", "Enrolled Courses", "Total Work Hours", "Contact Email"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        taTable = new JTable(tableModel);

        taTable.setRowHeight(45);
        taTable.setFont(MAIN_FONT);
        taTable.setShowVerticalLines(false);
        taTable.setGridColor(new Color(230, 230, 230));
        taTable.setSelectionBackground(new Color(235, 245, 251));
        taTable.setSelectionForeground(TEXT_DARK);
        taTable.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = taTable.getTableHeader();
        header.setFont(HEADER_FONT);
        header.setBackground(Color.WHITE);
        header.setForeground(TEXT_DARK);
        header.setPreferredSize(new Dimension(0, 50));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        setupTableLogic();

        JScrollPane scrollPane = new JScrollPane(taTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setPreferredSize(new Dimension(0, 80));

        JPanel limitPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 20));
        limitPanel.setOpaque(false);

        currentLimitLabel = new JLabel("Status: Max Courses per TA: " + currentLimit);
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

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 20));
        actionPanel.setOpaque(false);
        JButton exportBtn = createStyledButton("Export Excel (CSV)", new Color(46, 204, 113), true);
        exportBtn.addActionListener(e -> exportDataToCSV());
        actionPanel.add(exportBtn);

        bottomPanel.add(limitPanel, BorderLayout.WEST);
        bottomPanel.add(actionPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

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
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        taTable.setRowSorter(sorter);
        List<RowSorter.SortKey> keys = new ArrayList<>();
        keys.add(new RowSorter.SortKey(3, SortOrder.DESCENDING));
        sorter.setSortKeys(keys);

        taTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                label.setBorder(new EmptyBorder(0, 10, 0, 0));

                int modelRow = t.convertRowIndexToModel(r);
                int hours = Integer.parseInt(t.getModel().getValueAt(modelRow, 3).toString());

                if (hours > warningHourLimit) {
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
            currentLimitLabel.setText("Status: Max Courses per TA: " + currentLimit);
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

    // ===================== 完美统一的导出逻辑（和Request界面一样） =====================
    private void exportDataToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export to CSV");
        fileChooser.setSelectedFile(new File("TA_Workload_" + System.currentTimeMillis() + ".csv"));
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection != JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(this, "Export cancelled.");
            return;
        }

        File fileToSave = fileChooser.getSelectedFile();
        if (!fileToSave.getName().endsWith(".csv")) {
            fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".csv");
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToSave), "UTF-8"))) {
            DefaultTableModel model = (DefaultTableModel) taTable.getModel();
            int columnCount = model.getColumnCount();

            for (int i = 0; i < columnCount; i++) {
                writer.write(escapeCsvValue(model.getColumnName(i)));
                if (i < columnCount - 1) writer.write(",");
            }
            writer.newLine();

            int rowCount = taTable.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                int modelRow = taTable.convertRowIndexToModel(i);
                for (int j = 0; j < columnCount; j++) {
                    Object val = model.getValueAt(modelRow, j);
                    String cell = val == null ? "" : val.toString();
                    writer.write(escapeCsvValue(cell));
                    if (j < columnCount - 1) writer.write(",");
                }
                writer.newLine();
            }

            JOptionPane.showMessageDialog(this, "Export successful!\nSaved to: " + fileToSave.getAbsolutePath());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Export failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String escapeCsvValue(String value) {
        if (value.startsWith("=") || value.startsWith("+") || value.startsWith("-") || value.startsWith("@")) {
            value = "'" + value;
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            value = "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private void addMockData() {
        tableModel.addRow(new Object[]{"ID-901", "Alice Johnson", "3", "5", "alice.j@uni.edu"});
        tableModel.addRow(new Object[]{"ID-722", "Bob Smith", "2", "2", "b.smith@uni.edu"});
        tableModel.addRow(new Object[]{"ID-553", "Charlie Brown", "1", "4", "charlie@uni.edu"});
        tableModel.addRow(new Object[]{"ID-104", "David Wilson", "2", "1", "d.wilson@uni.edu"});
    }
}