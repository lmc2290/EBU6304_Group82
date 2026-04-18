package AdminPage;

import LoginPage.MockDataManager;
import LoginPage.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Admin_CourseApplicationControl {
    private final User currentUser;
    private final Admin_CourseApplicationControlUI boundary;
    private DefaultTableModel tableModel;

    private final String CSV_PATH = "data/modules.csv";

    public Admin_CourseApplicationControl(User user) {
        this.currentUser = user;
        this.boundary = new Admin_CourseApplicationControlUI(this);
    }

    public void setTableModel(DefaultTableModel model) {
        this.tableModel = model;
    }

    // Load table data from mock and CSV
    public void loadData() {
        if (tableModel == null) return;
        tableModel.setRowCount(0);
        addMockData();
        loadDataFromCSV();
    }

    // Add initial test data
    public void addMockData() {
        tableModel.addRow(new Object[]{"CS101", "Java Basics", "Prof. Lee", "VIEW", "Pending Review"});
        tableModel.addRow(new Object[]{"CS202", "Databases", "Dr. Wong", "VIEW", "Pending Review"});
        tableModel.addRow(new Object[]{"CS303", "AI Intro", "Dr. Chen", "VIEW", "Approved"});
    }

    // Load module records from CSV file
    public void loadDataFromCSV() {
        File file = new File(CSV_PATH);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length >= 6) {
                    tableModel.addRow(new Object[]{p[0], p[0], p[2], "VIEW", p[5]});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Approve module application
    public void approveModule(String moduleId) {
        MockDataManager.updateModuleStatus(moduleId, "Approved");
        updateCSVFile(moduleId, "Approved");
    }

    // Reject module application with reason
    public void rejectModule(String moduleId, String reason) {
        String status = "Rejected: " + reason.replace(",", ";");
        MockDataManager.updateModuleStatus(moduleId, "Rejected");
        updateCSVFile(moduleId, status);
    }

    // Update status in CSV file
    public void updateCSVFile(String moduleName, String newStatus) {
        List<String> lines = new ArrayList<>();
        File file = new File(CSV_PATH);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equalsIgnoreCase(moduleName)) {
                    parts[5] = newStatus;
                    line = String.join(",", parts);
                }
                lines.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (String l : lines) pw.println(l);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Export table data to CSV file
    public void exportData() {
        if (tableModel == null) return;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export to CSV");
        fileChooser.setSelectedFile(new File("Course_Applications_" + System.currentTimeMillis() + ".csv"));
        int userSelection = fileChooser.showSaveDialog(boundary);

        if (userSelection != JFileChooser.APPROVE_OPTION) return;

        File fileToSave = fileChooser.getSelectedFile();
        if (!fileToSave.getName().endsWith(".csv")) {
            fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".csv");
        }

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(fileToSave), StandardCharsets.UTF_8))) {

            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                writer.write(escapeCsvValue(tableModel.getColumnName(i)));
                if (i < tableModel.getColumnCount() - 1) writer.write(",");
            }
            writer.newLine();

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    Object val = tableModel.getValueAt(i, j);
                    String cell = val == null ? "" : val.toString();
                    writer.write(escapeCsvValue(cell));
                    if (j < tableModel.getColumnCount() - 1) writer.write(",");
                }
                writer.newLine();
            }

            JOptionPane.showMessageDialog(boundary, "Export successful!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Escape special characters for CSV format
    private String escapeCsvValue(String value) {
        if (value.contains(",") || value.contains("\"")) {
            value = "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    public Admin_CourseApplicationControlUI getUi() {
        return boundary;
    }
}