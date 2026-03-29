package LoginPage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Admin_CheckApplicationLimit {

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
                String courseId = parts[0];
                int limit = Integer.parseInt(parts[1]);
                map.put(courseId, limit);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static int getEnrolledCourses(String taName) {
        Map<String, Integer> mockData = new HashMap<>();
        mockData.put("Alice Johnson", 3);
        mockData.put("Bob Smith", 2);
        mockData.put("Charlie Brown", 1);
        mockData.put("David Wilson", 2);
        return mockData.getOrDefault(taName, 0);
    }

    public static boolean canApply(String taName, String courseId) {
        Map<String, Integer> limits = loadCourseLimits();
        int maxAllowed = limits.getOrDefault(courseId, 3);
        int currentCourses = getEnrolledCourses(taName);

        return currentCourses < maxAllowed;
    }
}