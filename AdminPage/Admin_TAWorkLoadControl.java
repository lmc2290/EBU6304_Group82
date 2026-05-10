package AdminPage;

import LoginPage.User;
import LoginPage.UnifiedDataStore;
import javax.swing.*;
import java.io.*;
import java.util.*;

public class Admin_TAWorkLoadControl {
    private final User currentUser;
    private final Admin_TAWorkLoadControlUI boundary;

    public int currentLimit = 3;
    public int warningHourLimit = 20;

    private final File configFile = new File("data/limit_config.txt");
    private final File hourConfigFile = new File("data/hour_limit_config.txt");

    public Admin_TAWorkLoadControl(User user) {
        this.currentUser = user;

        configFile.getParentFile().mkdirs();
        loadLimitFromFile();
        loadHourLimitFromFile();

        this.boundary = new Admin_TAWorkLoadControlUI(this);
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

    public List<Object[]> getRealTAWorkloadData() {
        List<Object[]> workloadData = new ArrayList<>();

        Map<String, String> taNames = new HashMap<>();
        Map<String, Integer> taApprovedCount = new HashMap<>();

        List<String[]> applicants = UnifiedDataStore.getAllApplicants();

        for (String[] applicant : applicants) {
            if (applicant.length >= 8) {
                String taId = applicant[1].trim();
                String name = applicant[2].trim();
                String status = applicant[7].trim();

                taNames.put(taId, name);
                taApprovedCount.putIfAbsent(taId, 0);

                if (status.equalsIgnoreCase("Approved")) {
                    taApprovedCount.put(taId, taApprovedCount.get(taId) + 1);
                }
            }
        }

        for (String taId : taNames.keySet()) {
            String name = taNames.get(taId);
            int enrolledCourses = taApprovedCount.get(taId);
            int estimatedHours = enrolledCourses * 10;
            String email = name.toLowerCase().replace(" ", ".") + "_" + taId.toLowerCase() + "@qmul.ac.uk";

            workloadData.add(new Object[]{taId, name, String.valueOf(enrolledCourses), String.valueOf(estimatedHours), email});
        }

        return workloadData;
    }

    public int cleanUpInvalidData() {
        return 0;
    }

    public int purgeTAById(String targetTaId) {
        return 0;
    }

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