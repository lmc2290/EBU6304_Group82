package TAUI;

/**
 * Entity Class
 * Encapsulates core data related to a job posting.
 */
public class Job {
    private String id;
    private String title;
    private String module;
    private String hours;
    private String salary;
    private String competitionRatio;
    private String responsibilities;
    private boolean isExpired;

    public Job(String id, String title, String module, String hours, String salary,
               String competitionRatio, String responsibilities, boolean isExpired) {
        this.id = id;
        this.title = title;
        this.module = module;
        this.hours = hours;
        this.salary = salary;
        this.competitionRatio = competitionRatio;
        this.responsibilities = responsibilities;
        this.isExpired = isExpired;
    }

    // --- Getters ---
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getModule() { return module; }
    public String getHours() { return hours; }
    public String getSalary() { return salary; }
    public String getCompetitionRatio() { return competitionRatio; }
    public String getResponsibilities() { return responsibilities; }
    public boolean isExpired() { return isExpired; }

    /**
     * Overrides the toString method to determine the text format
     * displayed in the left JList component.
     */
    @Override
    public String toString() {
        return "[" + module + "] " + title + (isExpired ? " (Closed)" : "");
    }
}