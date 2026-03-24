package LoginPage;

/**
 * Entity Class
 * Represents the core data object for a system user.
 */
public class User {
    private String id;
    private String role; // "Admin", "MO", or "TA"

    public User(String id, String role) {
        this.id = id;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}