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
    private List<CVRecord> uploadedCVs;

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
    // US-03: CV Management Logic
    // ==========================================

    public List<CVRecord> getUploadedCVs() {
        return uploadedCVs;
    }

    public String uploadCV(File file) {
        // 【修复 Bug 1】：抛弃整数除法，直接使用 Byte 进行精确比较 (5 * 1024 * 1024 bytes)
        if (file.length() > 5 * 1024 * 1024) {
            return "File size exceeds the 5MB limit.";
        }

        String originalName = file.getName();
        String lowerName = originalName.toLowerCase();
        if (!lowerName.endsWith(".pdf") && !lowerName.endsWith(".docx")) {
            return "Invalid format. Only .pdf and .docx are allowed.";
        }

        // 检查逻辑列表中是否已经有同名的简历 (防止 UI 上出现两个一样的名字)
        for (CVRecord cv : uploadedCVs) {
            if (cv.getOriginalName().equals(originalName)) {
                return "A CV with this name already exists in your list.";
            }
        }

        try {
            File targetDir = new File("uploaded_cvs");
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }

            // 【修复 Bug 2】：给物理文件加上时间戳，保证绝对唯一
            String extension = originalName.substring(originalName.lastIndexOf("."));
            String baseName = originalName.substring(0, originalName.lastIndexOf("."));
            String storedName = baseName + "_" + System.currentTimeMillis() + extension;

            File targetFile = new File(targetDir, storedName);

            java.nio.file.Files.copy(file.toPath(), targetFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            // 【架构升级】：不再存简单的 String，而是存入 CVRecord 对象
            uploadedCVs.add(new CVRecord(originalName, storedName));
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return "System Error: Failed to save file.";
        }
    }

    public boolean deleteCV(CVRecord cvRecord) {
        if (cvRecord == null) return false;

        try {
            File targetDir = new File("uploaded_cvs");
            // 【关键】：去硬盘删除时，使用带时间戳的 storedName！
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

        // 内存列表中移除对象
        return uploadedCVs.remove(cvRecord);
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