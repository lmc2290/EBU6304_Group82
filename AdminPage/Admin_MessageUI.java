package AdminPage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Admin_MessageUI extends JPanel {
    private JTextArea messageArea;

    public Admin_MessageUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("Send Message");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        messageArea = new JTextArea();
        messageArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        messageArea.setLineWrap(true);
        JScrollPane scroll = new JScrollPane(messageArea);
        scroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        add(scroll, BorderLayout.CENTER);

        JButton sendBtn = new JButton("Send Message");
        sendBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sendBtn.setBackground(new Color(41, 128, 185));
        sendBtn.setForeground(Color.WHITE);
        sendBtn.setFocusPainted(false);
        sendBtn.addActionListener(e -> {
            String content = messageArea.getText().trim();
            if (content.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a message!");
                return;
            }
            JOptionPane.showMessageDialog(this, "Message sent successfully!");
            messageArea.setText("");
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(sendBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}