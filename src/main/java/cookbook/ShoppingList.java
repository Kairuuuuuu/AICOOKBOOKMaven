package cookbook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class ShoppingList {

    public static void showShoppingList(JFrame parentFrame) {
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
            public void mousePressed(MouseEvent e) {
                dialog.dispose();
            }
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

        JLabel mealLabel = new JLabel("<html>Selected Meal:<br>Thai Green Curry</html>");
        mealLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        mealLabel.setForeground(Color.BLACK);
        mealLabel.setBounds(25, 75, 160, 45);
        card.add(mealLabel);

        RoundImagePanel mealImage = new RoundImagePanel("thai_curry.jpg");
        mealImage.setBounds(200, 65, 115, 75);
        card.add(mealImage);

        JLabel missingLabel = new JLabel("Missing:");
        missingLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        missingLabel.setForeground(Color.BLACK);
        missingLabel.setBounds(25, 140, 100, 20);
        card.add(missingLabel);

        CustomButton doneBtn = new CustomButton("Done", greyColor, Color.WHITE, greyColor);
        doneBtn.setBounds(180, 540, 135, 35);
        doneBtn.setEnabled(false); 
        
        // 🌟 ACTION: Update the label in MainMenu AND save it globally!
        doneBtn.addActionListener(e -> {
            MainMenu.savedMissingIngredients = "Missing:  None";
            if (MainMenu.missingLabel != null) {
                MainMenu.missingLabel.setText(MainMenu.savedMissingIngredients);
            }
            dialog.dispose();
        });
        card.add(doneBtn);

        CustomButton closeBtn = new CustomButton("Close", Color.WHITE, darkGreen, darkGreen);
        closeBtn.setBounds(25, 540, 135, 35);
        closeBtn.addActionListener(e -> dialog.dispose());
        card.add(closeBtn);

        int startY = 175;
        int gap = 30;
        List<JCheckBox> checkBoxes = new ArrayList<>();

        String[][] ingredients = {
            {"Coconut Milk", "Php 45"},
            {"Fish Sauce", "Php 20"},
            {"Palm Sugar", "Php 15"},
            {"Citrus", "Php 25"},
            {"Thai Chilies", "Php 30"},
            {"Umami Paste", "Php 35"},
            {"White Peppercorns", "Php 40"},
            {"Chicken Stock", "Php 60"},
            {"Kaffir Lime Leaves", "Php 15"},
            {"Thai Basil Leaves", "Php 15"}
        };

        for (int i = 0; i < ingredients.length; i++) {
            JCheckBox cb = addCheckboxRow(card, ingredients[i][0], ingredients[i][1], false, startY + gap * i);
            checkBoxes.add(cb);
            
            cb.addItemListener(e -> {
                boolean allChecked = true;
                for (JCheckBox box : checkBoxes) {
                    if (!box.isSelected()) {
                        allChecked = false;
                        break;
                    }
                }
                
                if (allChecked) {
                    doneBtn.setColors(darkGreen, Color.WHITE, darkGreen); 
                    doneBtn.setEnabled(true); 
                } else {
                    doneBtn.setColors(greyColor, Color.WHITE, greyColor); 
                    doneBtn.setEnabled(false); 
                }
            });
        }

        JLabel totalLabel = new JLabel("Total Cost: Php 300", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        totalLabel.setForeground(Color.BLACK);
        totalLabel.setBounds(140, 490, 175, 20);
        card.add(totalLabel);

        dialog.setVisible(true);
    }

    private static JCheckBox addCheckboxRow(JPanel panel, String ingredient, String price, boolean isChecked, int yPos) {
        JCheckBox checkBox = new JCheckBox(ingredient, isChecked);
        checkBox.setFont(new Font("SansSerif", Font.PLAIN, 15));
        checkBox.setForeground(Color.BLACK);
        checkBox.setOpaque(false);
        checkBox.setFocusPainted(false);
        checkBox.setBounds(22, yPos, 200, 25);
        
        checkBox.setIcon(new CustomCheckIcon(false));
        checkBox.setSelectedIcon(new CustomCheckIcon(true));

        JLabel priceLabel = new JLabel(price, SwingConstants.RIGHT);
        priceLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        priceLabel.setForeground(Color.BLACK);
        priceLabel.setBounds(230, yPos, 85, 25);

        panel.add(checkBox);
        panel.add(priceLabel);
        
        return checkBox; 
    }

    static class CustomButton extends JButton {
        Color bgColor, fgColor, borderColor;
        int shrink = 0;

        public CustomButton(String text, Color bgColor, Color fgColor, Color borderColor) {
            super(text);
            this.bgColor = bgColor;
            this.fgColor = fgColor;
            this.borderColor = borderColor;
            
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setForeground(fgColor);
            setFont(new Font("SansSerif", Font.PLAIN, 16));

            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) { if(isEnabled()) { shrink = 2; repaint(); } }
                public void mouseReleased(MouseEvent e) { if(isEnabled()) { shrink = 0; repaint(); } }
            });
        }
        
        public void setColors(Color bg, Color fg, Color border) {
            this.bgColor = bg;
            this.fgColor = fg;
            this.borderColor = border;
            setForeground(fg);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (shrink > 0) g2.translate(shrink, shrink);
            
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth() - 1 - (shrink * 2), getHeight() - 1 - (shrink * 2), 35, 35);
            
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1 - (shrink * 2), getHeight() - 1 - (shrink * 2), 35, 35);
            
            // 🌟 Manually drawing text ensures it stays white even when disabled
            g2.setColor(fgColor); 
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
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
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            Color darkGreen = new Color(14, 71, 17);
            
            if (selected) {
                g2.setColor(darkGreen);
                g2.fillRoundRect(x, y, 18, 18, 4, 4);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(x + 4, y + 9, x + 8, y + 13);
                g2.drawLine(x + 8, y + 13, x + 14, y + 5);
            } else {
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(x, y, 18, 18, 4, 4);
                g2.setColor(darkGreen);
                g2.drawRoundRect(x, y, 18, 18, 4, 4);
            }
        }
        public int getIconWidth() { return 20; }
        public int getIconHeight() { return 20; }
    }

    static class RoundImagePanel extends JPanel {
        Image image;
        public RoundImagePanel(String imagePath) {
            setOpaque(false);
            try {
                ImageIcon icon = new ImageIcon(imagePath);
                if (icon.getIconWidth() != -1) image = icon.getImage();
            } catch (Exception e) {}
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g; 
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.LIGHT_GRAY); 
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            
            if (image != null) {
                Shape oldClip = g2.getClip();
                g2.setClip(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
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