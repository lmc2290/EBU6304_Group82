package AdminPage;

import LoginPage.User;
import LoginPage.UnifiedDataStore;
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

    public void loadData() {
        if (tableModel == null) return;
        tableModel.setRowCount(0);

        List<String[]> modules = UnifiedDataStore.getAllModules();

        for (String[] module : modules) {
            if (module.length >= 5) {
                String moduleCode = module[0];
                String moduleName = module[1];
                String moId = module[2];
                String status = module[4];

                tableModel.addRow(new Object[]{
                        moduleCode,
                        moduleName,
                        moId,
                        "VIEW",
                        status,
                        status.equals("Pending") ? "Approve" : "Reject"
                });
            }
        }
    }

    public void approveModule(String moduleCode) {
        UnifiedDataStore.updateModuleStatus(moduleCode, "Approved", currentUser.getId(), null);
        loadData();
    }

    public void rejectModule(String moduleCode, String reason) {
        UnifiedDataStore.updateModuleStatus(moduleCode, "Rejected", currentUser.getId(), reason);
        loadData();
    }

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
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                writer.write(tableModel.getColumnName(i));
                if (i < tableModel.getColumnCount() - 1) {
                    writer.write(",");
                }
            }
            writer.newLine();

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