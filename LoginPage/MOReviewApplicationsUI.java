package LoginPage;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Card-based approval workflow UI for reviewing applications.
 * Shows only Pending and Shortlisted applicants with quick Approve/Reject/View CV actions.
 */
public class MOReviewApplicationsUI extends JPanel {
    private final User currentUser;

    // Indigo color palette
    private static final Color INDIGO_50 = new Color(238, 242, 255);
    private static final Color INDIGO_100 = new Color(224, 231, 255);
    private static final Color INDIGO_500 = new Color(99, 102, 241);
    private static final Color INDIGO_600 = new Color(79, 70, 229);
    private static final Color INDIGO_700 = new Color(67, 56, 202);
    private static final Color PAGE_BG = new Color(249, 250, 251);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color CARD_SHADOW = new Color(0, 0, 0, 20);
    private static final Color TITLE_COLOR = new Color(17, 24, 39);
    private static final Color SUBTITLE_COLOR = new Color(107, 114, 128);
    private static final Color SUCCESS_COLOR = new Color(16, 185, 129);
    private static final Color SUCCESS_LIGHT = new Color(209, 250, 229);
    private static final Color WARNING_COLOR = new Color(245, 158, 11);
    private static final Color WARNING_LIGHT = new Color(254, 243, 199);
    private static final Color DANGER_COLOR = new Color(239, 68, 68);
    private static final Color DANGER_LIGHT = new Color(254, 226, 226);
    private static final Color INFO_COLOR = new Color(59, 130, 246);

    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_STATS = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_CARD_TITLE = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FONT_CARD_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BADGE = new Font("Segoe UI", Font.BOLD, 11);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 12);

    private JLabel pendingCountLabel;
    private JLabel shortlistedCountLabel;
    private JLabel approvedCountLabel;
    private JPanel cardsContainer;

    public MOReviewApplicationsUI(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(PAGE_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        initializeUI();
    }

    private void initializeUI() {
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createStatsBar(), BorderLayout.CENTER);
        add(createCardsScrollPane(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PAGE_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        JLabel titleLabel = new JLabel("Review Applications");
        titleLabel.setFont(FONT_HEADER);
        titleLabel.setForeground(TITLE_COLOR);

        String moduleText = currentUser.getModuleName();
        if (moduleText == null || moduleText.trim().isEmpty()) {
            moduleText = "All Modules";
        }
        JLabel subtitleLabel = new JLabel("Module: " + moduleText + " - Approve or reject pending and shortlisted applicants");
        subtitleLabel.setFont(FONT_CARD_BODY);
        subtitleLabel.setForeground(SUBTITLE_COLOR);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(subtitleLabel);

        panel.add(textPanel, BorderLayout.WEST);
        return panel;
    }

    private JPanel createStatsBar() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 16, 0));
        panel.setBackground(PAGE_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        panel.setPreferredSize(new Dimension(800, 70));

        pendingCountLabel = new JLabel("0", SwingConstants.CENTER);
        shortlistedCountLabel = new JLabel("0", SwingConstants.CENTER);
        approvedCountLabel = new JLabel("0", SwingConstants.CENTER);

        panel.add(createStatCard("\u23F3 Pending", pendingCountLabel, WARNING_COLOR, WARNING_LIGHT));
        panel.add(createStatCard("\u2B50 Shortlisted", shortlistedCountLabel, INFO_COLOR, new Color(219, 234, 254)));
        panel.add(createStatCard("\u2705 Approved", approvedCountLabel, SUCCESS_COLOR, SUCCESS_LIGHT));

        refreshStats();
        return panel;
    }

    private JPanel createStatCard(String title, JLabel countLabel, Color accentColor, Color bgColor) {
        RoundedPanel card = new RoundedPanel(12);
        card.setLayout(new BorderLayout());
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_STATS);
        titleLabel.setForeground(accentColor.darker());

        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        countLabel.setForeground(accentColor);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(countLabel, BorderLayout.CENTER);
        return card;
    }

    private JScrollPane createCardsScrollPane() {
        cardsContainer = new JPanel();
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setBackground(PAGE_BG);
        cardsContainer.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        refreshCards();

        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(PAGE_BG);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }

    private void refreshStats() {
        String moId = currentUser.getMoId();
        List<String[]> applicants;
        if (moId != null && !moId.trim().isEmpty()) {
            applicants = UnifiedDataStore.getApplicationsByMoId(moId);
        } else {
            applicants = UnifiedDataStore.getAllApplicants();
        }

        int pending = 0, shortlisted = 0, approved = 0;
        for (String[] a : applicants) {
            if (a.length < 8) continue;
            String status = a[7];
            if ("Pending".equalsIgnoreCase(status)) pending++;
            else if ("Shortlisted".equalsIgnoreCase(status)) shortlisted++;
            else if ("Approved".equalsIgnoreCase(status)) approved++;
        }

        pendingCountLabel.setText(String.valueOf(pending));
        shortlistedCountLabel.setText(String.valueOf(shortlisted));
        approvedCountLabel.setText(String.valueOf(approved));
    }

    private void refreshCards() {
        cardsContainer.removeAll();

        String moId = currentUser.getMoId();
        List<String[]> applicants;
        if (moId != null && !moId.trim().isEmpty()) {
            applicants = UnifiedDataStore.getApplicationsByMoId(moId);
        } else {
            applicants = UnifiedDataStore.getAllApplicants();
        }

        List<String[]> reviewable = new ArrayList<>();
        for (String[] a : applicants) {
            if (a.length < 8) continue;
            String status = a[7];
            if ("Pending".equalsIgnoreCase(status) || "Shortlisted".equalsIgnoreCase(status)) {
                reviewable.add(a);
            }
        }

        if (reviewable.isEmpty()) {
            JLabel emptyLabel = new JLabel("No pending or shortlisted applications to review.");
            emptyLabel.setFont(FONT_CARD_BODY);
            emptyLabel.setForeground(SUBTITLE_COLOR);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            cardsContainer.add(Box.createVerticalGlue());
            cardsContainer.add(emptyLabel);
            cardsContainer.add(Box.createVerticalGlue());
        } else {
            for (String[] a : reviewable) {
                cardsContainer.add(createApplicantCard(a));
                cardsContainer.add(Box.createVerticalStrut(12));
            }
        }

        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    private JPanel createApplicantCard(String[] applicant) {
        RoundedPanel card = new RoundedPanel(12);
        card.setLayout(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        // Left side: applicant info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        String appId = applicant[0];
        String taId = applicant[1];
        String taName = applicant[2];
        String moduleCode = applicant[3];
        String moduleName = applicant[4];
        String status = applicant[7];

        // Name + status badge row
        JPanel nameRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        nameRow.setOpaque(false);

        JLabel nameLabel = new JLabel(taName);
        nameLabel.setFont(FONT_CARD_TITLE);
        nameLabel.setForeground(TITLE_COLOR);

        JLabel badgeLabel = new JLabel(status);
        badgeLabel.setFont(FONT_BADGE);
        badgeLabel.setOpaque(true);
        badgeLabel.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));

        if ("Pending".equalsIgnoreCase(status)) {
            badgeLabel.setBackground(WARNING_LIGHT);
            badgeLabel.setForeground(new Color(180, 83, 9));
        } else {
            badgeLabel.setBackground(new Color(219, 234, 254));
            badgeLabel.setForeground(new Color(30, 64, 175));
        }

        nameRow.add(nameLabel);
        nameRow.add(badgeLabel);

        JLabel detailsLabel = new JLabel("ID: " + taId + "  |  Application: " + appId + "  |  Module: " + moduleCode + " - " + moduleName);
        detailsLabel.setFont(FONT_CARD_BODY);
        detailsLabel.setForeground(SUBTITLE_COLOR);

        infoPanel.add(nameRow);
        infoPanel.add(Box.createVerticalStrut(6));
        infoPanel.add(detailsLabel);

        // Right side: action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setOpaque(false);

        JButton approveBtn = createActionButton("\u2705 Approve", SUCCESS_COLOR, new Color(6, 95, 70));
        approveBtn.addActionListener(e -> {
            int approvedCount = UnifiedDataStore.getApprovedCountByModule(moduleCode);
            int positionLimit = UnifiedDataStore.getModulePositionLimit(moduleCode);
            if (approvedCount >= positionLimit) {
                JOptionPane.showMessageDialog(this,
                        "No more positions available for module " + moduleCode + ".\n"
                                + "Approved: " + approvedCount + " / Limit: " + positionLimit,
                        "Approval Limit Reached", JOptionPane.WARNING_MESSAGE);
                return;
            }
            UnifiedDataStore.updateApplicantStatus(appId, moduleCode, "Approved", currentUser.getId());
            refreshCards();
            refreshStats();
            JOptionPane.showMessageDialog(this, "Applicant approved successfully!");
        });

        JButton rejectBtn = createActionButton("\u274C Reject", DANGER_COLOR, new Color(185, 28, 28));
        rejectBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to reject " + taName + "?",
                    "Confirm Rejection", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                UnifiedDataStore.updateApplicantStatus(appId, moduleCode, "Rejected", currentUser.getId());
                refreshCards();
                refreshStats();
                JOptionPane.showMessageDialog(this, "Applicant rejected.");
            }
        });

        JButton viewCvBtn = createActionButton("\uD83D\uDCC4 View CV", INDIGO_600, INDIGO_700);
        viewCvBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Application ID: " + appId
                            + "\nTA ID: " + taId
                            + "\nTA Name: " + taName
                            + "\nModule Code: " + moduleCode
                            + "\nModule Name: " + moduleName
                            + "\nStatus: " + status,
                    "Application Details", JOptionPane.INFORMATION_MESSAGE);
        });

        buttonPanel.add(viewCvBtn);
        buttonPanel.add(approveBtn);
        buttonPanel.add(rejectBtn);

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.EAST);
        return card;
    }

    private JButton createActionButton(String text, Color bgColor, Color hoverColor) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(FONT_BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });

        return btn;
    }

    /**
     * Custom rounded panel with subtle shadow
     */
    static class RoundedPanel extends JPanel {
        private final int radius;

        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(CARD_SHADOW);
            g2d.fillRoundRect(1, 2, getWidth() - 2, getHeight() - 2, radius, radius);
            g2d.setColor(getBackground());
            g2d.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, radius, radius);
            g2d.dispose();
            super.paintComponent(g);
        }
    }
}
