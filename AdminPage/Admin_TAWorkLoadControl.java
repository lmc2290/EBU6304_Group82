package AdminPage;

import LoginPage.User;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class Admin_TAWorkLoadControl {
    private final User currentUser;
    private final Admin_TAWorkLoadControlUI boundary;

    public int currentLimit = 3;
    public int warningHourLimit = 20; 

    // 配置保存路径
    private final File configFile = new File("data/limit_config.txt");
    private final File hourConfigFile = new File("data/hour_limit_config.txt");
    private final File applicantsFile = new File("data/applicants.csv"); 

    public Admin_TAWorkLoadControl(User user) {
        this.currentUser = user;
        
        configFile.getParentFile().mkdirs();
        loadLimitFromFile();
        loadHourLimitFromFile();
        
        this.boundary = new Admin_TAWorkLoadControlUI(this);
    }

    // ==========================================
    // 配置读写逻辑
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
        } catch (Exception e) { warningHourLimit = 20; }
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
    // 核心数据提供：完全解耦，只返回数据列表
    // ==========================================
    public List<Object[]> getRealTAWorkloadData() {
        List<Object[]> workloadData = new ArrayList<>();
        if (!applicantsFile.exists()) return workloadData;

        Map<String, String> taNames = new HashMap<>();
        Map<String, Integer> taApprovedCount = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(applicantsFile))) {
            String line;
            br.readLine(); // 跳过 Header
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 8) {
                    String taId = parts[0].trim();
                    String name = parts[1].trim();
                    String status = parts[7].trim(); 

                    taNames.put(taId, name);
                    taApprovedCount.putIfAbsent(taId, 0);

                    // 只有 Approved 的才计算工作量
                    if (status.equalsIgnoreCase("Approved")) {
                        taApprovedCount.put(taId, taApprovedCount.get(taId) + 1);
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }

        for (String taId : taNames.keySet()) {
            String name = taNames.get(taId);
            int enrolledCourses = taApprovedCount.get(taId);
            int estimatedHours = enrolledCourses * 10; 
            String email = name.toLowerCase().replace(" ", ".") + "_" + taId.toLowerCase() + "@qmul.ac.uk";

            workloadData.add(new Object[]{taId, name, String.valueOf(enrolledCourses), String.valueOf(estimatedHours), email});
        }
        return workloadData;
    }

    // ==========================================
    // 数据清理与移除逻辑
    // ==========================================
    
    // 清理废弃数据 (Rejected / Withdrawn)
    public int cleanUpInvalidData() {
        if (!applicantsFile.exists()) return 0;
        return filterCSVData(parts -> {
            String status = parts[7];
            return status.equalsIgnoreCase("Rejected") || status.equalsIgnoreCase("Withdrawn");
        });
    }

    // 按 ID 精准开除/清理毕业的 TA
    public int purgeTAById(String targetTaId) {
        if (!applicantsFile.exists() || targetTaId == null || targetTaId.isBlank()) return 0;
        return filterCSVData(parts -> parts[0].trim().equalsIgnoreCase(targetTaId.trim()));
    }

    // 内部通用方法：根据条件过滤 CSV 并覆写，返回删除的行数
    private int filterCSVData(java.util.function.Predicate<String[]> deleteCondition) {
        List<String> validLines = new ArrayList<>();
        int removedCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(applicantsFile))) {
            String header = br.readLine();
            if (header != null) validLines.add(header);

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 8) {
                    if (deleteCondition.test(parts)) {
                        removedCount++;
                        continue;
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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(boundary, "Please enter a valid number.");
        }
    }

    public void updateHourLimit(String input) {
        try {
            warningHourLimit = Integer.parseInt(input.trim());
            saveHourLimitToFile();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(boundary, "Please enter a valid number.");
        }
    }

    public Admin_TAWorkLoadControlUI getUi() {
        return boundary;
    }
}