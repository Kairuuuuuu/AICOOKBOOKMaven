package cookbook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class PantryScreen {

    public static class PantryItem {
        String name;
        String qty;
        public PantryItem(String name, String qty) {
            this.name = name;
            this.qty = qty;
        }
    }
    public static List<PantryItem> savedPantryItems = new ArrayList<>();

    static int itemCount = 0; 
    static JPanel pantryGrid; 
    public static JFrame frame;

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

        JPanel glassPane = new JPanel();
        glassPane.setOpaque(true);
        glassPane.setBackground(new Color(0, 0, 0, 150)); 
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

        frame.add(topBar);

        MainMenu.RoundTextField searchBar = new MainMenu.RoundTextField("🔍 Search Pantry");
        searchBar.setBounds(25, 115, 330, 40);
        searchBar.setForeground(Color.BLACK);
        frame.add(searchBar);

        pantryGrid = new JPanel();
        pantryGrid.setOpaque(false);
        pantryGrid.setLayout(null); 
        pantryGrid.setPreferredSize(new Dimension(390, 545)); 
        
        itemCount = 0;
        for (PantryItem item : savedPantryItems) {
            addItemToGrid(item.name, item.qty);
        }
        
        JScrollPane pantryScroll = new JScrollPane(pantryGrid);
        pantryScroll.setBounds(0, 175, 390, 545);
        pantryScroll.setOpaque(false);
        pantryScroll.getViewport().setOpaque(false);
        pantryScroll.setBorder(null);
        pantryScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        pantryScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        pantryScroll.getVerticalScrollBar().setUnitIncrement(16);
        pantryScroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));

        frame.add(pantryScroll);

        FloatingAddButton fab = new FloatingAddButton();
        fab.setBounds(285, 630, 65, 65);
        frame.add(fab);
        
        frame.getContentPane().setComponentZOrder(fab, 0);

        fab.addActionListener(e -> {
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

        frame.add(bottomNav);

        frame.setVisible(true);
    }

    private static void showAddMenu() {
        JDialog dialog = new JDialog(frame, true);
        dialog.setUndecorated(true); 
        dialog.setBackground(new Color(0, 0, 0, 0)); 
        
        // 🌟 UPDATED: Dialog is smaller now because we removed the image box
        dialog.setSize(300, 330);
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
        title.setBounds(0, 20, 300, 25);
        popupPanel.add(title);

        // 🌟 UPDATED: Shifted all text fields up to fill the gap left by the image box
        JLabel nameLabel = new JLabel("Item Name");
        nameLabel.setBounds(25, 55, 250, 15);
        popupPanel.add(nameLabel);
        MainMenu.RoundTextField nameField = new MainMenu.RoundTextField("Enter Item Name...");
        nameField.setBounds(25, 70, 250, 35);
        popupPanel.add(nameField);

        JLabel qtyLabel = new JLabel("Quantity");
        qtyLabel.setBounds(25, 115, 250, 15);
        popupPanel.add(qtyLabel);
        MainMenu.RoundTextField qtyField = new MainMenu.RoundTextField("Enter Quantity...");
        qtyField.setBounds(25, 130, 250, 35);
        popupPanel.add(qtyField);

        JLabel expLabel = new JLabel("Expiry Date");
        expLabel.setBounds(25, 175, 250, 15);
        popupPanel.add(expLabel);
        MainMenu.RoundTextField expField = new MainMenu.RoundTextField("MM/DD/YYYY   📅");
        expField.setBounds(25, 190, 250, 35);
        popupPanel.add(expField);

        MainMenu.AnimatedButton cancelBtn = new MainMenu.AnimatedButton("Cancel", false);
        cancelBtn.setBounds(25, 260, 115, 35);
        popupPanel.add(cancelBtn);

        MainMenu.AnimatedButton addBtn = new MainMenu.AnimatedButton("Add to Pantry", true);
        addBtn.setBounds(150, 260, 125, 35);
        popupPanel.add(addBtn);

        cancelBtn.addActionListener(e -> {
            frame.getGlassPane().setVisible(false);
            dialog.dispose(); 
        });

        addBtn.addActionListener(e -> {
            String newName = nameField.getText().isEmpty() || nameField.getText().equals("Enter Item Name...") ? "New Food" : nameField.getText();
            String newQty = qtyField.getText().isEmpty() || qtyField.getText().equals("Enter Quantity...") ? "Quantity: ?" : "Quantity: " + qtyField.getText();
            
            savedPantryItems.add(new PantryItem(newName, newQty));
            
            addItemToGrid(newName, newQty);
            frame.getGlassPane().setVisible(false);
            dialog.dispose();
        });

        dialog.setVisible(true);
    }

    private static void addItemToGrid(String name, String qty) {
        int xPos = 25 + (itemCount % 2) * 170; 
        int yPos = 0 + (itemCount / 2) * 170;  

        // 🌟 UPDATED: Removed the image string argument completely
        PantryCard newCard = new PantryCard(name, qty, "Fresh", new Color(40, 167, 69));
        newCard.setBounds(xPos, yPos, 155, 155);
        
        pantryGrid.add(newCard);
        itemCount++; 
        
        int rows = (itemCount + 1) / 2;
        int requiredHeight = Math.max(545, rows * 170 + 20); 
        pantryGrid.setPreferredSize(new Dimension(390, requiredHeight));
        
        pantryGrid.revalidate(); 
        pantryGrid.repaint();    
    }

    // 🌟 THE FIX: Completely removed the image panel and re-centered the text!
    static class PantryCard extends JPanel {
        Color statusColor;
        
        public PantryCard(String title, String qty, String status, Color statusColor) {
            this.statusColor = statusColor; 
            setLayout(null); 
            setOpaque(false);
            
            // Centered Title
            JLabel tLabel = new JLabel("<html><div style='text-align: center; width: 135px;'>" + title + "</div></html>", SwingConstants.CENTER); 
            tLabel.setFont(new Font("SansSerif", Font.BOLD, 16)); 
            tLabel.setBounds(10, 35, 135, 40); 
            add(tLabel);
            
            // Centered Quantity
            JLabel qLabel = new JLabel(qty, SwingConstants.CENTER); 
            qLabel.setFont(new Font("SansSerif", Font.PLAIN, 12)); 
            qLabel.setForeground(Color.DARK_GRAY); 
            qLabel.setBounds(10, 85, 135, 15); 
            add(qLabel);
            
            // Centered Status
            JLabel sLabel = new JLabel(status); 
            sLabel.setFont(new Font("SansSerif", Font.PLAIN, 12)); 
            sLabel.setForeground(statusColor); 
            sLabel.setBounds(65, 115, 80, 15); 
            add(sLabel);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g; 
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw white card background
            g2.setColor(Color.WHITE); 
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            
            // Draw status dot (aligned with the centered text)
            g2.setColor(statusColor); 
            g2.fillOval(50, 118, 8, 8); 
            
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