import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RecruitmentLoginSystem extends JFrame {

    // Define UI components
    private JTextField idField;
    private JPasswordField passwordField;
    private JButton loginBtn;

    public RecruitmentLoginSystem() {
        // --- 1. Basic window settings ---
        setTitle("International School Teaching Assistant Recruitment System");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window on screen
        setLayout(new GridBagLayout()); // Use GridBagLayout to simulate HTML centering effect
        getContentPane().setBackground(new Color(247, 247, 247)); // Light gray background

        // --- 2. Create main panel (simulating HTML .container) ---
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));

        // --- 3. Welcome message ---
        JLabel welcomeLabel = new JLabel("Welcome, User");
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // --- 4. ID input field ---
        JLabel idLabel = new JLabel("ID");
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        idField = new JTextField();
        idField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        // Simulate placeholder (Swing doesn't support it natively, using ToolTip as a fallback)
        idField.setToolTipText("Student/Staff ID");

        // --- 5. Password input field ---
        JLabel pwdLabel = new JLabel("Password");
        pwdLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        // --- 6. Login button ---
        loginBtn = new JButton("Login");
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        loginBtn.setBackground(new Color(0, 102, 204));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 16));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- 7. Bottom contact admin link ---
        JLabel contactAdmin = new JLabel("Contact admin");
        contactAdmin.setForeground(new Color(0, 102, 204));
        contactAdmin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        contactAdmin.setAlignmentX(Component.RIGHT_ALIGNMENT);

        // --- 8. Assemble the panel ---
        container.add(welcomeLabel);
        container.add(idLabel);
        container.add(Box.createRigidArea(new Dimension(0, 5)));
        container.add(idField);
        container.add(Box.createRigidArea(new Dimension(0, 20)));
        container.add(pwdLabel);
        container.add(Box.createRigidArea(new Dimension(0, 5)));
        container.add(passwordField);
        container.add(Box.createRigidArea(new Dimension(0, 20)));
        container.add(loginBtn);
        container.add(Box.createRigidArea(new Dimension(0, 15)));
        container.add(contactAdmin);

        add(container);

        // --- 9. Login logic (corresponding to the script in HTML) ---
        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
    }

    private void handleLogin() {
        String idStr = idField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Null/Empty check
        if (idStr.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both ID and Password!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int idNum = Integer.parseInt(idStr);

            // Identity judgment logic
            if (idNum == 0) {
                JOptionPane.showMessageDialog(this, "Admin identity detected, will redirect to admin page");
            } else if (idNum >= 1 && idNum <= 100) {
                JOptionPane.showMessageDialog(this, "MO identity detected, will redirect to MO page");
            } else {
                JOptionPane.showMessageDialog(this, "TA (Student) identity detected, will redirect to TA page");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID must be a number!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Run the Swing program on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            new RecruitmentLoginSystem().setVisible(true);
        });
    }
}
