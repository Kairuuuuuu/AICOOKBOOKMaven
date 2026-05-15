package cookbook.backend;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cookbook.frontend.MainMenu;

public class Chatbackend {

    public static String sendMessageToAI(String userMessage) {
        String apiKey = System.getenv("GROQ_API_KEY");
        String endpoint = System.getenv("GROQAIENDPOINT");

        if (apiKey == null || apiKey.trim().isEmpty()) {
            System.out.println("ERROR: GROQ_API_KEY is null. Are you using the Run & Debug sidebar?");
            return "AI is offline. API Key not found in environment.";
        }

        try {
            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setDoOutput(true);

            String safeMessage = userMessage.replace("\"", "\\\"").replace("\n", " ");

            String jsonInputString = "{"
                    + "\"model\": \"llama3-8b-8192\","
                    + "\"messages\": [{\"role\": \"user\", \"content\": \"" + safeMessage + "\"}],"
                    + "\"temperature\": 0.7"
                    + "}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return response.toString(); 
            } else {
                System.out.println("GROQ API ERROR CODE: " + responseCode);
                return "AI is offline. Error code: " + responseCode;
            }

        } catch (Exception e) {
            System.out.println("CONNECTION FAILED: " + e.getMessage());
            return "AI is offline. Connection failed.";
        }
    }

    public enum BudgetStatus {
        OK,
        NO_BUDGET,
        INSUFFICIENT_FUNDS
    }

    public static class RecipeAnalysisResult {
        public BudgetStatus status;
        public double finalOutOfPocketCost;
        public double currentBudget;

        public RecipeAnalysisResult(BudgetStatus status, double finalOutOfPocketCost, double currentBudget) {
            this.status = status;
            this.finalOutOfPocketCost = finalOutOfPocketCost;
            this.currentBudget = currentBudget;
        }
    }

    public static RecipeAnalysisResult analyzeRecipe(AIChatBot.ParsedResponse aiResponse) {
        double currentBudget = 0.0;
        
        try {
            if (CookbookState.currentBudget != null && !CookbookState.currentBudget.isEmpty()) {
                String budgetStr = CookbookState.currentBudget.replace("Php", "").replace(",", "").trim();
                currentBudget = Double.parseDouble(budgetStr);
            }
        } catch (Exception e) {
            currentBudget = 0.0;
        }

        if (currentBudget <= 0) {
            return new RecipeAnalysisResult(BudgetStatus.NO_BUDGET, aiResponse.totalEstimatedCost, 0.0);
        }

        if (aiResponse.totalEstimatedCost > currentBudget) {
            return new RecipeAnalysisResult(BudgetStatus.INSUFFICIENT_FUNDS, aiResponse.totalEstimatedCost, currentBudget);
        }

        return new RecipeAnalysisResult(BudgetStatus.OK, aiResponse.totalEstimatedCost, currentBudget);
    }

    // 🌟 THE FIX IS HERE 🌟
    public static void saveRecipeToMenu(AIChatBot.ParsedResponse aiResponse, RecipeAnalysisResult analysis) {
        
        // 1. Save the Recipe Name
        CookbookState.currentRecipeName = aiResponse.recipeName;
        
        // 2. Save the Nutrition and Cost
        CookbookState.currentCalories = aiResponse.calories;
        CookbookState.currentProtein = aiResponse.protein;
        CookbookState.currentTotalCost = aiResponse.totalEstimatedCost;
        
        // 3. Clear out any old recipe data so the new one takes over completely
        CookbookState.currentIngredients.clear();
        CookbookState.fullRecipeIngredients.clear();
        CookbookState.checkedIngredients.clear();
        
        // 4. Save the ingredients into the exact array Main Menu looks at
        if (aiResponse.ingredients != null) {
            for (String item : aiResponse.ingredients) {
                CookbookState.currentIngredients.add(item);
                CookbookState.fullRecipeIngredients.add(item); // Helps your pantry deduction system
                CookbookState.checkedIngredients.add(false); // Initializes the checkbox as "unchecked"
            }
        }

        // Note: I left out the budget subtraction math here since you specifically 
        // requested previously not to deduct from the budget upon saving!
    }
}