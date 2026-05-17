package LoginPage;

/**
 * Entity class representing a TA applicant in the recruitment system.
 * An applicant applies for a specific module and goes through a workflow
 * of statuses: Pending, Shortlisted, Approved, or Rejected.
 */
public class Applicant {
    private String applicantId;
    private String name;
    private String moduleName;
    private String course;
    private String englishLevel;
    private String completedCourses;
    private String cvFileName;
    private String status;

    /**
     * Constructs an Applicant with all fields.
     *
     * @param applicantId      unique applicant identifier (e.g. "A001")
     * @param name             applicant's full name
     * @param moduleName       the module applied for (e.g. "CS101")
     * @param course           applicant's current course of study
     * @param englishLevel     English proficiency level (e.g. "IELTS 7.0")
     * @param completedCourses semicolon-separated list of completed courses
     * @param cvFileName       name of the uploaded CV file
     * @param status           current application status
     */
    public Applicant(String applicantId, String name, String moduleName,
                     String course, String englishLevel,
                     String completedCourses, String cvFileName, String status) {
        this.applicantId = applicantId;
        this.name = name;
        this.moduleName = moduleName;
        this.course = course;
        this.englishLevel = englishLevel;
        this.completedCourses = completedCourses;
        this.cvFileName = cvFileName;
        this.status = status;
    }

    public String getApplicantId() { return applicantId; }
    public String getName() { return name; }
    public String getModuleName() { return moduleName; }
    public String getCourse() { return course; }
    public String getEnglishLevel() { return englishLevel; }
    public String getCompletedCourses() { return completedCourses; }
    public String getCvFileName() { return cvFileName; }
    public String getStatus() { return status; }

    /**
     * Updates the application status.
     *
     * @param status new status (e.g. "Pending", "Shortlisted", "Approved", "Rejected")
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
