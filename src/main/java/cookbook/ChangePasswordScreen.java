package cookbook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChangePasswordScreen {

    public static void showScreen(JFrame parentFrame) {
        JDialog dialog = new JDialog(parentFrame, true);
        dialog.setUndecorated(true);
        dialog.setSize(390, 844);
        dialog.setLocationRelativeTo(parentFrame);

        Color darkGreen = new Color(14, 71, 17);

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    Image bg = new ImageIcon("image_66697d.png").getImage();
                    g.drawImage(bg, 0, 0, 390, 844, this);
                } catch (Exception e) {}
            }
        };
        backgroundPanel.setLayout(null);
        dialog.setContentPane(backgroundPanel);

        JPanel topBar = new JPanel(null);
        topBar.setBounds(0, 0, 390, 100);
        topBar.setBackground(darkGreen);
        backgroundPanel.add(topBar);

        JLabel backBtn = new JLabel("〈 Back");
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        backBtn.setForeground(Color.WHITE);
        backBtn.setBounds(20, 50, 80, 30);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { dialog.dispose(); }
        });
        topBar.add(backBtn);

        JLabel title = new JLabel("Change Password", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        title.setBounds(0, 50, 390, 30);
        topBar.add(title);

        MainMenu.RoundPanel card = new MainMenu.RoundPanel();
        card.setBounds(45, 170, 300, 270);
        card.setLayout(null);
        backgroundPanel.add(card);

        addPasswordField(card, "Current Password", 20);
        addPasswordField(card, "New Password", 90);
        addPasswordField(card, "Confirm New Password", 160);

        MainMenu.AnimatedButton cancelBtn = new MainMenu.AnimatedButton("Cancel", false);
        cancelBtn.setBounds(40, 225, 100, 30);
        cancelBtn.addActionListener(e -> dialog.dispose());
        card.add(cancelBtn);

        MainMenu.AnimatedButton saveBtn = new MainMenu.AnimatedButton("Save", true);
        saveBtn.setBounds(160, 225, 100, 30);
        saveBtn.addActionListener(e -> dialog.dispose()); 
        card.add(saveBtn);

        dialog.setVisible(true);
    }

    private static void addPasswordField(JPanel panel, String labelText, int yPos) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setBounds(25, yPos, 200, 15);
        panel.add(label);

        JPanel fieldContainer = new JPanel(null) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(Color.WHITE); g.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                g.setColor(new Color(14, 71, 17)); g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            }
        };
        fieldContainer.setOpaque(false);
        fieldContainer.setBounds(25, yPos + 15, 250, 35);
        
        JPasswordField passField = new JPasswordField("************");
        passField.setBounds(10, 0, 200, 35);
        passField.setBorder(null);
        passField.setOpaque(false);
        fieldContainer.add(passField);

        JLabel eyeIcon = new JLabel("👁", SwingConstants.CENTER);
        eyeIcon.setBounds(215, 0, 35, 35);
        eyeIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        fieldContainer.add(eyeIcon);

        panel.add(fieldContainer);
    }
}