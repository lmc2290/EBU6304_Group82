package LoginPage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class MODataStore {
    private static final String DATA_DIR = "data";
    private static final String APPLICANTS_FILE = DATA_DIR + File.separator + "applicants.csv";
    private static final String MODULES_FILE = DATA_DIR + File.separator + "modules.csv";
    private static final String APPLICANT_HEADER = "applicantId,name,moduleName,course,englishLevel,completedCourses,cvFileName,status";
    private static final String MODULE_HEADER = "moduleName,responsibilities,requirements,positions,deadline,status";

    public static void ensureDataFilesExist() {
        try {
            Path dataDir = Paths.get(DATA_DIR);
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }

            ensureFileExists(Paths.get(APPLICANTS_FILE), APPLICANT_HEADER, List.of(
                "A001,Alice,CS101,Computer Science,IELTS 7.0,\"Java; OOP\",alice_cv.pdf,Pending",
                "A002,Bob,CS101,Software Engineering,IELTS 6.5,\"Java; Database\",bob_cv.pdf,Pending",
                "A003,Cathy,CS202,Artificial Intelligence,IELTS 7.5,\"Python; ML\",cathy_cv.pdf,Shortlisted",
                "A004,David,CS101,Computer Science,IELTS 7.5,\"Database; OOP\",david_cv.pdf,Pending"
            ));

            ensureFileExists(Paths.get(MODULES_FILE), MODULE_HEADER, List.of(
                "CS101,Assist in lab sessions and answer student questions,Good communication skills and Java knowledge,2,2026-04-30,Pending Review"
            ));
        } catch (IOException e) {
            System.err.println("Critical error: Could not initialize data storage. " + e.getMessage());
        }
    }

    private static void ensureFileExists(Path path, String header, List<String> defaultData) throws IOException {
        if (!Files.exists(path)) {
            List<String> content = new ArrayList<>();
            content.add(header);
            content.addAll(defaultData);
            Files.write(path, content, StandardCharsets.UTF_8);
        }
    }

    public static List<Applicant> loadApplicants() {
        ensureDataFilesExist();
        List<Applicant> applicants = new ArrayList<>();
        Path path = Paths.get(APPLICANTS_FILE);

        if (!Files.exists(path)) return applicants;

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = parseCsvLine(line);
                if (parts.length >= 8) {
                    applicants.add(new Applicant(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7]));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading applicants: " + e.getMessage());
        }
        return applicants;
    }

    public static void saveApplicants(List<Applicant> applicants) {
        List<String> lines = new ArrayList<>();
        lines.add(APPLICANT_HEADER);

        for (Applicant a : applicants) {
            lines.add(toCsv(a.getApplicantId(), a.getName(), a.getModuleName(), a.getCourse(), 
                           a.getEnglishLevel(), a.getCompletedCourses(), a.getCvFileName(), a.getStatus()));
        }

        try {
            Files.write(Paths.get(APPLICANTS_FILE), lines, StandardCharsets.UTF_8, 
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error saving applicants: " + e.getMessage());
        }
    }

    public static void updateApplicantStatus(String applicantId, String moduleName, String newStatus) {
        List<Applicant> applicants = loadApplicants();
        boolean updated = false;
        for (Applicant a : applicants) {
            if (a.getApplicantId().equals(applicantId) && a.getModuleName().equals(moduleName)) {
                a.setStatus(newStatus);
                updated = true;
                break;
            }
        }
        if (updated) saveApplicants(applicants);
    }

    public static void addModule(Module module) {
        ensureDataFilesExist();
        String line = toCsv(module.getModuleName(), module.getResponsibilities(), module.getRequirements(),
                            String.valueOf(module.getPositions()), module.getDeadline(), module.getStatus());
        try {
            Files.write(Paths.get(MODULES_FILE), List.of(line), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Error adding module: " + e.getMessage());
        }
    }

    private static String[] parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        values.add(sb.toString().trim());
        return values.toArray(new String[0]);
    }

    private static String toCsv(String... values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            String v = (values[i] == null) ? "" : values[i];
            if (v.contains(",") || v.contains("\"") || v.contains("\n")) {
                v = "\"" + v.replace("\"", "\"\"") + "\"";
            }
            sb.append(v);
            if (i < values.length - 1) sb.append(",");
        }
        return sb.toString();
    }
}
