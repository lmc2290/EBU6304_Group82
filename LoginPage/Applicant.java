package LoginPage;

public class Applicant {
    private String applicantId;
    private String name;
    private String moduleName;
    private String course;
    private String englishLevel;
    private String completedCourses;
    private String cvFileName;
    private String status;

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

    public String getApplicantId() {
        return applicantId;
    }

    public String getName() {
        return name;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getCourse() {
        return course;
    }

    public String getEnglishLevel() {
        return englishLevel;
    }

    public String getCompletedCourses() {
        return completedCourses;
    }

    public String getCvFileName() {
        return cvFileName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}