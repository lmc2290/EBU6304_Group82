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
        allJobs.add(new Job("J01", "Java Lab Assistant", "ECS401", "10 hours/week", "£15/hr", "1:5", "Assist students with Java lab exercises and mark weekly assignments.", false));
        allJobs.add(new Job("J02", "Python Tutor", "ECS414", "8 hours/week", "£16/hr", "1:3", "Hold tutorial sessions for basic Python programming.", false));
        allJobs.add(new Job("J03", "Data Structure Grader", "ECS505", "15 hours/week", "£14/hr", "1:10", "Grade mid-term and final projects. (Deadline Passed)", true));
    }

    /**
     * Gets the list of all jobs.
     */
    public List<Job> getAllJobs() {
        return allJobs;
    }

    /**
     * US-02: Job list filtering logic.
     * This is simplified; in reality, multiple filters could be applied
     * based on the dropdown conditions.
     */
    public List<Job> filterJobs(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllJobs();
        }
        List<Job> filtered = new ArrayList<>();
        for (Job job : allJobs) {
            // Simple implementation: matches only the title or module code
            if (job.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                    job.getModule().toLowerCase().contains(keyword.toLowerCase())) {
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