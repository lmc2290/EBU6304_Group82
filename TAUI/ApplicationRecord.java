package TAUI;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Entity Class: Application Record
 * * This class encapsulates all data related to a single job application submitted
 * by a Teaching Assistant. It acts as the core data transfer object (DTO) for
 * the application lifecycle, including submission, status tracking, and withdrawal.
 * * @author [Your Name/Team Name]
 * @version 1.0
 */
public class ApplicationRecord {

    // Unique identifier for each application record
    private String applicationId;

    // The specific job being applied for
    private Job targetJob;

    // The ID of the user submitting the application
    private String userId;

    // The specific CV chosen for this application
    private CVRecord submittedCV;

    // The optional cover letter text
    private String coverLetter;

    // The current status of the application (e.g., "Pending", "Withdrawn", "Hired", "Rejected")
    private String status;

    // The exact timestamp when the application was submitted
    private Date submissionDate;

    /**
     * Comprehensive constructor to initialize a new application record.
     * Automatically generates a unique application ID and sets the submission timestamp.
     * * @param targetJob   The Job object the user is applying to.
     * @param userId      The unique identifier of the applicant.
     * @param submittedCV The CVRecord selected from the user's uploaded CVs.
     * @param coverLetter The text content of the cover letter (can be empty).
     */
    public ApplicationRecord(Job targetJob, String userId, CVRecord submittedCV, String coverLetter) {
        // Generate a random UUID for tracking
        this.applicationId = UUID.randomUUID().toString();
        this.targetJob = targetJob;
        this.userId = userId;
        this.submittedCV = submittedCV;
        this.coverLetter = (coverLetter != null) ? coverLetter : "";

        // Default status upon initial submission is always "Pending"
        this.status = "Pending";

        // Record the exact time of submission
        this.submissionDate = new Date();
    }

    // ==========================================
    // Standard Getters and Setters
    // ==========================================

    public String getApplicationId() {
        return applicationId;
    }

    public Job getTargetJob() {
        return targetJob;
    }

    public void setTargetJob(Job targetJob) {
        this.targetJob = targetJob;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public CVRecord getSubmittedCV() {
        return submittedCV;
    }

    public void setSubmittedCV(CVRecord submittedCV) {
        this.submittedCV = submittedCV;
    }

    public String getCoverLetter() {
        return coverLetter;
    }

    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }

    public String getStatus() {
        return status;
    }

    /**
     * Updates the status of the application.
     * This will be used later for US-07 (Withdraw) and US-08 (Status Updates).
     * * @param status The new status string to apply.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    /**
     * Formats the submission date into a human-readable string.
     * * @return Formatted date string (e.g., "yyyy-MM-dd HH:mm:ss").
     */
    public String getFormattedSubmissionDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(this.submissionDate);
    }

    /**
     * Overrides the default toString method.
     * This is crucial for UI rendering in the JList later. It ensures the object
     * displays correctly without needing complex custom renderers initially.
     * * @return A formatted string displaying status and job title.
     */
    @Override
    public String toString() {
        return "[" + status + "] " + (targetJob != null ? targetJob.getTitle() : "Unknown Job");
    }
}