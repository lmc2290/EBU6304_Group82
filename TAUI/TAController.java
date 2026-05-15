package TAUI;

import LoginPage.UnifiedDataStore;
import java.util.*;

public class TAController {

    private List<Job> allJobs;

    // ==========================================
    // 1. Profile 档案存取 (直接连接底层 CSV)
    // ==========================================
    public UserProfile getUserProfile(String userId) {
        // 直接从统一数据库读
        return UnifiedDataStore.getProfileByTaId(userId);
    }

    public void saveUserProfile(String userId, UserProfile profile) {
        // 直接写进统一数据库
        UnifiedDataStore.saveProfile(userId, profile);
        System.out.println("[System] Profile data saved to UnifiedDataStore.");
    }

    // ==========================================
    // 2. 初始化与岗位加载 (Constructor & Jobs)
    // ==========================================
    public TAController() {
        allJobs = new ArrayList<>();
        loadJobsFromModules();
        System.out.println("[System] TAController initialized. Linked to UnifiedDataStore.");
    }

    private void loadJobsFromModules() {
        allJobs.clear();
        List<String[]> approvedModules = UnifiedDataStore.getApprovedModules();

        for (String[] module : approvedModules) {
            if (module.length >= 5) {
                String moduleCode = module[0];
                String moduleName = module[1];
                int requiredTas = 0;
                try {
                    requiredTas = Integer.parseInt(module[3]);
                } catch (NumberFormatException e) {
                    requiredTas = 1;
                }

                Job job = new Job(
                        "JOB-" + moduleCode,
                        moduleName + " TA",
                        moduleCode,
                        "10 hours/week",
                        "£15/hr",
                        "1:" + Math.max(3, requiredTas * 2),
                        "Assist in lab sessions and answer student questions",
                        false,
                        "Lab Assistant",
                        "Java"
                );
                allJobs.add(job);
            }
        }
    }

    public List<Job> getAllJobs() {
        loadJobsFromModules();
        return allJobs;
    }

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
    // 3. 申请流转 (Application Lifecycle)
    // ==========================================
    public List<ApplicationRecord> getUserApplications(String userId) {
        List<ApplicationRecord> myApps = new ArrayList<>();
        if (userId == null || userId.trim().isEmpty()) return myApps;

        try {
            // 直接从统一数据库读取该 TA 的所有申请
            List<String[]> csvApps = UnifiedDataStore.getApplicationsByTaId(userId);
            UserProfile currentProfile = getUserProfile(userId);

            for (String[] a : csvApps) {
                if (a.length >= 8) {
                    Job mockJob = new Job("JOB-" + a[3], a[4] + " TA", a[3], "10 hours/week", "£15/hr", "N/A", "Assist teaching for " + a[3], false, "Lab Assistant", "Java");
                    // 索引 a[6] 存放的是 coverLetter
                    ApplicationRecord record = new ApplicationRecord(mockJob, userId, currentProfile, a[6]);
                    record.setApplicationId(a[0]); // 设置真实的 appId
                    record.setStatus(a[7]);        // 设置 MO 审核的真实状态！
                    myApps.add(record);
                }
            }
        } catch (Exception e) {
            System.err.println("[Sync Error] Failed to read from UnifiedDataStore: " + e.getMessage());
        }

        return myApps;
    }

    public boolean submitApplication(Job targetJob, String userId, UserProfile selectedProfile, String coverLetter) {
        if (targetJob == null || userId == null || selectedProfile == null) {
            return false;
        }

        // 查重逻辑
        List<ApplicationRecord> existingApps = getUserApplications(userId);
        for (ApplicationRecord app : existingApps) {
            if (app.getTargetJob().getModule().equals(targetJob.getModule())) {
                String currentStatus = app.getStatus();
                if (!currentStatus.equalsIgnoreCase("Withdrawn") && !currentStatus.equalsIgnoreCase("Rejected")) {
                    System.err.println("Duplicate Error: Active application exists.");
                    return false;
                }
            }
        }

        try {
            String applicationId = "APP-" + System.currentTimeMillis();

            // 直接交由统一数据库写入 applicants.csv
            UnifiedDataStore.addApplicant(
                    applicationId,
                    userId,
                    selectedProfile.getName(),
                    targetJob.getModule(),
                    targetJob.getTitle(),
                    "Online Profile", // 以前传物理文件路径的地方，现在用文字占位
                    coverLetter
            );

            EmailService.sendEmail(targetJob.getModule(), selectedProfile.getName(), targetJob.getTitle());

            System.out.println("=== Application Transaction Success ===");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean withdrawApplication(ApplicationRecord record) {
        if (record == null) return false;

        String currentStatus = record.getStatus();
        if (currentStatus.equalsIgnoreCase("Hired") || currentStatus.equalsIgnoreCase("Rejected") || currentStatus.equalsIgnoreCase("Withdrawn")) {
            return false;
        }

        try {
            // 直接更新统一数据库的状态
            String moduleCode = record.getTargetJob().getModule();
            UnifiedDataStore.updateApplicantStatus(record.getApplicationId(), moduleCode, "Withdrawn", record.getUserId());

            System.out.println("=== Application Withdrawn ===");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==========================================
    // 4. 智能匹配算法 (Smart Match Engine)
    // ==========================================
    private static class JobScore implements Comparable<JobScore> {
        Job job;
        int score;

        public JobScore(Job job, int score) {
            this.job = job;
            this.score = score;
        }

        @Override
        public int compareTo(JobScore other) {
            return Integer.compare(other.score, this.score);
        }
    }

    public List<Job> getRecommendedJobs(String userId) {
        UserProfile profile = getUserProfile(userId);

        if (profile == null || profile.getName() == null || profile.getName().isEmpty()) {
            System.err.println("Smart Match: No profile found, returning default list.");
            return new ArrayList<>(allJobs);
        }

        List<JobScore> scoredJobs = new ArrayList<>();

        for (Job job : allJobs) {
            int currentScore = calculateMatchScore(job, profile);
            scoredJobs.add(new JobScore(job, currentScore));
        }

        java.util.Collections.sort(scoredJobs);

        List<Job> recommendedList = new ArrayList<>();
        for (JobScore js : scoredJobs) {
            recommendedList.add(js.job);
        }

        return recommendedList;
    }

    private int calculateMatchScore(Job job, UserProfile profile) {
        int score = 0;
        String requiredSkill = job.getRequiredSkill().toLowerCase();

        if (profile.getSelectedSkills() != null) {
            for (String userSkill : profile.getSelectedSkills()) {
                if (userSkill.toLowerCase().contains(requiredSkill) || requiredSkill.contains(userSkill.toLowerCase())) {
                    score += 50;
                    break;
                }
            }
        }

        String otherSkills = profile.getOtherSkills();
        if (otherSkills != null && otherSkills.toLowerCase().contains(requiredSkill)) {
            score += 30;
        }

        String experience = profile.getExperience();
        if (experience != null && experience.toLowerCase().contains(requiredSkill)) {
            score += 20;
        }

        if (job.isExpired()) {
            score -= 100;
        }

        return score;
    }
}