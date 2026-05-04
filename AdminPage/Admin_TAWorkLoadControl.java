package AdminPage;

import LoginPage.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class Admin_TAWorkLoadControl {
    private final User currentUser;
    private final Admin_TAWorkLoadControlUI boundary;

    public int currentLimit = 3;
    public int warningHourLimit = 5;

    private final File configFile = new File("data/limit_config.txt");
    private final File hourConfigFile = new File("data/hour_limit_config.txt");
    private final File taDataFile = new File("data/ta_workload_data.csv");

    public Admin_TAWorkLoadControl(User user) {
        this.currentUser = user;
        this.boundary = new Admin_TAWorkLoadControlUI(this);

        configFile.getParentFile().mkdirs();

        loadLimitFromFile();
        loadHourLimitFromFile();
        loadTADataFromFile();

        boundary.refreshLimitLabels();
    }

    public void loadLimitFromFile() {
        if (!configFile.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
            String line = br.readLine();
            if (line != null) currentLimit = Integer.parseInt(line.trim());
        } catch (Exception e) { currentLimit = 3; }
    }

    public void saveLimitToFile() {
        try {
            configFile.getParentFile().mkdirs();
            try (PrintWriter pw = new PrintWriter(new FileWriter(configFile))) {
                pw.println(currentLimit);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(boundary, "Save failed: " + e.getMessage());
        }
    }

    public void loadHourLimitFromFile() {
        if (!hourConfigFile.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(hourConfigFile))) {
            String line = br.readLine();
            if (line != null) warningHourLimit = Integer.parseInt(line.trim());
        } catch (Exception e) { warningHourLimit = 5; }
    }

    public void saveHourLimitToFile() {
        try {
            hourConfigFile.getParentFile().mkdirs();
            try (PrintWriter pw = new PrintWriter(new FileWriter(hourConfigFile))) {
                pw.println(warningHourLimit);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(boundary, "Save failed: " + e.getMessage());
        }
    }

    public void loadTADataFromFile() {
        boundary.getTableModel().setRowCount(0);
        if (!taDataFile.exists()) {
            saveInitialTAData();
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(taDataFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 5) boundary.getTableModel().addRow(parts);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void saveInitialTAData() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(taDataFile))) {
            pw.println("ID-901,Alice Johnson,3,5,alice.j@uni.edu");
            pw.println("ID-722,Bob Smith,2,2,bob@uni.edu");
            pw.println("ID-553,Charlie Brown,1,4,charlie@uni.edu");
            pw.println("ID-104,David Wilson,2,1,d.wilson@uni.edu");
            loadTADataFromFile();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void updateLimit(String input) {
        try {
            currentLimit = Integer.parseInt(input.trim());
            saveLimitToFile();
            boundary.refreshLimitLabels();
            boundary.getTaTable().repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(boundary, "Please enter a valid number.");
        }
    }

    public void updateHourLimit(String input) {
        try {
            warningHourLimit = Integer.parseInt(input.trim());
            saveHourLimitToFile();
            boundary.refreshLimitLabels();
            boundary.getTaTable().repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(boundary, "Please enter a valid number.");
        }
    }

    public void exportDataToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("TA_Workload.csv"));
        int result = fileChooser.showSaveDialog(boundary);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File file = fileChooser.getSelectedFile();
        if (!file.getName().endsWith(".csv"))
            file = new File(file.getParent(), file.getName() + ".csv");

        try (BufferedWriter w = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            DefaultTableModel m = boundary.getTableModel();
            for (int i = 0; i < m.getColumnCount(); i++) {
                w.write(escape(m.getColumnName(i)));
                if (i < m.getColumnCount()-1) w.write(",");
            }
            w.newLine();

            for (int i = 0; i < m.getRowCount(); i++) {
                for (int j = 0; j < m.getColumnCount(); j++) {
                    Object v = m.getValueAt(i, j);
                    w.write(escape(v == null ? "" : v.toString()));
                    if (j < m.getColumnCount()-1) w.write(",");
                }
                w.newLine();
            }
            JOptionPane.showMessageDialog(boundary, "Export success!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(boundary, "Export failed: "+e.getMessage());
        }
    }

    private String escape(String s) {
        if (s.startsWith("=")||s.startsWith("+")||s.startsWith("-")||s.startsWith("@")) s = "'"+s;
        if (s.contains(",")||s.contains("\"")||s.contains("\n"))
            s = "\"" + s.replace("\"","\"\"") + "\"";
        return s;
    }

    public Admin_TAWorkLoadControlUI getUi() {
        return boundary;
    }
}