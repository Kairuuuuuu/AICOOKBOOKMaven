package cookbook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class VerificationCodeScreen {
	
	public static void showScreen(boolean isForgotPassword, String userEmail, String correctOTP) {
		JFrame frame = new JFrame("Dirk's CookBook");
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
        
        // FORM CARD (Slightly taller to fit the new text)
        LoginScreen.RoundPanel formCard = new LoginScreen.RoundPanel();
        formCard.setBounds(37, 280, 315, 215); 
        formCard.setLayout(null);
        
        // Main Header
        JLabel header = new JLabel("<html>Enter the<br>Verification Code</html>", SwingConstants.LEFT);
        header.setFont(new Font("Reddit Sans", Font.BOLD, 24));
        header.setForeground(darkGreen);
        header.setBounds(20, 15, 280, 65);
        formCard.add(header);
        
        // Sub-Header (The "pls check email" text)
        JLabel subHeader = new JLabel("<html>We have sent a verification code<br>in your email account please check</html>");
        subHeader.setFont(new Font("Reddit Sans", Font.BOLD, 12));
        subHeader.setForeground(darkGreen);
        subHeader.setBounds(20, 85, 280, 30);
        formCard.add(subHeader);
        
        // Text Field
        LoginScreen.RoundTextField verificationField = new LoginScreen.RoundTextField("Enter Code");
        verificationField.setBounds(20, 125, 275, 45);
        verificationField.setForeground(Color.BLACK);
        formCard.add(verificationField);
        
        // 🌟 THE ERROR LABEL (Hidden by default)
        JLabel errorLabel = new JLabel("Invalid Code", SwingConstants.CENTER);
        errorLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        errorLabel.setForeground(Color.RED);
        errorLabel.setBounds(20, 175, 275, 20);
        errorLabel.setVisible(false); // Make it invisible at start
        formCard.add(errorLabel);
        
        frame.add(formCard); 
        
        // NEXT BUTTON
        LoginScreen.AnimatedButton nextButton = new LoginScreen.AnimatedButton("NEXT");
        nextButton.setBounds(37, 510, 315, 50);
        nextButton.setBackground(darkGreen);
        nextButton.setForeground(Color.WHITE);
        nextButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        frame.add(nextButton);
        
        // LOGIC
        nextButton.addActionListener(e -> {
            String typedCode = verificationField.getText();

            if (typedCode.equals(correctOTP)) {
                frame.dispose();
                if (isForgotPassword) {
                    ForgotPasswordScreen.showScreen();
                } else {
                    SignUpScreen.showScreen(userEmail); 
                }
            } else {
                // 🌟 SHOW ERROR INSTEAD OF JOPTIONPANE
                errorLabel.setVisible(true);
                // Shake effect or redraw
                formCard.repaint();
            }
        });

        // FOOTER
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
        
        // BACK BUTTON
        JLabel backBtn = new JLabel("← Back");
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        backBtn.setForeground(darkGreen);
        backBtn.setBounds(10, 106, 100, 20);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                frame.dispose();
                EnterEmailScreen.showScreen(isForgotPassword);
            }
        });
        frame.add(backBtn);

        frame.setVisible(true);
	}
}