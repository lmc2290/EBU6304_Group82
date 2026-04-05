package LoginPage;

import java.io.*;
import java.util.*;

/**
 * Logic for checking TA application limits against local configuration files.
 */
public class Admin_CheckApplicationLimit {

    // Load limits from "course_limits.txt" (Managed by Admin UI)
    public static Map<String, Integer> loadCourseLimits() {
        Map<String, Integer> map = new HashMap<>();
        File file = new File("course_limits.txt");

        if (!file.exists()) return map;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    // Case: CS101,3
                    map.put(parts[0].trim(), Integer.parseInt(parts[1].trim()));
                } else if (parts.length == 1) {
                    // Case: Just a single number (Global limit from Admin UI)
                    map.put("GLOBAL_LIMIT", Integer.parseInt(parts[0].trim()));
                }
            }
        } catch (Exception e) {
            // Silent catch for clean run
        }
        return map;
    }

    // Core logic: Check if a TA is allowed to apply for a specific course
    public static boolean canApply(String taName, String courseId) {
        Map<String, Integer> limits = loadCourseLimits();
        
        // Priority: 1. Specific course limit -> 2. Global limit from UI -> 3. Default constant (3)
        int maxAllowed = limits.getOrDefault(courseId, limits.getOrDefault("GLOBAL_LIMIT", 3));

        // Get current enrollment count (Simulated for now, should read from enrollment file)
        int currentCount = getTAEnrollmentCount(taName);

        return currentCount < maxAllowed;
    }

    // Helper: Simulated TA data (Next step: Read from TA_enrollment.csv)
    private static int getTAEnrollmentCount(String taName) {
        // Mock data for testing
        if (taName.equalsIgnoreCase("Alice Johnson")) return 3;
        return 1;
    }
}