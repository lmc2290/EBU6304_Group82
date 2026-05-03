package TAUI;

import LoginPage.Module;

/**
 * Utility Class: Mock Email Service
 * Handles the simulation of sending automated system emails to Module Organizers.
 */
public class EmailService {
    public static void sendEmail(String moduleName, String applicantName, String jobTitle) {
        String moEmail = "mo_" + moduleName.toLowerCase() + "@qmul.ac.uk";

        System.out.println("\n=============================================");
        System.out.println(" 📧 [OUTGOING SMTP SERVER: MailGate-v1.0]");
        System.out.println("=============================================");
        System.out.println("Status: 250 OK - Message accepted for delivery");
        System.out.println("To: " + moEmail);
        System.out.println("Subject: [Urgent] New TA Application for " + moduleName);
        System.out.println("---------------------------------------------");
        System.out.println("Dear Module Organizer,");
        System.out.println("\nA new application has been submitted for your module.");
        System.out.println("Position: " + jobTitle);
        System.out.println("Applicant: " + applicantName);
        System.out.println("\nPlease log in to the TA Recruitment System to review.");
        System.out.println("Best regards,\nSystem Administrator");
        System.out.println("=============================================\n");
    }
}
