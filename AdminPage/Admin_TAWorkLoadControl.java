package AdminPage;

import LoginPage.User;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Admin_TAWorkLoadControl {
    private final User currentUser;
    private final Admin_TAWorkLoadControlUI boundary;

    public int currentLimit = 3;
    public int warningHourLimit = 5;

    // 保留原有的配置保存路径
    private final File configFile = new File("data/limit_config.txt");
    private final File hourConfigFile = new File("data/hour_limit_config.txt");
    
    // 【修改点】数据源改为跨角色通用的 applicants.csv，解决数据孤岛问题
    private final File applicantsFile = new File("data/applicants.csv");

    public Admin_TAWorkLoadControl(User user) {
        this.currentUser = user;
        // 初始化必须在注入 UI 之前，否则 UI 拿不到 config 数据
        configFile.getParentFile().mkdirs();
        loadLimitFromFile();
        loadHourLimitFromFile();
        
       this.boundary = new Admin_TAWorkLoadControlUI(this);

// ✅ 关键：丢到Swing事件线程执行
SwingUtilities.invokeLater(() -> loadTADataFromFile());
    }

    // ==========================================
    // 配置读写逻辑 (保留你的优秀原生设计)
    // ==========================================
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

    // ==========================================
    // 【核心新功能 1】数据一致性：从全局 CSV 统计真实工作量
    // ==========================================
    public void loadTADataFromFile() {
        DefaultTableModel model = boundary.getTableModel();
        model.setRowCount(0); // 清空表格

        if (!applicantsFile.exists()) return;

        // 使用 Map 分组聚合每个 TA 的数据
        Map<String, String> taNames = new HashMap<>();
        Map<String, Integer> taApprovedCount = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(applicantsFile))) {
            String line;
            br.readLine(); // 跳过 Header
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 8) {
                    String taId = parts[0];
                    String name = parts[1];
                    String status = parts[7]; // 状态列

                    taNames.put(taId, name);
                    taApprovedCount.putIfAbsent(taId, 0);

                    // 只有 Approved (已通过) 的才计算工作量
                    if (status.equalsIgnoreCase("Approved")) {
                        taApprovedCount.put(taId, taApprovedCount.get(taId) + 1);
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }

        // 将聚合好的真实数据推送到前端表格
        for (String taId : taNames.keySet()) {
            String name = taNames.get(taId);
            int enrolledCourses = taApprovedCount.get(taId);
            
            // 假设一门课占用 10 小时工作量 (根据实际需求调整)
            int estimatedHours = enrolledCourses * 10; 
            // 自动生成学校后缀的标准邮箱格式
            String email = name.toLowerCase().replace(" ", ".") + "_" + taId.toLowerCase() + "@qmul.ac.uk";

            model.addRow(new String[]{
                taId, name, String.valueOf(enrolledCourses), String.valueOf(estimatedHours), email
            });
        }
    }

    // ==========================================
    // 【核心新功能 2】数据清理：物理删除被拒绝和撤回的数据
    // ==========================================
    public int cleanUpInvalidData() {
        if (!applicantsFile.exists()) return 0;

        List<String> validLines = new ArrayList<>();
        int removedCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(applicantsFile))) {
            String header = br.readLine();
            if (header != null) validLines.add(header);

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 8) {
                    String status = parts[7];
                    if (status.equalsIgnoreCase("Rejected") || status.equalsIgnoreCase("Withdrawn")) {
                        removedCount++;
                        continue; // 抛弃这些行
                    }
                }
                validLines.add(line);
            }
        } catch (Exception e) { e.printStackTrace(); }

        if (removedCount > 0) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(applicantsFile))) {
                for (String validLine : validLines) {
                    pw.println(validLine);
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
        return removedCount;
    }

    // ==========================================
    // UI 交互方法
    // ==========================================
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

    // ==========================================
    // 导出逻辑 (保留原生的完美实现)
    // ==========================================
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