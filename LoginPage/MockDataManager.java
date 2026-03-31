package LoginPage;

import java.util.ArrayList;
import java.util.List;

public class MockDataManager {
    private static List<Module> modules = new ArrayList<>();
    private static List<Applicant> applicants = new ArrayList<>();
    
    static {
        // Initialize mock modules
        modules.add(new Module("M001", "Programming Fundamentals", "MO001"));
        modules.add(new Module("M002", "Database Systems", "MO001"));
        modules.add(new Module("M003", "Web Development", "MO002"));
        
        // Initialize mock applicants
        List<String> courses1 = new ArrayList<>();
        courses1.add("Introduction to Computer Science");
        courses1.add("Java Programming");
        applicants.add(new Applicant("A001", "John Doe", "Computer Science", "Advanced", courses1, "John's CV: Education - BSc Computer Science, Skills - Java, Python, Experience - 2 years as TA"));
        
        List<String> courses2 = new ArrayList<>();
        courses2.add("Introduction to Computer Science");
        courses2.add("Database Design");
        applicants.add(new Applicant("A002", "Jane Smith", "Software Engineering", "Intermediate", courses2, "Jane's CV: Education - BSc Software Engineering, Skills - SQL, JavaScript, Experience - 1 year as TA"));
        
        List<String> courses3 = new ArrayList<>();
        courses3.add("Python Programming");
        courses3.add("Web Technologies");
        applicants.add(new Applicant("A003", "Bob Johnson", "Computer Science", "Advanced", courses3, "Bob's CV: Education - MSc Computer Science, Skills - Python, React, Experience - 3 years as TA"));
        
        List<String> courses4 = new ArrayList<>();
        courses4.add("Java Programming");
        courses4.add("Algorithm Design");
        applicants.add(new Applicant("A004", "Alice Brown", "Software Engineering", "Beginner", courses4, "Alice's CV: Education - BSc Software Engineering, Skills - Java, C++, Experience - 6 months as TA"));
    }
    
    public static List<Module> getModulesByOrganiser(String organiserId) {
        List<Module> result = new ArrayList<>();
        for (Module module : modules) {
            if (module.getOrganiserId().equals(organiserId)) {
                result.add(module);
            }
        }
        return result;
    }
    
    public static List<Applicant> getApplicantsByModule(String moduleId) {
        // For demo purposes, return all applicants
        // In real implementation, filter by module
        return applicants;
    }
    
    public static void addModule(Module module) {
        modules.add(module);
    }
    
    public static void updateApplicantStatus(String applicantId, String status) {
        for (Applicant applicant : applicants) {
            if (applicant.getId().equals(applicantId)) {
                applicant.setStatus(status);
                break;
            }
        }
    }
    
    public static Applicant getApplicantById(String applicantId) {
        for (Applicant applicant : applicants) {
            if (applicant.getId().equals(applicantId)) {
                return applicant;
            }
        }
        return null;
    }
    
    public static List<Module> getAllModules() {
        return modules;
    }
    
    public static void updateModuleStatus(String moduleId, String status) {
        for (Module module : modules) {
            if (module.getId().equals(moduleId)) {
                module.setStatus(status);
                break;
            }
        }
    }
}