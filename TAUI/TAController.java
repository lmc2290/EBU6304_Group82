package TAUI;

import LoginPage.UnifiedDataStore;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

public class TAController {

    private List<Job> allJobs;
    private Map<String, List<CVRecord>> userCVsMap;
    private Map<String, List<ApplicationRecord>> userApplicationsMap;
    private Map<String, UserProfile> userProfileMap = new HashMap<>();

    public UserProfile getUserProfile(String userId) {
        return userProfileMap.getOrDefault(userId, new UserProfile());
    }

    public void saveUserProfile(String userId, UserProfile profile) {
        userProfileMap.put(userId, profile);
        TADataStore.saveProfiles(userProfileMap);
        System.out.println("[System] Profile data saved to ta_profiles.txt.");
    }

    public TAController() {
        allJobs = new ArrayList<>();
        userCVsMap = new HashMap<>();
        userApplicationsMap = new HashMap<>();

        loadJobsFromModules();
        userProfileMap = TADataStore.loadProfiles();
        userApplicationsMap = TADataStore.loadApplications(allJobs, userProfileMap);
        System.out.println("[System] Data successfully loaded from local text files.");
    }

    private void loadJobsFromModules() {
        allJobs.clear();
        List<String[]> approvedModules = UnifiedDataStore.getApprovedModules();

        for (String[] module : approvedModules) {
            if (module.length >= 5) {
                String moduleCode = module[0];
                String moduleName = module[1];
                String moId = module[2];
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
        // No fallback mock jobs — TA must only see Approved modules from modules.csv
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

    public List<ApplicationRecord> getUserApplications(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<ApplicationRecord> myApps = userApplicationsMap.computeIfAbsent(userId, k -> new ArrayList<>());

        try {
            List<String[]> moApplicants = UnifiedDataStore.getAllApplicants();

            for (ApplicationRecord localApp : myApps) {
                if (localApp.getStatus().equalsIgnoreCase("Withdrawn")) {
                    continue;
                }

                for (String[] moApp : moApplicants) {
                    if (moApp.length >= 8 &&
                            moApp[1].equals(userId) &&
                            moApp[3].equals(localApp.getTargetJob().getModule())) {

                        if (!localApp.getStatus().equals(moApp[7])) {
                            localApp.setStatus(moApp[7]);
                            System.out.println("[Sync Engine] Status updated for " + moApp[3] + " -> " + moApp[7]);
                        }
                        // Sync applicationId from MO-side so withdraw works correctly
                        if (moApp[0] != null && !moApp[0].isEmpty()) {
                            localApp.setApplicationId(moApp[0]);
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[Sync Error] Failed to sync with DataStore: " + e.getMessage());
        }

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
            String applicationId = "APP-" + System.currentTimeMillis();
            ApplicationRecord newRecord = new ApplicationRecord(targetJob, userId, selectedProfile, coverLetter);
            newRecord.setApplicationId(applicationId);
            existingApps.add(newRecord);
            TADataStore.saveApplications(userApplicationsMap);

            UnifiedDataStore.addApplicant(
                    applicationId,
                    userId,
                    selectedProfile.getName(),
                    targetJob.getModule(),
                    targetJob.getTitle(),
                    "Online Profile",
                    coverLetter
            );

            EmailService.sendEmail(targetJob.getModule(), selectedProfile.getName(), targetJob.getTitle());

            System.out.println("=== Application Transaction Success ===");
            System.out.println("Generated App ID: " + applicationId);
            return true;

        } catch (Exception e) {
            System.err.println("System Error: Failed to process application record.");
            e.printStackTrace();
            return false;
        }
    }

    public boolean withdrawApplication(ApplicationRecord record) {
        if (record == null) {
            System.err.println("Withdrawal Error: Record is null.");
            return false;
        }

        String currentStatus = record.getStatus();

        if (currentStatus.equalsIgnoreCase("Hired") ||
                currentStatus.equalsIgnoreCase("Rejected") ||
                currentStatus.equalsIgnoreCase("Withdrawn")) {
            System.err.println("Withdrawal Blocked: Current status '" + currentStatus + "' does not allow withdrawal.");
            return false;
        }

        try {
            record.setStatus("Withdrawn");
            TADataStore.saveApplications(userApplicationsMap);

            // Also update UnifiedDataStore (applicants.csv)
            String moduleCode = record.getTargetJob().getModule();
            UnifiedDataStore.updateApplicantStatus(record.getApplicationId(), moduleCode, "Withdrawn", record.getUserId());

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

        if (profile == null || profile.getName() == null) {
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
            System.out.println("Match Score for [" + js.job.getTitle() + "]: " + js.score);
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