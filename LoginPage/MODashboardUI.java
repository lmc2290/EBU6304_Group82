package LoginPage;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MODashboardUI extends DashBoardUI {

    // Modern color palette
    private static final Color PRIMARY = new Color(79, 70, 229);       // Indigo
    private static final Color PRIMARY_LIGHT = new Color(129, 140, 248);
    private static final Color PRIMARY_DARK = new Color(55, 48, 163);
    private static final Color BG = new Color(241, 245, 249);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color CARD_SHADOW = new Color(203, 213, 225);
    private static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color TEXT_MUTED = new Color(148, 163, 184);
    private static final Color ACCENT_GREEN = new Color(16, 185, 129);
    private static final Color ACCENT_ORANGE = new Color(245, 158, 11);
    private static final Color ACCENT_ROSE = new Color(244, 63, 94);
    private static final Color ACCENT_CYAN = new Color(6, 182, 212);
    private static final Color ACCENT_VIOLET = new Color(139, 92, 246);

    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font CARD_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font CARD_DESC_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font BADGE_FONT = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font HINT_FONT = new Font("Segoe UI", Font.BOLD, 12);

    public MODashboardUI(User user) {
        super(user);
    }

    @Override
    protected void initializeUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG);
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_BG);
        headerPanel.setBorder(new EmptyBorder(0, 40, 0, 40));

        // Left side - Title
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(CARD_BG);
        leftPanel.setBorder(new EmptyBorder(30, 0, 30, 0));

        JLabel titleLabel = new JLabel("MO Dashboard");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_PRIMARY);

        String moduleText = currentUser.getModuleName();
        if (moduleText == null || moduleText.trim().isEmpty()) {
            moduleText = "Not Assigned";
        }

        JLabel subtitleLabel = new JLabel("Module: " + moduleText);
        subtitleLabel.setFont(SUBTITLE_FONT);
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setBorder(new EmptyBorder(4, 0, 0, 0));

        leftPanel.add(titleLabel, BorderLayout.NORTH);
        leftPanel.add(subtitleLabel, BorderLayout.CENTER);

        // Right side - User info
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setBackground(CARD_BG);
        rightPanel.setBorder(new EmptyBorder(30, 0, 30, 0));

        // Avatar circle
        JPanel avatarCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PRIMARY);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                String initials = "MO";
                int x = (getWidth() - fm.stringWidth(initials)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initials, x, y);
            }
        };
        avatarCircle.setPreferredSize(new Dimension(44, 44));
        avatarCircle.setOpaque(false);
        rightPanel.add(avatarCircle);

        JLabel userLabel = new JLabel("User: " + currentUser.getId());
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(TEXT_SECONDARY);
        rightPanel.add(userLabel);

        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        // Separator line
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(new Color(226, 232, 240));
        headerPanel.add(sep, BorderLayout.SOUTH);

        return headerPanel;
    }

    private JPanel createCenterPanel() {
        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setBackground(BG);

        // Use a 2-column layout for better visual balance
        JPanel gridPanel = new JPanel(new GridLayout(3, 2, 24, 24));
        gridPanel.setBackground(BG);
        gridPanel.setBorder(new EmptyBorder(30, 50, 50, 50));

        gridPanel.add(createFeatureCard(
                "View Applicants",
                "Browse and search all applicants for your module",
                "\uD83D\uDC65", ACCENT_CYAN,
                () -> openPanelMaximized("Applicant List", new MOApplicantListUI(currentUser))
        ));

        gridPanel.add(createFeatureCard(
                "Review Applications",
                "Approve, shortlist or reject pending applications",
                "\u270F\uFE0F", ACCENT_GREEN,
                () -> openPanelMaximized("Review Applications", new MOReviewApplicationsUI(currentUser))
        ));

        gridPanel.add(createFeatureCard(
                "Create TA Vacancy",
                "Publish a new teaching assistant job posting",
                "\uD83D\uDCCB", PRIMARY,
                () -> openPanelMaximized("Create TA Vacancy", new MOJobVacancyUI(currentUser))
        ));

        gridPanel.add(createFeatureCard(
                "Schedule Interviews",
                "Manage interview arrangements for candidates",
                "\uD83D\uDCD5", ACCENT_ORANGE,
                () -> openPanelMaximized("Schedule Interviews", new MOScheduleInterviewsUI(currentUser))
        ));

        gridPanel.add(createFeatureCard(
                "Message TA",
                "Send notifications and messages to TAs",
                "\u2709\uFE0F", ACCENT_ROSE,
                () -> openPanelMaximized("Message TA", new MOMessageTAUI(currentUser))
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

    private JPanel createFeatureCard(String title, String description, String emoji, Color accentColor, Runnable action) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Shadow
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(2, 3, getWidth() - 1, getHeight() - 1, 16, 16);
                // Card body
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 16, 16);
                super.paintComponent(g);
            }
        };
        card.setName("FeatureCard");
        card.setOpaque(false);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setBorder(new EmptyBorder(24, 24, 20, 24));

        // Top: emoji + title
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        topPanel.add(emojiLabel, BorderLayout.WEST);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(CARD_TITLE_FONT);
        titleLabel.setForeground(TEXT_PRIMARY);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        // Bottom: description + arrow hint
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        JLabel descLabel = new JLabel("<html><div style='width:260px; line-height:1.5;'>" + description + "</div></html>");
        descLabel.setFont(CARD_DESC_FONT);
        descLabel.setForeground(TEXT_SECONDARY);
        bottomPanel.add(descLabel, BorderLayout.NORTH);

        JLabel arrowLabel = new JLabel("Open \u2192");
        arrowLabel.setFont(HINT_FONT);
        arrowLabel.setForeground(accentColor);
        arrowLabel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        bottomPanel.add(arrowLabel, BorderLayout.SOUTH);

        card.add(topPanel, BorderLayout.NORTH);
        card.add(bottomPanel, BorderLayout.CENTER);

        // Accent bar on left
        JPanel accentBar = new JPanel();
        accentBar.setBackground(accentColor);
        accentBar.setPreferredSize(new Dimension(4, 0));
        accentBar.setOpaque(true);
        card.add(accentBar, BorderLayout.WEST);

        addCardInteraction(card, action);
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
                JPanel card = findParentCard(source);
                if (card != null) {
                    card.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                Component source = e.getComponent();
                JPanel card = findParentCard(source);
                if (card != null) {
                    card.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });
    }

    private JPanel findParentCard(Component component) {
        Component current = component;
        while (current != null) {
            if ("FeatureCard".equals(current.getName()) && current instanceof JPanel) {
                return (JPanel) current;
            }
            current = current.getParent();
        }
        return null;
    }

    private void openPanelMaximized(String title, Object panel) {
        if (panel instanceof JPanel) {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            JDialog dialog = new JDialog(parentWindow, title, Dialog.ModalityType.APPLICATION_MODAL);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setContentPane((JPanel) panel);
            // Maximize dialog to fill the screen
            dialog.setSize(Toolkit.getDefaultToolkit().getScreenSize());
            dialog.setLocation(0, 0);
            dialog.setVisible(true);
        } else if (panel instanceof JFrame) {
            JFrame frame = (JFrame) panel;
            frame.setTitle(title);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setLocationRelativeTo(this);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setVisible(true);
        }
    }
}
