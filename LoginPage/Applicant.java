package LoginPage;

import java.util.List;

public class Applicant {
    private String id;
    private String name;
    private String course;
    private String englishLevel;
    private List<String> previousCourses;
    private String cv;
    private String status; // "Pending", "Shortlisted", "Rejected"
    
    public Applicant(String id, String name, String course, String englishLevel, List<String> previousCourses, String cv) {
        this.id = id;
        this.name = name;
        this.course = course;
        this.englishLevel = englishLevel;
        this.previousCourses = previousCourses;
        this.cv = cv;
        this.status = "Pending";
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getCourse() {
        return course;
    }
    
    public String getEnglishLevel() {
        return englishLevel;
    }
    
    public List<String> getPreviousCourses() {
        return previousCourses;
    }
    
    public String getCv() {
        return cv;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}