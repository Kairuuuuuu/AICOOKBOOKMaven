package cookbook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class PantryScreen {

    public static class PantryItem {
        String name;
        String qty;
        String expDate; 
        public PantryItem(String name, String qty, String expDate) {
            this.name = name;
            this.qty = qty;
            this.expDate = expDate;
        }
    }
    public static List<PantryItem> savedPantryItems = new ArrayList<>();

    static int itemCount = 0; 
    static JPanel pantryGrid; 
    public static JFrame frame;
    public static FloatingAddButton fab; 

    public static void showPantry() {
        frame = new JFrame("Dirk's CookBook - My Pantry");
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

        JPanel glassPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0, 0, 0, 160)); 
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        glassPane.setOpaque(false); 
        glassPane.setLayout(null);
        glassPane.addMouseListener(new MouseAdapter() {}); 
        frame.setGlassPane(glassPane);

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

        MainMenu.RoundDollarIcon dollarIcon = new MainMenu.RoundDollarIcon();
        dollarIcon.setBounds(330, 45, 30, 30);
        dollarIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        dollarIcon.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { AddBudget.showBudgetMenu(frame); } 
        });
        topBar.add(dollarIcon);

        MainMenu.RoundTextField searchBar = new MainMenu.RoundTextField("🔍 Search Pantry");
        searchBar.setBounds(25, 115, 330, 40);
        searchBar.setForeground(Color.BLACK);

        pantryGrid = new JPanel();
        pantryGrid.setOpaque(false);
        pantryGrid.setLayout(null); 
        pantryGrid.setPreferredSize(new Dimension(390, 535)); 
        
        itemCount = 0;
        for (PantryItem item : savedPantryItems) {
            addItemToGrid(item.name, item.qty, item.expDate);
        }
        
        JScrollPane pantryScroll = new JScrollPane(pantryGrid);
        pantryScroll.setBounds(0, 175, 390, 535); 
        pantryScroll.setOpaque(false);
        pantryScroll.getViewport().setOpaque(false);
        pantryScroll.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE); 
        pantryScroll.setBorder(null);
        pantryScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        pantryScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        pantryScroll.getVerticalScrollBar().setUnitIncrement(16);
        pantryScroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));

        pantryScroll.getVerticalScrollBar().addAdjustmentListener(e -> {
            mainContent.repaint();
        });

        fab = new FloatingAddButton();
        fab.setBounds(285, 630, 65, 65);

        fab.addActionListener(e -> {
            fab.setVisible(false); 
            glassPane.setVisible(true);
            showAddMenu(); 
        });
        
        JPanel bottomNav = new JPanel();
        bottomNav.setBounds(0, 720, 390, 90);
        bottomNav.setBackground(darkGreen);
        bottomNav.setLayout(null);

        MainMenu.NavItem homeTab = new MainMenu.NavItem("🏠", "Home", false);  
        homeTab.setBounds(45, 10, 60, 60);
        homeTab.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                Point loc = frame.getLocation(); frame.dispose(); 
                MainMenu.showMenu(); 
                for(Window w : Window.getWindows()) if (w instanceof JFrame && w.isVisible()) w.setLocation(loc);
            }
        });
        bottomNav.add(homeTab);

        MainMenu.NavItem pantryTab = new MainMenu.NavItem("📋", "My Pantry", true); 
        pantryTab.setBounds(165, 10, 60, 60);
        bottomNav.add(pantryTab);

        MainMenu.NavItem chatTab = new MainMenu.NavItem("💬", "AI Chat", false);
        chatTab.setBounds(280, 10, 60, 60);
        chatTab.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                Point loc = frame.getLocation(); frame.dispose(); 
                ChatScreen.showChat(); ChatScreen.frame.setLocation(loc); 
            }
        });
        bottomNav.add(chatTab);

        frame.add(fab);
        frame.add(bottomNav);
        frame.add(topBar);
        frame.add(searchBar);
        frame.add(pantryScroll);

        frame.getContentPane().setComponentZOrder(fab, 0); 
        frame.getContentPane().setComponentZOrder(bottomNav, 1); 
        frame.getContentPane().setComponentZOrder(topBar, 2); 
        frame.getContentPane().setComponentZOrder(searchBar, 3);
        frame.getContentPane().setComponentZOrder(pantryScroll, 4); 

        frame.setVisible(true);
    }

    private static void showAddMenu() {
        JDialog dialog = new JDialog(frame, true);
        dialog.setUndecorated(true); 
        dialog.setBackground(new Color(0, 0, 0, 0)); 
        
        dialog.setSize(300, 450);
        dialog.setLocationRelativeTo(frame); 

        MainMenu.RoundPanel popupPanel = new MainMenu.RoundPanel();
        popupPanel.setBackground(Color.WHITE);
        popupPanel.setOpaque(false);
        popupPanel.setLayout(null);
        dialog.add(popupPanel);

        Color darkGreen = new Color(14, 71, 17);

        JLabel title = new JLabel("Add New Item to Pantry", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.PLAIN, 18));
        title.setForeground(darkGreen);
        title.setBounds(0, 15, 300, 25);
        popupPanel.add(title);

        JPanel imageBox = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(235, 235, 235)); 
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.setColor(Color.GRAY); 
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.dispose();
            }
        };
        imageBox.setLayout(new BorderLayout());
        imageBox.setBounds(25, 50, 250, 110);
        imageBox.setOpaque(false);
        imageBox.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        
        JLabel imageLabel = new JLabel("<html><center><font size='6'>📷</font><br>Insert Image</center></html>", SwingConstants.CENTER);
        imageLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        imageLabel.setForeground(Color.DARK_GRAY);
        imageBox.add(imageLabel, BorderLayout.CENTER);
        popupPanel.add(imageBox);

        JLabel nameLabel = new JLabel("Item Name");
        nameLabel.setBounds(25, 175, 250, 15);
        popupPanel.add(nameLabel);
        MainMenu.RoundTextField nameField = new MainMenu.RoundTextField("Enter Item Name...");
        nameField.setBounds(25, 190, 250, 35);
        popupPanel.add(nameField);

        JLabel qtyLabel = new JLabel("Quantity");
        qtyLabel.setBounds(25, 235, 250, 15);
        popupPanel.add(qtyLabel);
        MainMenu.RoundTextField qtyField = new MainMenu.RoundTextField("Enter Quantity...");
        qtyField.setBounds(25, 250, 250, 35);
        popupPanel.add(qtyField);

        JLabel expLabel = new JLabel("Expiry Date");
        expLabel.setBounds(25, 295, 250, 15);
        popupPanel.add(expLabel);
        MainMenu.RoundTextField expField = new MainMenu.RoundTextField("MM/DD/YYYY   📅");
        expField.setBounds(25, 310, 250, 35);
        popupPanel.add(expField);

        // 🌟 NEW: INLINE UI ERROR MESSAGE LABEL (Hidden by default)
        JLabel errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        errorLabel.setForeground(new Color(200, 50, 50)); // Red warning text
        errorLabel.setBounds(10, 355, 280, 20); // Sits right above the buttons
        popupPanel.add(errorLabel);

        MainMenu.AnimatedButton cancelBtn = new MainMenu.AnimatedButton("Cancel", false);
        cancelBtn.setBounds(25, 380, 115, 35);
        popupPanel.add(cancelBtn);

        MainMenu.AnimatedButton addBtn = new MainMenu.AnimatedButton("Add to Pantry", true);
        addBtn.setBounds(150, 380, 125, 35);
        popupPanel.add(addBtn);
        
        cancelBtn.addActionListener(e -> {
            frame.getGlassPane().setVisible(false);
            fab.setVisible(true); 
            dialog.dispose(); 
        });

        // Clear the error label if they click on the date field again to try fixing it
        expField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                errorLabel.setText(""); // Hide the error text when user clicks to type
            }
        });

        addBtn.addActionListener(e -> {
            String rawExp = expField.getText().replace("📅", "").trim();

            // 🌟 INLINE UI ERROR: Blank Date
            if (rawExp.isEmpty() || rawExp.equals("MM/DD/YYYY")) {
                errorLabel.setText("Please enter an expiry date.");
                return; // Stop the code here
            }

            // 🌟 INLINE UI ERROR: Invalid Date Format
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                LocalDate.parse(rawExp, formatter); 
            } catch (Exception ex) {
                errorLabel.setText("Invalid format! Use MM/DD/YYYY");
                return; // Stop the code here
            }

            // If valid, proceed to save:
            String newName = nameField.getText().isEmpty() || nameField.getText().equals("Enter Item Name...") ? "New Food" : nameField.getText();
            String newQty = qtyField.getText().isEmpty() || qtyField.getText().equals("Enter Quantity...") ? "Quantity: ?" : "Quantity: " + qtyField.getText();
            
            savedPantryItems.add(new PantryItem(newName, newQty, rawExp));
            
            addItemToGrid(newName, newQty, rawExp);
            frame.getGlassPane().setVisible(false);
            fab.setVisible(true); 
            dialog.dispose();
        });

        dialog.setVisible(true);
    }

    private static void addItemToGrid(String name, String qty, String expDateStr) {
        String statusText = "Fresh";
        Color statusColor = new Color(40, 167, 69); 

        if (expDateStr != null && !expDateStr.isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                LocalDate expiryDate = LocalDate.parse(expDateStr, formatter);
                LocalDate today = LocalDate.now();
                
                long daysBetween = ChronoUnit.DAYS.between(today, expiryDate);

                if (daysBetween < 0) {
                    statusText = "Expired";
                    statusColor = new Color(200, 50, 50); 
                } else if (daysBetween <= 3) { 
                    statusText = "Expiring: " + daysBetween + " day" + (daysBetween == 1 ? "" : "s");
                    statusColor = new Color(200, 50, 50); 
                }
            } catch (Exception ex) { }
        }

        int xPos = 25 + (itemCount % 2) * 170; 
        int yPos = 0 + (itemCount / 2) * 170;  

        PantryCard newCard = new PantryCard(name, qty, statusText, statusColor);
        newCard.setBounds(xPos, yPos, 155, 155);
        
        pantryGrid.add(newCard);
        itemCount++; 
        
        int rows = (itemCount + 1) / 2;
        int requiredHeight = Math.max(535, rows * 170 + 20); 
        pantryGrid.setPreferredSize(new Dimension(390, requiredHeight));
        
        pantryGrid.revalidate(); 
        frame.repaint(); 
    }

    static class PantryCard extends JPanel {
        Color statusColor;
        
        public PantryCard(String title, String qty, String status, Color statusColor) {
            this.statusColor = statusColor; 
            setLayout(null); 
            setOpaque(false);
            
            JLabel imgLabel = new JLabel("📷", SwingConstants.CENTER);
            imgLabel.setFont(new Font("SansSerif", Font.PLAIN, 30));
            imgLabel.setForeground(Color.GRAY);
            imgLabel.setBounds(0, 0, 155, 80);
            add(imgLabel);

            JLabel tLabel = new JLabel(title); 
            tLabel.setFont(new Font("SansSerif", Font.PLAIN, 15)); 
            tLabel.setBounds(10, 85, 135, 20); 
            add(tLabel);
            
            JLabel qLabel = new JLabel(qty); 
            qLabel.setFont(new Font("SansSerif", Font.PLAIN, 11)); 
            qLabel.setForeground(Color.DARK_GRAY); 
            qLabel.setBounds(10, 105, 135, 15); 
            add(qLabel);
            
            JLabel sLabel = new JLabel(status); 
            sLabel.setFont(new Font("SansSerif", Font.PLAIN, 11)); 
            sLabel.setForeground(statusColor); 
            sLabel.setBounds(22, 125, 120, 15); 
            add(sLabel);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g; 
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(Color.WHITE); 
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            
            Shape oldClip = g2.getClip();
            g2.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 15, 15));
            g2.setColor(new Color(230, 230, 230));
            g2.fillRect(0, 0, getWidth(), 80);
            g2.setClip(oldClip);
            
            g2.setColor(statusColor); 
            g2.fillOval(10, 128, 8, 8); 

            g2.setColor(new Color(210, 210, 210));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            
            super.paintComponent(g);
        }
    }

    static class FloatingAddButton extends JButton {
        public FloatingAddButton() {
            super("+"); setFont(new Font("SansSerif", Font.PLAIN, 40)); setForeground(Color.WHITE);
            setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(14, 71, 17)); g2.fillOval(0, 0, getWidth()-1, getHeight()-1); super.paintComponent(g);
        }
    }
}