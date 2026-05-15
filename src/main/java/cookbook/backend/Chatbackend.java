package cookbook.backend;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Chatbackend {

    public enum BudgetStatus {
        OK, NO_BUDGET, INSUFFICIENT_FUNDS
    }

    public static class RecipeAnalysisResult {
        public List<String> missingIngredients = new ArrayList<>();
        public double finalOutOfPocketCost = 0.0;
        public double currentBudget = 0.0;
        public BudgetStatus status = BudgetStatus.OK;
    }

    // Process ingredients against pantry and check budget
    public static RecipeAnalysisResult analyzeRecipe(AIChatBot.ParsedResponse aiResponse) {
        RecipeAnalysisResult result = new RecipeAnalysisResult();
        result.finalOutOfPocketCost = aiResponse.totalEstimatedCost;

        if (CookbookState.isFromPantry) {
            double costToSubtract = 0.0;

            for (String recipeIng : aiResponse.ingredients) {
                boolean alreadyHaveIt = checkPantryForIngredient(recipeIng);

                if (!alreadyHaveIt) {
                    result.missingIngredients.add(recipeIng);
                } else {
                    costToSubtract += extractCost(recipeIng);
                }
            }

            result.finalOutOfPocketCost -= costToSubtract;
            if (result.finalOutOfPocketCost < 0) result.finalOutOfPocketCost = 0.0;
        } else {
            result.missingIngredients.addAll(aiResponse.ingredients);
        }

        validateBudget(result);
        return result;
    }

    // Save accepted recipe to the MainMenu state
    public static void saveRecipeToMenu(AIChatBot.ParsedResponse aiResponse, RecipeAnalysisResult analysis) {
        CookbookState.currentRecipeName = aiResponse.recipeName;
        CookbookState.currentIngredients = new ArrayList<>(analysis.missingIngredients);
        CookbookState.fullRecipeIngredients = new ArrayList<>(aiResponse.ingredients);
        CookbookState.currentTotalCost = analysis.finalOutOfPocketCost;
        CookbookState.currentCalories = aiResponse.calories;
        CookbookState.currentProtein = aiResponse.protein;

        CookbookState.checkedIngredients = new ArrayList<>();
        for (int i = 0; i < analysis.missingIngredients.size(); i++) {
            CookbookState.checkedIngredients.add(false);
        }

        if (analysis.missingIngredients.isEmpty()) {
            CookbookState.savedMissingIngredients = "Missing: 0 items (You have everything!)";
        } else {
            CookbookState.savedMissingIngredients = "Missing: " + analysis.missingIngredients.size() + " items";
        }
    }


    private static boolean checkPantryForIngredient(String recipeIng) {
        for (PantryBackend.PantryItem pItem : PantryBackend.savedPantryItems) {
            boolean isExpired = false;
            if (pItem.expDate != null && !pItem.expDate.isEmpty()) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                    LocalDate expiryDate = LocalDate.parse(pItem.expDate, formatter);
                    if (ChronoUnit.DAYS.between(LocalDate.now(), expiryDate) < 0) {
                        isExpired = true;
                    }
                } catch (Exception ex) {}
            }

            if (!isExpired && recipeIng.toLowerCase().contains(pItem.name.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private static double extractCost(String recipeIng) {
        try {
            int phpIndex = recipeIng.lastIndexOf("Php ");
            if (phpIndex != -1) {
                String costStr = recipeIng.substring(phpIndex + 4, recipeIng.lastIndexOf(")")).trim();
                return Double.parseDouble(costStr);
            }
        } catch (Exception e) {}
        return 0.0;
    }

    private static void validateBudget(RecipeAnalysisResult result) {
        try {
            result.currentBudget = Double.parseDouble(CookbookState.currentBudget.replace("Php", "").replace(",", "").trim());
        } catch (Exception ex) {
            result.currentBudget = 0.0;
        }

        if (result.currentBudget <= 0.0) {
            result.status = BudgetStatus.NO_BUDGET;
        } else if (result.finalOutOfPocketCost > result.currentBudget) {
            result.status = BudgetStatus.INSUFFICIENT_FUNDS;
        } else {
            result.status = BudgetStatus.OK;
        }
    }
}