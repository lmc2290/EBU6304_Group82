package AdminPage;

import LoginPage.MockDataManager;
import LoginPage.User;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

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

    public Admin_CourseApplicationControlUI getUi() {
        return boundary;
    }

    // ==================================================
    // Load Data
    // ==================================================
    public void loadData() {

        if (tableModel == null) return;
        tableModel.setRowCount(0);
        addMockData();
        loadDataFromCSV();
    }

    // ==================================================
    // Default Demo Data
    // ==================================================
    public void addMockData() {
        tableModel.addRow(new Object[]{"CS101", "Java Basics", "Prof. Lee", "VIEW", "Approved", "Reject"});
        tableModel.addRow(new Object[]{"CS202", "Database Systems", "Dr. Wong", "VIEW", "Approved", "Reject"});
        tableModel.addRow(new Object[]{"CS303", "AI Intro", "Dr. Chen", "VIEW", "Approved", "Reject"});
    }

    // ==================================================
    // Read CSV File
    // ==================================================
    public void loadDataFromCSV() {

        File file = new File(CSV_PATH);
        if (!file.exists()) return;

        // 收集需要自动变更为 Approved 的数据ID
        List<String> modulesToAutoApprove = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); // skip header
            String line;

            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");

                if (p.length >= 3) {
                    String status = "Approved";
                    if (p.length >= 6 && !p[5].isBlank()) {
                        status = p[5];
                    }

                    // ==========================================
                    // 【核心修正区：自动审批逻辑】
                    // 落实老师反馈：“默认通过以减少工作量”。
                    // Admin 界面一打开，遇到未审批的请求，自动转为 Approved。
                    // ==========================================
                    if (status.equalsIgnoreCase("Pending Review") || status.equalsIgnoreCase("Pending")) {
                        status = "Approved";
                        modulesToAutoApprove.add(p[0]); // 标记需要自动回写数据库的行
                    }

                    tableModel.addRow(new Object[]{
                            p[0],
                            p[0],
                            p[2],
                            "VIEW",
                            status,
                            "Reject"
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 文件读完后，把自动通过的数据状态写回 CSV 文件，确保全局状态同步
        for (String moduleId : modulesToAutoApprove) {
            updateCSVFile(moduleId, "Approved");
            // 同步修改内存中的静态数据
            MockDataManager.updateModuleStatus(moduleId, "Approved");
        }
    }

    // ==================================================
    // Reject Action
    // ==================================================
    public void rejectModule(String moduleId, String reason) {
        String status = "Rejected: " + reason.replace(",", ";");
        MockDataManager.updateModuleStatus(moduleId, status);
        updateCSVFile(moduleId, status);
    }

    // ==================================================
    // Update CSV
    // ==================================================
    public void updateCSVFile(String moduleId, String newStatus) {

        File file = new File(CSV_PATH);
        if (!file.exists()) return;

        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6 && parts[0].equalsIgnoreCase(moduleId)) {
                    parts[5] = newStatus;
                    line = String.join(",", parts);
                }
                lines.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (String s : lines) {
                pw.println(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==================================================
    // Export CSV
    // ==================================================
    public void exportData() {

        if (tableModel == null) return;
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("Course_Applications.csv"));
        int result = chooser.showSaveDialog(boundary);

        if (result != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        if (!file.getName().endsWith(".csv")) {
            file = new File(file.getParent(), file.getName() + ".csv");
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            // Header
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                writer.write(tableModel.getColumnName(i));
                if (i < tableModel.getColumnCount() - 1) {
                    writer.write(",");
                }
            }
            writer.newLine();

            // Rows
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    Object val = tableModel.getValueAt(i, j);
                    writer.write(val == null ? "" : val.toString());
                    if (j < tableModel.getColumnCount() - 1) {
                        writer.write(",");
                    }
                }
                writer.newLine();
            }

            JOptionPane.showMessageDialog(boundary, "Export successful!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
