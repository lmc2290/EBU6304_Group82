package LoginPage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

public class EmailService {
    private String smtpHost;
    private String smtpPort;
    private String fromEmail;
    private String fromName;
    private String username;
    private String password;
    private boolean configured;

    private static Map<String, List<String>> sentEmailsLog;

    static {
        sentEmailsLog = new HashMap<>();
    }

    public EmailService() {
        this.smtpHost = "";
        this.smtpPort = "";
        this.fromEmail = "";
        this.fromName = "";
        this.username = "";
        this.password = "";
        this.configured = false;
    }

    public boolean sendEmail(String toEmail, String subject, String body) {
        if (!configured) {
            System.err.println("Email service not configured. Please configure SMTP settings first.");
            showEmailPreview(toEmail, subject, body);
            return false;
        }

        try {
            logEmail(toEmail, subject, body);
            showEmailPreview(toEmail, subject, body);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            return false;
        }
    }

    private void logEmail(String to, String subject, String body) {
        List<String> logs = sentEmailsLog.get(to);
        if (logs == null) {
            logs = new ArrayList<>();
            sentEmailsLog.put(to, logs);
        }
        logs.add("Subject: " + subject + "\nBody: " + body + "\n---");
    }

    private void showEmailPreview(String toEmail, String subject, String body) {
        String message = String.format(
            "Email Preview\n\n" +
            "To: %s\n" +
            "From: %s\n" +
            "Subject: %s\n\n" +
            "Body:\n%s\n\n" +
            "--- Email would be sent in production environment ---",
            toEmail, fromEmail.isEmpty() ? "noreply@ta-system.com" : fromEmail, subject, body
        );

        JOptionPane.showMessageDialog(null, message, "Email Preview", JOptionPane.INFORMATION_MESSAGE);
    }

    public boolean sendInterviewInvitation(String toEmail, String applicantName, String moduleName,
                                          String date, String time, String location) {
        String subject = "Interview Invitation - " + moduleName;
        String body = String.format(
            "Dear %s,\n\n" +
            "You have been shortlisted for the Teaching Assistant position in %s.\n\n" +
            "We would like to invite you to attend an interview with the Module Organiser.\n\n" +
            "Interview Details:\n" +
            "Date: %s\n" +
            "Time: %s\n" +
            "Location: %s\n\n" +
            "Please confirm your attendance by replying to this email.\n\n" +
            "Best regards,\n" +
            "Module Organiser\n" +
            "%s",
            applicantName, moduleName, date, time, location, moduleName
        );
        return sendEmail(toEmail, subject, body);
    }

    public boolean sendMeetingInvitation(String toEmail, String recipientName, String moduleName,
                                        String meetingTopic, String date, String time, String location) {
        String subject = "Meeting Invitation - " + moduleName;
        String body = String.format(
            "Dear %s,\n\n" +
            "You are invited to attend a meeting regarding the Teaching Assistant position in %s.\n\n" +
            "Meeting Details:\n" +
            "Topic: %s\n" +
            "Date: %s\n" +
            "Time: %s\n" +
            "Location: %s\n\n" +
            "Please confirm your attendance.\n\n" +
            "Best regards,\n" +
            "Module Organiser\n" +
            "%s",
            recipientName, moduleName, meetingTopic, date, time, location, moduleName
        );
        return sendEmail(toEmail, subject, body);
    }

    public boolean sendGeneralMessage(String toEmail, String recipientName, String moduleName,
                                     String message) {
        String subject = "Message from Module Organiser - " + moduleName;
        String body = String.format(
            "Dear %s,\n\n" +
            "%s\n\n" +
            "Best regards,\n" +
            "Module Organiser\n" +
            "%s",
            recipientName, message, moduleName
        );
        return sendEmail(toEmail, subject, body);
    }

    public boolean sendBulkEmails(List<String> toEmails, String subject, String body) {
        boolean allSent = true;
        for (String toEmail : toEmails) {
            if (!sendEmail(toEmail, subject, body)) {
                allSent = false;
            }
        }
        return allSent;
    }

    public void configureSMTP(String host, String port, String email, String pwd) {
        this.smtpHost = host;
        this.smtpPort = port;
        this.fromEmail = email;
        this.username = email;
        this.password = pwd;
        this.configured = true;
        System.out.println("SMTP configured: " + host + ":" + port);
    }

    public boolean isConfigured() {
        return configured;
    }

    public static Map<String, List<String>> getSentEmailsLog() {
        return sentEmailsLog;
    }

    public static void clearLogs() {
        sentEmailsLog.clear();
    }
}
