package TAUI;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Control Class
 * Handles the business logic for the TA side, connecting the UI and data.
 */
public class TAController {

    private List<Job> allJobs;
    // [新增] 用于存储已上传简历文件名的列表
    private List<String> uploadedCVs;

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

        // [新增] 初始化简历列表
        uploadedCVs = new ArrayList<>();
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
    // US-03: CV Management Logic (New Methods)
    // ==========================================

    public List<String> getUploadedCVs() {
        return uploadedCVs;
    }

    /**
     * Validates and processes the CV upload.
     * Returns an error message if invalid, or null if successful.
     */
    public String uploadCV(File file) {
        // US-03 AC2: Enforce file size limit (e.g., Max 5MB)
        long fileSizeInMB = file.length() / (1024 * 1024);
        if (fileSizeInMB > 5) {
            return "File size exceeds the 5MB limit.";
        }

        // US-03 AC1: Accept standard file formats
        String fileName = file.getName().toLowerCase();
        if (!fileName.endsWith(".pdf") && !fileName.endsWith(".docx")) {
            return "Invalid format. Only .pdf and .docx are allowed.";
        }

        // Prevent duplicate names
        if (uploadedCVs.contains(file.getName())) {
            return "A CV with this name already exists.";
        }

        // Simulate saving the file
        uploadedCVs.add(file.getName());
        return null; // Return null indicates success
    }

    public boolean deleteCV(String fileName) {
        return uploadedCVs.remove(fileName);
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