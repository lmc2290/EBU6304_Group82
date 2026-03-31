package LoginPage;

public class Module {
    private String id;
    private String name;
    private String organiserId;
    private int positionCount;
    private String responsibilities;
    private String requirements;
    private String status; // "Pending", "Approved", "Rejected"
    
    public Module(String id, String name, String organiserId) {
        this.id = id;
        this.name = name;
        this.organiserId = organiserId;
        this.positionCount = 0;
        this.responsibilities = "";
        this.requirements = "";
        this.status = "Pending";
    }
    
    public Module(String id, String name, String organiserId, int positionCount, String responsibilities, String requirements) {
        this.id = id;
        this.name = name;
        this.organiserId = organiserId;
        this.positionCount = positionCount;
        this.responsibilities = responsibilities;
        this.requirements = requirements;
        this.status = "Pending";
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getOrganiserId() {
        return organiserId;
    }
    
    public int getPositionCount() {
        return positionCount;
    }
    
    public void setPositionCount(int positionCount) {
        this.positionCount = positionCount;
    }
    
    public String getResponsibilities() {
        return responsibilities;
    }
    
    public void setResponsibilities(String responsibilities) {
        this.responsibilities = responsibilities;
    }
    
    public String getRequirements() {
        return requirements;
    }
    
    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }
}