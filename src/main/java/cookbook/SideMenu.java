package cookbook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SideMenu {

    // 🌟 PROFILE MEMORY BANK (Now defaults to Guest until LoginScreen updates it!)
    public static String firstName;
    public static String lastName;
    public static String email;

    public static void showMenu(JFrame parentFrame) {
        JDialog dialog = new JDialog(parentFrame, true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 100)); 
        dialog.setSize(390, 844);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setLayout(null);

        dialog.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getX() > 280) dialog.dispose();
            }
        });

        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(Color.WHITE);
        menuPanel.setBounds(0, 0, 280, 844);
        menuPanel.setLayout(null);
        dialog.add(menuPanel);

        Color darkGreen = new Color(14, 71, 17);

        JLabel profileLabel = new JLabel("Profile");
        profileLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        profileLabel.setForeground(Color.GRAY);
        profileLabel.setBounds(20, 50, 100, 20);
        menuPanel.add(profileLabel);

        JPanel avatar = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(darkGreen);
                g2.fillOval(0, 0, getWidth(), getHeight());
                
                // Automatically grabs the first letter of the first name
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 40));
                String initial = firstName.isEmpty() ? "" : firstName.substring(0, 1).toUpperCase();
                FontMetrics fm = g2.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(initial)) / 2;
                int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initial, textX, textY); 
            }
        };
        avatar.setOpaque(false);
        avatar.setBounds(20, 80, 70, 70);
        menuPanel.add(avatar);

        // 🌟 DISPLAYS THE DYNAMIC NAME
        JLabel nameLabel = new JLabel(firstName + " " + lastName);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        nameLabel.setBounds(105, 85, 160, 25);
        menuPanel.add(nameLabel);

        // 🌟 DISPLAYS THE DYNAMIC EMAIL PULLED FROM LOGIN SCREEN
        JLabel emailLabel = new JLabel(email);
        emailLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        emailLabel.setBounds(105, 110, 170, 15);
        menuPanel.add(emailLabel);

        JButton editProfileBtn = new JButton("Edit Profile");
        editProfileBtn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        editProfileBtn.setBackground(Color.WHITE);
        editProfileBtn.setForeground(Color.BLACK);
        editProfileBtn.setFocusPainted(false);
        editProfileBtn.setContentAreaFilled(false);
        editProfileBtn.setBounds(105, 130, 100, 25);
        editProfileBtn.setBorder(new javax.swing.border.LineBorder(Color.BLACK, 1, true));
        editProfileBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        editProfileBtn.addActionListener(e -> {
            dialog.dispose();
            showEditProfilePopup(parentFrame);
        });
        menuPanel.add(editProfileBtn);

        JSeparator separator = new JSeparator();
        separator.setForeground(Color.LIGHT_GRAY);
        separator.setBounds(0, 180, 280, 10);
        menuPanel.add(separator);

        JLabel settingsLabel = new JLabel("Settings & Support");
        settingsLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        settingsLabel.setForeground(Color.GRAY);
        settingsLabel.setBounds(20, 200, 150, 20);
        menuPanel.add(settingsLabel);

        addMenuItem(menuPanel, "🔑", "Change Password", 235, () -> {
            dialog.dispose();
            ChangePasswordScreen.showScreen(parentFrame);
        });

        addMenuItem(menuPanel, "👥", "Logout", 275, () -> {
            dialog.dispose();
            showLogoutPopup(parentFrame);
        });

        addMenuItem(menuPanel, "❓", "Help & FAQs", 315, () -> {
            dialog.dispose();
            showHelpPopup(parentFrame);
        });

        JLabel backLabel = new JLabel("⬅  Back To Home Page");
        backLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        backLabel.setBounds(20, 780, 200, 30);
        backLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { dialog.dispose(); }
        });
        menuPanel.add(backLabel);

        dialog.setVisible(true);
    }

    private static void addMenuItem(JPanel panel, String iconText, String titleText, int yPos, Runnable onClick) {
        JPanel itemPanel = new JPanel(null);
        itemPanel.setBounds(20, yPos, 250, 30);
        itemPanel.setOpaque(false);
        itemPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel icon = new JLabel(iconText, SwingConstants.CENTER);
        icon.setFont(new Font("SansSerif", Font.PLAIN, 18));
        icon.setBounds(0, 0, 30, 30);
        itemPanel.add(icon);

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setBounds(40, 0, 150, 30);
        itemPanel.add(title);

        JLabel arrow = new JLabel(">");
        arrow.setFont(new Font("SansSerif", Font.BOLD, 18));
        arrow.setBounds(220, 0, 30, 30);
        itemPanel.add(arrow);

        itemPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { onClick.run(); }
        });

        panel.add(itemPanel);
    }

    private static void showHelpPopup(JFrame parentFrame) {
        JDialog dialog = createOverlayDialog(parentFrame);

        MainMenu.RoundPanel popup = new MainMenu.RoundPanel();
        popup.setBounds(45, 320, 300, 180); 
        popup.setLayout(null);
        dialog.add(popup);

        Color darkGreen = new Color(14, 71, 17);

        JLabel title = new JLabel("Help & Support", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(darkGreen);
        title.setBounds(0, 20, 300, 25);
        popup.add(title);

        JLabel subtitle = new JLabel("<html><center>For any issues, feedback, or inquiries,<br>please contact our support team:</center></html>", SwingConstants.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(Color.DARK_GRAY);
        subtitle.setBounds(0, 55, 300, 35);
        popup.add(subtitle);

        JLabel emailLabel = new JLabel("yjac2005@gmail.com", SwingConstants.CENTER);
        emailLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        emailLabel.setForeground(Color.BLACK);
        emailLabel.setBounds(0, 95, 300, 25);
        popup.add(emailLabel);

        MainMenu.AnimatedButton closeBtn = new MainMenu.AnimatedButton("Close", true);
        closeBtn.setBounds(100, 130, 100, 30);
        closeBtn.addActionListener(e -> dialog.dispose());
        popup.add(closeBtn);

        dialog.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getX() < 45 || e.getX() > 345 || e.getY() < 320 || e.getY() > 500) {
                    dialog.dispose();
                }
            }
        });

        dialog.setVisible(true);
    }

    private static void showLogoutPopup(JFrame parentFrame) {
        JDialog dialog = createOverlayDialog(parentFrame);

        MainMenu.RoundPanel popup = new MainMenu.RoundPanel();
        popup.setBounds(65, 360, 260, 120);
        popup.setLayout(null);
        dialog.add(popup);

        JLabel title = new JLabel("Logout?", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setBounds(0, 25, 260, 25);
        popup.add(title);

        MainMenu.AnimatedButton cancelBtn = new MainMenu.AnimatedButton("Cancel", false);
        cancelBtn.setBounds(25, 70, 100, 30);
        cancelBtn.addActionListener(e -> dialog.dispose());
        popup.add(cancelBtn);

        MainMenu.AnimatedButton logoutBtn = new MainMenu.AnimatedButton("Logout", true);
        logoutBtn.setBounds(135, 70, 100, 30);
        logoutBtn.addActionListener(e -> {
            dialog.dispose();
            parentFrame.dispose();
            SwingUtilities.invokeLater(() -> {
                LoginScreen.main(new String[0]);
                JOptionPane.showMessageDialog(null, "Logout successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            });
        }); 
        popup.add(logoutBtn);

        dialog.setVisible(true);
    }

    private static void showEditProfilePopup(JFrame parentFrame) {
        JDialog dialog = createOverlayDialog(parentFrame);

        MainMenu.RoundPanel popup = new MainMenu.RoundPanel();
        popup.setBounds(45, 220, 300, 320); 
        popup.setLayout(null);
        dialog.add(popup);

        JLabel title = new JLabel("Edit Profile", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.PLAIN, 18));
        title.setBounds(0, 20, 300, 25);
        popup.add(title);

        JPanel avatar = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(14, 71, 17));
                g2.fillOval(0, 0, 70, 70);
                
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 40));
                String initial = firstName.isEmpty() ? "" : firstName.substring(0, 1).toUpperCase();
                FontMetrics fm = g2.getFontMetrics();
                int textX = (70 - fm.stringWidth(initial)) / 2;
                int textY = ((70 - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initial, textX, textY); 

                g2.setColor(Color.WHITE);
                g2.fillOval(45, 45, 25, 25);
                g2.setColor(Color.DARK_GRAY);
                g2.drawOval(45, 45, 25, 25);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                g2.drawString("📷", 50, 62);
            }
        };
        avatar.setOpaque(false);
        avatar.setBounds(115, 55, 75, 75);
        popup.add(avatar);

        // 🌟 DISPLAYS THE DYNAMIC EMAIL IN THE POPUP
        JLabel emailLabel = new JLabel(email, SwingConstants.CENTER);
        emailLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        emailLabel.setBounds(0, 135, 300, 15);
        popup.add(emailLabel);

        JLabel fNameLabel = new JLabel("First Name:");
        fNameLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        fNameLabel.setBounds(30, 160, 240, 15);
        popup.add(fNameLabel);

        JTextField fNameField = new JTextField(firstName);
        fNameField.setBounds(30, 175, 240, 30);
        fNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(0, 5, 0, 5)
        ));
        popup.add(fNameField);

        JLabel lNameLabel = new JLabel("Last Name:");
        lNameLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lNameLabel.setBounds(30, 215, 240, 15);
        popup.add(lNameLabel);

        JTextField lNameField = new JTextField(lastName);
        lNameField.setBounds(30, 230, 240, 30);
        lNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(0, 5, 0, 5)
        ));
        popup.add(lNameField);

        MainMenu.AnimatedButton cancelBtn = new MainMenu.AnimatedButton("Cancel", false);
        cancelBtn.setBounds(30, 275, 110, 30);
        cancelBtn.addActionListener(e -> {
            dialog.dispose();
            SideMenu.showMenu(parentFrame); 
        });
        popup.add(cancelBtn);

        MainMenu.AnimatedButton saveBtn = new MainMenu.AnimatedButton("Save", true);
        saveBtn.setBounds(160, 275, 110, 30);
        
        saveBtn.addActionListener(e -> {
            firstName = fNameField.getText().trim();
            lastName = lNameField.getText().trim();
            
            dialog.dispose();
            SideMenu.showMenu(parentFrame); 
        });
        popup.add(saveBtn);

        dialog.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getX() < 45 || e.getX() > 345 || e.getY() < 220 || e.getY() > 540) {
                    dialog.dispose();
                }
            }
        });

        dialog.setVisible(true);
    }

    private static JDialog createOverlayDialog(JFrame parentFrame) {
        JDialog dialog = new JDialog(parentFrame, true);
        dialog.setUndecorated(true);
        dialog.setSize(390, 844);
        dialog.setLocationRelativeTo(parentFrame);

        JPanel bgPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                try {
                    Image bg = new ImageIcon("image_66695d.png").getImage();
                    g.drawImage(bg, 0, 0, 390, 844, this);
                } catch (Exception e) {}
                g.setColor(new Color(0, 0, 0, 150)); 
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bgPanel.setLayout(null);
        dialog.setContentPane(bgPanel);
        return dialog;
    }
}