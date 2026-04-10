package LoginPage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.border.Border;
public class MODashboardUI extends DashBoardUI {

    private static final Color PAGE_BG = new Color(245, 247, 250);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color CARD_BORDER = new Color(220, 224, 230);
    private static final Color CARD_HOVER = new Color(232, 240, 254);
    private static final Color TITLE_COLOR = new Color(33, 37, 41);
    private static final Color SUBTITLE_COLOR = new Color(108, 117, 125);
    private static final Color ICON_BG = new Color(0, 123, 255);

    public MODashboardUI(User user) {
        super(user);
        initializeUI();
    }

    @Override
    protected void initializeUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(PAGE_BG);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(PAGE_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 10, 20));

        JLabel titleLabel = new JLabel("Welcome to MO Dashboard");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(TITLE_COLOR);

        String moduleText = currentUser.getModuleName();
        if (moduleText == null || moduleText.trim().isEmpty()) {
            moduleText = "Not Assigned";
        }

        JLabel subtitleLabel = new JLabel("Module: " + moduleText);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        subtitleLabel.setForeground(SUBTITLE_COLOR);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(subtitleLabel);

        return headerPanel;
    }

    private JPanel createCenterPanel() {
        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setBackground(PAGE_BG);
        outerPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 40, 40));

        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 24, 24));
        gridPanel.setBackground(PAGE_BG);

        gridPanel.add(createFeatureCard(
                "View Applicants",
                "See all applicants for your module",
                "VA",
                () -> openPanelInFrame("Applicant List", new MOApplicantListUI(currentUser))
        ));

        gridPanel.add(createFeatureCard(
                "Create TA Vacancy",
                "Publish a new teaching assistant vacancy",
                "CV",
                () -> openPanelInFrame("Create TA Vacancy", new MOJobVacancyUI(currentUser))
        ));

        gridPanel.add(createFeatureCard(
                "Review Applications",
                "Update application status and shortlist candidates",
                "RA",
                () -> openPanelInFrame("Review Applications", new MOApplicantListUI(currentUser))
        ));

        gridPanel.add(createFeatureCard(
                "Schedule Interviews",
                "Manage interview arrangements for selected applicants",
                "SI",
                () -> JOptionPane.showMessageDialog(
                        this,
                        "Interview scheduling will be added later.",
                        "Coming Soon",
                        JOptionPane.INFORMATION_MESSAGE
                )
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

    private JPanel createFeatureCard(String title, String description, String badgeText, Runnable action) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER, 1, true),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel badge = new JPanel(new GridBagLayout());
        badge.setBackground(ICON_BG);
        badge.setMaximumSize(new Dimension(52, 52));
        badge.setPreferredSize(new Dimension(52, 52));
        badge.setMinimumSize(new Dimension(52, 52));
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel badgeLabel = new JLabel(badgeText);
        badgeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        badgeLabel.setForeground(Color.WHITE);
        badge.add(badgeLabel);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(TITLE_COLOR);

        JLabel descLabel = new JLabel("<html><div style='width:220px;'>" + description + "</div></html>");
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descLabel.setForeground(SUBTITLE_COLOR);

        JLabel actionHint = new JLabel("Click to open");
        actionHint.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionHint.setFont(new Font("Arial", Font.BOLD, 13));
        actionHint.setForeground(new Color(0, 123, 255));

        card.add(badge);
        card.add(Box.createVerticalStrut(18));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(descLabel);
        card.add(Box.createVerticalGlue());
        card.add(Box.createVerticalStrut(18));
        card.add(actionHint);

        addCardInteraction(card, action);
        addCardInteraction(badge, action);
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
                JPanel card = findParentCard(source);
                if (card != null) {
                    card.setBackground(CARD_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                Component source = e.getComponent();
                JPanel card = findParentCard(source);
                if (card != null) {
                    card.setBackground(CARD_BG);
                }
            }
        });
    }

    private JPanel findParentCard(Component component) {
        Component current = component;
        while (current != null) {
            if (current instanceof JPanel) {
                JPanel panel = (JPanel) current;
                Border border = panel.getBorder();
                if (border != null && panel.getLayout() instanceof BoxLayout) {
                    return panel;
                }
            }
            current = current.getParent();
        }
        return null;
    }

    private void openPanelInFrame(String title, JPanel panel) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(panel);
        frame.setSize(1100, 650);
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
    }
}