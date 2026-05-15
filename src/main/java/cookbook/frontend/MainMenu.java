package cookbook.frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import cookbook.backend.CookbookState;
import cookbook.backend.PantryBackend;

public class MainMenu {

    public static JFrame frame;
    public static JLabel missingLabel; 
    public static JLabel mealLabel; 
    public static JLabel budgetLabel; 
    public static JLabel nutritionLabel; 

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

        // --- TOP BAR ---
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

        // --- CARD 1: Generate Meal ---
        RoundPanel card1 = new RoundPanel();
        card1.setBounds(25, 120, 325, 125); 
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
        genButton.setBounds(20, 65, 285, 35); 
        
        JLabel emptyPantryWarning = new JLabel("", SwingConstants.CENTER);
        emptyPantryWarning.setFont(new Font("SansSerif", Font.BOLD, 12));
        emptyPantryWarning.setForeground(new Color(200, 50, 50)); 
        emptyPantryWarning.setBounds(20, 102, 285, 20);
        card1.add(emptyPantryWarning);

        genButton.addActionListener(e -> {
            try {
                CookbookState.generatePromptFromPantry(PantryBackend.savedPantryItems);
                emptyPantryWarning.setText(""); // Success, clear warnings

                Point loc = frame.getLocation(); 
                frame.dispose(); 
                ChatScreen.showChat(); 
                ChatScreen.frame.setLocation(loc); 
            } catch (Exception ex) {
                if (ex.getMessage().equals("EMPTY_PANTRY")) {
                    emptyPantryWarning.setText("Your pantry is empty! Add items first.");
                } else if (ex.getMessage().equals("ALL_EXPIRED")) {
                    emptyPantryWarning.setText("All items are expired! Add fresh ones.");
                }
            }
        });
        
        card1.add(genButton);
        frame.add(card1);

        // --- CARD 3: Shopping List ---
        RoundPanel card3 = new RoundPanel();
        card3.setBounds(25, 255, 325, 440); 
        card3.setLayout(null);

        JLabel shopTitle = new JLabel("🛒 Shopping List");
        shopTitle.setFont(new Font("SansSerif", Font.PLAIN, 18));
        shopTitle.setForeground(darkGreen);
        shopTitle.setBounds(15, 15, 200, 25);
        card3.add(shopTitle);

        if (!CookbookState.currentRecipeName.equals("No meal selected")) {
            JLabel trashBtn = new JLabel("🗑");
            trashBtn.setFont(new Font("SansSerif", Font.PLAIN, 22));
            trashBtn.setForeground(new Color(200, 50, 50)); 
            trashBtn.setBounds(285, 15, 30, 30);
            trashBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            trashBtn.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    JDialog confirmDialog = new JDialog(MainMenu.frame, true);
                    confirmDialog.setUndecorated(true);
                    confirmDialog.setBackground(new Color(0, 0, 0, 0));
                    confirmDialog.setSize(390, 844);
                    confirmDialog.setLocationRelativeTo(MainMenu.frame);

                    JPanel overlay = new JPanel(null) {
                        protected void paintComponent(Graphics g) {
                            g.setColor(new Color(0, 0, 0, 160));
                            g.fillRect(0, 0, getWidth(), getHeight());
                        }
                    };
                    overlay.setOpaque(false);
                    confirmDialog.setContentPane(overlay);

                    JPanel popup = new JPanel(null) {
                        protected void paintComponent(Graphics g) {
                            Graphics2D g2 = (Graphics2D) g;
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(new Color(245, 243, 235));
                            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                            g2.setColor(darkGreen);
                            g2.setStroke(new BasicStroke(2));
                            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
                        }
                    };
                    popup.setBounds(45, 350, 300, 150);
                    popup.setOpaque(false);
                    overlay.add(popup);

                    JLabel askLabel = new JLabel("<html><center>Are you sure you want to<br>trash this recipe?</center></html>", SwingConstants.CENTER);
                    askLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
                    askLabel.setForeground(Color.BLACK);
                    askLabel.setBounds(10, 25, 280, 40);
                    popup.add(askLabel);

                    JButton yesBtn = new JButton("Yes");
                    yesBtn.setBounds(40, 90, 90, 35);
                    yesBtn.setBackground(new Color(200, 50, 50)); 
                    yesBtn.setForeground(Color.WHITE);
                    yesBtn.setFocusPainted(false);
                    yesBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
                    yesBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    
                    yesBtn.addActionListener(eYes -> {
                        confirmDialog.dispose();
                        
                        // 🌟 DELEGATED TO BACKEND 
                        CookbookState.clearState();
                        
                        Point loc = frame.getLocation(); 
                        frame.dispose(); 
                        showMenu(); 
                        frame.setLocation(loc); 
                    });
                    popup.add(yesBtn);

                    JButton noBtn = new JButton("No");
                    noBtn.setBounds(170, 90, 90, 35);
                    noBtn.setBackground(Color.WHITE);
                    noBtn.setForeground(darkGreen);
                    noBtn.setFocusPainted(false);
                    noBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
                    noBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    noBtn.addActionListener(eNo -> confirmDialog.dispose());
                    popup.add(noBtn);

                    confirmDialog.setVisible(true);
                }
            });
            card3.add(trashBtn);
        }

        JLabel mealLabelTitle = new JLabel("Selected Meal:");
        mealLabelTitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        mealLabelTitle.setForeground(Color.DARK_GRAY);
        mealLabelTitle.setBounds(15, 45, 300, 20);
        card3.add(mealLabelTitle);

        mealLabel = new JLabel(CookbookState.currentRecipeName);
        mealLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        mealLabel.setForeground(Color.BLACK);
        mealLabel.setBounds(15, 65, 300, 20);
        card3.add(mealLabel);

        budgetLabel = new JLabel("Budget: " + CookbookState.currentBudget);
        budgetLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        budgetLabel.setForeground(darkGreen);
        budgetLabel.setBounds(15, 85, 300, 20);
        card3.add(budgetLabel);

        JLabel totalCostLabel = new JLabel("Estimated Cost: Php " + String.format("%.2f", CookbookState.currentTotalCost));
        totalCostLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        double numericBudget = 0.0;
        try { numericBudget = Double.parseDouble(CookbookState.currentBudget.replace("Php", "").replace(",", "").trim()); } catch (Exception e) {}
        
        if (!CookbookState.isFromPantry && CookbookState.currentTotalCost > numericBudget && numericBudget > 0) {
            totalCostLabel.setForeground(Color.RED);
        } else {
            totalCostLabel.setForeground(darkGreen);
        }
        totalCostLabel.setBounds(15, 105, 300, 20);
        card3.add(totalCostLabel);

        nutritionLabel = new JLabel("💪 Calories: " + CookbookState.currentCalories + " | Protein: " + CookbookState.currentProtein);
        nutritionLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        nutritionLabel.setForeground(Color.DARK_GRAY);
        nutritionLabel.setBounds(15, 125, 300, 20);
        card3.add(nutritionLabel);

        missingLabel = new JLabel(CookbookState.savedMissingIngredients);
        missingLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        missingLabel.setForeground(Color.BLACK);
        missingLabel.setBounds(15, 145, 300, 20);
        card3.add(missingLabel);

        AnimatedButton doneBtn = new AnimatedButton("Done", false);
        doneBtn.setBounds(100, 395, 135, 30); 
        
        // 🌟 DELEGATED TO BACKEND 
        doneBtn.addActionListener(e -> {
            CookbookState.deductIngredientsAndClearState(PantryBackend.savedPantryItems);
            
            Point loc = frame.getLocation(); 
            frame.dispose(); 
            showMenu(); 
            frame.setLocation(loc); 
        });

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        listPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15)); 

        if (CookbookState.currentIngredients.isEmpty()) {
            JLabel emptyLabel = new JLabel("List is empty. Generate a meal!");
            emptyLabel.setFont(new Font("SansSerif", Font.ITALIC, 13));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 0));
            listPanel.add(emptyLabel);
        } else {
            for (int i = 0; i < CookbookState.currentIngredients.size(); i++) {
                final int index = i; 
                
                String wrappedText = "<html><body style='width: 190px'>" + CookbookState.currentIngredients.get(i) + "</body></html>";
                JCheckBox cb = new JCheckBox(wrappedText);
                cb.setFont(new Font("SansSerif", Font.BOLD, 13)); 
                cb.setOpaque(false);
                cb.setFocusPainted(false);
                cb.setIconTextGap(8); 
                
                cb.setIcon(new CustomCheckBoxIcon(false));
                cb.setSelectedIcon(new CustomCheckBoxIcon(true));

                if (index < CookbookState.checkedIngredients.size() && CookbookState.checkedIngredients.get(index)) {
                    cb.setSelected(true);
                }
                
                cb.addItemListener(e -> {
                    if (index < CookbookState.checkedIngredients.size()) {
                        CookbookState.checkedIngredients.set(index, e.getStateChange() == ItemEvent.SELECTED);
                    }
                    boolean allChecked = !CookbookState.checkedIngredients.isEmpty() && !CookbookState.checkedIngredients.contains(false);
                    doneBtn.setEnabled(allChecked); 
                    doneBtn.setSolid(allChecked); 
                });

                listPanel.add(cb);
                listPanel.add(Box.createRigidArea(new Dimension(0, 10))); 
            }
        }

        JScrollPane listScroll = new JScrollPane(listPanel);
        listScroll.setBounds(15, 170, 295, 215); 
        listScroll.setOpaque(false);
        listScroll.getViewport().setOpaque(false);
        listScroll.setBorder(null); 
        listScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        listScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        listScroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        listScroll.getVerticalScrollBar().setUnitIncrement(25); 
        
        card3.add(listScroll);

        boolean initialAllChecked = !CookbookState.checkedIngredients.isEmpty() && !CookbookState.checkedIngredients.contains(false);
        doneBtn.setEnabled(initialAllChecked);
        doneBtn.setSolid(initialAllChecked);
        card3.add(doneBtn);

        frame.add(card3);

        // --- BOTTOM NAVIGATION ---
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

    // --- Component Classes ---

    public static class CustomCheckBoxIcon implements Icon {
        private final boolean selected;
        private final int size = 18; 
        private final Color darkGreen = new Color(14, 71, 17);

        public CustomCheckBoxIcon(boolean selected) {
            this.selected = selected;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int drawY = y + 1; 

            if (selected) {
                g2.setColor(darkGreen);
                g2.fillRoundRect(x, drawY, size, size, 4, 4);
                
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(x + 4, drawY + 9, x + 8, drawY + 13);
                g2.drawLine(x + 8, drawY + 13, x + 14, drawY + 5);
            } else {
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(x, drawY, size, size, 4, 4);
                
                g2.setColor(darkGreen);
                g2.drawRoundRect(x, drawY, size, size, 4, 4);
            }
            g2.dispose();
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size + 2; }
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
            setFont(new Font("SansSerif", Font.BOLD, 14));
            updateTextColor();
            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) { if(isEnabled()) { shrink = 2; repaint(); } } 
                public void mouseReleased(MouseEvent e) { if(isEnabled()) { shrink = 0; repaint(); } } 
            });
        }
        
        public void setSolid(boolean isSolid) {
            this.isSolid = isSolid;
            updateTextColor();
            repaint();
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            updateTextColor();
            repaint();
        }

        private void updateTextColor() {
            if (!isEnabled()) setForeground(Color.GRAY);
            else if (isSolid) setForeground(Color.WHITE); 
            else setForeground(new Color(14, 71, 17));
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g; 
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int currentShrink = (isEnabled() && shrink > 0) ? shrink : 0;
            if (currentShrink > 0) g2.translate(currentShrink, currentShrink);

            if (!isEnabled()) {
                g.setColor(new Color(220, 220, 220));
                g.fillRoundRect(0, 0, getWidth()-1-(currentShrink*2), getHeight()-1-(currentShrink*2), 30, 30);
            } else if (isSolid) { 
                g.setColor(new Color(14, 71, 17)); 
                g.fillRoundRect(0, 0, getWidth()-1-(currentShrink*2), getHeight()-1-(currentShrink*2), 30, 30); 
            } else { 
                g.setColor(Color.WHITE); 
                g.fillRoundRect(0, 0, getWidth()-1-(currentShrink*2), getHeight()-1-(currentShrink*2), 30, 30); 
                g.setColor(new Color(14, 71, 17)); 
                g.drawRoundRect(0, 0, getWidth()-1-(currentShrink*2), getHeight()-1-(currentShrink*2), 30, 30); 
            }
            super.paintComponent(g);
            if (currentShrink > 0) g2.translate(-currentShrink, -currentShrink);
        }
    }

    public static class RoundDollarIcon extends JPanel {
        public RoundDollarIcon() { setOpaque(false); }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(14, 71, 17)); g2.fillOval(0, 0, getWidth(), getHeight());
            g2.setColor(Color.WHITE); g2.setFont(new Font("SansSerif", Font.BOLD, 16)); g2.drawString("$", 10, 21);
        }
    }

    public static class NavItem extends JPanel {
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
