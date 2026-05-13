package cookbook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AddBudget {

    public static void showBudgetMenu(JFrame parentFrame) {
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

        MainMenu.RoundPanel popupPanel = new MainMenu.RoundPanel();
        popupPanel.setBackground(Color.WHITE);
        popupPanel.setBounds(45, 312, 300, 225); // Slightly taller to fit the error label
        popupPanel.setLayout(null);
        
        popupPanel.addMouseListener(new MouseAdapter() {});
        bgPanel.add(popupPanel);

        JLabel title = new JLabel("Set Your Budget");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(darkGreen);
        title.setBounds(20, 20, 260, 30);
        popupPanel.add(title);

        JLabel subtitle = new JLabel("<html>Set and manage your kitchen<br>spending limits.</html>");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setBounds(20, 50, 260, 40);
        popupPanel.add(subtitle);

        JLabel budgetLabel = new JLabel("Your Budget:");
        budgetLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        budgetLabel.setBounds(20, 105, 100, 30);
        popupPanel.add(budgetLabel);

        // Fill field with existing budget if it's already set
        String currentText = MainMenu.currentBudget.equals("Php 0.00") ? "Enter Budget" : MainMenu.currentBudget.replace("Php ", "");
        MainMenu.RoundTextField budgetField = new MainMenu.RoundTextField(currentText);
        budgetField.setBounds(120, 105, 150, 35);
        popupPanel.add(budgetField);

        // 🌟 NEW: Error Label for contradicting budgets!
        JLabel errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        errorLabel.setForeground(Color.RED);
        errorLabel.setBounds(20, 145, 260, 15);
        errorLabel.setVisible(false);
        popupPanel.add(errorLabel);

        MainMenu.AnimatedButton closeBtn = new MainMenu.AnimatedButton("Close", false);
        closeBtn.setBounds(20, 165, 120, 35);
        closeBtn.addActionListener(e -> dialog.dispose());
        popupPanel.add(closeBtn);

        MainMenu.AnimatedButton saveBtn = new MainMenu.AnimatedButton("Save", true);
        saveBtn.setBounds(150, 165, 120, 35);
        
        saveBtn.addActionListener(e -> {
            String newBudgetStr = budgetField.getText().trim();
            
            if (!newBudgetStr.isEmpty() && !newBudgetStr.equals("Enter Budget")) {
                
                // Convert the user's text into a math number so we can check it
                double numericNewBudget = 0.0;
                try {
                    numericNewBudget = Double.parseDouble(newBudgetStr.toLowerCase().replace("php", "").replace(",", "").trim());
                } catch (Exception ex) {
                    errorLabel.setText("Please enter a valid number!");
                    errorLabel.setVisible(true);
                    return; // Stops the save
                }

                // 🌟 THE FIX: Compare the new budget to the current shopping cart cost!
                if (MainMenu.currentTotalCost > 0.0 && numericNewBudget < MainMenu.currentTotalCost) {
                    errorLabel.setText("Too low! Current meal costs Php " + String.format("%.2f", MainMenu.currentTotalCost));
                    errorLabel.setVisible(true);
                    return; // Blocks the save and forces them to keep the higher budget!
                }
                
                // Add "Php" automatically if they forgot to type it
                if (!newBudgetStr.toLowerCase().startsWith("php")) {
                    MainMenu.currentBudget = "Php " + String.format("%.2f", numericNewBudget);
                } else {
                    MainMenu.currentBudget = newBudgetStr;
                }
                
                // Instantly update the Shopping List Dashboard label
                if (MainMenu.budgetLabel != null) {
                    MainMenu.budgetLabel.setText("Budget: " + MainMenu.currentBudget);
                }
            }
            
            dialog.dispose();
            showSuccessToast(parentFrame);
        });
        
        popupPanel.add(saveBtn);

        dialog.setVisible(true);
    }

    private static void showSuccessToast(JFrame parentFrame) {
        JDialog successDialog = new JDialog(parentFrame, false);
        successDialog.setUndecorated(true);
        successDialog.setBackground(new Color(0, 0, 0, 0));
        successDialog.setSize(260, 50);
        successDialog.setLocationRelativeTo(parentFrame);
        
        JPanel toastPanel = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(14, 71, 17, 230)); // Transparent Dark Green
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            }
        };
        toastPanel.setOpaque(false);
        
        JLabel toastLabel = new JLabel("✓ Budget successfully updated!", SwingConstants.CENTER);
        toastLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        toastLabel.setForeground(Color.WHITE);
        toastPanel.add(toastLabel, BorderLayout.CENTER);
        
        successDialog.setContentPane(toastPanel);
        successDialog.setVisible(true);

        Timer fadeTimer = new Timer(2000, fadeEvt -> successDialog.dispose());
        fadeTimer.setRepeats(false);
        fadeTimer.start();
    }
}