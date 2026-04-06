package TAUI;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Control Class
 * Handles the business logic for the TA side, connecting the UI and data.
 */
public class TAController {

    private List<Job> allJobs;

    // [Feature]: Data Isolation.
    // Use a Map to bind CV lists to specific User IDs. Key: userId, Value: List of CVRecords.
    private Map<String, List<CVRecord>> userCVsMap;

    public TAController() {
        allJobs = new ArrayList<>();

        allJobs.add(new Job("J01", "Java Lab Assistant", "ECS401", "10 hours/week", "£15/hr", "1:5",
                "Assist students with Java lab exercises and mark weekly assignments.", false,
                "Lab Assistant", "Java"));

        allJobs.add(new Job("J02", "Python Tutor", "ECS414", "8 hours/week", "£16/hr", "1:3",
                "Hold tutorial sessions for Python data modeling and machine learning basics.", false,
                "Tutor", "Python"));

        allJobs.add(new Job("J03", "Signal Processing Grader", "ECS505", "15 hours/week", "£14/hr", "1:10",
                "Grade MATLAB scripts for communication systems and signal processing assignments. (Deadline Passed)", true,
                "Grader", "MATLAB"));

        // Initialize the Map
        userCVsMap = new HashMap<>();
    }

    public List<Job> getAllJobs() {
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
    // US-03: CV Management Logic (User Isolated)
    // ==========================================

    /**
     * Retrieves the CV list for a specific user.
     * Creates an empty list if the user has no uploaded CVs yet.
     */
    public List<CVRecord> getUploadedCVs(String userId) {
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

        // Get the specific CV list for the current logged-in user
        List<CVRecord> myCVs = getUploadedCVs(userId);

        // Check for duplicates within this user's list
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

            java.nio.file.Files.copy(file.toPath(), targetFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            // Add the CV record to the specific user's list
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

        // Remove from the specific user's list
        List<CVRecord> myCVs = getUploadedCVs(userId);
        return myCVs.remove(cvRecord);
    }

    // ==========================================
    // US-06: Submit Application Logic
    // ==========================================
    public boolean submitApplication(Job job, String cvName, String coverLetter) {
        System.out.println("=== Application Submitted ===");
        System.out.println("Target Job: " + job.getTitle());
        System.out.println("Selected CV: " + cvName);
        System.out.println("Cover Letter Length: " + coverLetter.length() + " chars");
        System.out.println("=============================");
        return true;
    }
}