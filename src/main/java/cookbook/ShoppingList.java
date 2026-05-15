package cookbook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class ShoppingList {

    public static void showShoppingList(JFrame parentFrame, String recipeName, List<String> dynamicIngredients, List<Boolean> checkedState) {
        JDialog dialog = new JDialog(parentFrame, true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0)); 
        dialog.setSize(390, 844);
        dialog.setLocationRelativeTo(parentFrame);

        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 0, 0, 160)); 
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bgPanel.setOpaque(false);
        bgPanel.setLayout(null);
        dialog.setContentPane(bgPanel);

        bgPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { dialog.dispose(); }
        });

        Color darkGreen = new Color(14, 71, 17);
        Color greyColor = new Color(150, 145, 140);

        JPanel card = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(new Color(235, 235, 235, 255)); 
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        card.setOpaque(false);
        card.setBounds(25, 120, 340, 620);
        card.setLayout(null);
        card.addMouseListener(new MouseAdapter() {}); 
        bgPanel.add(card);

        JLabel title = new JLabel("🛒 Shopping List");
        title.setFont(new Font("SansSerif", Font.PLAIN, 20));
        title.setForeground(darkGreen);
        title.setBounds(25, 20, 200, 30);
        card.add(title);

        JLabel mealTitleLabel = new JLabel("Selected Meal:");
        mealTitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        mealTitleLabel.setForeground(Color.DARK_GRAY);
        mealTitleLabel.setBounds(25, 55, 160, 20);
        card.add(mealTitleLabel);

        JLabel mealNameLabel = new JLabel("<html>" + recipeName + "</html>");
        mealNameLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        mealNameLabel.setForeground(Color.BLACK);
        mealNameLabel.setVerticalAlignment(SwingConstants.TOP); 
        mealNameLabel.setBounds(25, 75, 165, 60); 
        card.add(mealNameLabel);

        // 🌟 CREATE THE IMAGE PANEL AND TRIGGER THE SEARCH
        RoundImagePanel mealImage = new RoundImagePanel("thai_curry.jpg"); // Fallback local image
        mealImage.setBounds(200, 65, 115, 75);
        
        if (!recipeName.equals("No meal selected") && !recipeName.equals("AI Suggested Recipe")) {
            mealImage.loadFromInternet(recipeName);
        }
        
        card.add(mealImage);

        JLabel missingLabel = new JLabel("Missing:");
        missingLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        missingLabel.setForeground(Color.BLACK);
        missingLabel.setBounds(25, 140, 100, 20);
        card.add(missingLabel);

        CustomButton doneBtn = new CustomButton("Done", greyColor, Color.WHITE, greyColor);
        doneBtn.setBounds(180, 540, 135, 35);
        doneBtn.setEnabled(false); 
        
        doneBtn.addActionListener(e -> {
            MainMenu.savedMissingIngredients = "Missing:  None";
            if (MainMenu.missingLabel != null) { MainMenu.missingLabel.setText(MainMenu.savedMissingIngredients); }
            dialog.dispose();
        });
        card.add(doneBtn);

        CustomButton closeBtn = new CustomButton("Close", Color.WHITE, darkGreen, darkGreen);
        closeBtn.setBounds(25, 540, 135, 35);
        closeBtn.addActionListener(e -> dialog.dispose());
        card.add(closeBtn);

        JLabel totalLabel = new JLabel("Total Cost: TBD", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        totalLabel.setForeground(Color.BLACK);
        totalLabel.setBounds(140, 490, 175, 20);
        card.add(totalLabel);

        JPanel listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(listContainer);
        scrollPane.setBounds(15, 170, 310, 310);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0)); 

        List<JCheckBox> checkBoxes = new ArrayList<>();

        for (int i = 0; i < dynamicIngredients.size(); i++) {
            String ingredient = dynamicIngredients.get(i);
            
            // 🌟 READ THE MEMORY BANK
            boolean isInitiallyChecked = (checkedState != null && i < checkedState.size()) ? checkedState.get(i) : false;
            
            JPanel rowPanel = new JPanel(new BorderLayout());
            rowPanel.setOpaque(false);
            rowPanel.setMaximumSize(new Dimension(300, Integer.MAX_VALUE)); 
            
            String htmlText = "<html><div style='width: 155px; font-family: SansSerif; font-size: 11px;'>" + ingredient + "</div></html>";
            
            JCheckBox cb = new JCheckBox(htmlText, isInitiallyChecked);
            cb.setOpaque(false); cb.setFocusPainted(false); cb.setForeground(Color.BLACK);
            cb.setIcon(new CustomCheckIcon(false)); cb.setSelectedIcon(new CustomCheckIcon(true));
            cb.setVerticalTextPosition(SwingConstants.TOP); 
            
            JLabel priceLabel = new JLabel("Php --", SwingConstants.RIGHT);
            priceLabel.setFont(new Font("SansSerif", Font.PLAIN, 13)); priceLabel.setForeground(Color.BLACK);
            priceLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

            rowPanel.add(cb, BorderLayout.CENTER); rowPanel.add(priceLabel, BorderLayout.EAST);
            listContainer.add(rowPanel); listContainer.add(Box.createRigidArea(new Dimension(0, 10))); 
            
            checkBoxes.add(cb);
            
            final int index = i; 
            cb.addItemListener(e -> {
                // 🌟 WRITE TO THE MEMORY BANK
                if (checkedState != null && index < checkedState.size()) {
                    checkedState.set(index, cb.isSelected());
                }
                
                boolean allChecked = true;
                for (JCheckBox box : checkBoxes) { if (!box.isSelected()) { allChecked = false; break; } }
                if (allChecked) { doneBtn.setColors(darkGreen, Color.WHITE, darkGreen); doneBtn.setEnabled(true); } 
                else { doneBtn.setColors(greyColor, Color.WHITE, greyColor); doneBtn.setEnabled(false); }
            });
        }
        
        card.add(scrollPane);
        dialog.setVisible(true);
    }

    static class CustomButton extends JButton {
        Color bgColor, fgColor, borderColor; int shrink = 0;
        public CustomButton(String text, Color bgColor, Color fgColor, Color borderColor) {
            super(text); this.bgColor = bgColor; this.fgColor = fgColor; this.borderColor = borderColor;
            setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false);
            setForeground(fgColor); setFont(new Font("SansSerif", Font.PLAIN, 16));
            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) { if(isEnabled()) { shrink = 2; repaint(); } }
                public void mouseReleased(MouseEvent e) { if(isEnabled()) { shrink = 0; repaint(); } }
            });
        }
        public void setColors(Color bg, Color fg, Color border) { this.bgColor = bg; this.fgColor = fg; this.borderColor = border; setForeground(fg); repaint(); }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (shrink > 0) g2.translate(shrink, shrink);
            g2.setColor(bgColor); g2.fillRoundRect(0, 0, getWidth() - 1 - (shrink * 2), getHeight() - 1 - (shrink * 2), 35, 35);
            g2.setColor(borderColor); g2.drawRoundRect(0, 0, getWidth() - 1 - (shrink * 2), getHeight() - 1 - (shrink * 2), 35, 35);
            g2.setColor(fgColor); g2.setFont(getFont()); FontMetrics fm = g2.getFontMetrics();
            int textX = (getWidth() - 1 - (shrink * 2) - fm.stringWidth(getText())) / 2;
            int textY = ((getHeight() - 1 - (shrink * 2) - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(getText(), textX, textY);
            if (shrink > 0) g2.translate(-shrink, -shrink);
        }
    }

    static class CustomCheckIcon implements Icon {
        private boolean selected;
        public CustomCheckIcon(boolean selected) { this.selected = selected; }
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color darkGreen = new Color(14, 71, 17);
            if (selected) {
                g2.setColor(darkGreen); g2.fillRoundRect(x, y, 18, 18, 4, 4);
                g2.setColor(Color.WHITE); g2.setStroke(new BasicStroke(2));
                g2.drawLine(x + 4, y + 9, x + 8, y + 13); g2.drawLine(x + 8, y + 13, x + 14, y + 5);
            } else { g2.setColor(Color.WHITE); g2.fillRoundRect(x, y, 18, 18, 4, 4); g2.setColor(darkGreen); g2.drawRoundRect(x, y, 18, 18, 4, 4); }
        }
        public int getIconWidth() { return 20; }
        public int getIconHeight() { return 20; }
    }

    // 🌟 THE UPGRADED IMAGE PANEL WITH CACHING & AI GENERATION
    static class RoundImagePanel extends JPanel {
        Image image;
        
        // 🌟 THE IMAGE MEMORY BANK: Remembers downloaded photos so we don't spam the API!
        static java.util.HashMap<String, Image> imageCache = new java.util.HashMap<>();

        public RoundImagePanel(String imagePath) { 
            setOpaque(false); 
            try { 
                ImageIcon icon = new ImageIcon(imagePath); 
                if (icon.getIconWidth() != -1) image = icon.getImage(); 
            } catch (Exception e) {} 
        }

        public void loadFromInternet(String searchKeyword) {
            // 1. CHECK THE MEMORY BANK FIRST
            if (imageCache.containsKey(searchKeyword)) {
                this.image = imageCache.get(searchKeyword);
                repaint();
                return; 
            }

            // 2. IF NOT IN MEMORY, DOWNLOAD IT
            new Thread(() -> {
                try {
                    String safeName = searchKeyword.replace(" ", "%20");
                    
                    // 🌟 UPGRADE 1: Better Prompt Engineering for Culinary Accuracy
                    String prompt = "Authentic%20" + safeName + "%20dish,%20realistic%20food%20photography,%20restaurant%20plating";
                    
                    // 🌟 UPGRADE 2: Use the "Flux" model (State-of-the-Art realism)
                    String urlStr = "https://image.pollinations.ai/prompt/" + prompt + "?width=320&height=240&nologo=true&model=flux";
                    
                    // 3. THE DISGUISE: Pretend to be a web browser
                    java.net.HttpURLConnection connection = (java.net.HttpURLConnection) new java.net.URL(urlStr).openConnection();
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
                    connection.setConnectTimeout(5000); 
                    connection.setReadTimeout(5000);
                    
                    Image webImage = javax.imageio.ImageIO.read(connection.getInputStream());
                    
                    if (webImage != null) {
                        this.image = webImage;
                        
                        // 4. SAVE IT TO THE MEMORY BANK FOR NEXT TIME
                        imageCache.put(searchKeyword, webImage);
                        
                        SwingUtilities.invokeLater(() -> repaint());
                    }
                } catch (Exception e) {
                    System.out.println("Could not generate image online, keeping default.");
                }
            }).start();
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g; 
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.LIGHT_GRAY); 
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            
            if (image != null) { 
                Shape oldClip = g2.getClip(); 
                g2.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15)); 
                g2.drawImage(image, 0, 0, getWidth(), getHeight(), this); 
                g2.setClip(oldClip); 
            } else { 
                g2.setColor(Color.DARK_GRAY); 
                g2.setFont(new Font("SansSerif", Font.PLAIN, 24)); 
                g2.drawString("📷", getWidth()/2 - 12, getHeight()/2 + 8); 
            }
        }
    }
}