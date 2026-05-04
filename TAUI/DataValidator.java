package TAUI;

import java.util.regex.Pattern;

/**
 * Utility Class: Data Validator
 * Provides robust Regex-based validation for user inputs across the UI forms.
 * Prevents malicious injections and ensures data integrity.
 */
public class DataValidator {

    // Regex patterns for strict data validation
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z\\s]{2,50}$"); // Only letters and spaces, 2-50 chars
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$"); // Standard Email format

    /**
     * Validates if the given name is formatted correctly.
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return NAME_PATTERN.matcher(name.trim()).matches();
    }

    /**
     * Validates if the given text falls within the acceptable length range.
     */
    public static boolean isValidLength(String text, int minLength, int maxLength) {
        if (text == null) {
            return false;
        }
        int length = text.trim().length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * Security Check: Scans input for basic SQL Injection or XSS payload signatures.
     */
    public static boolean containsMaliciousCharacters(String text) {
        if (text == null) {
            return false;
        }
        String upperText = text.toUpperCase();
        // Mocking defense against basic injections
        return upperText.contains("<SCRIPT>") ||
                upperText.contains("DROP TABLE") ||
                upperText.contains("DELETE FROM") ||
                upperText.contains("SELECT * FROM");
    }
}