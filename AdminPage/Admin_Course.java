package AdminPage;

public class Admin_Course {
    private String moduleId;
    private String moduleName;
    private String organiser;
    private String content;
    private String status;

    public Admin_Course(String moduleId, String moduleName, String organiser, String content, String status) {
        this.moduleId = moduleId;
        this.moduleName = moduleName;
        this.organiser = organiser;
        this.content = content;
        this.status = status;
    }

    public String getModuleId() {
        return moduleId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getOrganiser() {
        return organiser;
    }

    public String getContent() {
        return content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}