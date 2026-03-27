package TAUI;

import java.util.ArrayList;
import java.util.List;

/**
 * Control Class
 * Handles the business logic for the TA side, connecting the UI and data.
 */
public class TAController {

    private List<Job> allJobs;

    public TAController() {
        // Mock data simulating reads from a local file or database
        allJobs = new ArrayList<>();

        // [修改] 模拟数据中新增了两个字段：JobType (岗位类型) 和 RequiredSkill (技能要求)
        allJobs.add(new Job("J01", "Java Lab Assistant", "ECS401", "10 hours/week", "£15/hr", "1:5",
                "Assist students with Java lab exercises and mark weekly assignments.", false,
                "Lab Assistant", "Java"));

        allJobs.add(new Job("J02", "Python Tutor", "ECS414", "8 hours/week", "£16/hr", "1:3",
                "Hold tutorial sessions for Python data modeling and machine learning basics.", false,
                "Tutor", "Python"));

        allJobs.add(new Job("J03", "Signal Processing Grader", "ECS505", "15 hours/week", "£14/hr", "1:10",
                "Grade MATLAB scripts for communication systems and signal processing assignments. (Deadline Passed)", true,
                "Grader", "MATLAB"));
    }

    /**
     * Gets the list of all jobs.
     */
    public List<Job> getAllJobs() {
        return allJobs;
    }

    /**
     * US-02: Advanced job list filtering logic.
     * [修改] 接收 5 个参数，增加了 jobType 和 skills 的过滤逻辑。
     */
    public List<Job> filterJobs(String module, String status, String jobType, String skills, String keyword) {
        List<Job> filtered = new ArrayList<>();

        for (Job job : allJobs) {
            boolean match = true;

            // 1. Check Module filter
            if (!"All".equals(module) && !job.getModule().equalsIgnoreCase(module)) {
                match = false;
            }

            // 2. Check Status filter (Open Only vs Closed)
            if (match && !"All".equals(status)) {
                if ("Open Only".equals(status) && job.isExpired()) {
                    match = false;
                } else if ("Closed".equals(status) && !job.isExpired()) {
                    match = false;
                }
            }

            // 3. [新增] Check Job Type filter
            if (match && !"All".equals(jobType) && !job.getJobType().equalsIgnoreCase(jobType)) {
                match = false;
            }

            // 4. [新增] Check Required Skills filter
            if (match && !"All".equals(skills) && !job.getRequiredSkill().equalsIgnoreCase(skills)) {
                match = false;
            }

            // 5. Check Keyword (searches title and responsibilities)
            if (match && keyword != null && !keyword.trim().isEmpty()) {
                String kw = keyword.toLowerCase().trim();
                boolean titleMatch = job.getTitle().toLowerCase().contains(kw);
                boolean respMatch = job.getResponsibilities().toLowerCase().contains(kw);

                if (!titleMatch && !respMatch) {
                    match = false;
                }
            }

            if (match) {
                filtered.add(job);
            }
        }
        return filtered;
    }

    /**
     * US-06: Logic for submitting an application.
     */
    public boolean submitApplication(Job job, String cvName, String coverLetter) {
        // In the future, this can be replaced with code to write to a local file or database
        System.out.println("=== Application Submitted ===");
        System.out.println("Target Job: " + job.getTitle());
        System.out.println("Selected CV: " + cvName);
        System.out.println("Cover Letter Length: " + coverLetter.length() + " chars");
        System.out.println("=============================");

        // Simulate a successful submission
        return true;
    }
}