package cookbook.backend;

import java.util.ArrayList;
import java.util.List;

public class PantryBackend {

    // This object holds the data structure for a single food item
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

    // This is the main list that the Frontend will read from and save to!
    public static List<PantryItem> savedPantryItems = new ArrayList<>();
}