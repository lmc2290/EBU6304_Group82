package LoginPage;

public class Module {
    private String moduleName;
    private String responsibilities;
    private String requirements;
    private int positions;
    private String deadline;
    private String status;
    private int totalWorkHours;

    public Module(String moduleName, String responsibilities, String requirements,
                  int positions, String deadline, String status) {
        this.moduleName = moduleName;
        this.responsibilities = responsibilities;
        this.requirements = requirements;
        this.positions = positions;
        this.deadline = deadline;
        this.status = status;
        this.totalWorkHours = 0;
    }

    public Module(String moduleName, String responsibilities, String requirements,
                  int positions, String deadline, String status, int totalWorkHours) {
        this.moduleName = moduleName;
        this.responsibilities = responsibilities;
        this.requirements = requirements;
        this.positions = positions;
        this.deadline = deadline;
        this.status = status;
        this.totalWorkHours = totalWorkHours;
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

    public void setPositions(int positions) {
        this.positions = positions;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public int getTotalWorkHours() {
        return totalWorkHours;
    }

    public void setTotalWorkHours(int totalWorkHours) {
        this.totalWorkHours = totalWorkHours;
    }

    @Override
    public String toString() {
        return moduleName + "," + responsibilities + "," + requirements + ","
                + positions + "," + deadline + "," + status + "," + totalWorkHours;
    }
}
