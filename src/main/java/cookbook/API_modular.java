package cookbook;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class API_modular {

    public static void main(String[] args) {
        
        // 1. THE ENDPOINT: This is the specific API URL for the Modular repo
        String apiUrl = "https://api.github.com/repos/modular/modular";

        // 2. THE CLIENT: Think of this as the "browser" for your code
        HttpClient client = HttpClient.newHttpClient();

        // 3. THE REQUEST: We are telling the client exactly what to do
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET() // We use GET because we want to "get" data from GitHub
                .build();

        // 4. SENDING IT: We send the request and wait for GitHub to answer
        try {
            System.out.println("Connecting to GitHub API...");
            
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // A 200 Status Code means "SUCCESS!" 
            System.out.println("Status Code: " + response.statusCode());
            
            // This is the actual data GitHub sends back!
            System.out.println("The Data:");
            System.out.println(response.body());

        } catch (Exception e) {
            System.out.println("Something went wrong!");
            e.printStackTrace();
        }
    }
}