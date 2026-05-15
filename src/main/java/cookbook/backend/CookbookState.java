package cookbook.backend;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class CookbookState {

    // 🌟 THE MEMORY BANK (State Variables)
    public static String currentRecipeName = "No meal selected";
    public static List<String> currentIngredients = new ArrayList<>();
    public static List<Boolean> checkedIngredients = new ArrayList<>();
    
    // 🌟 FULL RECIPE MEMORY SLOT
    public static List<String> fullRecipeIngredients = new ArrayList<>();
    
    public static String savedMissingIngredients = "Missing: 0 items"; 
    public static String currentBudget = "Php 0.00"; 
    public static double currentTotalCost = 0.0; 
    
    public static String currentCalories = "0 kcal"; 
    public static String currentProtein = "0g";

    public static String pendingPantryPrompt = ""; 
    public static boolean isFromPantry = false; 

    public static void generatePromptFromPantry(List<PantryBackend.PantryItem> pantryItems) throws Exception {
        if (pantryItems == null || pantryItems.isEmpty()) {
            throw new Exception("EMPTY_PANTRY");
        } 
        
        StringBuilder autoPrompt = new StringBuilder("Please generate a recipe using some of these ingredients I have in my pantry:\n\n[MY PANTRY INVENTORY]\n");
        boolean hasFreshItems = false;
        
        for (PantryBackend.PantryItem item : pantryItems) {
            boolean isExpired = false;
            
            if (item.expDate != null && !item.expDate.isEmpty()) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                    LocalDate expiryDate = LocalDate.parse(item.expDate, formatter);
                    LocalDate today = LocalDate.now();
                    
                    if (ChronoUnit.DAYS.between(today, expiryDate) < 0) {
                        isExpired = true;
                    }
                } catch (Exception ex) {} // Ignore malformed dates
            }
            
            if (!isExpired) {
                hasFreshItems = true;
                String cleanQty = item.qty.replace("Quantity:", "").trim();
                autoPrompt.append("- ").append(cleanQty).append(" ").append(item.name).append("\n");
            }
        }
        
        if (!hasFreshItems) {
            throw new Exception("ALL_EXPIRED");
        }
        
        autoPrompt.append("\nCRITICAL RULE: You MUST NOT exceed the quantities I have available in my inventory. For example, if I only have '3 Potatoes', you must use 3 or fewer. Specify the exact amounts used.");
        
        isFromPantry = true;
        pendingPantryPrompt = autoPrompt.toString();
    }

    /**
     * Spillover Deduction System: Deducts used recipe ingredients from the pantry list,
     * then completely wipes the current meal state.
     */
    public static void deductIngredientsAndClearState(List<PantryBackend.PantryItem> pantryItems) {
        if (!fullRecipeIngredients.isEmpty() && pantryItems != null) {
            for (String recipeIng : fullRecipeIngredients) {
                
                int remainingAmountToDeduct = 1; 
                try {
                    String numStr = recipeIng.replaceAll("\\(.*?\\)", "").replaceAll("[^0-9]", " ").trim().split("\\s+")[0];
                    if (!numStr.isEmpty()) remainingAmountToDeduct = Integer.parseInt(numStr);
                } catch (Exception ex) {}

                for (int i = 0; i < pantryItems.size(); i++) {
                    if (remainingAmountToDeduct <= 0) break;
                    
                    PantryBackend.PantryItem pItem = pantryItems.get(i);
                    
                    if (recipeIng.toLowerCase().contains(pItem.name.toLowerCase())) {
                        int currentQty = 1;
                        try {
                            String numStr = pItem.qty.replaceAll("[^0-9]", " ").trim().split("\\s+")[0];
                            if (!numStr.isEmpty()) currentQty = Integer.parseInt(numStr);
                        } catch (Exception ex) {}

                        if (currentQty <= remainingAmountToDeduct) {
                            remainingAmountToDeduct -= currentQty;      
                            pantryItems.remove(i);    
                            i--;                                        
                        } else {
                            int newQty = currentQty - remainingAmountToDeduct;
                            pItem.qty = "Quantity: " + newQty;          
                            remainingAmountToDeduct = 0;                
                        }
                    }
                }
            }
        }
        clearState();
    }

    /**
     * Resets the application's meal data to a clean slate.
     */
    public static void clearState() {
        currentRecipeName = "No meal selected";
        currentIngredients.clear();
        fullRecipeIngredients.clear(); 
        checkedIngredients.clear();
        savedMissingIngredients = "Missing: 0 items";
        currentTotalCost = 0.0; 
        currentCalories = "0 kcal"; 
        currentProtein = "0g";
        isFromPantry = false; 
    }
}