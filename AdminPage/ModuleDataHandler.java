package AdminPage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Backend logic for handling modules.csv data.
 * This ensures data persistence and interoperability between MO, Admin, and TA.
 */
public class ModuleDataHandler {
    private static final String FILE_PATH = "data/modules.csv";

    // Read all rows from the CSV file
    public static List<String[]> readAllModules() {
        List<String[]> data = new ArrayList<>();
        File file = new File(FILE_PATH);
        
        if (!file.exists()) return data;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHeader = true;
            while ((line = br.readLine()) != null) {
                if (isHeader) { isHeader = false; continue; } // Skip header row
                if (line.trim().isEmpty()) continue;
                data.add(line.split(","));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Updates the status of a specific module in the CSV file.
     * @param moduleName The name of the module to find.
     * @param newStatus The new status (e.g., "Approved" or "Rejected: Reason")
     */
    public static void updateModuleStatus(String moduleName, String newStatus) {
        List<String[]> allData = readAllModules();
        
        // Write the updated data back to the CSV
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            // Re-write the header first
            pw.println("moduleName,responsibilities,requirements,positions,deadline,status");
            
            for (String[] row : allData) {
                // If this is the module we want to change, update the status column (Index 5)
                if (row[0].equalsIgnoreCase(moduleName)) {
                    row[5] = newStatus;
                }
                pw.println(String.join(",", row));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}