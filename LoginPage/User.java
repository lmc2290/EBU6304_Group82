package LoginPage;

public class User {
    private String id;
    private String role;
    private String moduleName;
    private String moId;
    private String taId;

    public User(String id, String role) {
        this.id = id;
        this.role = role;
        this.moduleName = "";
        this.moId = "";
        this.taId = "";
    }

    public User(String id, String role, String moduleName) {
        this.id = id;
        this.role = role;
        this.moduleName = moduleName;
        this.moId = role.equals("MO") ? id : "";
        this.taId = role.equals("TA") ? id : "";
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

    public String getMoId() {
        return moId;
    }

    public String getTaId() {
        return taId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setMoId(String moId) {
        this.moId = moId;
    }

    public void setTaId(String taId) {
        this.taId = taId;
    }
}