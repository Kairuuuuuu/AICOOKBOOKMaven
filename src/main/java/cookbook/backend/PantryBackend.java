package cookbook.backend;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class PantryBackend {

    public static class PantryItem {
        public String name;
        public String qty;
        public String expDate; 
        
        public PantryItem(String name, String qty, String expDate) {
            this.name = name;
            this.qty = qty;
            this.expDate = expDate;
        }
    }
    
    public static List<PantryItem> savedPantryItems = new ArrayList<>();


    public static void addItem(String name, String qty, String expDate) throws Exception {
        if (expDate == null || expDate.isEmpty() || expDate.equals("MM/DD/YYYY")) {
            throw new Exception("Please enter an expiry date.");
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate.parse(expDate, formatter); 
        } catch (Exception ex) {
            throw new Exception("Invalid format! Use MM/DD/YYYY");
        }

        String finalName = (name == null || name.isEmpty() || name.equals("Enter Item Name...")) ? "New Food" : name;
        String finalQty = (qty == null || qty.isEmpty() || qty.equals("Enter Quantity...")) ? "Quantity: ?" : "Quantity: " + qty;
        
        savedPantryItems.add(new PantryItem(finalName, finalQty, expDate));
    }

    /**
     * Edits the quantity of an existing item.
     */
    public static void editItemQuantity(int index, String newQty) {
        if (index >= 0 && index < savedPantryItems.size()) {
            PantryItem item = savedPantryItems.get(index);
            if (newQty != null && !newQty.isEmpty() && !newQty.equals("New Quantity...")) {
                item.qty = "Quantity: " + newQty;
            }
        }
    }

    /**
     * Deletes an item from the pantry.
     */
    public static void deleteItem(int index) {
        if (index >= 0 && index < savedPantryItems.size()) {
            savedPantryItems.remove(index);
        }
    }

    public static boolean isItemLocked(int index) {
        if (CookbookState.isFromPantry && !CookbookState.fullRecipeIngredients.isEmpty()) {
            if (index < 0 || index >= savedPantryItems.size()) return false;
            
            PantryItem item = savedPantryItems.get(index);
            for (String recipeIng : CookbookState.fullRecipeIngredients) {
                if (recipeIng.toLowerCase().contains(item.name.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Calculates the number of days until an item expires.
     */
    public static long getDaysUntilExpiry(String expDate) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate expiryDate = LocalDate.parse(expDate, formatter);
        LocalDate today = LocalDate.now();
        return ChronoUnit.DAYS.between(today, expiryDate);
    }
}
