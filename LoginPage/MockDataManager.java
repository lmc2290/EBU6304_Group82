package LoginPage;

import java.util.ArrayList;
import java.util.List;

public class MockDataManager {
    private static final List<Applicant> applicants = new ArrayList<>();
    private static final List<Module> modules = new ArrayList<>();

    static {
        applicants.add(new Applicant(
                "A001", "Alice", "CS101",
                "Computer Science", "IELTS 7.0",
                "Java, OOP", "alice_cv.pdf", "Pending"
        ));

        applicants.add(new Applicant(
                "A002", "Bob", "CS101",
                "Software Engineering", "IELTS 6.5",
                "Java, Database", "bob_cv.pdf", "Pending"
        ));

        applicants.add(new Applicant(
                "A003", "Cathy", "CS202",
                "Artificial Intelligence", "IELTS 7.5",
                "Python, ML", "cathy_cv.pdf", "Shortlisted"
        ));

        applicants.add(new Applicant(
                "A004", "David", "CS101",
                "Computer Science", "IELTS 7.5",
                "Database, OOP", "david_cv.pdf", "Pending"
        ));

        modules.add(new Module(
                "CS101",
                "Assist in lab sessions and answer student questions",
                "Good communication skills and Java knowledge",
                2,
                "2026-04-30",
                "Pending Review"
        ));
    }

    public static List<Applicant> getApplicants() {
        return applicants;
    }

    public static List<Module> getModules() {
        return modules;
    }
    public static void updateModuleStatus(String moduleName, String newStatus) {
        for (Module module : modules) {
            if (module.getModuleName().equals(moduleName)) {
                module.setStatus(newStatus);
                break;
            }
        }
    }
    public static void addModule(Module module) {
        modules.add(module);
    }
}