package AdminPage;

public class Admin_TA {
    private String id;
    private String name;
    private int enrolledCourses;
    private int workHours;
    private String email;

    public Admin_TA(String id, String name, int enrolledCourses, int workHours, String email) {
        this.id = id;
        this.name = name;
        this.enrolledCourses = enrolledCourses;
        this.workHours = workHours;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getEnrolledCourses() {
        return enrolledCourses;
    }

    public int getWorkHours() {
        return workHours;
    }

    public String getEmail() {
        return email;
    }
}