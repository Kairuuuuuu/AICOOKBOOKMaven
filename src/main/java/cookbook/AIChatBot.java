package cookbook;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AIChatBot {

    private static final String AI_ENDPOINT = "https://api.groq.com/openai/v1/chat/completions";
    private static final String API_KEY = System.getenv("GROQ_API_KEY"); 

    public static class ParsedResponse {
        public String displayMessage;
        public String recipeName; 
        public List<String> ingredients;
        public boolean hasRecipe;
        public double totalEstimatedCost; 
    }

    public static ParsedResponse askChefAI(String userMessage) {
        ParsedResponse result = new ParsedResponse();
        result.ingredients = new ArrayList<>();
        result.hasRecipe = false;
        result.recipeName = "AI Suggested Recipe"; 
        result.totalEstimatedCost = 0.0;

        try {
            HttpClient client = HttpClient.newHttpClient();

            double currentBudget = 0.0;
            try {
                String budgetStr = MainMenu.currentBudget.replace("Php", "").replace(",", "").trim();
                currentBudget = Double.parseDouble(budgetStr);
            } catch (Exception e) {}

            // 🌟 RELAXED BUDGET INSTRUCTION: AI will try to stay under budget but won't refuse to output the recipe.
            String budgetInstruction = "";
            if (currentBudget > 0) {
                budgetInstruction = "The user has a budget of Php " + currentBudget + ". "
                        + "Try to recommend a recipe where the total estimated market price of ingredients stays under this amount. "
                        + "However, ALWAYS output the recipe and ingredients block even if it goes slightly over budget.";
            }

            String systemPrompt = "You are a Filipino AI chef. " + budgetInstruction + "\\n\\n"
                    + "CRITICAL RULES IF PROVIDING A RECIPE:\\n"
                    + "1. The VERY FIRST LINE MUST be exactly 'RECIPE_NAME: ' followed by the dish name.\\n"
                    + "2. You MUST wrap ingredients EXACTLY like this:\\n"
                    + "[INGREDIENTS_START]\\n- 1kg Chicken | 180.00\\n- 2 cloves Garlic | 5.00\\n[INGREDIENTS_END]\\n"
                    + "RULE 2b: DO NOT write the price in the ingredient name string. Put the price ONLY as a raw number after the '|' symbol.\\n"
                    + "3. Include Numbered Instructions below that.";

            String jsonPayload = String.format("{\n" +
                "    \"model\": \"llama-3.1-8b-instant\",\n" +
                "    \"messages\": [\n" +
                "        {\n" +
                "            \"role\": \"system\", \n" +
                "            \"content\": \"%s\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"role\": \"user\", \n" +
                "            \"content\": \"%s\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"temperature\": 0.7\n" +
                "}", systemPrompt, userMessage.replace("\"", "\\\""));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(AI_ENDPOINT))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_KEY) 
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
            
            if (jsonResponse.has("error")) {
                String errorMsg = jsonResponse.getAsJsonObject("error").get("message").getAsString();
                result.displayMessage = "Groq API Connection Failed: " + errorMsg;
                return result;
            }

            String rawText = jsonResponse.getAsJsonArray("choices").get(0).getAsJsonObject().getAsJsonObject("message").get("content").getAsString();

            String[] lines = rawText.split("\n");
            for (String line : lines) {
                if (line.toUpperCase().contains("RECIPE_NAME:")) {
                    result.recipeName = line.substring(line.toUpperCase().indexOf("RECIPE_NAME:") + 12).replace("*", "").trim();
                    rawText = rawText.replace(line, "🍽️ **Recipe:** " + result.recipeName);
                    break;
                }
            }

            if (rawText.contains("[INGREDIENTS_START]") && rawText.contains("[INGREDIENTS_END]")) {
                result.hasRecipe = true;
                int startIndex = rawText.indexOf("[INGREDIENTS_START]") + 19;
                int endIndex = rawText.indexOf("[INGREDIENTS_END]");
                String rawIngredients = rawText.substring(startIndex, endIndex).trim();
                
                String[] items = rawIngredients.split("\n");
                for (String item : items) {
                    if (!item.trim().isEmpty()) {
                        String cleanItem = item.replace("-", "").replace("*", "").trim();
                        
                        if (cleanItem.contains("|")) {
                            String[] parts = cleanItem.split("\\|");
                            
                            String name = parts[0].replaceAll("\\(.*?\\d+.*?\\)", "").trim(); 
                            
                            try {
                                double price = Double.parseDouble(parts[1].trim());
                                result.totalEstimatedCost += price;
                                result.ingredients.add(name + " (Php " + String.format("%.2f", price) + ")");
                            } catch (Exception e) {
                                result.ingredients.add(name); 
                            }
                        } else {
                            result.ingredients.add(cleanItem.replaceAll("\\(.*?\\d+.*?\\)", "").trim());
                        }
                    }
                }
                rawText = rawText.replace("[INGREDIENTS_START]", "\n🛒 **Ingredients & Estimated Cost:**\n").replace("[INGREDIENTS_END]", "\n");
            }

            result.displayMessage = rawText;
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            result.displayMessage = "Chef AI is offline. Check your Wi-Fi connection.";
            return result;
        }
    }
}