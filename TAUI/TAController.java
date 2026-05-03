package TAUI;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Control Class: TA Controller
 * Handles the core business logic, data routing, and persistence (mock database)
 * for the Teaching Assistant application system. It acts as the central hub between
 * UI components and the underlying data entities.
 * * @version 2.0 (Updated with US-07 & US-08 Application Management Architecture)
 */
public class TAController {

    // Central repository for all available job postings
    private List<Job> allJobs;

    // [Feature US-03]: Data Isolation for CVs.
    // Key: userId, Value: List of CVRecords uploaded by that user.
    private Map<String, List<CVRecord>> userCVsMap;

    // [Feature US-07 & US-08]: Data Isolation for Applications.
    // Key: userId, Value: List of ApplicationRecords submitted by that user.
    private Map<String, List<ApplicationRecord>> userApplicationsMap;

    /**
     * Default constructor. Initializes the data structures and populates the
     * system with mock job data for demonstration purposes.
     */
    // 在 TAController 类中修改
    private Map<String, UserProfile> userProfileMap = new HashMap<>();

    public UserProfile getUserProfile(String userId) {
        // 如果没有档案，返回一个空对象避免空指针
        return userProfileMap.getOrDefault(userId, new UserProfile());
    }

    public void saveUserProfile(String userId, UserProfile profile) {
        userProfileMap.put(userId, profile);
    }
    public TAController() {
        allJobs = new ArrayList<>();

        // Mock Data: Initializing Job Postings
        allJobs.add(new Job("J01", "Java Lab Assistant", "ECS401", "10 hours/week", "£15/hr", "1:5",
                "Assist students with Java lab exercises and mark weekly assignments.", false,
                "Lab Assistant", "Java"));

        allJobs.add(new Job("J02", "Python Tutor", "ECS414", "8 hours/week", "£16/hr", "1:3",
                "Hold tutorial sessions for Python data modeling and machine learning basics.", false,
                "Tutor", "Python"));

        allJobs.add(new Job("J03", "Signal Processing Grader", "ECS505", "15 hours/week", "£14/hr", "1:10",
                "Grade MATLAB scripts for communication systems and signal processing assignments. (Deadline Passed)", true,
                "Grader", "MATLAB"));

        // Initialize the Maps for user data isolation
        userCVsMap = new HashMap<>();
        userApplicationsMap = new HashMap<>();
    }


    // ==========================================
    // Core Job Retrieval and Filtering Logic
    // ==========================================

    public List<Job> getAllJobs() {
        return allJobs;
    }

    /**
     * Filters the central job list based on multiple criteria provided by the UI.
     */
    public List<Job> filterJobs(String module, String status, String jobType, String skills, String keyword) {
        List<Job> filtered = new ArrayList<>();

        for (Job job : allJobs) {
            boolean match = true;
            if (!"All".equals(module) && !job.getModule().equalsIgnoreCase(module)) { match = false; }
            if (match && !"All".equals(status)) {
                if ("Open Only".equals(status) && job.isExpired()) { match = false; }
                else if ("Closed".equals(status) && !job.isExpired()) { match = false; }
            }
            if (match && !"All".equals(jobType) && !job.getJobType().equalsIgnoreCase(jobType)) { match = false; }
            if (match && !"All".equals(skills) && !job.getRequiredSkill().equalsIgnoreCase(skills)) { match = false; }
            if (match && keyword != null && !keyword.trim().isEmpty()) {
                String kw = keyword.toLowerCase().trim();
                boolean titleMatch = job.getTitle().toLowerCase().contains(kw);
                boolean respMatch = job.getResponsibilities().toLowerCase().contains(kw);
                if (!titleMatch && !respMatch) { match = false; }
            }
            if (match) { filtered.add(job); }
        }
        return filtered;
    }

    // ==========================================
    // US-03: CV Management Logic (User Isolated)
    // ==========================================

    /**
     * Retrieves the CV list for a specific user.
     * Creates an empty list if the user has no uploaded CVs yet to prevent NullPointerExceptions.
     */
    public List<CVRecord> getUploadedCVs(String userId) {
        if (userId == null || userId.isEmpty()) return new ArrayList<>();
        return userCVsMap.computeIfAbsent(userId, k -> new ArrayList<>());
    }

    public String uploadCV(String userId, File file) {
        if (file.length() > 5 * 1024 * 1024) {
            return "File size exceeds the 5MB limit.";
        }

        String originalName = file.getName();
        String lowerName = originalName.toLowerCase();
        if (!lowerName.endsWith(".pdf") && !lowerName.endsWith(".docx")) {
            return "Invalid format. Only .pdf and .docx are allowed.";
        }

        List<CVRecord> myCVs = getUploadedCVs(userId);

        for (CVRecord cv : myCVs) {
            if (cv.getOriginalName().equals(originalName)) {
                return "A CV with this name already exists in your list.";
            }
        }

        try {
            File targetDir = new File("uploaded_cvs");
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }

            String extension = originalName.substring(originalName.lastIndexOf("."));
            String baseName = originalName.substring(0, originalName.lastIndexOf("."));
            String storedName = baseName + "_" + System.currentTimeMillis() + extension;

            File targetFile = new File(targetDir, storedName);

            Files.copy(file.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            myCVs.add(new CVRecord(originalName, storedName));
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return "System Error: Failed to save file.";
        }
    }

    public boolean deleteCV(String userId, CVRecord cvRecord) {
        if (cvRecord == null) return false;

        try {
            File targetDir = new File("uploaded_cvs");
            File fileToDelete = new File(targetDir, cvRecord.getStoredName());

            if (fileToDelete.exists()) {
                boolean deleted = fileToDelete.delete();
                if (!deleted) {
                    System.err.println("Failed to delete physical file: " + cvRecord.getStoredName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        List<CVRecord> myCVs = getUploadedCVs(userId);
        return myCVs.remove(cvRecord);
    }

    // ==========================================
    // US-07 & US-08: Application Lifecycle Management
    // ==========================================

    /**
     * Retrieves the complete list of job applications for a specific user.
     * [Feature Task 4]: Synchronizes status with MO DataStore dynamically.
     */
    public List<ApplicationRecord> getUserApplications(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<ApplicationRecord> myApps = userApplicationsMap.computeIfAbsent(userId, k -> new ArrayList<>());

        boolean dataChanged = false;

        try {
            // 1. 调用之前集成的桥梁，直接读取 MO 端的最新申请列表 (CSV)
            List<LoginPage.Applicant> moApplicants = LoginPage.MODataStore.loadApplicants();

            // 2. 遍历本地当前 TA 的所有申请记录
            for (ApplicationRecord localApp : myApps) {
                // 如果 TA 已经主动撤回 (Withdrawn)，则忽略 MO 的状态，保持撤回
                if (localApp.getStatus().equalsIgnoreCase("Withdrawn")) {
                    continue;
                }

                // 3. 在 MO 的数据库里寻找对应的记录 (通过 UserId 和 Module 交叉匹配)
                for (LoginPage.Applicant moApp : moApplicants) {
                    if (moApp.getApplicantId().equals(userId) &&
                            moApp.getModuleName().equals(localApp.getTargetJob().getModule())) {

                        // 4. 核心比对：如果发现 MO 那边的状态跟本地不一样 (比如变成了 Approved/Rejected)
                        if (!localApp.getStatus().equals(moApp.getStatus())) {
                            localApp.setStatus(moApp.getStatus()); // 更新本地状态
                            dataChanged = true;
                            System.out.println("[Sync Engine] Status updated for " + moApp.getModuleName() + " -> " + moApp.getStatus());
                        }
                        break; // 匹配到记录后跳出内层循环
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[Sync Error] Failed to sync with MO DataStore: " + e.getMessage());
        }

        // 5. 如果发现数据有变动，调用队友新写的底层保存引擎写入本地文件
        if (dataChanged) {
            System.out.println("[DataStore] Changes detected, triggering local file save...");
            // ⚠️ 等你拉取了队友的代码后，把下面这行的注释解开，并换成她写的保存方法：
            // TADataStore.saveApplications(userApplicationsMap);
        }
        // =========================================================

        return myApps;
    }


    public boolean submitApplication(Job targetJob, String userId, UserProfile selectedProfile, String coverLetter) {
        if (targetJob == null || userId == null || selectedProfile == null) {
            System.err.println("Validation Error: Missing critical application data.");
            return false;
        }

        List<ApplicationRecord> existingApps = getUserApplications(userId);
        for (ApplicationRecord app : existingApps) {
            if (app.getTargetJob().getId().equals(targetJob.getId())) {
                String currentStatus = app.getStatus();
                if (!currentStatus.equals("Withdrawn") && !currentStatus.equals("Rejected")) {
                    System.err.println("Duplicate Error: User already has an active application for this job.");
                    return false;
                }
            }
        }

        try {
            ApplicationRecord newRecord = new ApplicationRecord(targetJob, userId, selectedProfile, coverLetter);
            existingApps.add(newRecord); // 存入 TA 自己的内存中

            // =========================================================
            // 🚀 [CROSS-TEAM INTEGRATION]: 将数据写入 MO 组的 CSV 数据库
            // =========================================================
            try {
                // 1. 读取 MO 组现有的 CSV 数据
                List<LoginPage.Applicant> moApplicants = LoginPage.MODataStore.loadApplicants();

                // 2. 将 TA 的 UserProfile 转换成 MO 认识的 Applicant 格式
                String skillsStr = selectedProfile.getSelectedSkills() != null ? String.join(";", selectedProfile.getSelectedSkills()) : "N/A";

                LoginPage.Applicant moFormatApplicant = new LoginPage.Applicant(
                        userId,
                        selectedProfile.getName(),
                        targetJob.getModule(),
                        selectedProfile.getCollege(),
                        "Not Specified",
                        skillsStr,
                        "Online Profile",
                        "Pending"
                );

                // 3. 追加并保存回 MO 的 CSV 文件
                moApplicants.add(moFormatApplicant);
                LoginPage.MODataStore.saveApplicants(moApplicants);
                System.out.println("[Data Bridge] Successfully pushed data to MO CSV DataStore.");

            } catch (Exception ex) {
                System.err.println("[Data Bridge] Failed to connect to MO DataStore: " + ex.getMessage());
            }

            // =========================================================
            // 📧 [US-08 FEATURE]: 发送模拟邮件给 MO
            // =========================================================
            EmailService.sendEmail(targetJob.getModule(), selectedProfile.getName(), targetJob.getTitle());

            System.out.println("=== Application Transaction Success ===");
            System.out.println("Generated App ID: " + newRecord.getApplicationId());
            return true;

        } catch (Exception e) {
            System.err.println("System Error: Failed to process application record.");
            e.printStackTrace();
            return false;
        }
    }
    /**
     * [Feature US-07]: Withdraw Application Logic
     * Processes the withdrawal request for a specific application record.
     * Implements status constraints to prevent withdrawing applications that
     * are already finalized (e.g., Hired, Rejected) or already withdrawn.
     * * @param record The ApplicationRecord to be withdrawn.
     * @return boolean True if withdrawal was successful, false if constrained or null.
     */
    public boolean withdrawApplication(ApplicationRecord record) {
        // 1. Defensive null check
        if (record == null) {
            System.err.println("Withdrawal Error: Record is null.");
            return false;
        }

        String currentStatus = record.getStatus();

        // 2. State Machine Validation (US-07 AC4)
        // Cannot withdraw if the decision has already been made or already withdrawn.
        if (currentStatus.equalsIgnoreCase("Hired") ||
                currentStatus.equalsIgnoreCase("Rejected") ||
                currentStatus.equalsIgnoreCase("Withdrawn")) {
            System.err.println("Withdrawal Blocked: Current status '" + currentStatus + "' does not allow withdrawal.");
            return false;
        }

        try {
            // 3. Execute State Change (US-07 AC3)
            record.setStatus("Withdrawn");

            // Log transaction for audit purposes
            System.out.println("=== Application Withdrawn ===");
            System.out.println("App ID: " + record.getApplicationId());
            System.out.println("New Status: " + record.getStatus());
            System.out.println("=============================");

            return true;
        } catch (Exception e) {
            System.err.println("System Error during application withdrawal.");
            e.printStackTrace();
            return false;
        }
    }
    //smart match
    /**
     * Nested inner class to hold a Job and its calculated compatibility score.
     * Implements Comparable to allow easy sorting by score in descending order.
     */
    private static class JobScore implements Comparable<JobScore> {
        Job job;
        int score;

        public JobScore(Job job, int score) {
            this.job = job;
            this.score = score;
        }

        @Override
        public int compareTo(JobScore other) {
            // Descending order: highest score first
            return Integer.compare(other.score, this.score);
        }
    }

    /**
     * Core Algorithm: Calculates and sorts all available jobs based on the user's profile.
     * Uses a weighted scoring system based on core skills, other skills, and experience keywords.
     * * @param userId The ID of the current user.
     * @return A sorted List of Jobs from most recommended to least recommended.
     */
    public List<Job> getRecommendedJobs(String userId) {
        UserProfile profile = getUserProfile(userId);

        // Defensive check: If profile is empty, just return the default list
        if (profile == null || profile.getName() == null) {
            System.err.println("Smart Match: No profile found, returning default list.");
            return new ArrayList<>(allJobs);
        }

        List<JobScore> scoredJobs = new ArrayList<>();

        for (Job job : allJobs) {
            int currentScore = calculateMatchScore(job, profile);
            scoredJobs.add(new JobScore(job, currentScore));
        }

        // Sort the list based on the compareTo method (descending score)
        java.util.Collections.sort(scoredJobs);

        // Extract the sorted Job objects back into a standard list
        List<Job> recommendedList = new ArrayList<>();
        for (JobScore js : scoredJobs) {
            System.out.println("Match Score for [" + js.job.getTitle() + "]: " + js.score); // Console log for debugging
            recommendedList.add(js.job);
        }

        return recommendedList;
    }

    /**
     * Helper method to compute the weighted score for a single job against a profile.
     */
    private int calculateMatchScore(Job job, UserProfile profile) {
        int score = 0;
        String requiredSkill = job.getRequiredSkill().toLowerCase();

        // 1. Core Skills Match (Weight: +50)
        if (profile.getSelectedSkills() != null) {
            for (String userSkill : profile.getSelectedSkills()) {
                if (userSkill.toLowerCase().contains(requiredSkill) || requiredSkill.contains(userSkill.toLowerCase())) {
                    score += 50;
                    break; // Max 50 points from core skills to prevent score inflation
                }
            }
        }

        // 2. Other Skills Match (Weight: +30)
        String otherSkills = profile.getOtherSkills();
        if (otherSkills != null && otherSkills.toLowerCase().contains(requiredSkill)) {
            score += 30;
        }

        // 3. Experience Keyword Match (Weight: +20)
        String experience = profile.getExperience();
        if (experience != null && experience.toLowerCase().contains(requiredSkill)) {
            score += 20;
        }

        // 4. Status Penalty: Deprioritize closed jobs (Penalty: -100)
        if (job.isExpired()) {
            score -= 100;
        }

        return score;
    }
}
