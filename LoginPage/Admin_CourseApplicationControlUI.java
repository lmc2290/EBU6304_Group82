package LoginPage;

import java.awt.*;
import java.io.*;
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

    private final Color SUCCESS_GREEN = new Color(46, 204, 113);
    private final Color DANGER_RED = new Color(231, 76, 60);
    private final Color BG_LIGHT = new Color(245, 247, 250);
    private final Color TEXT_DARK = new Color(44, 62, 80);
    private final Color PRIMARY_BLUE = new Color(41, 128, 185);
    private final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 15);

    public Admin_CourseApplicationControlUI(User user) {
        this.setLayout(new BorderLayout(0, 20));
        this.setBackground(BG_LIGHT);
        this.setBorder(new EmptyBorder(30, 40, 30, 40));

        initializeUI();
        addMockData();
    }

    private void initializeUI() {
        JLabel title = new JLabel("Module Posting Approval");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Module Name", "Organiser", "Content", "Status & Actions"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int r, int c) {
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

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setPreferredSize(new Dimension(0, 80));

        JPanel emptyPanel = new JPanel();
        emptyPanel.setOpaque(false);
        bottomPanel.add(emptyPanel, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 20));
        actionPanel.setOpaque(false);
        JButton exportBtn = createStyledButton("Export Excel (CSV)", new Color(46, 204, 113), true);
        exportBtn.addActionListener(e -> exportDataToCSV());
        actionPanel.add(exportBtn);

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
        requestTable.setRowHeight(60);
        requestTable.setShowVerticalLines(false);
        requestTable.setIntercellSpacing(new Dimension(0, 0));

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

        requestTable.getColumnModel().getColumn(4).setCellRenderer(new StatusActionRenderer());
        requestTable.getColumnModel().getColumn(4).setCellEditor(new StatusActionEditor());
    }

    private void addMockData() {
        List<Module> modules = MockDataManager.getModules();
        for (Module module : modules) {
            tableModel.addRow(new Object[]{
                    module.getModuleName(),
                    module.getResponsibilities(),
                    module.getRequirements(),
                    "VIEW",
                    module.getStatus()
            });
        }
        tableModel.addRow(new Object[]{"CS101", "Java Basics", "Prof. Lee", "VIEW", "Pending"});
        tableModel.addRow(new Object[]{"CS202", "Databases", "Dr. Wong", "VIEW", "Pending"});
        tableModel.addRow(new Object[]{"CS303", "AI Intro", "Dr. Chen", "VIEW", "Approved"});
    }

    private void exportDataToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export to CSV");
        fileChooser.setSelectedFile(new File("Course_Applications_" + System.currentTimeMillis() + ".csv"));
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
            DefaultTableModel model = (DefaultTableModel) requestTable.getModel();
            int columnCount = model.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                writer.write(escapeCsvValue(model.getColumnName(i)));
                if (i < columnCount - 1) writer.write(",");
            }
            writer.newLine();

            int rowCount = model.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                int modelRow = requestTable.convertRowIndexToModel(i);
                for (int j = 0; j < columnCount; j++) {
                    Object value = model.getValueAt(modelRow, j);
                    String cellValue = value != null ? value.toString() : "";
                    writer.write(escapeCsvValue(cellValue));
                    if (j < columnCount - 1) writer.write(",");
                }
                writer.newLine();
            }

            JOptionPane.showMessageDialog(this, "Export successful!\nFile saved to: " + fileToSave.getAbsolutePath());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Export failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private String escapeCsvValue(String value) {
        if (value.startsWith("=") || value.startsWith("+") || value.startsWith("-") || value.startsWith("@")) {
            value = "'" + value;
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            value = "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

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
                fireEditingStopped(); 
                fireEditingStopped();
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

    private JButton createSmallButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBorderPainted(false);
        return b;
    }
}