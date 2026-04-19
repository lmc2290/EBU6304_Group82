package AdminPage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Admin_MessageUI extends JPanel {
    private JTextArea messageArea;
    private JComboBox<String> scopeCombo;
    private JComboBox<String> userCombo;

    // Mock user list —— you can later load from CSV
    private final Map<String, String> userRoles = new HashMap<>();

    public Admin_MessageUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // Prepare mock users
        initMockUsers();

        // Title
        JLabel title = new JLabel("Send Message");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));

        // Scope selection: All / All TA / All MO / Specific user
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topPanel.setBackground(new Color(245, 247, 250));

        JLabel scopeLabel = new JLabel("Send to:");
        scopeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        String[] scopes = {
                "All Users",
                "All TAs",
                "All MOs",
                "Specific User"
        };
        scopeCombo = new JComboBox<>(scopes);
        scopeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        scopeCombo.setPreferredSize(new Dimension(160, 30));

        // User selection (for specific user)
        userCombo = new JComboBox<>();
        userCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userCombo.setPreferredSize(new Dimension(200, 30));
        loadAllUsers();

        topPanel.add(scopeLabel);
        topPanel.add(scopeCombo);
        topPanel.add(userCombo);

        // Listener: show/hide userCombo based on selection
        scopeCombo.addActionListener(e -> {
            String selected = (String) scopeCombo.getSelectedItem();
            boolean isSpecific = "Specific User".equals(selected);
            userCombo.setEnabled(isSpecific);

            // Auto-filter user list
            userCombo.removeAllItems();
            if ("All TAs".equals(selected)) {
                loadTAs();
            } else if ("All MOs".equals(selected)) {
                loadMOs();
            } else if ("Specific User".equals(selected)) {
                loadAllUsers();
            }
        });

        // Message area
        messageArea = new JTextArea();
        messageArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        messageArea.setLineWrap(true);
        JScrollPane scroll = new JScrollPane(messageArea);
        scroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        // Send button
        JButton sendBtn = new JButton("Send Message");
        sendBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sendBtn.setBackground(new Color(41, 128, 185));
        sendBtn.setForeground(Color.WHITE);
        sendBtn.setFocusPainted(false);
        sendBtn.addActionListener(e -> sendMessage());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(245, 247, 250));
        bottomPanel.add(sendBtn);

        // Layout
        add(title, BorderLayout.NORTH);
        add(topPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void initMockUsers() {
        // TA users
        userRoles.put("Alice (TA)", "TA");
        userRoles.put("Bob (TA)", "TA");
        userRoles.put("Charlie (TA)", "TA");

        // MO users
        userRoles.put("Prof.Lee (MO)", "MO");
        userRoles.put("Dr.Chen (MO)", "MO");
        userRoles.put("Dr.Wong (MO)", "MO");
    }

    private void loadAllUsers() {
        userCombo.removeAllItems();
        userRoles.keySet().forEach(userCombo::addItem);
    }

    private void loadTAs() {
        userRoles.keySet().stream()
                .filter(user -> userRoles.get(user).equals("TA"))
                .forEach(userCombo::addItem);
    }

    private void loadMOs() {
        userRoles.keySet().stream()
                .filter(user -> userRoles.get(user).equals("MO"))
                .forEach(userCombo::addItem);
    }

    private void sendMessage() {
        String content = messageArea.getText().trim();
        if (content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter message content.");
            return;
        }

        String scope = (String) scopeCombo.getSelectedItem();
        String target;

        if ("Specific User".equals(scope)) {
            target = (String) userCombo.getSelectedItem();
        } else {
            target = scope;
        }

        JOptionPane.showMessageDialog(this,
                "Message sent successfully!\n\nTo: " + target + "\nContent:\n" + content);
        messageArea.setText("");
    }
}