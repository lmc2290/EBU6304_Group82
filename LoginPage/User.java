package LoginPage;

public class User {
    private String id;
    private String role; // "Admin", "MO", or "TA"
    private String moduleName;

    public User(String id, String role) {
        this.id = id;
        this.role = role;
        this.moduleName = "";
    }

    public User(String id, String role, String moduleName) {
        this.id = id;
        this.role = role;
        this.moduleName = moduleName;
    }

    public String getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
}