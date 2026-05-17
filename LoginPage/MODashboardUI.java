package LoginPage;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.*;

public class MODashboardUI extends DashBoardUI {

    // Indigo color palette
    private static final Color INDIGO_50 = new Color(238, 242, 255);
    private static final Color INDIGO_100 = new Color(224, 231, 255);
    private static final Color INDIGO_200 = new Color(199, 210, 254);
    private static final Color INDIGO_500 = new Color(99, 102, 241);
    private static final Color INDIGO_600 = new Color(79, 70, 229);
    private static final Color INDIGO_700 = new Color(67, 56, 202);
    private static final Color PAGE_BG = new Color(249, 250, 251);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color CARD_SHADOW = new Color(0, 0, 0, 25);
    private static final Color TITLE_COLOR = new Color(17, 24, 39);
    private static final Color SUBTITLE_COLOR = new Color(107, 114, 128);
    private static final Color TEXT_MUTED = new Color(156, 163, 175);

    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font FONT_CARD_TITLE = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONT_CARD_DESC = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_AVATAR = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONT_MODULE = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_ACTION = new Font("Segoe UI", Font.BOLD, 12);

    private static final int CARD_RADIUS = 16;
    private static final int AVATAR_SIZE = 48;

    public MODashboardUI(User user) {
        super(user);
    }

    @Override
    protected void initializeUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(PAGE_BG);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(INDIGO_600);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        // Left side: Avatar + welcome text
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
        leftPanel.setOpaque(false);

        // Avatar circle with first letter
        String userName = currentUser.getId();
        if (userName == null || userName.isEmpty()) userName = "U";
        String firstLetter = userName.substring(0, 1).toUpperCase();

        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(INDIGO_200);
                g2d.fillOval(0, 0, AVATAR_SIZE, AVATAR_SIZE);
                g2d.setColor(INDIGO_700);
                FontMetrics fm = g2d.getFontMetrics(FONT_AVATAR);
                int x = (AVATAR_SIZE - fm.stringWidth(firstLetter)) / 2;
                int y = (AVATAR_SIZE + fm.getAscent() - fm.getDescent()) / 2;
                g2d.setFont(FONT_AVATAR);
                g2d.drawString(firstLetter, x, y);
            }
        };
        avatarPanel.setPreferredSize(new Dimension(AVATAR_SIZE, AVATAR_SIZE));
        avatarPanel.setMaximumSize(new Dimension(AVATAR_SIZE, AVATAR_SIZE));
        avatarPanel.setOpaque(false);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));

        JLabel welcomeLabel = new JLabel("Welcome back, " + userName);
        welcomeLabel.setFont(FONT_HEADER);
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String moduleText = currentUser.getModuleName();
        if (moduleText == null || moduleText.trim().isEmpty()) {
            moduleText = "Not Assigned";
        }

        JLabel moduleLabel = new JLabel("Module: " + moduleText + "  |  Role: Module Organiser");
        moduleLabel.setFont(FONT_MODULE);
        moduleLabel.setForeground(INDIGO_200);
        moduleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(welcomeLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(moduleLabel);

        leftPanel.add(avatarPanel);
        leftPanel.add(textPanel);

        headerPanel.add(leftPanel, BorderLayout.WEST);
        return headerPanel;
    }

    private JPanel createCenterPanel() {
        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setBackground(PAGE_BG);
        outerPanel.setBorder(BorderFactory.createEmptyBorder(24, 40, 40, 40));

        // 2-column grid with 3 rows
        JPanel gridPanel = new JPanel(new GridLayout(3, 2, 24, 24));
        gridPanel.setBackground(PAGE_BG);

        gridPanel.add(createFeatureCard(
                "View Applicants",
                "See all applicants for your module",
                "\uD83D\uDCCB",
                INDIGO_500,
                () -> openPanelMaximized("Applicant List", new MOApplicantListUI(currentUser))
        ));

        gridPanel.add(createFeatureCard(
                "Review Applications",
                "Update application status and shortlist candidates",
                "\uD83D\uDCDD",
                new Color(245, 158, 11),
                () -> openPanelMaximized("Review Applications", new MOReviewApplicationsUI(currentUser))
        ));

        gridPanel.add(createFeatureCard(
                "Create TA Vacancy",
                "Publish a new teaching assistant vacancy",
                "\u2795",
                new Color(16, 185, 129),
                () -> openPanelMaximized("Create TA Vacancy", new MOJobVacancyUI(currentUser))
        ));

        gridPanel.add(createFeatureCard(
                "Schedule Interviews",
                "Manage interview arrangements for selected applicants",
                "\uD83D\uDCC5",
                new Color(139, 92, 246),
                () -> openPanelMaximized("Schedule Interviews", new MOScheduleInterviewsUI(currentUser))
        ));

        gridPanel.add(createFeatureCard(
                "Message TA",
                "Send messages to teaching assistants",
                "\uD83D\uDCAC",
                new Color(236, 72, 153),
                () -> openPanelMaximized("Message TA", new MOMessageTAUI(currentUser))
        ));

        gridPanel.add(createFeatureCard(
                "View Statistics",
                "View application and TA recruitment statistics",
                "\uD83D\uDCCA",
                new Color(59, 130, 246),
                () -> openPanelMaximized("View Statistics", new MOStatisticsUI(currentUser))
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;

        outerPanel.add(gridPanel, gbc);
        return outerPanel;
    }

    private JPanel createFeatureCard(String title, String description, String emoji,
                                      Color accentColor, Runnable action) {
        RoundedPanel card = new RoundedPanel(CARD_RADIUS);
        card.setName("FeatureCard");
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // Emoji label with accent background
        JPanel emojiPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(accentColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            }
        };
        emojiPanel.setLayout(new GridBagLayout());
        emojiPanel.setMaximumSize(new Dimension(44, 44));
        emojiPanel.setPreferredSize(new Dimension(44, 44));
        emojiPanel.setMinimumSize(new Dimension(44, 44));
        emojiPanel.setOpaque(false);

        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        emojiPanel.add(emojiLabel);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setFont(FONT_CARD_TITLE);
        titleLabel.setForeground(TITLE_COLOR);

        JLabel descLabel = new JLabel("<html><div style='width:240px;'>" + description + "</div></html>");
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        descLabel.setFont(FONT_CARD_DESC);
        descLabel.setForeground(SUBTITLE_COLOR);

        JLabel actionHint = new JLabel("Click to open \u2192");
        actionHint.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionHint.setFont(FONT_ACTION);
        actionHint.setForeground(accentColor);

        card.add(emojiPanel);
        card.add(Box.createVerticalStrut(16));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(descLabel);
        card.add(Box.createVerticalGlue());
        card.add(Box.createVerticalStrut(16));
        card.add(actionHint);

        addCardInteraction(card, action);
        addCardInteraction(emojiPanel, action);
        addCardInteraction(titleLabel, action);
        addCardInteraction(descLabel, action);
        addCardInteraction(actionHint, action);

        return card;
    }

    private void addCardInteraction(Component component, Runnable action) {
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                action.run();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                Component source = e.getComponent();
                RoundedPanel card = findParentCard(source);
                if (card != null) {
                    card.setBackground(INDIGO_50);
                    card.repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                Component source = e.getComponent();
                RoundedPanel card = findParentCard(source);
                if (card != null) {
                    card.setBackground(CARD_BG);
                    card.repaint();
                }
            }
        });
    }

    private RoundedPanel findParentCard(Component component) {
        Component current = component;
        while (current != null) {
            if ("FeatureCard".equals(current.getName()) && current instanceof RoundedPanel) {
                return (RoundedPanel) current;
            }
            current = current.getParent();
        }
        return null;
    }

    /**
     * Custom rounded panel with shadow effect
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

            // Draw shadow
            g2d.setColor(CARD_SHADOW);
            g2d.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 4, radius, radius);

            // Draw card background
            g2d.setColor(getBackground());
            g2d.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, radius, radius);

            g2d.dispose();
            super.paintComponent(g);
        }
    }
}
