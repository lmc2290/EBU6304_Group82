package LoginPage;

import java.util.ArrayList;
import java.util.List;

public class MockDatabase {
    private static List<Applicant> applicants = new ArrayList<>();
    private static List<Vacancy> vacancies = new ArrayList<>();

    static {
        applicants.add(new Applicant("A001", "Alice", "CS101",
                "Computer Science", "IELTS 7.0",
                "Java, OOP", "alice_cv.pdf", "Pending"));

        applicants.add(new Applicant("A002", "Bob", "CS101",
                "Software Engineering", "IELTS 6.5",
                "Java, Database", "bob_cv.pdf", "Pending"));

        applicants.add(new Applicant("A003", "Cathy", "CS202",
                "AI", "IELTS 7.5",
                "Python, ML", "cathy_cv.pdf", "Shortlisted"));
    }

    public static List<Applicant> getApplicants() {
        return applicants;
    }

    public static List<Vacancy> getVacancies() {
        return vacancies;
    }

    public static void addVacancy(Vacancy vacancy) {
        vacancies.add(vacancy);
    }
}