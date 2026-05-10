package LoginPage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class MOStatisticsUI extends JFrame {
    private User currentUser;
    private JTable statsTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> moduleComboBox;
    private String selectedModuleId;

    public MOStatisticsUI(User user) {
        this.currentUser = user;
        this.selectedModuleId = user.getModuleName();

        setTitle("View Statistics");
        setSize(950, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(247, 247, 247));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(0, 123, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("View Statistics");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel("View application and TA recruitment statistics");
        subLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subLabel.setForeground(Color.WHITE);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(headerLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        filterPanel.setBackground(Color.WHITE);

        filterPanel.add(new JLabel("Select Module:"));
        moduleComboBox = new JComboBox<>();
        List<Module> modules = MockDataManager.getModules();
        for (Module module : modules) {
            moduleComboBox.addItem(module.getModuleName());
        }
        if (!modules.isEmpty()) {
            moduleComboBox.setSelectedItem(selectedModuleId);
        }
        moduleComboBox.addActionListener(e -> {
            String selected = (String) moduleComboBox.getSelectedItem();
            if (selected != null) {
                selectedModuleId = selected;
                refreshStatistics();
            }
        });
        filterPanel.add(moduleComboBox);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshStatistics());
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(refreshButton);

        mainPanel.add(filterPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(247, 247, 247));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        centerPanel.add(createSummaryCards());
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(createDetailedStatsTable());

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);
        refreshStatistics();
    }

    private JPanel createSummaryCards() {
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 15, 15));
        cardsPanel.setBackground(new Color(247, 247, 247));

        cardsPanel.add(createStatCard("Total Applicants", "0", new Color(0, 123, 255)));
        cardsPanel.add(createStatCard("Shortlisted", "0", new Color(40, 167, 69)));
        cardsPanel.add(createStatCard("Rejected", "0", new Color(220, 53, 69)));
        cardsPanel.add(createStatCard("Pending Review", "0", new Color(255, 193, 7)));

        return cardsPanel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 224, 230), 1, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(108, 117, 125));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);

        return card;
    }

    private JPanel createDetailedStatsTable() {
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230), 1, true));

        String[] columnNames = {"Category", "Count", "Percentage", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        statsTable = new JTable(tableModel);
        statsTable.setRowHeight(35);
        statsTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        statsTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        statsTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        statsTable.getColumnModel().getColumn(3).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(statsTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private void refreshStatistics() {
        List<Applicant> applicants = MockDataManager.getApplicants();
        Map<String, Integer> stats = new HashMap<>();

        int total = 0;
        int shortlisted = 0;
        int rejected = 0;
        int pending = 0;

        for (Applicant app : applicants) {
            if (app.getModuleName().equals(selectedModuleId)) {
                total++;
                if (app.getStatus().equals("Shortlisted")) {
                    shortlisted++;
                } else if (app.getStatus().equals("Rejected")) {
                    rejected++;
                } else {
                    pending++;
                }
            }
        }

        tableModel.setRowCount(0);

        tableModel.addRow(new Object[]{"Total Applicants", total, "100%", "All"});
        tableModel.addRow(new Object[]{"Shortlisted", shortlisted, getPercentage(shortlisted, total), "Qualified"});
        tableModel.addRow(new Object[]{"Rejected", rejected, getPercentage(rejected, total), "Not Qualified"});
        tableModel.addRow(new Object[]{"Pending Review", pending, getPercentage(pending, total), "Under Review"});
        tableModel.addRow(new Object[]{"Average English Level", "-", "-", getAverageEnglishLevel(applicants)});

        updateSummaryCards(total, shortlisted, rejected, pending);
    }

    private String getPercentage(int value, int total) {
        if (total == 0) return "0%";
        return String.format("%.1f%%", (double)value / total * 100);
    }

    private String getAverageEnglishLevel(List<Applicant> applicants) {
        int advanced = 0;
        int intermediate = 0;
        int basic = 0;

        for (Applicant app : applicants) {
            if (app.getModuleName().equals(selectedModuleId)) {
                String level = app.getEnglishLevel().toLowerCase();
                if (level.contains("advanced")) advanced++;
                else if (level.contains("intermediate")) intermediate++;
                else if (level.contains("basic")) basic++;
            }
        }

        if (advanced > intermediate && advanced > basic) return "Advanced";
        else if (intermediate > advanced && intermediate > basic) return "Intermediate";
        else if (basic > advanced && basic > intermediate) return "Basic";
        return "Mixed";
    }

    private void updateSummaryCards(int total, int shortlisted, int rejected, int pending) {
        JPanel centerPanel = (JPanel) getContentPane().getComponent(1);
        if (centerPanel != null) {
            JPanel cardsPanel = (JPanel) centerPanel.getComponent(0);
            if (cardsPanel != null && cardsPanel.getComponentCount() >= 4) {
                ((JLabel)((JPanel)cardsPanel.getComponent(0)).getComponent(2)).setText(String.valueOf(total));
                ((JLabel)((JPanel)cardsPanel.getComponent(1)).getComponent(2)).setText(String.valueOf(shortlisted));
                ((JLabel)((JPanel)cardsPanel.getComponent(2)).getComponent(2)).setText(String.valueOf(rejected));
                ((JLabel)((JPanel)cardsPanel.getComponent(3)).getComponent(2)).setText(String.valueOf(pending));
            }
        }
    }
}
