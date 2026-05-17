package LoginPage;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Review Applications UI - Distinct from Applicant List.
 * Focuses on the approval workflow: pending/shortlisted applications
 * presented as actionable cards with quick-approve/reject buttons.
 */
public class MOReviewApplicationsUI extends JPanel {

    private final User currentUser;

    // Color palette (consistent with MODashboardUI)
    private static final Color BG = new Color(241, 245, 249);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color SUCCESS = new Color(16, 185, 129);
    private static final Color WARNING = new Color(245, 158, 11);
    private static final Color DANGER = new Color(239, 68, 68);
    private static final Color INFO = new Color(59, 130, 246);
    private static final Color PRIMARY = new Color(79, 70, 229);
    private static final Color BORDER = new Color(226, 232, 240);

    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font STAT_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font CARD_NAME_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font CARD_DETAIL_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font BTN_FONT = new Font("Segoe UI", Font.BOLD, 12);

    private JPanel cardsContainer;
    private JLabel pendingCountLabel;
    private JLabel shortlistedCountLabel;
    private JLabel approvedCountLabel;

    public MOReviewApplicationsUI(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(10, 10));
        setBackground(BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        initializeUI();
    }

    private void initializeUI() {
        // Combine header + stats into NORTH so they don't take up stretchable space
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BG);
        topPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        topPanel.add(createStatsBar(), BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(createScrollableCardsPanel(), BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(16, 24, 16, 24)
        ));

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(CARD_BG);

        JLabel titleLabel = new JLabel("\u270F\uFE0F  Review Applications");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(TEXT_PRIMARY);

        String moduleText = currentUser.getModuleName();
        if (moduleText == null || moduleText.trim().isEmpty()) {
            moduleText = "Not Assigned";
        }
        JLabel moduleLabel = new JLabel("Module: " + moduleText);
        moduleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        moduleLabel.setForeground(TEXT_SECONDARY);
        moduleLabel.setBorder(new EmptyBorder(4, 0, 0, 0));

        leftPanel.add(titleLabel, BorderLayout.NORTH);
        leftPanel.add(moduleLabel, BorderLayout.CENTER);

        JButton refreshBtn = createStyledButton("Refresh", PRIMARY);
        refreshBtn.addActionListener(e -> refreshCards());
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setBackground(CARD_BG);
        rightPanel.add(refreshBtn);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        return panel;
    }

    private JPanel createStatsBar() {
        JPanel bar = new JPanel(new GridLayout(1, 3, 16, 0));
        bar.setBackground(BG);
        bar.setBorder(new EmptyBorder(16, 0, 8, 0));

        bar.add(createStatCard("Pending", WARNING, "0"));
        bar.add(createStatCard("Shortlisted", INFO, "0"));
        bar.add(createStatCard("Approved", SUCCESS, "0"));

        return bar;
    }

    private JPanel createStatCard(String label, Color color, String initialCount) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, color),
                new EmptyBorder(12, 20, 12, 20)
        ));

        JLabel countLabel = new JLabel(initialCount);
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        countLabel.setForeground(color);
        card.add(countLabel, BorderLayout.WEST);

        JLabel nameLabel = new JLabel(label);
        nameLabel.setFont(STAT_FONT);
        nameLabel.setForeground(TEXT_SECONDARY);
        card.add(nameLabel, BorderLayout.EAST);

        // Store reference for updating
        if ("Pending".equals(label)) pendingCountLabel = countLabel;
        else if ("Shortlisted".equals(label)) shortlistedCountLabel = countLabel;
        else if ("Approved".equals(label)) approvedCountLabel = countLabel;

        return card;
    }

    private JScrollPane createScrollableCardsPanel() {
        cardsContainer = new JPanel();
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setBackground(BG);

        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BG);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Load cards
        refreshCards();

        return scrollPane;
    }

    private void refreshCards() {
        cardsContainer.removeAll();

        List<Applicant> applicants = MODataStore.loadApplicants();
        String moduleName = currentUser.getModuleName();

        int pendingCount = 0, shortlistedCount = 0, approvedCount = 0;

        // Filter: only show Pending and Shortlisted for review
        List<Applicant> reviewable = new ArrayList<>();
        for (Applicant a : applicants) {
            if (moduleName == null || moduleName.trim().isEmpty() || a.getModuleName().equals(moduleName)) {
                if ("Pending".equals(a.getStatus()) || "Shortlisted".equals(a.getStatus())) {
                    reviewable.add(a);
                }
                if ("Pending".equals(a.getStatus())) pendingCount++;
                else if ("Shortlisted".equals(a.getStatus())) shortlistedCount++;
                else if ("Approved".equals(a.getStatus())) approvedCount++;
            }
        }

        // Update stats
        if (pendingCountLabel != null) pendingCountLabel.setText(String.valueOf(pendingCount));
        if (shortlistedCountLabel != null) shortlistedCountLabel.setText(String.valueOf(shortlistedCount));
        if (approvedCountLabel != null) approvedCountLabel.setText(String.valueOf(approvedCount));

        if (reviewable.isEmpty()) {
            JLabel emptyLabel = new JLabel("\u2705  All applications have been reviewed!");
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            emptyLabel.setForeground(TEXT_SECONDARY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyLabel.setBorder(new EmptyBorder(40, 0, 40, 0));
            cardsContainer.add(emptyLabel);
        } else {
            for (Applicant applicant : reviewable) {
                cardsContainer.add(createApplicantCard(applicant));
                cardsContainer.add(Box.createVerticalStrut(12));
            }
        }

        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    private JPanel createApplicantCard(Applicant applicant) {
        JPanel card = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fillRoundRect(2, 3, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 12, 12);
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Left: Avatar + info
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);

        // Status indicator + Name
        JPanel nameRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        nameRow.setOpaque(false);

        Color statusColor = "Pending".equals(applicant.getStatus()) ? WARNING : INFO;
        JLabel statusDot = new JLabel("\u25CF");
        statusDot.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusDot.setForeground(statusColor);
        nameRow.add(statusDot);

        JLabel nameLabel = new JLabel(applicant.getName() + "  (" + applicant.getApplicantId() + ")");
        nameLabel.setFont(CARD_NAME_FONT);
        nameLabel.setForeground(TEXT_PRIMARY);
        nameRow.add(nameLabel);

        // Status badge
        String statusText = applicant.getStatus();
        JLabel badge = createStatusBadge(statusText, statusColor);
        nameRow.add(badge);

        // Detail row
        JPanel detailRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        detailRow.setOpaque(false);

        JLabel courseLabel = new JLabel("Course: " + applicant.getCourse());
        courseLabel.setFont(CARD_DETAIL_FONT);
        courseLabel.setForeground(TEXT_SECONDARY);
        detailRow.add(courseLabel);

        detailRow.add(Box.createHorizontalStrut(20));

        JLabel englishLabel = new JLabel("English: " + applicant.getEnglishLevel());
        englishLabel.setFont(CARD_DETAIL_FONT);
        englishLabel.setForeground(TEXT_SECONDARY);
        detailRow.add(englishLabel);

        detailRow.add(Box.createHorizontalStrut(20));

        JLabel skillsLabel = new JLabel("Skills: Communication, Teamwork, Subject Knowledge");
        skillsLabel.setFont(CARD_DETAIL_FONT);
        skillsLabel.setForeground(TEXT_SECONDARY);
        detailRow.add(skillsLabel);

        infoPanel.add(nameRow, BorderLayout.NORTH);
        infoPanel.add(detailRow, BorderLayout.CENTER);

        // Right: Action buttons
        JPanel actionPanel = new JPanel(new GridLayout(2, 1, 8, 8));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(new EmptyBorder(0, 0, 0, 10));

        JButton approveBtn = createStyledButton("\u2713  Approve", SUCCESS);
        approveBtn.addActionListener(e -> {
            String applicantId = applicant.getApplicantId();
            MODataStore.updateApplicantStatus(applicantId, currentUser.getModuleName(), "Approved");
            refreshCards();
        });

        JButton rejectBtn = createStyledButton("\u2717  Reject", DANGER);
        rejectBtn.addActionListener(e -> {
            String applicantId = applicant.getApplicantId();
            MODataStore.updateApplicantStatus(applicantId, currentUser.getModuleName(), "Rejected");
            refreshCards();
        });

        // Add View CV button
        JButton cvBtn = createStyledButton("\uD83D\uDCC4  View CV", PRIMARY);
        cvBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Name: " + applicant.getName()
                            + "\nCourse: " + applicant.getCourse()
                            + "\nEnglish Level: " + applicant.getEnglishLevel()
                            + "\nCompleted Courses: " + applicant.getCompletedCourses()
                            + "\nStatus: " + applicant.getStatus()
                            + "\nSkills: Communication, Teamwork, Subject Knowledge"
                            + "\nExperience: Tutoring / Lab Support",
                    "CV Details - " + applicant.getName(),
                    JOptionPane.INFORMATION_MESSAGE);
        });

        actionPanel.add(approveBtn);
        actionPanel.add(rejectBtn);
        actionPanel.add(cvBtn);

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(actionPanel, BorderLayout.EAST);

        return card;
    }

    private JLabel createStatusBadge(String status, Color color) {
        JLabel badge = new JLabel("  " + status + "  ");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(Color.WHITE);
        badge.setOpaque(true);
        badge.setBackground(color);
        badge.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        return badge;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(BTN_FONT);
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 30));
        return btn;
    }
}
