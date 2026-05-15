package cookbook.frontend;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import cookbook.backend.AuthenticationService;
import cookbook.backend.UserProfileBackend;

public class LoginScreen {
    public static void main(String[] args) {
        showScreen();
    }

    public static void showScreen() {
        JFrame frame = new JFrame("Dirk's CookBook");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(390, 844); 
        frame.setLocationRelativeTo(null); 

        JPanel mainContent = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    ImageIcon bgIcon = new ImageIcon("BackgroundImage_LoginScreen.jpg");
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

        RoundPanel formCard = new RoundPanel();
        formCard.setBounds(37, 270, 300, 180);
        formCard.setLayout(null);

        RoundTextField emailField = new RoundTextField("Email");
        emailField.setBounds(20, 20, 260, 40);
        emailField.setForeground(Color.BLACK); 
        formCard.add(emailField);

        RoundPasswordField passwordField = new RoundPasswordField("Password");
        passwordField.setBounds(20, 75, 260, 40);
        passwordField.setForeground(Color.BLACK); 
        formCard.add(passwordField);
        
        JLabel passEye = new JLabel("👁");
        passEye.setBounds(257, 73, 30, 45);
        passEye.setForeground(darkGreen);
        passEye.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formCard.add(passEye);
        formCard.setComponentZOrder(passEye, 0);

        JLabel forgotPass = new JLabel("<html><u>Forgot Password?</u></html>"); 
        forgotPass.setFont(new Font("SansSerif", Font.PLAIN, 10));
        forgotPass.setForeground(darkGreen);
        forgotPass.setBounds(185, 120, 100, 20);
        forgotPass.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        
        forgotPass.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                frame.dispose();
                EnterEmailScreen.showScreen(true); 
            }
        });
        formCard.add(forgotPass); 

        JLabel errorLabel = new JLabel("Invalid Email or Password!", SwingConstants.CENTER);
        errorLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        errorLabel.setForeground(Color.RED);
        errorLabel.setBounds(20, 145, 260, 20);
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
        
        JLabel loadingTextLabel = new JLabel("<html><div style='text-align: center;'>Logging In...<br><span style='font-size:10px;'>Firing up the stove</span></div></html>");
        loadingTextLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        loadingTextLabel.setForeground(Color.WHITE);
        loadingOverlay.add(loadingTextLabel);
        
        loadingOverlay.addMouseListener(new MouseAdapter() {});
        frame.setGlassPane(loadingOverlay);

        AnimatedButton loginButton = new AnimatedButton("LOGIN");
        loginButton.setBounds(37, 460, 300, 45);
        loginButton.setBackground(darkGreen);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        frame.add(loginButton);
        
        loginButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            errorLabel.setVisible(false);
            
            AuthenticationService.attemptLogin(email, password, new AuthenticationService.LoginCallback() {
                @Override
                public void onSuccess(String firstName, String userEmail) {
                    SwingUtilities.invokeLater(() -> {
                        loadingOverlay.setVisible(false);
                        
                        UserProfileBackend.email = userEmail;
                        UserProfileBackend.firstName = firstName;
                        UserProfileBackend.lastName = ""; 

                        frame.dispose(); 
                        MainMenu.showMenu(); 
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
            
            if (!errorLabel.isVisible()) {
                loadingOverlay.setVisible(true);
            }
        });
        
        JLabel signUpText = new JLabel("<html>New to Dirk's CookBook? <b>Create an account</b></html>", SwingConstants.CENTER);
        signUpText.setFont(new Font("SansSerif", Font.PLAIN, 12));
        signUpText.setForeground(darkGreen);
        signUpText.setBounds(0, 750, 390, 20);
        signUpText.setCursor(new Cursor(Cursor.HAND_CURSOR)); 

        signUpText.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                frame.dispose(); 
                EnterEmailScreen.showScreen(false); 
            }
        });
        frame.add(signUpText);
        
        passEye.addMouseListener(new MouseAdapter() {
            boolean isVisible = false;
            public void mousePressed(MouseEvent e) {
                isVisible = !isVisible;
                passwordField.setEchoChar(isVisible ? (char)0 : '•');
            }
        });
        frame.setVisible(true);
    }

    // --- INNER UI COMPONENTS RETAINED BELOW ---
    static class RoundPanel extends JPanel {
        public RoundPanel() { setOpaque(false); }
        protected void paintComponent(Graphics g) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(255, 255, 255, 245));
            g.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
        }
    }

    static class RoundTextField extends JTextField {
        private String placeholder;
        public RoundTextField(String placeholder) { 
            this.placeholder = placeholder; setOpaque(false); setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); 
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(Color.WHITE); g.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
            g.setColor(new Color(14, 71, 17)); g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
            super.paintComponent(g);
            if (getText().isEmpty()) {
                g.setColor(Color.GRAY);
                int y = (getHeight() - g.getFontMetrics().getHeight()) / 2 + g.getFontMetrics().getAscent();
                g.drawString(placeholder, getInsets().left, y);
            }
        }
    }

    static class RoundPasswordField extends JPasswordField {
        private String placeholder;
        public RoundPasswordField(String placeholder) { 
            this.placeholder = placeholder; setOpaque(false); setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 45)); 
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(Color.WHITE); g.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
            g.setColor(new Color(14, 71, 17)); g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
            super.paintComponent(g);
            if (getPassword().length == 0) {
                g.setColor(Color.GRAY);
                int y = (getHeight() - g.getFontMetrics().getHeight()) / 2 + g.getFontMetrics().getAscent();
                g.drawString(placeholder, getInsets().left, y);
            }
        }
    }

    static class AnimatedButton extends JButton {
        int shrink = 0; 
        public AnimatedButton(String text) {
            super(text); setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false);
            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) { shrink = 2; repaint(); } 
                public void mouseReleased(MouseEvent e) { shrink = 0; repaint(); } 
            });
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (shrink > 0) g2.translate(shrink, shrink);
            g.setColor(getBackground());
            g.fillRoundRect(0, 0, getWidth()-1-(shrink*2), getHeight()-1-(shrink*2), 40, 40);
            super.paintComponent(g);
            if (shrink > 0) g2.translate(-shrink, -shrink);
        }
    }
}
