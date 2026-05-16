package cookbook.frontend;

import javax.swing.*;
import cookbook.backend.EmailAuthenticationService;
import java.awt.*;
import java.awt.event.*;

public class EnterEmailScreen {
    
    public static void showScreen(boolean isForgotPassword) {
        
        JFrame frame = new JFrame("Dirk's CookBook");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(390, 844);
        frame.setLocationRelativeTo(null);
        
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
        
        // Top Bar
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
        
        // Form Card
        LoginScreen.RoundPanel formCard = new LoginScreen.RoundPanel();
        formCard.setBounds(37, 280, 300, 170); 
        formCard.setLayout(null);
        
        String topText = isForgotPassword ? "Enter your" : "Sign Up using your";
        
        JLabel header1 = new JLabel(topText, SwingConstants.LEFT);
        header1.setFont(new Font("Reddit Sans", Font.BOLD, 18));
        header1.setForeground(darkGreen);
        header1.setBounds(20, 20, 260, 25); 
        formCard.add(header1); 
        
        JLabel header2 = new JLabel("Email Account", SwingConstants.LEFT);
        header2.setFont(new Font("Reddit Sans", Font.BOLD, 18));
        header2.setForeground(darkGreen);
        header2.setBounds(20, 45, 260, 25); 
        formCard.add(header2);
        
        LoginScreen.RoundTextField EmailField = new LoginScreen.RoundTextField("Enter Email");
        EmailField.setBounds(20, 85, 260, 40);
        EmailField.setForeground(Color.BLACK);
        formCard.add(EmailField);

        JLabel errorLabel = new JLabel("Invalid Email", SwingConstants.CENTER);
        errorLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        errorLabel.setForeground(Color.RED);
        errorLabel.setBounds(20, 130, 260, 20);
        errorLabel.setVisible(false);
        formCard.add(errorLabel);
        
        frame.add(formCard);
        
        // Loading Overlay
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
        
        JLabel loadingTextLabel = new JLabel("<html><div style='text-align: center;'>Sending Code...<br><span style='font-size:10px;'>Please wait</span></div></html>");
        loadingTextLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        loadingTextLabel.setForeground(Color.WHITE);
        loadingOverlay.add(loadingTextLabel);
        
        loadingOverlay.addMouseListener(new MouseAdapter() {}); 
        frame.setGlassPane(loadingOverlay);
        
        // Next Button
        LoginScreen.AnimatedButton nextButton = new LoginScreen.AnimatedButton("NEXT");
        nextButton.setBounds(37, 470, 300, 45); 
        nextButton.setBackground(darkGreen);
        nextButton.setForeground(Color.WHITE);
        nextButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        frame.add(nextButton);
        
        // Action Listener - Now delegates to the Backend
        nextButton.addActionListener(e -> {
            String email = EmailField.getText();
            
            errorLabel.setVisible(false);
            loadingOverlay.setVisible(true); 

            new Thread(() -> {
                EmailAuthenticationService.OTPResult result = EmailAuthenticationService.processEmailForOTP(email);

                SwingUtilities.invokeLater(() -> {
                    loadingOverlay.setVisible(false);

        
                    switch (result.status) {
                        case SUCCESS:
                            frame.dispose();
                            VerificationCodeScreen.showScreen(isForgotPassword, email, result.sentCode);
                            break;
                        case INVALID_EMAIL:
                            errorLabel.setText("Invalid Email Format");
                            errorLabel.setVisible(true);
                            formCard.repaint();
                            break;
                        case CONNECTION_ERROR:
                            errorLabel.setText("Connection Error. Try again!");
                            errorLabel.setVisible(true);
                            formCard.repaint();
                            break;
                    }
                });
            }).start(); 
        });
        
        // Login Redirect
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