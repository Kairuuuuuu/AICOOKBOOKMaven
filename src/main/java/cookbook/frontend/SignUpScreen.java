package cookbook.frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import cookbook.backend.SignUpBackend;

public class SignUpScreen {

    // userEmail is passed from the EnterEmailScreen!
    public static void showScreen(String userEmail) {
        
        SignUpBackend.initConnection();
        
        JFrame frame = new JFrame("Dirk's CookBook - Sign Up");
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
        formCard.setBounds(37, 240, 315, 180); 
        formCard.setLayout(null);

        LoginScreen.RoundPasswordField passwordField = new LoginScreen.RoundPasswordField("Password");
        passwordField.setBounds(20, 35, 275, 45);
        passwordField.setForeground(Color.BLACK); 
        formCard.add(passwordField);

        JLabel passEye = new JLabel("👁");
        passEye.setBounds(260, 35, 30, 45);
        passEye.setForeground(darkGreen);
        passEye.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formCard.add(passEye);
        formCard.setComponentZOrder(passEye, 0);

        LoginScreen.RoundPasswordField confirmPasswordField = new LoginScreen.RoundPasswordField("Confirm Password");
        confirmPasswordField.setBounds(20, 95, 275, 45);
        confirmPasswordField.setForeground(Color.BLACK); 
        formCard.add(confirmPasswordField);

        JLabel confirmEye = new JLabel("👁");
        confirmEye.setBounds(260, 95, 30, 45);
        confirmEye.setForeground(darkGreen);
        confirmEye.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formCard.add(confirmEye);
        formCard.setComponentZOrder(confirmEye, 0);

        JLabel errorLabel = new JLabel("Passwords do not match!", SwingConstants.CENTER);
        errorLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        errorLabel.setForeground(Color.RED);
        errorLabel.setBounds(20, 145, 275, 25);
        errorLabel.setVisible(false); 
        formCard.add(errorLabel);

        frame.add(formCard);
        
        JPanel loadingOverlay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        loadingOverlay.setOpaque(false);
        loadingOverlay.setBackground(new Color(0, 0, 0, 180)); 
        loadingOverlay.setLayout(new GridBagLayout()); 
        
        JLabel loadingTextLabel = new JLabel("<html><div style='text-align: center;'>Creating Account...<br><span style='font-size:10px;'>Whipping things up in the kitchen</span></div></html>");
        loadingTextLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        loadingTextLabel.setForeground(Color.WHITE);
        loadingOverlay.add(loadingTextLabel);
        
        loadingOverlay.addMouseListener(new MouseAdapter() {});
        frame.setGlassPane(loadingOverlay);

        LoginScreen.AnimatedButton signUpBtn = new LoginScreen.AnimatedButton("SIGN UP");
        signUpBtn.setBounds(37, 440, 315, 50); 
        signUpBtn.setBackground(darkGreen);
        signUpBtn.setForeground(Color.WHITE);
        signUpBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        frame.add(signUpBtn);
        
        // 🌟 DELEGATED TO BACKEND 
        signUpBtn.addActionListener(e -> {
            String password = new String(passwordField.getPassword()); 
            String confirmPass = new String(confirmPasswordField.getPassword()); 

            errorLabel.setVisible(false);
            loadingOverlay.setVisible(true); 

            SignUpBackend.attemptSignUp(userEmail, password, confirmPass, new SignUpBackend.SignUpListener() {
                @Override
                public void onSuccess() {
                    SwingUtilities.invokeLater(() -> {
                        loadingOverlay.setVisible(false);
                        JOptionPane.showMessageDialog(frame, "Account Created Successfully!"); 
                        frame.dispose(); 
                        LoginScreen.showScreen(); 
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    SwingUtilities.invokeLater(() -> {
                        loadingOverlay.setVisible(false);
                        errorLabel.setText(errorMessage); 
                        errorLabel.setVisible(true);
                        formCard.repaint();
                    });
                }
            });
        });

        JLabel loginText = new JLabel("<html>Already have an account? <b>Log in</b></html>", SwingConstants.CENTER);
        loginText.setFont(new Font("SansSerif", Font.PLAIN, 12));
        loginText.setForeground(darkGreen);
        loginText.setBounds(0, 750, 390, 20);
        loginText.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        loginText.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                frame.dispose();
                LoginScreen.showScreen();
            }
        });
        frame.add(loginText);

        passEye.addMouseListener(new MouseAdapter() {
            boolean isVisible = false;
            public void mousePressed(MouseEvent e) {
                isVisible = !isVisible;
                passwordField.setEchoChar(isVisible ? (char)0 : '•');
            }
        });

        confirmEye.addMouseListener(new MouseAdapter() {
            boolean isVisible = false;
            public void mousePressed(MouseEvent e) {
                isVisible = !isVisible;
                confirmPasswordField.setEchoChar(isVisible ? (char)0 : '•');
            }
        });

        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        showScreen(""); 
    }
}
