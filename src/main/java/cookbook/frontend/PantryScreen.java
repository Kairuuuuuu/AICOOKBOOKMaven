package cookbook.frontend;

import cookbook.backend.PantryBackend;
import javax.swing.*;
import java.awt.*;

public class PantryScreen {

    public static JFrame frame;
    public static java.util.List<PantryBackend.PantryItem> savedPantryItems = PantryBackend.savedPantryItems;

    public static void showPantry() {
        frame = new JFrame("Dirk's CookBook - Pantry");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(390, 844);
        frame.setLocationRelativeTo(null);

        JPanel mainContent = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(245, 243, 235));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainContent.setLayout(null);
        frame.setContentPane(mainContent);

        JLabel header = new JLabel("My Pantry", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 26));
        header.setBounds(0, 20, 390, 40);
        mainContent.add(header);

        JButton backButton = new JButton("Back");
        backButton.setBounds(20, 80, 80, 35);
        backButton.addActionListener(e -> {
            Point loc = frame.getLocation();
            frame.dispose();
            MainMenu.showMenu();
            MainMenu.frame.setLocation(loc);
        });
        mainContent.add(backButton);

        JTextArea pantryText = new JTextArea();
        pantryText.setEditable(false);
        pantryText.setFont(new Font("SansSerif", Font.PLAIN, 14));

        if (savedPantryItems.isEmpty()) {
            pantryText.setText("Your pantry is empty. Add items from the main menu.");
        } else {
            StringBuilder builder = new StringBuilder();
            for (PantryBackend.PantryItem item : savedPantryItems) {
                builder.append(item.qty)
                       .append(" ")
                       .append(item.name)
                       .append(" (exp: ")
                       .append(item.expDate)
                       .append(")\n");
            }
            pantryText.setText(builder.toString());
        }

        JScrollPane scrollPane = new JScrollPane(pantryText);
        scrollPane.setBounds(20, 130, 350, 650);
        mainContent.add(scrollPane);

        frame.setVisible(true);
    }
}
