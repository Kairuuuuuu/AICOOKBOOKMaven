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
        public String imagePath; // <-- NEW: Stores the generated image filename
        public List<String> ingredients;
        public boolean hasRecipe;
    }

    public static ParsedResponse askChefAI(String userMessage) {
        ParsedResponse result = new ParsedResponse();
        result.ingredients = new ArrayList<>();
        result.hasRecipe = false;
        result.recipeName = "AI Suggested Recipe"; 
        result.imagePath = "default_food.jpg"; // Default fallback

        try {
            HttpClient client = HttpClient.newHttpClient();

            String systemPrompt = "You are an AI chef. CRITICAL RULES:\\n"
                    + "1. The VERY FIRST LINE of your response MUST be exactly 'RECIPE_NAME: ' followed by the dish name. Do not say anything else first.\\n"
                    + "2. You MUST wrap ingredients EXACTLY like this:\\n"
                    + "[INGREDIENTS_START]\\n- ingredient 1\\n- ingredient 2\\n[INGREDIENTS_END]\\n"
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
                    
                    // --- NEW: Generate a filename based on the recipe name ---
                    // Example: "Filipino-Style Lechon" -> "filipino_style_lechon.jpg"
                    result.imagePath = result.recipeName.toLowerCase().replaceAll("[^a-z0-9]", "_") + ".jpg";
                    
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
                        result.ingredients.add(item.replace("-", "").replace("*", "").trim()); 
                    }
                }
                rawText = rawText.replace("[INGREDIENTS_START]", "\n🛒 **Ingredients:**\n").replace("[INGREDIENTS_END]", "\n");
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