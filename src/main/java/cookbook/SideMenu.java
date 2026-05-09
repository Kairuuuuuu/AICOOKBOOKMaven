package cookbook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SideMenu {

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
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 40));
                g2.drawString("J", 22, 48); 
            }
        };
        avatar.setOpaque(false);
        avatar.setBounds(20, 80, 70, 70);
        menuPanel.add(avatar);

        JLabel nameLabel = new JLabel("Jowin Dirk");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        nameLabel.setBounds(105, 85, 160, 25);
        menuPanel.add(nameLabel);

        JLabel emailLabel = new JLabel("Jowin@Dirkcookbook.com");
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
            System.out.println("Help clicked");
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
        popup.setBounds(45, 140, 300, 440); 
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
                g2.drawString("J", 22, 48); 

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

        JLabel emailLabel = new JLabel("Jowin@Dirkcookbook.com", SwingConstants.CENTER);
        emailLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        emailLabel.setBounds(0, 135, 300, 15);
        popup.add(emailLabel);

        addFormInput(popup, "First Name:", "Jowin", 160, 30);
        addFormInput(popup, "Last Name:", "Dirk", 215, 30);

        JLabel bioLabel = new JLabel("Culinary Bio:");
        bioLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        bioLabel.setBounds(30, 270, 240, 15);
        popup.add(bioLabel);

        JTextArea bioField = new JTextArea("Tell us about your cooking style...");
        bioField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        bioField.setForeground(Color.BLACK);
        bioField.setLineWrap(true);
        bioField.setWrapStyleWord(true);
        bioField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1), 
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        bioField.setBounds(30, 285, 240, 80);
        popup.add(bioField);

        MainMenu.AnimatedButton cancelBtn = new MainMenu.AnimatedButton("Cancel", false);
        cancelBtn.setBounds(30, 385, 110, 30);
        cancelBtn.addActionListener(e -> {
            dialog.dispose();
            SideMenu.showMenu(parentFrame); 
        });
        popup.add(cancelBtn);

        MainMenu.AnimatedButton saveBtn = new MainMenu.AnimatedButton("Save", true);
        saveBtn.setBounds(160, 385, 110, 30);
        saveBtn.addActionListener(e -> {
            dialog.dispose();
            SideMenu.showMenu(parentFrame);
        });
        popup.add(saveBtn);

        dialog.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getX() < 45 || e.getX() > 345 || e.getY() < 140 || e.getY() > 580) {
                    dialog.dispose();
                }
            }
        });

        dialog.setVisible(true);
    }

    private static void addFormInput(JPanel parent, String labelText, String defaultText, int y, int height) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setBounds(30, y, 240, 15);
        parent.add(label);

        JTextField field = new JTextField(defaultText);
        field.setBounds(30, y + 15, 240, height);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(0, 5, 0, 5)
        ));
        parent.add(field);
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