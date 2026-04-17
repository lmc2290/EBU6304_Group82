package AdminPage;

import LoginPage.MockDataManager;
import LoginPage.User;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

// Course Application Approval Panel for Admin
public class Admin_CourseApplicationControlUI extends JPanel {
    private JTable requestTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    // UI Style Constants
    private final Color SUCCESS_GREEN = new Color(46, 204, 113);
    private final Color DANGER_RED = new Color(231, 76, 60);
    private final Color BG_LIGHT = new Color(245, 247, 250);
    private final Color TEXT_DARK = new Color(44, 62, 80);
    private final Color PRIMARY_BLUE = new Color(41, 128, 185);
    private final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 15);

    // File path for data Interoperability
    private final String CSV_PATH = "data/modules.csv";

    public Admin_CourseApplicationControlUI(User user) {
        this.setLayout(new BorderLayout(0, 20));
        this.setBackground(BG_LIGHT);
        this.setBorder(new EmptyBorder(30, 40, 30, 40));

        initializeUI();
        
        // 1. Load mock/shared data from memory first
        addMockData();
        
        // 2. Load persistent data from CSV file (For Interoperability)
        loadDataFromCSV();
    }

    private void initializeUI() {
        JLabel title = new JLabel("Module Posting Approval");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Module Name", "Organiser", "Content", "Status & Actions"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int r, int c) {
                String status = getValueAt(r, 4).toString();
                return c == 4 && status.contains("Pending");
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
                if (s1.contains("Pending")) return -1;
                if (s2.contains("Pending")) return 1;
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
        // Comment out this loop if you don't want duplicate entries from MockDataManager
        /*
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
        */
        tableModel.addRow(new Object[]{"CS101", "Java Basics", "Prof. Lee", "VIEW", "Pending Review"});
        tableModel.addRow(new Object[]{"CS202", "Databases", "Dr. Wong", "VIEW", "Pending Review"});
        tableModel.addRow(new Object[]{"CS303", "AI Intro", "Dr. Chen", "VIEW", "Approved"});
    }

    // ===================== INTEROPERABILITY METHODS =====================

    private void loadDataFromCSV() {
        File file = new File(CSV_PATH);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length >= 6) {
                    // ID(0), Name(0), Organiser(2), View(VIEW), Status(5)
                    tableModel.addRow(new Object[]{p[0], p[0], p[2], "VIEW", p[5]});
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void updateCSVFile(String moduleName, String newStatus) {
        List<String> lines = new ArrayList<>();
        File file = new File(CSV_PATH);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                // If it's the target module, update the status column (Index 5)
                if (parts[0].equalsIgnoreCase(moduleName)) {
                    parts[5] = newStatus;
                    line = String.join(",", parts);
                }
                lines.add(line);
            }
        } catch (Exception e) { e.printStackTrace(); }

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (String l : lines) pw.println(l);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ===================== EXPORT & UI RENDERING =====================

    private void exportDataToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export to CSV");
        fileChooser.setSelectedFile(new File("Course_Applications_" + System.currentTimeMillis() + ".csv"));
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) return;

        File fileToSave = fileChooser.getSelectedFile();
        if (!fileToSave.getName().endsWith(".csv")) fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".csv");

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToSave), "UTF-8"))) {
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                writer.write(escapeCsvValue(tableModel.getColumnName(i)) + (i < tableModel.getColumnCount() - 1 ? "," : ""));
            }
            writer.newLine();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                int modelRow = requestTable.convertRowIndexToModel(i);
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    Object val = tableModel.getValueAt(modelRow, j);
                    writer.write(escapeCsvValue(val != null ? val.toString() : "") + (j < tableModel.getColumnCount() - 1 ? "," : ""));
                }
                writer.newLine();
            }
            JOptionPane.showMessageDialog(this, "Export successful!");
        } catch (Exception e) { e.printStackTrace(); }
    }

    private String escapeCsvValue(String value) {
        if (value.contains(",") || value.contains("\"")) value = "\"" + value.replace("\"", "\"\"") + "\"";
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
            if (status.contains("Pending")) {
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

            // Approve action
            appBtn.addActionListener(e -> {
                currentStatus = "Approved";
                int row = requestTable.getEditingRow();
                String moduleId = (String) requestTable.getValueAt(row, 0);
                
                // Keep existing Mock logic
                MockDataManager.updateModuleStatus(moduleId, "Approved");
                // Add persistent file logic
                updateCSVFile(moduleId, currentStatus);
                
                fireEditingStopped();
            });

            // Reject action with mandatory reason
            rejBtn.addActionListener(e -> {
                String reason = JOptionPane.showInputDialog(null, "Rejection Reason is REQUIRED:", "Mandatory Feedback", JOptionPane.WARNING_MESSAGE);
                
                // VALIDATION: Ensure user typed something and didn't click Cancel/Close
                if (reason != null && !reason.trim().isEmpty()) {
                    currentStatus = "Rejected: " + reason.replace(",", ";");
                    int row = requestTable.getEditingRow();
                    String moduleId = (String) requestTable.getValueAt(row, 0);
                    
                    MockDataManager.updateModuleStatus(moduleId, "Rejected");
                    updateCSVFile(moduleId, currentStatus);
                    
                    fireEditingStopped();
                } else {
                    // If Cancelled or Empty, don't change anything
                    cancelCellEditing();
                    if (reason != null) { // Clicked OK but empty
                        JOptionPane.showMessageDialog(null, "Reject action aborted: Reason cannot be empty.");
                    }
                }
            });

            panel.add(appBtn);
            panel.add(rejBtn);
        }

        @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean isS, int r, int c) {
            panel.setBackground(t.getSelectionBackground());
            currentStatus = v.toString();
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