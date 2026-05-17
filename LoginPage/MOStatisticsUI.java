package LoginPage;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

/**
 * MOStatisticsUI - Module Organiser statistics dashboard.
 * Displays aggregated recruitment metrics using the indigo colour palette.
 */
public class MOStatisticsUI extends JPanel {

    private final User currentUser;

    // Indigo palette
    private static final Color INDIGO_PRIMARY = new Color(0x4F, 0x46, 0xE5);
    private static final Color INDIGO_LIGHT   = new Color(0xEE, 0xF2, 0xFF);
    private static final Color INDIGO_HOVER   = new Color(0xC7, 0xD2, 0xFE);
    private static final Color PAGE_BG        = new Color(0xF5, 0xF7, 0xFA);
    private static final Color CARD_BG        = Color.WHITE;
    private static final Color TITLE_COLOR    = new Color(0x1E, 0x1B, 0x4B);
    private static final Color SUBTITLE_COLOR = new Color(0x6B, 0x72, 0x80);
    private static final Color BORDER_COLOR   = new Color(0xE2, 0xE8, 0xF0);

    private static final Color SUCCESS_COLOR  = new Color(0x16, 0xA3, 0x4A);
    private static final Color WARNING_COLOR  = new Color(0xD9, 0x77, 0x06);
    private static final Color DANGER_COLOR   = new Color(0xDC, 0x26, 0x26);
    private static final Color INFO_COLOR     = new Color(0x25, 0x63, 0xEB);

    public MOStatisticsUI(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(PAGE_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initializeUI();
    }

    private void initializeUI() {
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createStatsPanel(), BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(INDIGO_PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        JLabel titleLabel = new JLabel("Statistics Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Overview of module and recruitment metrics");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(INDIGO_HOVER);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(INDIGO_PRIMARY);
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(subtitleLabel);

        header.add(textPanel, BorderLayout.WEST);
        return header;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 20, 20));
        panel.setBackground(PAGE_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Compute stats
        List<String[]> allModules = UnifiedDataStore.getAllModules();
        List<String[]> allApplicants = UnifiedDataStore.getAllApplicants();
        List<String[]> allInterviews = UnifiedDataStore.getAllInterviews();

        int totalModules = allModules.size();
        int approvedModules = (int) allModules.stream()
                .filter(m -> m.length >= 5 && "Approved".equalsIgnoreCase(m[4])).count();
        int pendingModules = (int) allModules.stream()
                .filter(m -> m.length >= 5 && "Pending".equalsIgnoreCase(m[4])).count();

        int totalApplicants = allApplicants.size();
        int approvedApplicants = (int) allApplicants.stream()
                .filter(a -> a.length >= 8 && "Approved".equalsIgnoreCase(a[7])).count();
        int pendingApplicants = (int) allApplicants.stream()
                .filter(a -> a.length >= 8 && "Pending".equalsIgnoreCase(a[7])).count();
        int shortlistedApplicants = (int) allApplicants.stream()
                .filter(a -> a.length >= 8 && "Shortlisted".equalsIgnoreCase(a[7])).count();
        int rejectedApplicants = (int) allApplicants.stream()
                .filter(a -> a.length >= 8 && "Rejected".equalsIgnoreCase(a[7])).count();

        int totalInterviews = allInterviews.size();
        int scheduledInterviews = (int) allInterviews.stream()
                .filter(i -> i.length >= 9 && "Scheduled".equalsIgnoreCase(i[8])).count();
        int completedInterviews = (int) allInterviews.stream()
                .filter(i -> i.length >= 9 && "Completed".equalsIgnoreCase(i[8])).count();

        panel.add(createStatCard("Total Modules", String.valueOf(totalModules),
                String.format("%d Approved / %d Pending", approvedModules, pendingModules), INFO_COLOR));
        panel.add(createStatCard("Total Applicants", String.valueOf(totalApplicants),
                String.format("%d Approved / %d Shortlisted", approvedApplicants, shortlistedApplicants), INDIGO_PRIMARY));
        panel.add(createStatCard("Pending Review", String.valueOf(pendingApplicants),
                String.format("%d Rejected total", rejectedApplicants), WARNING_COLOR));
        panel.add(createStatCard("Approved TAs", String.valueOf(approvedApplicants),
                "Successfully recruited", SUCCESS_COLOR));
        panel.add(createStatCard("Interviews", String.valueOf(totalInterviews),
                String.format("%d Scheduled / %d Completed", scheduledInterviews, completedInterviews), new Color(139, 92, 246)));
        panel.add(createStatCard("Work Hours (Total)", String.valueOf(computeTotalWorkHours(allModules)),
                "Across all modules", DANGER_COLOR));

        return panel;
    }

    private int computeTotalWorkHours(List<String[]> modules) {
        int total = 0;
        for (String[] m : modules) {
            if (m.length >= 11) {
                try {
                    total += Integer.parseInt(m[10]);
                } catch (NumberFormatException ignored) {}
            }
        }
        return total;
    }

    private JPanel createStatCard(String title, String value, String subtitle, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Top accent bar
        JPanel accentBar = new JPanel();
        accentBar.setBackground(accentColor);
        accentBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 4));
        accentBar.setPreferredSize(new Dimension(0, 4));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(accentColor);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(TITLE_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(SUBTITLE_COLOR);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(accentBar);
        card.add(Box.createVerticalStrut(12));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitleLabel);

        return card;
    }
}
