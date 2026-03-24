package LoginPage;

import javax.swing.*;
import java.awt.*;

/**
 * Boundary Class
 * Models the interaction for user login.
 */
public class LoginUI extends JFrame {

    private static final String APP_TITLE = "International School TA Recruitment System";
    // 基础尺寸可以保留作为最小化窗口时的默认大小
    private static final int WINDOW_WIDTH = 450;
    private static final int WINDOW_HEIGHT = 400;

    private static final Color BG_COLOR = new Color(247, 247, 247);
    private static final Color PANEL_BG_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(230, 230, 230);
    private static final Color PRIMARY_COLOR = new Color(0, 102, 204);

    private static final Font TITLE_FONT = new Font("Arial", Font.PLAIN, 24);
    private static final Font BTN_FONT = new Font("Arial", Font.BOLD, 16);

    private JTextField idField;
    private JPasswordField passwordField;
    private JButton loginBtn;

    // Reference to the Control class
    private LoginController controller;

    public LoginUI(LoginController controller) {
        this.controller = controller;
        initFrameSettings();
        buildUI();
        bindEvents();
    }

    private void initFrameSettings() {
        setTitle(APP_TITLE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        // 【修改点 1】：设置窗口启动时自动全屏（最大化）
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(BG_COLOR);
    }

    private void buildUI() {
        JPanel container = createContainerPanel();

        JLabel welcomeLabel = createWelcomeLabel();
        JLabel idLabel = createFormLabel("ID");
        idField = createInputField("Student/Staff ID");
        JLabel pwdLabel = createFormLabel("Password");
        passwordField = new JPasswordField();
        setupInputStyle(passwordField);
        loginBtn = createLoginButton();
        JLabel contactAdmin = createContactAdminLabel();

        container.add(welcomeLabel);
        container.add(Box.createRigidArea(new Dimension(0, 10))); // 增加一点欢迎语下方的间距
        container.add(idLabel);
        container.add(Box.createRigidArea(new Dimension(0, 5)));
        container.add(idField);
        container.add(Box.createRigidArea(new Dimension(0, 15)));
        container.add(pwdLabel);
        container.add(Box.createRigidArea(new Dimension(0, 5)));
        container.add(passwordField);
        container.add(Box.createRigidArea(new Dimension(0, 25)));
        container.add(loginBtn);
        container.add(Box.createRigidArea(new Dimension(0, 15)));
        container.add(contactAdmin);

        add(container);
    }

    private void bindEvents() {
        // Delegate the action to the controller instead of handling it here
        loginBtn.addActionListener(e -> triggerLogin());
        passwordField.addActionListener(e -> triggerLogin());
    }

    private void triggerLogin() {
        String idStr = idField.getText().trim();
        String password = new String(passwordField.getPassword());
        // Pass data to the control class
        controller.authenticate(idStr, password);
    }

    // --- UI Helper Methods ---

    private JPanel createContainerPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL_BG_COLOR);

        // 稍微加宽一点白色面板，让居中的视觉效果更好
        panel.setPreferredSize(new Dimension(350, 400));

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
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        return label;
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        // 【修改点 2】：将 Label 设置为居中对齐
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JTextField createInputField(String tooltip) {
        JTextField field = new JTextField();
        setupInputStyle(field);
        field.setToolTipText(tooltip);
        return field;
    }

    private void setupInputStyle(JTextField field) {
        // 【修改点 3】：限制最大宽度为 200（这样才能看出居中效果），并设置为居中对齐
        field.setMaximumSize(new Dimension(200, 35));
        field.setPreferredSize(new Dimension(200, 35));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private JButton createLoginButton() {
        JButton btn = new JButton("Login");
        // 按钮宽度稍微比输入框宽一点，符合您图片的视觉效果
        btn.setMaximumSize(new Dimension(250, 45));
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

        // 如果您希望 Contact admin 也居中，改为 CENTER_ALIGNMENT；
        // 图中似乎偏左，这里暂时也给它居中，保持整体对称，或者您可以改回 RIGHT/LEFT
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    // Public methods for the Controller to update the View
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}