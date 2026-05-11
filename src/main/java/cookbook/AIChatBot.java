package cookbook;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AIChatBot {

    // 1. THE NEW URL: Pointing to Groq instead of OpenAI
    private static final String AI_ENDPOINT = "https://api.groq.com/openai/v1/chat/completions";
    
    // 2. YOUR GROQ KEY: Paste your "gsk_..." key right here inside the quotes
    private static final String API_KEY = System.getenv("GROQ_API_KEY");

    public static String askChefAI(String userMessage) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            // 3. THE NEW MODEL: We are using Llama 3 (8 billion parameters), which runs lightning fast on Groq
            String jsonPayload = String.format("{\n" +
            	"    \"model\": \"llama-3.1-8b-instant\",\n" +
                "    \"messages\": [\n" +
                "        {\n" +
                "            \"role\": \"system\", \n" +
                "            \"content\": \"You are a friendly and expert AI chef. Your SOLE purpose is to discuss food, cooking, ingredients, and recipes. If the user asks about ANY non-culinary topic (e.g., math, history, coding, sports, or general trivia), you must politely refuse to answer and remind them that you are a chef. If the user says hello, greet them warmly and ask what they want to cook. ONLY provide a structured recipe when they explicitly ask for one or provide ingredients. When giving a recipe, include a Title, Ingredients list, and Numbered Instructions. Provide exactly 1 recipe strictly "
                + "When you do provide a recipe, include a Title, Ingredients list, and Numbered Instructions., Only provide 1 recipe strictly\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"role\": \"user\", \n" +
                "            \"content\": \"%s\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"temperature\": 0.7\n" +
                "}", userMessage.replace("\"", "\\\"")); // Escape quotes safely

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(AI_ENDPOINT))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_KEY) 
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // --- Error Handling Check ---
            JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
            
            // Catch any API key errors so Eclipse doesn't just crash
            if (jsonResponse.has("error")) {
                String errorMsg = jsonResponse.getAsJsonObject("error").get("message").getAsString();
                return "Groq API Connection Failed: " + errorMsg;
            }

            // Extract the recipe text from the JSON
            JsonArray choices = jsonResponse.getAsJsonArray("choices");
            return choices.get(0).getAsJsonObject().getAsJsonObject("message").get("content").getAsString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Chef AI is offline. Check your Wi-Fi connection.";
        }
    }
}