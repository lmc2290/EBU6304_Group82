package LoginPage;

public class Vacancy {
    private String moduleName;
    private String responsibilities;
    private String requirements;
    private int positions;
    private String deadline;
    private String status;

    public Vacancy(String moduleName, String responsibilities, String requirements,
                   int positions, String deadline, String status) {
        this.moduleName = moduleName;
        this.responsibilities = responsibilities;
        this.requirements = requirements;
        this.positions = positions;
        this.deadline = deadline;
        this.status = status;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getResponsibilities() {
        return responsibilities;
    }

    public String getRequirements() {
        return requirements;
    }

    public int getPositions() {
        return positions;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getStatus() {
        return status;
    }
}