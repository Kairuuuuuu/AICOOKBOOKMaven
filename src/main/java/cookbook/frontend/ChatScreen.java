package cookbook.frontend;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

import cookbook.backend.AIChatBot;
import cookbook.backend.Chatbackend;
import cookbook.backend.CookbookState;

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

        // Top Bar Setup
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

        // Input Bar Setup
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
                inputField.setText("");
                processAIMessage(message, false, scrollPane, darkGreen); 
            }
        };

        sendBtn.addActionListener(sendAction);
        inputField.addActionListener(sendAction);
        frame.add(inputBar);

        // Bottom Nav Setup
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

        // Initial Prompts Handling
        if (!CookbookState.pendingPantryPrompt.isEmpty()) {
            String hiddenPrompt = CookbookState.pendingPantryPrompt;
            CookbookState.pendingPantryPrompt = "";
            SwingUtilities.invokeLater(() -> processAIMessage(hiddenPrompt, true, scrollPane, darkGreen));
        } else if (isFirstLoad) {
            SwingUtilities.invokeLater(() -> {
                Timer initialGreeting = new Timer(500, evt -> {
                    chatHistoryPanel.add(new ChatBubble("Welcome back Kyle! 👋 I can help you with recipes, pantry management, or meal planning. What's on your mind today?", false));
                    chatHistoryPanel.add(Box.createRigidArea(new Dimension(0, 15)));
                    refreshUI(scrollPane);
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

        frame.setVisible(true);
    }

    private static void processAIMessage(String message, boolean isHidden, JScrollPane scrollPane, Color darkGreen) {
        if (!isHidden) {
            chatHistoryPanel.add(new ChatBubble(message, true));
            chatHistoryPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        ChatBubble thinkingBubble = new ChatBubble(isHidden ? "👨‍🍳 Chef AI is checking your pantry..." : "👨‍🍳 Chef AI is thinking...", false);
        Component spacing = Box.createRigidArea(new Dimension(0, 15));
        chatHistoryPanel.add(thinkingBubble);
        chatHistoryPanel.add(spacing);
        refreshUI(scrollPane);

        new Thread(() -> {
            AIChatBot.ParsedResponse aiResponse = AIChatBot.askChefAI(message);
            
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
                        // BACKEND CALL: Analyze the recipe
                        Chatbackend.RecipeAnalysisResult analysis = Chatbackend.analyzeRecipe(aiResponse);

                        if (analysis.status == Chatbackend.BudgetStatus.NO_BUDGET) {
                            showWarningDialog("<html><center>You didn't input a budget yet!<br>Please set it via the '$' icon.</center></html>", Color.BLACK, darkGreen);
                            return;
                        }

                        if (analysis.status == Chatbackend.BudgetStatus.INSUFFICIENT_FUNDS) {
                            String errorMsg = "<html><center>Insufficient Budget!<br>Missing items cost Php " + String.format("%.2f", analysis.finalOutOfPocketCost) + "<br>Your budget is only Php " + String.format("%.2f", analysis.currentBudget) + "</center></html>";
                            showWarningDialog(errorMsg, Color.RED, darkGreen);
                            return;
                        }

                        showConfirmationDialog(aiResponse, analysis, darkGreen, buttonPanel);
                    });
                    
                    buttonPanel.add(addToListBtn);
                    chatHistoryPanel.add(Box.createRigidArea(new Dimension(0, 5))); 
                    chatHistoryPanel.add(buttonPanel);
                }
                
                chatHistoryPanel.add(Box.createRigidArea(new Dimension(0, 15)));
                refreshUI(scrollPane);
            });
        }).start();
    }

    // --- UI Helper Methods ---

    private static void showWarningDialog(String message, Color textColor, Color brandColor) {
        JDialog warningDialog = createBaseDialog();
        JPanel warnPopup = createBasePopup(brandColor);
        warningDialog.getContentPane().add(warnPopup);

        JLabel warnLabel = new JLabel(message, SwingConstants.CENTER);
        warnLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        warnLabel.setForeground(textColor);
        warnLabel.setBounds(10, 20, 280, 60);
        warnPopup.add(warnLabel);

        JButton okBtn = new JButton("OK");
        okBtn.setBounds(100, 100, 100, 35);
        styleButton(okBtn, brandColor, Color.WHITE);
        okBtn.addActionListener(eOk -> warningDialog.dispose());
        warnPopup.add(okBtn);

        warningDialog.setVisible(true);
    }

    private static void showConfirmationDialog(AIChatBot.ParsedResponse aiResponse, Chatbackend.RecipeAnalysisResult analysis, Color brandColor, JPanel buttonPanel) {
        JDialog confirmDialog = createBaseDialog();
        JPanel popup = createBasePopup(brandColor);
        confirmDialog.getContentPane().add(popup);

        JLabel askLabel = new JLabel("<html><center>Do you want to add missing ingredients<br>to the shopping list?</center></html>", SwingConstants.CENTER);
        askLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        askLabel.setForeground(Color.BLACK);
        askLabel.setBounds(10, 25, 280, 40);
        popup.add(askLabel);

        JButton yesBtn = new JButton("Yes");
        yesBtn.setBounds(40, 90, 90, 35);
        styleButton(yesBtn, brandColor, Color.WHITE);
        yesBtn.addActionListener(eYes -> {
            confirmDialog.dispose(); 
            
            // BACKEND CALL: Save the data
            Chatbackend.saveRecipeToMenu(aiResponse, analysis);
            
            buttonPanel.setVisible(false);
            chatHistoryPanel.revalidate();
            chatHistoryPanel.repaint();
            
            showSuccessToast(brandColor);
        });
        popup.add(yesBtn);

        JButton noBtn = new JButton("No");
        noBtn.setBounds(170, 90, 90, 35);
        styleButton(noBtn, Color.WHITE, brandColor);
        noBtn.addActionListener(eNo -> confirmDialog.dispose()); 
        popup.add(noBtn);

        confirmDialog.setVisible(true);
    }

    private static void showSuccessToast(Color brandColor) {
        JDialog successDialog = new JDialog(ChatScreen.frame, false);
        successDialog.setUndecorated(true);
        successDialog.setBackground(new Color(0, 0, 0, 0));
        successDialog.setSize(260, 50);
        successDialog.setLocationRelativeTo(ChatScreen.frame);
        
        JPanel toastPanel = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(brandColor.getRed(), brandColor.getGreen(), brandColor.getBlue(), 230)); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            }
        };
        toastPanel.setOpaque(false);
        
        JLabel toastLabel = new JLabel("✓ Successfully added to list!", SwingConstants.CENTER);
        toastLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        toastLabel.setForeground(Color.WHITE);
        toastPanel.add(toastLabel, BorderLayout.CENTER);
        
        successDialog.setContentPane(toastPanel);
        successDialog.setVisible(true);

        Timer fadeTimer = new Timer(2000, fadeEvt -> successDialog.dispose());
        fadeTimer.setRepeats(false);
        fadeTimer.start();
    }

    private static JDialog createBaseDialog() {
        JDialog dialog = new JDialog(ChatScreen.frame, true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));
        dialog.setSize(390, 844);
        dialog.setLocationRelativeTo(ChatScreen.frame);

        JPanel overlay = new JPanel(null) {
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0, 0, 0, 160));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlay.setOpaque(false);
        dialog.setContentPane(overlay);
        return dialog;
    }

    private static JPanel createBasePopup(Color brandColor) {
        JPanel popup = new JPanel(null) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(245, 243, 235)); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(brandColor);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
            }
        };
        popup.setBounds(45, 350, 300, 160);
        popup.setOpaque(false);
        return popup;
    }

    private static void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private static void refreshUI(JScrollPane scrollPane) {
        chatHistoryPanel.revalidate();
        chatHistoryPanel.repaint();
        scrollToBottom(scrollPane);
    }

    private static void scrollToBottom(JScrollPane scrollPane) {
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    // --- Inner Classes ---

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