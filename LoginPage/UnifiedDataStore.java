package LoginPage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UnifiedDataStore {
    private static final String DATA_DIR = "data";
    private static final String MODULES_FILE = DATA_DIR + File.separator + "modules.csv";
    private static final String APPLICANTS_FILE = DATA_DIR + File.separator + "applicants.csv";
    private static final String INTERVIEWS_FILE = DATA_DIR + File.separator + "interviews.csv";
    private static final String MESSAGES_FILE = DATA_DIR + File.separator + "messages.csv";

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static {
        ensureDataFilesExist();
    }

    public static void ensureDataFilesExist() {
        try {
            Path dataDir = Paths.get(DATA_DIR);
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }

            if (!Files.exists(Paths.get(MODULES_FILE))) {
                Files.write(Paths.get(MODULES_FILE),
                    List.of("moduleCode,moduleName,moId,requiredTas,status,createdBy,createdAt,approvedBy,approvedAt,rejectReason"),
                    StandardCharsets.UTF_8, StandardOpenOption.CREATE);
            }

            if (!Files.exists(Paths.get(APPLICANTS_FILE))) {
                Files.write(Paths.get(APPLICANTS_FILE),
                    List.of("applicationId,taId,taName,moduleCode,moduleName,cvFile,coverLetter,status,submittedAt,reviewedBy,reviewedAt"),
                    StandardCharsets.UTF_8, StandardOpenOption.CREATE);
            }

            if (!Files.exists(Paths.get(INTERVIEWS_FILE))) {
                Files.write(Paths.get(INTERVIEWS_FILE),
                    List.of("interviewId,applicationId,taId,taName,moduleCode,moduleName,interviewTime,location,status,createdBy,createdAt"),
                    StandardCharsets.UTF_8, StandardOpenOption.CREATE);
            }

            if (!Files.exists(Paths.get(MESSAGES_FILE))) {
                Files.write(Paths.get(MESSAGES_FILE),
                    List.of("messageId,fromRole,fromId,toTaId,toTaName,moduleCode,subject,content,sentAt"),
                    StandardCharsets.UTF_8, StandardOpenOption.CREATE);
            }
        } catch (IOException e) {
            System.err.println("Failed to initialize data files: " + e.getMessage());
        }
    }

    // ==================== MODULES CSV OPERATIONS ====================

    public static void addModule(String moduleCode, String moduleName, String moId,
                                  int requiredTas, String createdBy) {
        ensureDataFilesExist();
        String timestamp = LocalDateTime.now().format(DATE_FORMAT);
        String line = String.format("%s,%s,%s,%d,Pending,%s,%s,,,",
                escapeCsv(moduleCode), escapeCsv(moduleName), escapeCsv(moId),
                requiredTas, escapeCsv(createdBy), timestamp);

        try {
            Files.write(Paths.get(MODULES_FILE), List.of(line),
                    StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Failed to add module: " + e.getMessage());
        }
    }

    public static List<String[]> getAllModules() {
        List<String[]> modules = new ArrayList<>();
        ensureDataFilesExist();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(MODULES_FILE), StandardCharsets.UTF_8)) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    modules.add(parseCsvLine(line));
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read modules: " + e.getMessage());
        }
        return modules;
    }

    public static List<String[]> getModulesByStatus(String status) {
        return getAllModules().stream()
                .filter(m -> m.length >= 5 && status.equalsIgnoreCase(m[4]))
                .collect(Collectors.toList());
    }

    public static List<String[]> getApprovedModules() {
        return getModulesByStatus("Approved");
    }

    public static List<String[]> getPendingModules() {
        return getModulesByStatus("Pending");
    }

    public static List<String[]> getModulesByMoId(String moId) {
        return getAllModules().stream()
                .filter(m -> m.length >= 3 && m[2].equalsIgnoreCase(moId))
                .collect(Collectors.toList());
    }

    public static void updateModuleStatus(String moduleCode, String newStatus,
                                          String approvedBy, String rejectReason) {
        List<String[]> modules = getAllModules();
        String timestamp = LocalDateTime.now().format(DATE_FORMAT);

        for (String[] module : modules) {
            if (module.length >= 5 && module[0].equalsIgnoreCase(moduleCode)) {
                module[4] = newStatus;
                module[7] = approvedBy;
                module[8] = timestamp;
                if (newStatus.equalsIgnoreCase("Rejected") && rejectReason != null) {
                    module[9] = rejectReason;
                }
                break;
            }
        }

        saveModules(modules);
    }

    private static void saveModules(List<String[]> modules) {
        List<String> lines = new ArrayList<>();
        lines.add("moduleCode,moduleName,moId,requiredTas,status,createdBy,createdAt,approvedBy,approvedAt,rejectReason");

        for (String[] m : modules) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < m.length; i++) {
                if (i > 0) sb.append(",");
                sb.append(escapeCsv(m[i]));
            }
            lines.add(sb.toString());
        }

        try {
            Files.write(Paths.get(MODULES_FILE), lines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Failed to save modules: " + e.getMessage());
        }
    }

    // ==================== APPLICANTS CSV OPERATIONS ====================

    public static void addApplicant(String applicationId, String taId, String taName,
                                    String moduleCode, String moduleName, String cvFile,
                                    String coverLetter) {
        ensureDataFilesExist();
        String timestamp = LocalDateTime.now().format(DATE_FORMAT);
        String line = String.format("%s,%s,%s,%s,%s,%s,%s,Pending,%s,,,",
                escapeCsv(applicationId), escapeCsv(taId), escapeCsv(taName),
                escapeCsv(moduleCode), escapeCsv(moduleName), escapeCsv(cvFile),
                escapeCsv(coverLetter), timestamp);

        try {
            Files.write(Paths.get(APPLICANTS_FILE), List.of(line),
                    StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Failed to add applicant: " + e.getMessage());
        }
    }

    public static List<String[]> getAllApplicants() {
        List<String[]> applicants = new ArrayList<>();
        ensureDataFilesExist();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(APPLICANTS_FILE), StandardCharsets.UTF_8)) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    applicants.add(parseCsvLine(line));
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read applicants: " + e.getMessage());
        }
        return applicants;
    }

    public static List<String[]> getApplicantsByModule(String moduleCode) {
        return getAllApplicants().stream()
                .filter(a -> a.length >= 4 && a[3].equalsIgnoreCase(moduleCode))
                .collect(Collectors.toList());
    }

    public static List<String[]> getApprovedApplicants() {
        return getAllApplicants().stream()
                .filter(a -> a.length >= 8 && "Approved".equalsIgnoreCase(a[7]))
                .collect(Collectors.toList());
    }

    public static List<String[]> getApplicantsByStatus(String status) {
        return getAllApplicants().stream()
                .filter(a -> a.length >= 8 && status.equalsIgnoreCase(a[7]))
                .collect(Collectors.toList());
    }

    public static List<String[]> getShortlistedApplicants() {
        return getApplicantsByStatus("Shortlisted");
    }

    public static List<String[]> getApplicationsByMoId(String moId) {
        List<String[]> moModules = getModulesByMoId(moId);
        if (moModules.isEmpty()) {
            return getAllApplicants();
        }
        Set<String> moModuleCodes = moModules.stream()
                .filter(m -> m.length >= 1)
                .map(m -> m[0].toLowerCase())
                .collect(Collectors.toSet());
        return getAllApplicants().stream()
                .filter(a -> a.length >= 4 && moModuleCodes.contains(a[3].toLowerCase()))
                .collect(Collectors.toList());
    }

    public static List<String[]> getApplicationsByTaId(String taId) {
        return getAllApplicants().stream()
                .filter(a -> a.length >= 2 && a[1].equalsIgnoreCase(taId))
                .collect(Collectors.toList());
    }

    public static void updateApplicantStatus(String applicationId, String moduleCode,
                                             String newStatus, String reviewedBy) {
        List<String[]> applicants = getAllApplicants();
        String timestamp = LocalDateTime.now().format(DATE_FORMAT);

        for (String[] applicant : applicants) {
            if (applicant.length >= 8 && applicant[0].equalsIgnoreCase(applicationId)
                    && applicant[3].equalsIgnoreCase(moduleCode)) {
                applicant[7] = newStatus;
                applicant[9] = reviewedBy;
                applicant[10] = timestamp;
                break;
            }
        }

        saveApplicants(applicants);
    }

    private static void saveApplicants(List<String[]> applicants) {
        List<String> lines = new ArrayList<>();
        lines.add("applicationId,taId,taName,moduleCode,moduleName,cvFile,coverLetter,status,submittedAt,reviewedBy,reviewedAt");

        for (String[] a : applicants) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < a.length; i++) {
                if (i > 0) sb.append(",");
                sb.append(escapeCsv(a[i]));
            }
            lines.add(sb.toString());
        }

        try {
            Files.write(Paths.get(APPLICANTS_FILE), lines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Failed to save applicants: " + e.getMessage());
        }
    }

    // ==================== INTERVIEWS CSV OPERATIONS ====================

    public static void addInterview(String applicationId, String taId, String taName,
                                    String moduleCode, String moduleName,
                                    String interviewTime, String location,
                                    String createdBy) {
        ensureDataFilesExist();
        String interviewId = "INT-" + System.currentTimeMillis();
        String timestamp = LocalDateTime.now().format(DATE_FORMAT);
        String line = String.format("%s,%s,%s,%s,%s,%s,%s,%s,Scheduled,%s,%s",
                escapeCsv(interviewId), escapeCsv(applicationId), escapeCsv(taId),
                escapeCsv(taName), escapeCsv(moduleCode), escapeCsv(moduleName),
                escapeCsv(interviewTime), escapeCsv(location),
                escapeCsv(createdBy), timestamp);

        try {
            Files.write(Paths.get(INTERVIEWS_FILE), List.of(line),
                    StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Failed to add interview: " + e.getMessage());
        }
    }

    public static List<String[]> getAllInterviews() {
        List<String[]> interviews = new ArrayList<>();
        ensureDataFilesExist();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(INTERVIEWS_FILE), StandardCharsets.UTF_8)) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    interviews.add(parseCsvLine(line));
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read interviews: " + e.getMessage());
        }
        return interviews;
    }

    // ==================== MESSAGES CSV OPERATIONS ====================

    public static void addMessage(String fromRole, String fromId, String toTaId,
                                   String toTaName, String moduleCode, String subject,
                                   String content) {
        ensureDataFilesExist();
        String messageId = "MSG-" + System.currentTimeMillis();
        String timestamp = LocalDateTime.now().format(DATE_FORMAT);
        String line = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s",
                escapeCsv(messageId), escapeCsv(fromRole), escapeCsv(fromId),
                escapeCsv(toTaId), escapeCsv(toTaName), escapeCsv(moduleCode),
                escapeCsv(subject != null ? subject : ""), escapeCsv(content), timestamp);

        try {
            Files.write(Paths.get(MESSAGES_FILE), List.of(line),
                    StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Failed to add message: " + e.getMessage());
        }
    }

    // ==================== CONVENIENCE ALIASES ====================

    public static List<String[]> loadModules() { return getAllModules(); }
    public static List<String[]> loadApplications() { return getAllApplicants(); }
    public static List<String[]> loadInterviews() { return getAllInterviews(); }

    public static List<String[]> getAllMessages() {
        List<String[]> messages = new ArrayList<>();
        ensureDataFilesExist();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(MESSAGES_FILE), StandardCharsets.UTF_8)) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    messages.add(parseCsvLine(line));
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read messages: " + e.getMessage());
        }
        return messages;
    }

    public static List<String[]> loadMessages() { return getAllMessages(); }

    // ==================== UTILITY METHODS ====================

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

    private static String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    public static int getApprovedCountByModule(String moduleCode) {
        return (int) getAllApplicants().stream()
                .filter(a -> a.length >= 8 && a[3].equalsIgnoreCase(moduleCode)
                        && "Approved".equalsIgnoreCase(a[7]))
                .count();
    }

    public static int getModulePositionLimit(String moduleCode) {
        for (String[] m : getAllModules()) {
            if (m.length >= 4 && m[0].equalsIgnoreCase(moduleCode)) {
                try {
                    return Integer.parseInt(m[3]);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        }
        return 0;
    }

    public static void updateModulePositionLimit(String moduleCode, int newLimit) {
        List<String[]> modules = getAllModules();
        for (String[] module : modules) {
            if (module.length >= 4 && module[0].equalsIgnoreCase(moduleCode)) {
                module[3] = String.valueOf(newLimit);
                break;
            }
        }
        saveModules(modules);
    }
}