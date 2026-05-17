package LoginPage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Central data persistence layer for the TA Recruitment System.
 * All data is stored in CSV files under the {@code data/} directory.
 *
 * <p>Managed data files:</p>
 * <ul>
 *   <li>{@code data/applicants.csv} — applicant records</li>
 *   <li>{@code data/modules.csv} — module/job vacancy records</li>
 *   <li>{@code data/messages.csv} — message history between MO and TAs</li>
 *   <li>{@code data/interviews.csv} — scheduled interview records</li>
 * </ul>
 *
 * <p>CSV files are created with sample data on first access if they do not exist.</p>
 */
public class MODataStore {
    private static final String DATA_DIR = "data";
    private static final String APPLICANTS_FILE = DATA_DIR + File.separator + "applicants.csv";
    private static final String MODULES_FILE = DATA_DIR + File.separator + "modules.csv";
    private static final String MESSAGES_FILE = DATA_DIR + File.separator + "messages.csv";
    private static final String INTERVIEWS_FILE = DATA_DIR + File.separator + "interviews.csv";

    // ──────────────────────────────────────────────
    //  Initialisation
    // ──────────────────────────────────────────────

    /**
     * Ensures all required CSV data files exist. If a file is missing,
     * it is created with a header row and sample data.
     */
    public static void ensureDataFilesExist() {
        try {
            Path dataDir = Paths.get(DATA_DIR);
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }

            // --- applicants.csv ---
            Path applicantsPath = Paths.get(APPLICANTS_FILE);
            if (!Files.exists(applicantsPath)) {
                Files.write(applicantsPath, List.of(
                        "applicantId,name,moduleName,course,englishLevel,completedCourses,cvFileName,status",
                        "A001,Alice,CS101,Computer Science,IELTS 7.0,\"Java; OOP\",alice_cv.pdf,Pending",
                        "A002,Bob,CS101,Software Engineering,IELTS 6.5,\"Java; Database\",bob_cv.pdf,Pending",
                        "A003,Cathy,CS202,Artificial Intelligence,IELTS 7.5,\"Python; ML\",cathy_cv.pdf,Shortlisted",
                        "A004,David,CS101,Computer Science,IELTS 7.5,\"Database; OOP\",david_cv.pdf,Pending"
                ), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
            }

            // --- modules.csv ---
            Path modulesPath = Paths.get(MODULES_FILE);
            if (!Files.exists(modulesPath)) {
                Files.write(modulesPath, List.of(
                        "moduleName,responsibilities,requirements,positions,deadline,status",
                        "CS101,Assist in lab sessions and answer student questions,Good communication skills and Java knowledge,2,2026-04-30,Pending Review",
                        "CS202,Support AI lab sessions and coursework guidance,Python and machine learning knowledge,1,2026-05-05,Pending Review"
                ), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
            }

            // --- messages.csv ---
            Path messagesPath = Paths.get(MESSAGES_FILE);
            if (!Files.exists(messagesPath)) {
                Files.write(messagesPath, List.of(
                        "timestamp,senderId,senderName,recipientId,recipientName,moduleName,content"
                ), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
            }

            // --- interviews.csv ---
            Path interviewsPath = Paths.get(INTERVIEWS_FILE);
            if (!Files.exists(interviewsPath)) {
                Files.write(interviewsPath, List.of(
                        "applicantId,applicantName,moduleName,date,time,location,status"
                ), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to initialise data files.", e);
        }
    }

    // ──────────────────────────────────────────────
    //  Applicant CRUD
    // ──────────────────────────────────────────────

    /**
     * Loads all applicant records from the CSV file.
     *
     * @return list of {@link Applicant} entities
     */
    public static List<Applicant> loadApplicants() {
        ensureDataFilesExist();
        List<Applicant> applicants = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(APPLICANTS_FILE), StandardCharsets.UTF_8)) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = parseCsvLine(line);
                if (parts.length < 8) continue;
                applicants.add(new Applicant(parts[0], parts[1], parts[2],
                        parts[3], parts[4], parts[5], parts[6], parts[7]));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load applicants.", e);
        }

        return applicants;
    }

    /**
     * Saves the full list of applicants back to the CSV file (overwrite).
     *
     * @param applicants the complete applicant list to persist
     */
    public static void saveApplicants(List<Applicant> applicants) {
        ensureDataFilesExist();
        List<String> lines = new ArrayList<>();
        lines.add("applicantId,name,moduleName,course,englishLevel,completedCourses,cvFileName,status");
        for (Applicant a : applicants) {
            lines.add(toCsv(a.getApplicantId(), a.getName(), a.getModuleName(),
                    a.getCourse(), a.getEnglishLevel(), a.getCompletedCourses(),
                    a.getCvFileName(), a.getStatus()));
        }
        writeAllLines(APPLICANTS_FILE, lines);
    }

    /**
     * Updates the status of a specific applicant (by ID + module) and persists the change.
     *
     * @param applicantId the applicant's ID
     * @param moduleName  the module the applicant applied for
     * @param newStatus   the new status (e.g. "Approved", "Rejected", "Shortlisted")
     */
    public static void updateApplicantStatus(String applicantId, String moduleName, String newStatus) {
        List<Applicant> applicants = loadApplicants();
        for (Applicant a : applicants) {
            if (a.getApplicantId().equals(applicantId) && a.getModuleName().equals(moduleName)) {
                a.setStatus(newStatus);
                break;
            }
        }
        saveApplicants(applicants);
    }

    /**
     * Returns the number of applicants with status "Approved" for a given module.
     *
     * @param moduleName the module to query
     * @return count of approved applicants
     */
    public static int getApprovedCountForModule(String moduleName) {
        return (int) loadApplicants().stream()
                .filter(a -> a.getModuleName().equals(moduleName) && "Approved".equalsIgnoreCase(a.getStatus()))
                .count();
    }

    // ──────────────────────────────────────────────
    //  Module CRUD
    // ──────────────────────────────────────────────

    /**
     * Loads all module records from the CSV file.
     *
     * @return list of {@link Module} entities
     */
    public static List<Module> loadModules() {
        ensureDataFilesExist();
        List<Module> modules = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(MODULES_FILE), StandardCharsets.UTF_8)) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = parseCsvLine(line);
                if (parts.length < 6) continue;
                modules.add(new Module(parts[0], parts[1], parts[2],
                        Integer.parseInt(parts[3]), parts[4], parts[5]));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load modules.", e);
        }

        return modules;
    }

    /**
     * Appends a new module record to the CSV file.
     *
     * @param module the module to add
     */
    public static void addModule(Module module) {
        ensureDataFilesExist();
        String line = toCsv(module.getModuleName(), module.getResponsibilities(),
                module.getRequirements(), String.valueOf(module.getPositions()),
                module.getDeadline(), module.getStatus());
        appendLine(MODULES_FILE, line);
    }

    /**
     * Returns the position limit (max TAs) for a given module.
     *
     * @param moduleName the module name
     * @return the configured position limit, or 0 if not found
     */
    public static int getPositionLimitForModule(String moduleName) {
        ensureDataFilesExist();
        for (Module m : loadModules()) {
            if (m.getModuleName().equals(moduleName)) {
                return m.getPositions();
            }
        }
        return 0;
    }

    /**
     * Updates the position limit for a given module and persists the change.
     *
     * @param moduleName the module name
     * @param newLimit   the new position limit
     */
    public static void updateModulePositionLimit(String moduleName, int newLimit) {
        ensureDataFilesExist();
        List<Module> modules = loadModules();
        for (Module m : modules) {
            if (m.getModuleName().equals(moduleName)) {
                m.setPositions(newLimit);
                break;
            }
        }
        saveModules(modules);
    }

    /**
     * Updates the status of a module and persists the change.
     *
     * @param moduleName the module name
     * @param newStatus  the new status (e.g. "Approved", "Rejected: reason")
     */
    public static void updateModuleStatus(String moduleName, String newStatus) {
        ensureDataFilesExist();
        List<Module> modules = loadModules();
        for (Module m : modules) {
            if (m.getModuleName().equals(moduleName)) {
                m.setStatus(newStatus);
                break;
            }
        }
        saveModules(modules);
    }

    /**
     * Saves the full list of modules back to the CSV file (overwrite).
     *
     * @param modules the complete module list to persist
     */
    private static void saveModules(List<Module> modules) {
        List<String> lines = new ArrayList<>();
        lines.add("moduleName,responsibilities,requirements,positions,deadline,status");
        for (Module m : modules) {
            lines.add(toCsv(m.getModuleName(), m.getResponsibilities(),
                    m.getRequirements(), String.valueOf(m.getPositions()),
                    m.getDeadline(), m.getStatus()));
        }
        writeAllLines(MODULES_FILE, lines);
    }

    // ──────────────────────────────────────────────
    //  Message Persistence
    // ──────────────────────────────────────────────

    /**
     * Appends a sent message record to the messages CSV file.
     *
     * @param senderId   ID of the sender
     * @param senderName name of the sender
     * @param recipientId   ID of the recipient
     * @param recipientName name of the recipient
     * @param moduleName the related module
     * @param content    the message body
     */
    public static void saveMessage(String senderId, String senderName,
                                   String recipientId, String recipientName,
                                   String moduleName, String content) {
        ensureDataFilesExist();
        String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String line = toCsv(timestamp, senderId, senderName, recipientId, recipientName, moduleName, content);
        appendLine(MESSAGES_FILE, line);
    }

    /**
     * Loads all message records from the CSV file.
     *
     * @return list of CSV rows (each row is a String array)
     */
    public static List<String[]> loadMessages() {
        ensureDataFilesExist();
        List<String[]> messages = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(MESSAGES_FILE), StandardCharsets.UTF_8)) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = parseCsvLine(line);
                if (parts.length >= 7) messages.add(parts);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load messages.", e);
        }
        return messages;
    }

    // ──────────────────────────────────────────────
    //  Interview Persistence
    // ──────────────────────────────────────────────

    /**
     * Appends a scheduled interview record to the interviews CSV file.
     *
     * @param applicantId   the applicant's ID
     * @param applicantName the applicant's name
     * @param moduleName    the related module
     * @param date          interview date (YYYY-MM-DD)
     * @param time          interview time (HH:MM)
     * @param location      interview location
     */
    public static void saveInterview(String applicantId, String applicantName,
                                     String moduleName, String date, String time, String location) {
        ensureDataFilesExist();
        String line = toCsv(applicantId, applicantName, moduleName, date, time, location, "Scheduled");
        appendLine(INTERVIEWS_FILE, line);
    }

    /**
     * Loads all interview records from the CSV file.
     *
     * @return list of CSV rows (each row is a String array)
     */
    public static List<String[]> loadInterviews() {
        ensureDataFilesExist();
        List<String[]> interviews = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(INTERVIEWS_FILE), StandardCharsets.UTF_8)) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = parseCsvLine(line);
                if (parts.length >= 7) interviews.add(parts);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load interviews.", e);
        }
        return interviews;
    }

    // ──────────────────────────────────────────────
    //  CSV I/O Helpers
    // ──────────────────────────────────────────────

    private static void appendLine(String filePath, String line) {
        try {
            Files.write(Paths.get(filePath), List.of(line),
                    StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException("Failed to append to " + filePath, e);
        }
    }

    private static void writeAllLines(String filePath, List<String> lines) {
        try {
            Files.write(Paths.get(filePath), lines,
                    StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write " + filePath, e);
        }
    }

    /**
     * Parses a single CSV line respecting quoted fields.
     *
     * @param line the raw CSV line
     * @return array of field values
     */
    static String[] parseCsvLine(String line) {
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

    /**
     * Converts field values into a single CSV line, quoting fields that contain
     * commas or double-quotes.
     *
     * @param values the field values
     * @return a properly escaped CSV line
     */
    static String toCsv(String... values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            String v = values[i] == null ? "" : values[i];
            if (v.contains(",") || v.contains("\"")) {
                v = "\"" + v.replace("\"", "\"\"") + "\"";
            }
            sb.append(v);
            if (i < values.length - 1) sb.append(",");
        }
        return sb.toString();
    }
}
