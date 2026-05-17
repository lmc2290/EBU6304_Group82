package LoginPage;

/**
 * Entity class representing a teaching module (course).
 * Stores module metadata and its current status in the approval workflow.
 */
public class Module {
    private String moduleName;
    private String responsibilities;
    private String requirements;
    private int positions;
    private String deadline;
    private String status;

    /**
     * Constructs a Module with all fields.
     *
     * @param moduleName      unique module identifier (e.g. "CS101")
     * @param responsibilities description of TA responsibilities
     * @param requirements    required skills/qualifications for TA applicants
     * @param positions       maximum number of TA positions available
     * @param deadline        application deadline (YYYY-MM-DD)
     * @param status          current workflow status (e.g. "Pending Review", "Approved")
     */
    public Module(String moduleName, String responsibilities, String requirements,
                  int positions, String deadline, String status) {
        this.moduleName = moduleName;
        this.responsibilities = responsibilities;
        this.requirements = requirements;
        this.positions = positions;
        this.deadline = deadline;
        this.status = status;
    }

    public String getModuleName() { return moduleName; }
    public String getResponsibilities() { return responsibilities; }
    public String getRequirements() { return requirements; }
    public int getPositions() { return positions; }
    public String getDeadline() { return deadline; }
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
    public void setPositions(int positions) { this.positions = positions; }
}
