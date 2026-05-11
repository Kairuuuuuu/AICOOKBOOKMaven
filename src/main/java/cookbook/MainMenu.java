package cookbook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.ArrayList;

public class MainMenu {

    public static JFrame frame;
    public static JLabel missingLabel; 
    public static JLabel mealLabel; 
    
    // 🌟 THE MEMORY BANK (Now completely empty by default!)
    public static String currentRecipeName = "No meal selected";
    public static List<String> currentIngredients = new ArrayList<>();
    public static List<Boolean> checkedIngredients = new ArrayList<>();
    public static String savedMissingIngredients = "Missing: 0 items";

    public static void showMenu() {
        frame = new JFrame("Dirk's CookBook - Main Menu");
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
                
                g.setColor(new Color(245, 245, 245, 120)); 
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainContent.setLayout(null);
        frame.setContentPane(mainContent); 

        Color darkGreen = new Color(14, 71, 17);

        JPanel topBar = new JPanel();
        topBar.setBounds(0, 0, 390, 100);
        topBar.setBackground(Color.WHITE);
        topBar.setLayout(null);

        JLabel menuIcon = new JLabel("≡");
        menuIcon.setFont(new Font("SansSerif", Font.BOLD, 30));
        menuIcon.setForeground(darkGreen);
        menuIcon.setBounds(20, 40, 40, 40);
        menuIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        menuIcon.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { SideMenu.showMenu(frame); }
        });
        topBar.add(menuIcon);

        JLabel title1 = new JLabel("Dirk's", SwingConstants.CENTER);
        title1.setFont(new Font("Serif", Font.PLAIN, 28));
        title1.setForeground(darkGreen);
        title1.setBounds(0, 25, 390, 35);
        topBar.add(title1);

        JLabel title2 = new JLabel("CookBook", SwingConstants.CENTER);
        title2.setFont(new Font("Serif", Font.PLAIN, 28));
        title2.setForeground(darkGreen);
        title2.setBounds(0, 55, 390, 35);
        topBar.add(title2);

        RoundDollarIcon dollarIcon = new RoundDollarIcon();
        dollarIcon.setBounds(330, 45, 30, 30);
        dollarIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        dollarIcon.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { AddBudget.showBudgetMenu(frame); }
        });
        topBar.add(dollarIcon);

        frame.add(topBar);

        RoundPanel card1 = new RoundPanel();
        card1.setBounds(25, 120, 325, 120);
        card1.setLayout(null);

        JLabel genText1 = new JLabel("Generate Your Next Meal,");
        genText1.setFont(new Font("SansSerif", Font.BOLD, 16));
        genText1.setForeground(darkGreen);
        genText1.setBounds(20, 15, 300, 20);
        card1.add(genText1);
        
        JLabel genText2 = new JLabel("Let AI decide.");
        genText2.setFont(new Font("SansSerif", Font.BOLD, 16));
        genText2.setForeground(darkGreen);
        genText2.setBounds(20, 35, 300, 20);
        card1.add(genText2);

        AnimatedButton genButton = new AnimatedButton("Generate from My Pantry  📖", true);
        genButton.setBounds(20, 65, 285, 40);
        card1.add(genButton);

        frame.add(card1);

        RoundPanel card2 = new RoundPanel();
        card2.setBounds(25, 255, 325, 185);
        card2.setLayout(null);

        JLabel suggText = new JLabel("Suggested For You");
        suggText.setFont(new Font("SansSerif", Font.PLAIN, 16));
        suggText.setBounds(15, 15, 200, 20);
        card2.add(suggText);

        RoundImagePanel food1 = new RoundImagePanel("food1.jpg");
        food1.setBounds(15, 45, 90, 80);
        card2.add(food1);
        JLabel f1Title = new JLabel("Creamy Spinach", SwingConstants.CENTER);
        f1Title.setFont(new Font("SansSerif", Font.BOLD, 10));
        f1Title.setBounds(15, 130, 90, 15);
        card2.add(f1Title);

        RoundImagePanel food2 = new RoundImagePanel("food2.jpg");
        food2.setBounds(117, 45, 90, 80);
        card2.add(food2);
        JLabel f2Title = new JLabel("Kaldereta", SwingConstants.CENTER);
        f2Title.setFont(new Font("SansSerif", Font.BOLD, 10));
        f2Title.setBounds(117, 130, 90, 15);
        card2.add(f2Title);

        RoundImagePanel food3 = new RoundImagePanel("food3.jpg");
        food3.setBounds(220, 45, 90, 80);
        card2.add(food3);
        JLabel f3Title = new JLabel("Chicken Curry", SwingConstants.CENTER);
        f3Title.setFont(new Font("SansSerif", Font.BOLD, 10));
        f3Title.setBounds(220, 130, 90, 15);
        card2.add(f3Title);

        frame.add(card2);

        RoundPanel card3 = new RoundPanel();
        card3.setBounds(25, 455, 325, 170); 
        card3.setLayout(null);

        JLabel shopTitle = new JLabel("🛒 Shopping List");
        shopTitle.setFont(new Font("SansSerif", Font.PLAIN, 18));
        shopTitle.setForeground(darkGreen);
        shopTitle.setBounds(15, 15, 200, 25);
        card3.add(shopTitle);

        mealLabel = new JLabel("Selected Meal: " + currentRecipeName);
        mealLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        mealLabel.setForeground(Color.BLACK);
        mealLabel.setBounds(15, 50, 300, 20);
        card3.add(mealLabel);

        missingLabel = new JLabel(savedMissingIngredients);
        missingLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        missingLabel.setForeground(Color.BLACK);
        missingLabel.setBounds(15, 75, 300, 20);
        card3.add(missingLabel);

        JLabel budgetLabel = new JLabel("Budget: [=====Php 300=====]");
        budgetLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        budgetLabel.setForeground(Color.BLACK);
        budgetLabel.setBounds(15, 100, 300, 20);
        card3.add(budgetLabel);

        AnimatedButton viewListBtn = new AnimatedButton("View Full List", false);
        viewListBtn.setBounds(20, 130, 285, 30);
        viewListBtn.addActionListener(e -> {
            ShoppingList.showShoppingList(frame, currentRecipeName, currentIngredients, checkedIngredients);
        });
        card3.add(viewListBtn);

        frame.add(card3);

        JPanel bottomNav = new JPanel();
        bottomNav.setBounds(0, 720, 390, 90);
        bottomNav.setBackground(darkGreen);
        bottomNav.setLayout(null);

        NavItem homeTab = new NavItem("🏠", "Home", true);  
        homeTab.setBounds(45, 10, 60, 60);
        bottomNav.add(homeTab);

        NavItem pantryTab = new NavItem("📋", "My Pantry", false); 
        pantryTab.setBounds(165, 10, 60, 60);
        pantryTab.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                Point loc = frame.getLocation(); 
                frame.dispose(); 
                PantryScreen.showPantry(); 
                PantryScreen.frame.setLocation(loc); 
            }
        });
        bottomNav.add(pantryTab);

        NavItem chatTab = new NavItem("💬", "AI Chat", false);
        chatTab.setBounds(280, 10, 60, 60);
        chatTab.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                Point loc = frame.getLocation(); 
                frame.dispose(); 
                ChatScreen.showChat(); 
                ChatScreen.frame.setLocation(loc); 
            }
        });
        bottomNav.add(chatTab);

        frame.add(bottomNav);

        frame.setVisible(true);
    }

    public static class RoundPanel extends JPanel {
        public RoundPanel() { setOpaque(false); }
        protected void paintComponent(Graphics g) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(255, 255, 255, 245));
            g.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
        }
    }
    
    public static class RoundTextField extends JTextField {
        private String placeholder;
        public RoundTextField(String placeholder) { 
            this.placeholder = placeholder; setOpaque(false); setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); 
            addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) { if (getText().equals(placeholder)) { setText(""); setForeground(Color.BLACK); } }
                public void focusLost(FocusEvent e) { if (getText().isEmpty()) { setForeground(Color.GRAY); setText(placeholder); } }
            });
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(Color.WHITE); g.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            g.setColor(new Color(14, 71, 17)); g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            super.paintComponent(g);
            if (getText().isEmpty() && !isFocusOwner()) {
                g.setColor(Color.GRAY); 
                int y = (getHeight() - g.getFontMetrics().getHeight()) / 2 + g.getFontMetrics().getAscent(); 
                g.drawString(placeholder, getInsets().left, y);
            }
        }
    }
    
    public static class AnimatedButton extends JButton {
        int shrink = 0; boolean isSolid;
        public AnimatedButton(String text, boolean isSolid) {
            super(text); this.isSolid = isSolid; setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false);
            if(isSolid) setForeground(Color.WHITE); else setForeground(new Color(14, 71, 17));
            setFont(new Font("SansSerif", Font.BOLD, 14));
            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) { shrink = 2; repaint(); } 
                public void mouseReleased(MouseEvent e) { shrink = 0; repaint(); } 
            });
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (shrink > 0) g2.translate(shrink, shrink);
            if (isSolid) { g.setColor(new Color(14, 71, 17)); g.fillRoundRect(0, 0, getWidth()-1-(shrink*2), getHeight()-1-(shrink*2), 30, 30); } 
            else { g.setColor(Color.WHITE); g.fillRoundRect(0, 0, getWidth()-1-(shrink*2), getHeight()-1-(shrink*2), 30, 30); g.setColor(new Color(14, 71, 17)); g.drawRoundRect(0, 0, getWidth()-1-(shrink*2), getHeight()-1-(shrink*2), 30, 30); }
            super.paintComponent(g);
            if (shrink > 0) g2.translate(-shrink, -shrink);
        }
    }

    static class RoundImagePanel extends JPanel {
        Image image;
        public RoundImagePanel(String imagePath) { setOpaque(false); try { ImageIcon icon = new ImageIcon(imagePath); if (icon.getIconWidth() != -1) image = icon.getImage(); } catch (Exception e) {} }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.LIGHT_GRAY); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            if (image != null) { Shape oldClip = g2.getClip(); g2.setClip(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15)); g2.drawImage(image, 0, 0, getWidth(), getHeight(), this); g2.setClip(oldClip); } 
            else { g2.setColor(Color.DARK_GRAY); g2.setFont(new Font("SansSerif", Font.PLAIN, 24)); g2.drawString("📷", getWidth()/2 - 12, getHeight()/2 + 8); }
        }
    }

    static class RoundDollarIcon extends JPanel {
        public RoundDollarIcon() { setOpaque(false); }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(14, 71, 17)); g2.fillOval(0, 0, getWidth(), getHeight());
            g2.setColor(Color.WHITE); g2.setFont(new Font("SansSerif", Font.BOLD, 16)); g2.drawString("$", 10, 21);
        }
    }

    static class NavItem extends JPanel {
        boolean isActive;
        public NavItem(String iconText, String titleText, boolean isActive) {
            this.isActive = isActive; setOpaque(false); setLayout(null);
            JLabel icon = new JLabel(iconText, SwingConstants.CENTER); icon.setFont(new Font("SansSerif", Font.PLAIN, 24)); icon.setForeground(Color.WHITE); icon.setBounds(0, 8, 60, 30); add(icon);
            JLabel title = new JLabel(titleText, SwingConstants.CENTER); title.setFont(new Font("SansSerif", Font.PLAIN, 10)); title.setForeground(Color.WHITE); title.setBounds(0, 38, 60, 15); add(title); setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); if (isActive) { Graphics2D g2 = (Graphics2D) g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); g2.setColor(new Color(255, 255, 255, 60)); g2.fillOval(3, 3, 54, 54); }
        }
    }
}