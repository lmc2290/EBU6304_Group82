import javax.swing.*;
import java.awt.*;

public class RecruitmentLoginSystem extends JFrame {

    // 1. Constants Definition
    // Eliminate magic numbers/strings for easier future updates to themes and text
    private static final String APP_TITLE = "International School TA Recruitment System";
    private static final int WINDOW_WIDTH = 450;
    private static final int WINDOW_HEIGHT = 400;

    // Color configuration
    private static final Color BG_COLOR = new Color(247, 247, 247);
    private static final Color PANEL_BG_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(230, 230, 230);
    private static final Color PRIMARY_COLOR = new Color(0, 102, 204);

    // Font configuration
    private static final Font TITLE_FONT = new Font("Arial", Font.PLAIN, 24);
    private static final Font BTN_FONT = new Font("Arial", Font.BOLD, 16);

    // 2. Core UI Component Declarations
    // Only declare components that need to be read or manipulated in the business logic
    private JTextField idField;
    private JPasswordField passwordField;
    private JButton loginBtn;

    // 3. Constructor
    // Clear flow: Initialize settings -> Build UI -> Bind events
    public RecruitmentLoginSystem() {
        initFrameSettings();
        buildUI();
        bindEvents();
    }

    // 4. Initialization Methods
    /**
     * Set basic properties of the main window
     */
    private void initFrameSettings() {
        setTitle(APP_TITLE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setLayout(new GridBagLayout()); // Used to center the inner card panel
        getContentPane().setBackground(BG_COLOR);
    }

    /**
     * Build and assemble all UI components
     */
    private void buildUI() {
        // 1. Create the main card panel
        JPanel container = createContainerPanel();

        // 2. Initialize individual components
        JLabel welcomeLabel = createWelcomeLabel();
        JLabel idLabel = createFormLabel("ID");
        idField = createInputField("Student/Staff ID");
        JLabel pwdLabel = createFormLabel("Password");
        passwordField = new JPasswordField();
        setupInputStyle(passwordField); // Reuse the style settings of the text field
        loginBtn = createLoginButton();
        JLabel contactAdmin = createContactAdminLabel();

        // 3. Assemble components into the panel from top to bottom
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

        // 4. Add the panel to the window
        add(container);
    }

    /**
     * Bind interactive events such as button clicks
     */
    private void bindEvents() {
        // Use Lambda expressions to make event binding extremely concise
        loginBtn.addActionListener(e -> handleLogin());
        
        // Optional: Allow pressing the Enter key in the password field to log in
        passwordField.addActionListener(e -> handleLogin());
    }

    // 5. Business Logic
    
    /**
     * Handle login validation logic
     */
    private void handleLogin() {
        String idStr = idField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Form null/empty check
        if (idStr.isEmpty() || password.isEmpty()) {
            showError("Please enter both ID and Password!");
            return;
        }

        // Identity verification logic
        try {
            int idNum = Integer.parseInt(idStr);

            if (idNum == 0) {
                showMessage("Admin identity detected, will redirect to admin page");
            } else if (idNum >= 1 && idNum <= 100) {
                showMessage("MO identity detected, will redirect to MO page");
            } else {
                showMessage("TA (Student) identity detected, will redirect to TA page");
            }

        } catch (NumberFormatException ex) {
            showError("ID must be a valid number!");
        }
    }

    // 6. UI Helper / Factory Methods
    // Extract repetitive component setup code to keep the main flow clean
    private JPanel createContainerPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL_BG_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));
        return panel;
    }

    private JLabel createWelcomeLabel() {
        JLabel label = new JLabel("Welcome, User");
        label.setFont(TITLE_FONT);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        return label;
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField createInputField(String tooltip) {
        JTextField field = new JTextField();
        setupInputStyle(field);
        field.setToolTipText(tooltip);
        return field;
    }

    private void setupInputStyle(JTextField field) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private JButton createLoginButton() {
        JButton btn = new JButton("Login");
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setBackground(PRIMARY_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(BTN_FONT);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    private JLabel createContactAdminLabel() {
        JLabel label = new JLabel("Contact admin");
        label.setForeground(PRIMARY_COLOR);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
        return label;
    }

    // Encapsulate message dialogs to simplify code
    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // 7. Main Method Entry Point
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RecruitmentLoginSystem().setVisible(true));
    }
}
