package TAUI;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Entity Class: Application Record
 * 已更新：将 submittedCV 替换为 submittedProfile，对应新的档案系统。
 */
public class ApplicationRecord {

    private String applicationId;
    private Job targetJob;
    private String userId;

    // 关键点：变量名已更改为 submittedProfile
    private UserProfile submittedProfile;

    private String coverLetter;
    private String status;
    private Date submissionDate;

    /**
     * 构造函数已同步更新参数名和赋值逻辑
     */
    public ApplicationRecord(Job targetJob, String userId, UserProfile submittedProfile, String coverLetter) {
        this.applicationId = UUID.randomUUID().toString();
        this.targetJob = targetJob;
        this.userId = userId;

        // 修复报错：确保这里使用的是新的变量名
        this.submittedProfile = submittedProfile;

        this.coverLetter = (coverLetter != null) ? coverLetter : "";
        this.status = "Pending";
        this.submissionDate = new Date();
    }

    // ==========================================
    // Getters and Setters (已同步更新)
    // ==========================================

    public String getApplicationId() { return applicationId; }

    public Job getTargetJob() { return targetJob; }

    public String getUserId() { return userId; }

    // 修复：更新为 Profile 相关的 Getter
    public UserProfile getSubmittedProfile() {
        return submittedProfile;
    }

    public void setSubmittedProfile(UserProfile submittedProfile) {
        this.submittedProfile = submittedProfile;
    }

    public String getCoverLetter() { return coverLetter; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public Date getSubmissionDate() { return submissionDate; }

    public String getFormattedSubmissionDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(this.submissionDate);
    }
    // [DataStore 需要用到]：用于从 TXT 文件中恢复原始申请 ID 和时间
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }
    public void setSubmissionDate(Date submissionDate) { this.submissionDate = submissionDate; }
    @Override
    public String toString() {
        return "[" + status + "] " + (targetJob != null ? targetJob.getTitle() : "Unknown Job");
    }
}