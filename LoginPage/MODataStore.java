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

    public static void ensureDataFilesExist() {
        try {
            Path dataDir = Paths.get(DATA_DIR);
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }

            Path applicantsPath = Paths.get(APPLICANTS_FILE);
            if (!Files.exists(applicantsPath)) {
                Files.write(
                        applicantsPath,
                        List.of(
                                "applicantId,name,moduleName,course,englishLevel,completedCourses,cvFileName,status",
                                "A001,Alice,CS101,Computer Science,IELTS 7.0,\"Java; OOP\",alice_cv.pdf,Pending",
                                "A002,Bob,CS101,Software Engineering,IELTS 6.5,\"Java; Database\",bob_cv.pdf,Pending",
                                "A003,Cathy,CS202,Artificial Intelligence,IELTS 7.5,\"Python; ML\",cathy_cv.pdf,Shortlisted",
                                "A004,David,CS101,Computer Science,IELTS 7.5,\"Database; OOP\",david_cv.pdf,Pending"
                        ),
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE
                );
            }

            Path modulesPath = Paths.get(MODULES_FILE);
            if (!Files.exists(modulesPath)) {
                Files.write(
                        modulesPath,
                        List.of(
                                "moduleName,responsibilities,requirements,positions,deadline,status",
                                "CS101,Assist in lab sessions and answer student questions,Good communication skills and Java knowledge,2,2026-04-30,Pending Review"
                        ),
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE
                );
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialise data files.", e);
        }
    }

    public static List<Applicant> loadApplicants() {
        ensureDataFilesExist();
        List<Applicant> applicants = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(APPLICANTS_FILE), StandardCharsets.UTF_8)) {
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = parseCsvLine(line);
                if (parts.length < 8) {
                    continue;
                }

                applicants.add(new Applicant(
                        parts[0],
                        parts[1],
                        parts[2],
                        parts[3],
                        parts[4],
                        parts[5],
                        parts[6],
                        parts[7]
                ));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load applicants.", e);
        }

        return applicants;
    }

    public static void saveApplicants(List<Applicant> applicants) {
        ensureDataFilesExist();
        List<String> lines = new ArrayList<>();
        lines.add("applicantId,name,moduleName,course,englishLevel,completedCourses,cvFileName,status");

        for (Applicant applicant : applicants) {
            lines.add(toCsv(
                    applicant.getApplicantId(),
                    applicant.getName(),
                    applicant.getModuleName(),
                    applicant.getCourse(),
                    applicant.getEnglishLevel(),
                    applicant.getCompletedCourses(),
                    applicant.getCvFileName(),
                    applicant.getStatus()
            ));
        }

        try {
            Files.write(Paths.get(APPLICANTS_FILE), lines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save applicants.", e);
        }
    }

    public static void updateApplicantStatus(String applicantId, String moduleName, String newStatus) {
        List<Applicant> applicants = loadApplicants();
        for (Applicant applicant : applicants) {
            if (applicant.getApplicantId().equals(applicantId)
                    && applicant.getModuleName().equals(moduleName)) {
                applicant.setStatus(newStatus);
                break;
            }
        }
        saveApplicants(applicants);
    }

    public static void addModule(Module module) {
        ensureDataFilesExist();
        String line = toCsv(
                module.getModuleName(),
                module.getResponsibilities(),
                module.getRequirements(),
                String.valueOf(module.getPositions()),
                module.getDeadline(),
                module.getStatus()
        );

        try {
            Files.write(
                    Paths.get(MODULES_FILE),
                    List.of(line),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to save module.", e);
        }
    }

    private static String[] parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);

            if (ch == '"') {
                inQuotes = !inQuotes;
            } else if (ch == ',' && !inQuotes) {
                values.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        values.add(current.toString().trim());

        return values.toArray(new String[0]);
    }

    private static String toCsv(String... values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            String value = values[i] == null ? "" : values[i];
            if (value.contains(",") || value.contains("\"")) {
                value = "\"" + value.replace("\"", "\"\"") + "\"";
            }
            sb.append(value);
            if (i < values.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}