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
        popupPanel.setBounds(45, 312, 300, 220); 
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

        MainMenu.RoundTextField budgetField = new MainMenu.RoundTextField("Enter Budget");
        budgetField.setBounds(120, 105, 150, 35);
        popupPanel.add(budgetField);

        MainMenu.AnimatedButton closeBtn = new MainMenu.AnimatedButton("Close", false);
        closeBtn.setBounds(20, 160, 120, 35);
        closeBtn.addActionListener(e -> dialog.dispose());
        popupPanel.add(closeBtn);

        MainMenu.AnimatedButton saveBtn = new MainMenu.AnimatedButton("Save", true);
        saveBtn.setBounds(150, 160, 120, 35);
        saveBtn.addActionListener(e -> {
            dialog.dispose();
        });
        popupPanel.add(saveBtn);

        dialog.setVisible(true);
    }
}