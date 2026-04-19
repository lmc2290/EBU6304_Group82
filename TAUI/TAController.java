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
     * Implements defensive programming to guarantee a non-null return value.
     * * @param userId The unique identifier of the applicant.
     * @return A List containing all ApplicationRecords for the user.
     */
    public List<ApplicationRecord> getUserApplications(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return userApplicationsMap.computeIfAbsent(userId, k -> new ArrayList<>());
    }

    /**
     * Core business logic to process and persist a new job application.
     * Validates input, prevents duplicates, and records the transaction.
     * * @param targetJob   The Job entity being applied to.
     * @param userId      The unique ID of the user submitting the application.
     * @param selectedCV  The specific CVRecord chosen for this application.
     * @param coverLetter The submitted cover letter text (can be empty).
     * @return boolean    True if the submission was successful, false otherwise.
     */
    public boolean submitApplication(Job targetJob, String userId, CVRecord selectedCV, String coverLetter) {

        // 1. Strict null-check validation
        if (targetJob == null || userId == null || selectedCV == null) {
            System.err.println("Validation Error: Missing critical application data.");
            return false;
        }

        // 2. Prevent duplicate active applications for the exact same job
        List<ApplicationRecord> existingApps = getUserApplications(userId);
        for (ApplicationRecord app : existingApps) {
            if (app.getTargetJob().getId().equals(targetJob.getId())) {
                String currentStatus = app.getStatus();
                // If it's not Withdrawn or Rejected, it means it's active (Pending/Interviewing)
                if (!currentStatus.equals("Withdrawn") && !currentStatus.equals("Rejected")) {
                    System.err.println("Duplicate Error: User already has an active application for this job.");
                    return false;
                }
            }
        }

        try {
            // 3. Instantiate the new application record entity
            ApplicationRecord newRecord = new ApplicationRecord(targetJob, userId, selectedCV, coverLetter);

            // 4. Persist the record into the user's isolated application list
            existingApps.add(newRecord);

            // 5. System logging for debugging and audit trails
            System.out.println("=== Application Transaction Success ===");
            System.out.println("Generated App ID: " + newRecord.getApplicationId());
            System.out.println("Applicant ID: " + userId);
            System.out.println("Target Position: " + targetJob.getTitle());
            System.out.println("Selected CV: " + selectedCV.getOriginalName());
            System.out.println("Timestamp: " + newRecord.getFormattedSubmissionDate());
            System.out.println("=======================================");

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
}
