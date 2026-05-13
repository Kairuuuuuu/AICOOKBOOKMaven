package cookbook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PantryScreen {

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
        dialog.setSize(300, 470);
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

        JPanel imageBox = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(new Color(230, 230, 230));
                g.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                g.setColor(Color.GRAY);
                g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            }
        };
        imageBox.setOpaque(false);
        imageBox.setBounds(25, 55, 250, 120);
        imageBox.setLayout(new BorderLayout());
        JLabel imgText = new JLabel("<html><div style='text-align:center;'>📷<br>Insert Image</div></html>", SwingConstants.CENTER);
        imgText.setFont(new Font("SansSerif", Font.PLAIN, 14));
        imageBox.add(imgText, BorderLayout.CENTER);
        popupPanel.add(imageBox);

        JLabel nameLabel = new JLabel("Item Name");
        nameLabel.setBounds(25, 185, 250, 15);
        popupPanel.add(nameLabel);
        MainMenu.RoundTextField nameField = new MainMenu.RoundTextField("Enter Item Name...");
        nameField.setBounds(25, 200, 250, 35);
        popupPanel.add(nameField);

        JLabel qtyLabel = new JLabel("Quantity");
        qtyLabel.setBounds(25, 245, 250, 15);
        popupPanel.add(qtyLabel);
        MainMenu.RoundTextField qtyField = new MainMenu.RoundTextField("Enter Quantity...");
        qtyField.setBounds(25, 260, 250, 35);
        popupPanel.add(qtyField);

        JLabel expLabel = new JLabel("Expiry Date");
        expLabel.setBounds(25, 305, 250, 15);
        popupPanel.add(expLabel);
        MainMenu.RoundTextField expField = new MainMenu.RoundTextField("MM/DD/YYYY   📅");
        expField.setBounds(25, 320, 250, 35);
        popupPanel.add(expField);

        MainMenu.AnimatedButton cancelBtn = new MainMenu.AnimatedButton("Cancel", false);
        cancelBtn.setBounds(25, 390, 115, 35);
        popupPanel.add(cancelBtn);

        MainMenu.AnimatedButton addBtn = new MainMenu.AnimatedButton("Add to Pantry", true);
        addBtn.setBounds(150, 390, 125, 35);
        popupPanel.add(addBtn);

        cancelBtn.addActionListener(e -> {
            frame.getGlassPane().setVisible(false);
            dialog.dispose(); 
        });

        addBtn.addActionListener(e -> {
            String newName = nameField.getText().isEmpty() || nameField.getText().equals("Enter Item Name...") ? "New Food" : nameField.getText();
            String newQty = qtyField.getText().isEmpty() || qtyField.getText().equals("Enter Quantity...") ? "Quantity: ?" : "Quantity: " + qtyField.getText();
            
            addItemToGrid(newName, newQty);
            frame.getGlassPane().setVisible(false);
            dialog.dispose();
        });

        dialog.setVisible(true);
    }

    private static void addItemToGrid(String name, String qty) {
        int xPos = 25 + (itemCount % 2) * 170; 
        int yPos = 0 + (itemCount / 2) * 170;  

        PantryCard newCard = new PantryCard("default_food.jpg", name, qty, "Fresh", new Color(40, 167, 69));
        newCard.setBounds(xPos, yPos, 155, 155);
        
        pantryGrid.add(newCard);
        itemCount++; 
        
        int rows = (itemCount + 1) / 2;
        int requiredHeight = Math.max(545, rows * 170 + 20); 
        pantryGrid.setPreferredSize(new Dimension(390, requiredHeight));
        
        pantryGrid.revalidate(); 
        pantryGrid.repaint();    
    }

    static class PantryCard extends JPanel {
        Color statusColor;
        public PantryCard(String imgPath, String title, String qty, String status, Color statusColor) {
            this.statusColor = statusColor; setLayout(null); setOpaque(false);
            MainMenu.RoundImagePanel img = new MainMenu.RoundImagePanel(imgPath);
            img.setBounds(0, 0, 155, 95); add(img);
            JLabel tLabel = new JLabel(title); tLabel.setFont(new Font("SansSerif", Font.PLAIN, 14)); tLabel.setBounds(10, 100, 135, 20); add(tLabel);
            JLabel qLabel = new JLabel(qty); qLabel.setFont(new Font("SansSerif", Font.PLAIN, 10)); qLabel.setForeground(Color.DARK_GRAY); qLabel.setBounds(10, 118, 135, 15); add(qLabel);
            JLabel sLabel = new JLabel(status); sLabel.setFont(new Font("SansSerif", Font.PLAIN, 10)); sLabel.setForeground(statusColor); sLabel.setBounds(22, 133, 120, 15); add(sLabel);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE); g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            g2.setColor(statusColor); g2.fillOval(10, 136, 8, 8); super.paintComponent(g);
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