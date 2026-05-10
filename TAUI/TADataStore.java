package TAUI;

import java.io.*;
import java.util.*;

/**
 * Utility Class: TA Data Store
 * 专门负责将数据持久化到本地 TXT 文件，完全不依赖外部数据库或第三方库。
 */
public class TADataStore {
    private static final String PROFILE_FILE = "ta_profiles.txt";
    private static final String APP_FILE = "ta_applications.txt";
    private static final String DELIMITER = "||";

    // 辅助方法：处理文本中的换行和 null
    private static String escape(String text) {
        if (text == null || text.isEmpty()) return "NULL";
        return text.replace("\n", "[NEWLINE]").replace("||", "~~");
    }

    private static String unescape(String text) {
        if (text.equals("NULL")) return "";
        return text.replace("[NEWLINE]", "\n").replace("~~", "||");
    }

    // ==========================================
    // 1. Profile (档案) 读写
    // ==========================================
    public static void saveProfiles(Map<String, UserProfile> userProfileMap) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PROFILE_FILE))) {
            for (Map.Entry<String, UserProfile> entry : userProfileMap.entrySet()) {
                String userId = entry.getKey();
                UserProfile p = entry.getValue();

                String skills = p.getSelectedSkills() != null ? String.join(",", p.getSelectedSkills()) : "";

                String line = userId + DELIMITER +
                        escape(p.getName()) + DELIMITER +
                        escape(p.getGender()) + DELIMITER +
                        escape(p.getGrade()) + DELIMITER +
                        escape(p.getCollege()) + DELIMITER +
                        escape(skills) + DELIMITER +
                        escape(p.getOtherSkills()) + DELIMITER +
                        escape(p.getExperience()) + DELIMITER +
                        escape(p.getCoverLetterTemplate());
                writer.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error saving profiles: " + e.getMessage());
        }
    }

    public static Map<String, UserProfile> loadProfiles() {
        Map<String, UserProfile> map = new HashMap<>();
        File file = new File(PROFILE_FILE);
        if (!file.exists()) return map; // 如果文件不存在，返回空 Map

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|\\|", -1); // 保留空字符串
                if (parts.length >= 9) {
                    UserProfile p = new UserProfile();
                    String userId = parts[0];
                    p.setName(unescape(parts[1]));
                    p.setGender(unescape(parts[2]));
                    p.setGrade(unescape(parts[3]));
                    p.setCollege(unescape(parts[4]));

                    String skillsStr = unescape(parts[5]);
                    if (!skillsStr.isEmpty()) {
                        p.setSelectedSkills(new ArrayList<>(Arrays.asList(skillsStr.split(","))));
                    }
                    p.setOtherSkills(unescape(parts[6]));
                    p.setExperience(unescape(parts[7]));
                    p.setCoverLetterTemplate(unescape(parts[8]));

                    map.put(userId, p);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading profiles: " + e.getMessage());
        }
        return map;
    }

    // ==========================================
    // 2. Applications (申请记录) 读写
    // ==========================================
    public static void saveApplications(Map<String, List<ApplicationRecord>> appMap) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(APP_FILE))) {
            for (Map.Entry<String, List<ApplicationRecord>> entry : appMap.entrySet()) {
                String userId = entry.getKey();
                for (ApplicationRecord app : entry.getValue()) {
                    String line = app.getApplicationId() + DELIMITER +
                            userId + DELIMITER +
                            app.getTargetJob().getId() + DELIMITER +
                            app.getStatus() + DELIMITER +
                            app.getSubmissionDate().getTime() + DELIMITER + // 保存时间戳
                            escape(app.getCoverLetter());
                    writer.println(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving applications: " + e.getMessage());
        }
    }

    public static Map<String, List<ApplicationRecord>> loadApplications(List<Job> allJobs, Map<String, UserProfile> profiles) {
        Map<String, List<ApplicationRecord>> map = new HashMap<>();
        File file = new File(APP_FILE);
        if (!file.exists()) return map;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|\\|", -1);
                if (parts.length >= 6) {
                    String appId = parts[0];
                    String userId = parts[1];
                    String jobId = parts[2];
                    String status = parts[3];
                    long timestamp = Long.parseLong(parts[4]);
                    String coverLetter = unescape(parts[5]);

                    // 重新组装 ApplicationRecord
                    Job targetJob = allJobs.stream().filter(j -> j.getId().equals(jobId)).findFirst().orElse(null);
                    UserProfile profile = profiles.getOrDefault(userId, new UserProfile());

                    if (targetJob != null) {
                        ApplicationRecord record = new ApplicationRecord(targetJob, userId, profile, coverLetter);
                        record.setApplicationId(appId); // 恢复原有 ID
                        record.setStatus(status);       // 恢复原有状态
                        record.setSubmissionDate(new Date(timestamp)); // 恢复原有时间

                        map.computeIfAbsent(userId, k -> new ArrayList<>()).add(record);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading applications: " + e.getMessage());
        }
        return map;
    }
}