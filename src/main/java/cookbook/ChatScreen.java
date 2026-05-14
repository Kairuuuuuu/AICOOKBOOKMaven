package cookbook;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ChatScreen {

    public static JFrame frame;
    public static JPanel chatHistoryPanel;

    public static void showChat() {
        frame = new JFrame("Dirk's CookBook - AI Chat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(390, 844);
        frame.setLocationRelativeTo(null);

        JPanel mainContent = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    Image bg = new ImageIcon("chat_background.png").getImage();
                    if (bg.getWidth(null) > 0) {
                        g.drawImage(bg, 0, 0, 390, 844, this);
                        return;
                    }
                } catch (Exception e) {}
                g.setColor(new Color(245, 243, 235)); 
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

        MainMenu.RoundDollarIcon dollarIcon = new MainMenu.RoundDollarIcon();
        dollarIcon.setBounds(330, 45, 30, 30);
        dollarIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        dollarIcon.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { AddBudget.showBudgetMenu(frame); }
        });
        topBar.add(dollarIcon);

        frame.add(topBar);

        boolean isFirstLoad = (chatHistoryPanel == null);
        
        if (isFirstLoad) {
            chatHistoryPanel = new JPanel();
            chatHistoryPanel.setLayout(new BoxLayout(chatHistoryPanel, BoxLayout.Y_AXIS));
            chatHistoryPanel.setOpaque(false);
            chatHistoryPanel.setBorder(new EmptyBorder(15, 0, 15, 0)); 
        }

        JScrollPane scrollPane = new JScrollPane(chatHistoryPanel);
        scrollPane.setBounds(0, 100, 390, 560); 
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        frame.add(scrollPane);

        JPanel inputBar = new JPanel(null);
        inputBar.setBounds(0, 660, 390, 60);
        inputBar.setBackground(Color.WHITE);
        
        JTextField inputField = new JTextField(" Type a message...");
        inputField.setForeground(Color.GRAY);
        inputField.setBounds(20, 10, 290, 40);
        inputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        
        inputField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (inputField.getText().equals(" Type a message...")) { inputField.setText(""); inputField.setForeground(Color.BLACK); }
            }
            public void focusLost(FocusEvent e) {
                if (inputField.getText().isEmpty()) { inputField.setForeground(Color.GRAY); inputField.setText(" Type a message..."); }
            }
        });
        inputBar.add(inputField);

        JButton sendBtn = new JButton("➤");
        sendBtn.setBounds(320, 10, 45, 40);
        sendBtn.setBackground(darkGreen);
        sendBtn.setForeground(Color.WHITE);
        sendBtn.setFocusPainted(false);
        sendBtn.setBorderPainted(false);
        sendBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        inputBar.add(sendBtn);

        ActionListener sendAction = e -> {
            String message = inputField.getText().trim();
            if (!message.isEmpty() && !message.equals("Type a message...")) {
                
                boolean bypassBudget = message.contains("ingredients I have in my pantry");
                
                chatHistoryPanel.add(new ChatBubble(message, true));
                chatHistoryPanel.add(Box.createRigidArea(new Dimension(0, 15)));
                inputField.setText("");
                
                ChatBubble thinkingBubble = new ChatBubble("👨‍🍳 Chef AI is thinking...", false);
                Component spacing = Box.createRigidArea(new Dimension(0, 15));
                chatHistoryPanel.add(thinkingBubble);
                chatHistoryPanel.add(spacing);
                
                chatHistoryPanel.revalidate();
                chatHistoryPanel.repaint();
                scrollToBottom(scrollPane);

                new Thread(() -> {
                    String finalMessage = message;
                    if (bypassBudget) {
                        finalMessage += " [SYSTEM NOTE: Since I am using ingredients from my pantry, IGNORE any budget limits for this specific recipe.]";
                    }
                    finalMessage = finalMessage.replace("\n", " ").replace("\r", "");
                    
                    AIChatBot.ParsedResponse aiResponse = AIChatBot.askChefAI(finalMessage);
                    
                    SwingUtilities.invokeLater(() -> {
                        chatHistoryPanel.remove(thinkingBubble);
                        chatHistoryPanel.remove(spacing);
                        
                        String formattedResponse = aiResponse.displayMessage.replaceAll("\n", "<br>");
                        chatHistoryPanel.add(new ChatBubble(formattedResponse, false));
                        
                        if (aiResponse.hasRecipe) {
                            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
                            buttonPanel.setOpaque(false);
                            
                            JButton addToListBtn = new JButton(new CartIcon());
                            addToListBtn.setPreferredSize(new Dimension(50, 40));
                            addToListBtn.setBackground(darkGreen);
                            addToListBtn.setFocusPainted(false);
                            addToListBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                            
                            addToListBtn.addActionListener(evt -> {
                                
                                double checkBudget = 0.0;
                                try {
                                    checkBudget = Double.parseDouble(MainMenu.currentBudget.replace("Php", "").replace(",", "").trim());
                                } catch (Exception ex) {}

                                if (!bypassBudget && checkBudget <= 0.0) {
                                    JDialog warningDialog = new JDialog(ChatScreen.frame, true);
                                    warningDialog.setUndecorated(true);
                                    warningDialog.setBackground(new Color(0, 0, 0, 0));
                                    warningDialog.setSize(390, 844);
                                    warningDialog.setLocationRelativeTo(ChatScreen.frame);

                                    JPanel warnOverlay = new JPanel(null) {
                                        protected void paintComponent(Graphics g) {
                                            g.setColor(new Color(0, 0, 0, 160));
                                            g.fillRect(0, 0, getWidth(), getHeight());
                                        }
                                    };
                                    warnOverlay.setOpaque(false);
                                    warningDialog.setContentPane(warnOverlay);

                                    JPanel warnPopup = new JPanel(null) {
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
                                    warnPopup.setBounds(45, 350, 300, 130);
                                    warnPopup.setOpaque(false);
                                    warnOverlay.add(warnPopup);

                                    JLabel warnLabel = new JLabel("<html><center>You didn't input a budget yet!<br>Please set it via the '$' icon.</center></html>", SwingConstants.CENTER);
                                    warnLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
                                    warnLabel.setForeground(Color.BLACK);
                                    warnLabel.setBounds(10, 25, 280, 40);
                                    warnPopup.add(warnLabel);

                                    JButton okBtn = new JButton("OK");
                                    okBtn.setBounds(100, 75, 100, 35);
                                    okBtn.setBackground(darkGreen);
                                    okBtn.setForeground(Color.WHITE);
                                    okBtn.setFocusPainted(false);
                                    okBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
                                    okBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                                    okBtn.addActionListener(eOk -> warningDialog.dispose());
                                    warnPopup.add(okBtn);

                                    warningDialog.setVisible(true);
                                    return; 
                                }

                                if (!bypassBudget && aiResponse.totalEstimatedCost > checkBudget) {
                                    JDialog warningDialog = new JDialog(ChatScreen.frame, true);
                                    warningDialog.setUndecorated(true);
                                    warningDialog.setBackground(new Color(0, 0, 0, 0));
                                    warningDialog.setSize(390, 844);
                                    warningDialog.setLocationRelativeTo(ChatScreen.frame);

                                    JPanel warnOverlay = new JPanel(null) {
                                        protected void paintComponent(Graphics g) {
                                            g.setColor(new Color(0, 0, 0, 160));
                                            g.fillRect(0, 0, getWidth(), getHeight());
                                        }
                                    };
                                    warnOverlay.setOpaque(false);
                                    warningDialog.setContentPane(warnOverlay);

                                    JPanel warnPopup = new JPanel(null) {
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
                                    warnPopup.setBounds(45, 350, 300, 160); 
                                    warnPopup.setOpaque(false);
                                    warnOverlay.add(warnPopup);

                                    JLabel warnLabel = new JLabel("<html><center>Insufficient Budget!<br>This recipe costs Php " + String.format("%.2f", aiResponse.totalEstimatedCost) + "<br>Your budget is only Php " + String.format("%.2f", checkBudget) + "</center></html>", SwingConstants.CENTER);
                                    warnLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
                                    warnLabel.setForeground(Color.RED); 
                                    warnLabel.setBounds(10, 20, 280, 60);
                                    warnPopup.add(warnLabel);

                                    JButton okBtn = new JButton("OK");
                                    okBtn.setBounds(100, 100, 100, 35);
                                    okBtn.setBackground(darkGreen);
                                    okBtn.setForeground(Color.WHITE);
                                    okBtn.setFocusPainted(false);
                                    okBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
                                    okBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                                    okBtn.addActionListener(eOk -> warningDialog.dispose());
                                    warnPopup.add(okBtn);

                                    warningDialog.setVisible(true);
                                    return; 
                                }

                                JDialog confirmDialog = new JDialog(ChatScreen.frame, true);
                                confirmDialog.setUndecorated(true);
                                confirmDialog.setBackground(new Color(0, 0, 0, 0));
                                confirmDialog.setSize(390, 844);
                                confirmDialog.setLocationRelativeTo(ChatScreen.frame);

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

                                JLabel askLabel = new JLabel("<html><center>Do you want to add ingredients<br>to the shopping list?</center></html>", SwingConstants.CENTER);
                                askLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
                                askLabel.setForeground(Color.BLACK);
                                askLabel.setBounds(10, 25, 280, 40);
                                popup.add(askLabel);

                                JButton yesBtn = new JButton("Yes");
                                yesBtn.setBounds(40, 90, 90, 35);
                                yesBtn.setBackground(darkGreen);
                                yesBtn.setForeground(Color.WHITE);
                                yesBtn.setFocusPainted(false);
                                yesBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
                                yesBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                                yesBtn.addActionListener(eYes -> {
                                    confirmDialog.dispose(); 
                                    
                                    MainMenu.currentRecipeName = aiResponse.recipeName;
                                    MainMenu.currentIngredients = aiResponse.ingredients;
                                    MainMenu.currentTotalCost = aiResponse.totalEstimatedCost;
                                    MainMenu.currentCalories = aiResponse.calories;
                                    MainMenu.currentProtein = aiResponse.protein;
                                    
                                    MainMenu.checkedIngredients = new java.util.ArrayList<>();
                                    for(int i = 0; i < aiResponse.ingredients.size(); i++) {
                                        MainMenu.checkedIngredients.add(false); 
                                    }
                                    
                                    MainMenu.savedMissingIngredients = "Missing: " + aiResponse.ingredients.size() + " items";
                                    
                                    // 🌟 NEW LOGIC: DEDUCT FROM PANTRY
                                    List<PantryScreen.PantryItem> itemsToRemove = new ArrayList<>();
                                    for (String recipeIng : aiResponse.ingredients) {
                                        String lowerReq = recipeIng.toLowerCase();
                                        for (PantryScreen.PantryItem pItem : PantryScreen.savedPantryItems) {
                                            if (lowerReq.contains(pItem.name.toLowerCase())) {
                                                if (!itemsToRemove.contains(pItem)) {
                                                    itemsToRemove.add(pItem); // Mark matched item for deletion
                                                }
                                            }
                                        }
                                    }
                                    PantryScreen.savedPantryItems.removeAll(itemsToRemove); // Completely remove them from the Pantry!

                                    buttonPanel.setVisible(false);
                                    chatHistoryPanel.revalidate();
                                    chatHistoryPanel.repaint();
                                    
                                    JDialog successDialog = new JDialog(ChatScreen.frame, false);
                                    successDialog.setUndecorated(true);
                                    successDialog.setBackground(new Color(0, 0, 0, 0));
                                    successDialog.setSize(260, 50);
                                    successDialog.setLocationRelativeTo(ChatScreen.frame);
                                    
                                    JPanel toastPanel = new JPanel(new BorderLayout()) {
                                        protected void paintComponent(Graphics g) {
                                            Graphics2D g2 = (Graphics2D) g;
                                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                                            g2.setColor(new Color(14, 71, 17, 230)); 
                                            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                                        }
                                    };
                                    toastPanel.setOpaque(false);
                                    
                                    JLabel toastLabel = new JLabel("✓ Successfully added & deducted from Pantry!", SwingConstants.CENTER);
                                    toastLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
                                    toastLabel.setForeground(Color.WHITE);
                                    toastPanel.add(toastLabel, BorderLayout.CENTER);
                                    
                                    successDialog.setContentPane(toastPanel);
                                    successDialog.setVisible(true);

                                    Timer fadeTimer = new Timer(2000, fadeEvt -> successDialog.dispose());
                                    fadeTimer.setRepeats(false);
                                    fadeTimer.start();
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
                            });
                            
                            buttonPanel.add(addToListBtn);
                            chatHistoryPanel.add(Box.createRigidArea(new Dimension(0, 5))); 
                            chatHistoryPanel.add(buttonPanel);
                        }
                        
                        chatHistoryPanel.add(Box.createRigidArea(new Dimension(0, 15)));
                        chatHistoryPanel.revalidate();
                        chatHistoryPanel.repaint();
                        scrollToBottom(scrollPane);
                    });
                }).start();
            }
        };

        sendBtn.addActionListener(sendAction);
        inputField.addActionListener(sendAction);
        
        frame.add(inputBar);

        JPanel bottomNav = new JPanel();
        bottomNav.setBounds(0, 720, 390, 90);
        bottomNav.setBackground(darkGreen);
        bottomNav.setLayout(null);

        MainMenu.NavItem homeTab = new MainMenu.NavItem("🏠", "Home", false);  
        homeTab.setBounds(45, 10, 60, 60);
        homeTab.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { Point loc = frame.getLocation(); frame.dispose(); MainMenu.showMenu(); for(Window w : Window.getWindows()) if (w instanceof JFrame && w.isVisible()) w.setLocation(loc); }
        });
        bottomNav.add(homeTab);

        MainMenu.NavItem pantryTab = new MainMenu.NavItem("📋", "My Pantry", false); 
        pantryTab.setBounds(165, 10, 60, 60);
        pantryTab.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { Point loc = frame.getLocation(); frame.dispose(); PantryScreen.showPantry(); PantryScreen.frame.setLocation(loc); }
        });
        bottomNav.add(pantryTab);

        MainMenu.NavItem chatTab = new MainMenu.NavItem("💬", "AI Chat", true);
        chatTab.setBounds(280, 10, 60, 60);
        bottomNav.add(chatTab);

        frame.add(bottomNav);

        frame.setVisible(true);

        if (MainMenu.pendingPantryPrompt != null && !MainMenu.pendingPantryPrompt.isEmpty()) {
            String autoPrompt = MainMenu.pendingPantryPrompt;
            MainMenu.pendingPantryPrompt = ""; 
            
            SwingUtilities.invokeLater(() -> {
                inputField.setText(autoPrompt);
                inputField.setForeground(Color.BLACK);
                sendBtn.doClick(); 
            });
        } else if (isFirstLoad) {
            SwingUtilities.invokeLater(() -> {
                Timer initialGreeting = new Timer(500, evt -> {
                    chatHistoryPanel.add(new ChatBubble("Welcome back Kyle! 👋 I can help you with recipes, pantry management, or meal planning. What's on your mind today?", false));
                    chatHistoryPanel.add(Box.createRigidArea(new Dimension(0, 15)));
                    chatHistoryPanel.revalidate();
                    chatHistoryPanel.repaint();
                    scrollToBottom(scrollPane);
                    inputField.requestFocusInWindow();
                });
                initialGreeting.setRepeats(false);
                initialGreeting.start();
            });
        } else {
            SwingUtilities.invokeLater(() -> {
                scrollToBottom(scrollPane);
                inputField.requestFocusInWindow();
            });
        }
    }

    private static void scrollToBottom(JScrollPane scrollPane) {
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    static class ChatBubble extends JPanel {
        public ChatBubble(String text, boolean isUser) {
            setOpaque(false); setLayout(new FlowLayout(isUser ? FlowLayout.RIGHT : FlowLayout.LEFT, 20, 0));
            Color aiColor = new Color(246, 244, 210); Color userColor = new Color(14, 71, 17);  
            String htmlText = "<html><div style='width: 220px; font-family: SansSerif; font-size: 11px; letter-spacing: 0.5px; line-height: 1.2; color: " 
                            + (isUser ? "white" : "black") + ";'>" + text + "</div></html>";
            JLabel textLabel = new JLabel(htmlText); textLabel.setBorder(new EmptyBorder(10, 15, 10, 15)); 
            JPanel bubble = new JPanel(new BorderLayout()) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(0, 0, 0, 40)); g2.fillRoundRect(2, 4, getWidth()-4, getHeight()-4, 15, 15);
                    g2.setColor(isUser ? userColor : aiColor); g2.fillRoundRect(0, 0, getWidth()-4, getHeight()-4, 15, 15);
                }
            };
            bubble.setOpaque(false); bubble.add(textLabel, BorderLayout.CENTER); add(bubble);
        }
    }

    static class CartIcon implements Icon {
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            g2.drawLine(x + 4, y + 6, x + 8, y + 6);
            g2.drawLine(x + 8, y + 6, x + 11, y + 16);
            g2.drawLine(x + 11, y + 16, x + 21, y + 16);
            g2.drawLine(x + 21, y + 16, x + 23, y + 8);
            g2.drawLine(x + 23, y + 8, x + 9, y + 8);
            
            g2.fillOval(x + 11, y + 18, 4, 4);
            g2.fillOval(x + 18, y + 18, 4, 4);
            g2.dispose();
        }
        public int getIconWidth() { return 28; }
        public int getIconHeight() { return 28; }
    }
}