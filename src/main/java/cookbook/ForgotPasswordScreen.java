package cookbook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ForgotPasswordScreen {
	
	public static void showScreen() {
		JFrame frame = new JFrame("Dirk's CookBook");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(390, 844); 
        frame.setLocationRelativeTo(null); 

        // 🌟 BULLETPROOF BACKGROUND
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
        
        LoginScreen.RoundPanel formCard = new LoginScreen.RoundPanel();
        formCard.setBounds(37, 260, 300, 210); 
        formCard.setLayout(null);
        
        JLabel header1 = new JLabel("Enter your", SwingConstants.LEFT);
        header1.setFont(new Font("Reddit Sans", Font.BOLD, 18));
        header1.setForeground(darkGreen);
        header1.setBounds(20,20,280,25);
        formCard.add(header1);
        
        JLabel header2 = new JLabel("new password", SwingConstants.LEFT);
        header2.setFont(new Font("Reddit Sans", Font.BOLD, 18));
        header2.setForeground(darkGreen);
        header2.setBounds(20,45,280, 35);
        formCard.add(header2);
        
        LoginScreen.RoundPasswordField newPassField = new LoginScreen.RoundPasswordField("New Password");
        newPassField.setBounds(20, 85, 260, 40); 
        newPassField.setForeground(Color.BLACK); 
        formCard.add(newPassField);

        LoginScreen.RoundPasswordField confirmPasswordField = new LoginScreen.RoundPasswordField("Confirm Password");
        confirmPasswordField.setBounds(20, 140, 260, 40); 
        confirmPasswordField.setForeground(Color.BLACK); 
        formCard.add(confirmPasswordField);
        
        JLabel passEye = new JLabel("👁");
        passEye.setBounds(250, 85, 30, 40); 
        passEye.setForeground(darkGreen);
        passEye.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formCard.add(passEye);
        formCard.setComponentZOrder(passEye, 0); 
        
        JLabel confirmEye = new JLabel("👁");
        confirmEye.setBounds(250, 140, 30, 40); 
        confirmEye.setForeground(darkGreen);
        confirmEye.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formCard.add(confirmEye);
        formCard.setComponentZOrder(confirmEye, 0); 
        
        frame.add(formCard); 
        
        LoginScreen.AnimatedButton confirmPass = new LoginScreen.AnimatedButton("CONFIRM NEW PASSWORD");
        confirmPass.setBounds(37, 490, 300, 45); 
        confirmPass.setBackground(darkGreen);
        confirmPass.setForeground(Color.WHITE);
        confirmPass.setFont(new Font("SansSerif", Font.BOLD, 14));
        frame.add(confirmPass); 
        
        confirmPass.addActionListener(e -> {
        	frame.dispose();
            JOptionPane.showMessageDialog(frame, "Password Successfully Changed!");
        	LoginScreen.main(new String [0]);
        });
        
        passEye.addMouseListener(new MouseAdapter() {
            boolean isVisible = false;
            public void mousePressed(MouseEvent e) {
                isVisible = !isVisible;
                newPassField.setEchoChar(isVisible ? (char)0 : '•'); 
            }
        });

        confirmEye.addMouseListener(new MouseAdapter() {
            boolean isVisible = false;
            public void mousePressed(MouseEvent e) {
                isVisible = !isVisible;
                confirmPasswordField.setEchoChar(isVisible ? (char)0 : '•');
            }
        });
        
        JLabel loginText = new JLabel("<html>Already have an account? <b>Log in</b></html>", SwingConstants.CENTER);
        loginText.setFont(new Font("SansSerif", Font.PLAIN, 12));
        loginText.setForeground(darkGreen);
        loginText.setBounds(0, 750, 390, 20);
        loginText.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        loginText.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                frame.dispose();
                LoginScreen.main(new String[0]);
            }
        });
        frame.add(loginText);
        
        frame.setVisible(true);
	}
}