package cookbook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChangePasswordScreen {
    
    public static void showScreen(JFrame parentFrame) {
        JFrame frame = new JFrame("Dirk's CookBook - Change Password");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(390, 844); 
        frame.setLocationRelativeTo(null); 

        // BACKGROUND
        JPanel mainContent = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    ImageIcon bgIcon = new ImageIcon("BackgroundImage_LoginScreen.png");
                    if (bgIcon.getIconWidth() == -1) bgIcon = new ImageIcon("BackgroundImage_LoginScreen.jpg");
                    if (bgIcon.getIconWidth() != -1) {
                        g.drawImage(bgIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
                    }
                } catch (Exception e) {}
            }
        };
        mainContent.setLayout(null);
        frame.setContentPane(mainContent);

        Color darkGreen = new Color(14, 71, 17);

        // TOP BAR
        JPanel topBar = new JPanel();
        topBar.setBounds(0, 0, 390, 100);
        topBar.setBackground(Color.WHITE);
        topBar.setLayout(null);

        JLabel title1 = new JLabel("Dirk's", SwingConstants.CENTER);
        title1.setFont(new Font("SansSerif", Font.PLAIN, 28));
        title1.setForeground(darkGreen);
        title1.setBounds(0, 25, 390, 35);
        topBar.add(title1);

        JLabel title2 = new JLabel("CookBook", SwingConstants.CENTER);
        title2.setFont(new Font("SansSerif", Font.PLAIN, 28));
        title2.setForeground(darkGreen);
        title2.setBounds(0, 55, 390, 35);
        topBar.add(title2);

        frame.add(topBar);
        
        // FORM CARD
        LoginScreen.RoundPanel formCard = new LoginScreen.RoundPanel();
        formCard.setBounds(37, 220, 300, 280); 
        formCard.setLayout(null);
        
        JLabel header1 = new JLabel("Update your", SwingConstants.LEFT);
        header1.setFont(new Font("Reddit Sans", Font.BOLD, 18));
        header1.setForeground(darkGreen);
        header1.setBounds(20, 20, 280, 25);
        formCard.add(header1);
        
        JLabel header2 = new JLabel("Security Settings", SwingConstants.LEFT);
        header2.setFont(new Font("Reddit Sans", Font.BOLD, 18));
        header2.setForeground(darkGreen);
        header2.setBounds(20, 45, 280, 35);
        formCard.add(header2);
        
        LoginScreen.RoundPasswordField currentPassField = new LoginScreen.RoundPasswordField("Current Password");
        currentPassField.setBounds(20, 85, 260, 40); 
        currentPassField.setForeground(Color.BLACK); 
        formCard.add(currentPassField);

        LoginScreen.RoundPasswordField newPassField = new LoginScreen.RoundPasswordField("New Password");
        newPassField.setBounds(20, 140, 260, 40); 
        newPassField.setForeground(Color.BLACK); 
        formCard.add(newPassField);

        LoginScreen.RoundPasswordField confirmPasswordField = new LoginScreen.RoundPasswordField("Confirm New Password");
        confirmPasswordField.setBounds(20, 195, 260, 40); 
        confirmPasswordField.setForeground(Color.BLACK); 
        formCard.add(confirmPasswordField);
        
        // EYE ICONS
        JLabel currentEye = new JLabel("👁");
        currentEye.setBounds(250, 85, 30, 40); 
        currentEye.setForeground(darkGreen);
        currentEye.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formCard.add(currentEye);
        formCard.setComponentZOrder(currentEye, 0); 
        
        JLabel newEye = new JLabel("👁");
        newEye.setBounds(250, 140, 30, 40); 
        newEye.setForeground(darkGreen);
        newEye.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formCard.add(newEye);
        formCard.setComponentZOrder(newEye, 0); 
        
        JLabel confirmEye = new JLabel("👁");
        confirmEye.setBounds(250, 195, 30, 40); 
        confirmEye.setForeground(darkGreen);
        confirmEye.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formCard.add(confirmEye);
        formCard.setComponentZOrder(confirmEye, 0); 
        
        // ERROR LABEL
        JLabel errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        errorLabel.setForeground(Color.RED);
        errorLabel.setBounds(20, 245, 260, 20); 
        errorLabel.setVisible(false);
        formCard.add(errorLabel);
        
        frame.add(formCard); 
        
        // SAVE BUTTON
        LoginScreen.AnimatedButton savePassBtn = new LoginScreen.AnimatedButton("SAVE NEW PASSWORD");
        savePassBtn.setBounds(37, 520, 300, 45); 
        savePassBtn.setBackground(darkGreen);
        savePassBtn.setForeground(Color.WHITE);
        savePassBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        frame.add(savePassBtn); 
        
        savePassBtn.addActionListener(e -> {
            String currentPass = new String(currentPassField.getPassword());
            String newPass = new String(newPassField.getPassword());
            String confirmPassStr = new String(confirmPasswordField.getPassword());

            if (currentPass.isEmpty() || newPass.isEmpty() || confirmPassStr.isEmpty() || currentPass.equals("Current Password")) {
                errorLabel.setText("Please fill in all fields!");
                errorLabel.setVisible(true);
                return;
            }

            if (!newPass.equals(confirmPassStr)) {
                errorLabel.setText("New passwords do not match!");
                errorLabel.setVisible(true);
                return;
            }
            
            errorLabel.setVisible(false);
            savePassBtn.setText("UPDATING...");
            savePassBtn.setEnabled(false);

            new Thread(() -> {
                String apiKey = System.getenv("FIREBASE_API_KEY");
                String resultMessage = FirebaseManager.updatePasswordREST(SideMenu.email, currentPass, newPass, apiKey);

                SwingUtilities.invokeLater(() -> {
                    savePassBtn.setText("SAVE NEW PASSWORD");
                    savePassBtn.setEnabled(true);

                    if (resultMessage.equals("SUCCESS")) {
                        
                        // 🌟 THE FIX: The "Window Nuke" Loop
                        // This loops through EVERY open tab, menu, and popup in the app and destroys them!
                        for (Window window : Window.getWindows()) {
                            window.dispose(); 
                        }
                        
                        JOptionPane.showMessageDialog(null, "Password Successfully Changed! Please log in again.");
                        
                        // Now we spawn a fresh, clean Login Screen
                        LoginScreen.main(new String [0]); 
                        
                    } else {
                        errorLabel.setText(resultMessage);
                        errorLabel.setVisible(true);
                        formCard.repaint();
                    }
                });
            }).start();
        });
        
        // EYE ICON CLICK LOGIC
        currentEye.addMouseListener(new MouseAdapter() { boolean isVis = false; public void mousePressed(MouseEvent e) { isVis = !isVis; currentPassField.setEchoChar(isVis ? (char)0 : '•'); }});
        newEye.addMouseListener(new MouseAdapter() { boolean isVis = false; public void mousePressed(MouseEvent e) { isVis = !isVis; newPassField.setEchoChar(isVis ? (char)0 : '•'); }});
        confirmEye.addMouseListener(new MouseAdapter() { boolean isVis = false; public void mousePressed(MouseEvent e) { isVis = !isVis; confirmPasswordField.setEchoChar(isVis ? (char)0 : '•'); }});
        
        // BACK TO MENU LABEL
        JLabel backBtn = new JLabel("← Back to Menu");
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        backBtn.setForeground(darkGreen);
        backBtn.setBounds(15, 115, 150, 20);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                frame.dispose();
                parentFrame.setVisible(true); 
            }
        });
        frame.add(backBtn);
        
        frame.setVisible(true);
    }
}