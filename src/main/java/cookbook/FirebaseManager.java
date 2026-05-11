package cookbook;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.cloud.FirestoreClient;

public class FirebaseManager {

    public static void connect() {
        try {//Github
            FileInputStream serviceAccount = 
                new FileInputStream("ai-cookbook-f347b-firebase-adminsdk-fbsvc-526f99b3f7.json");

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase Connected!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String signUpUser(String email, String password, String webApiKey) {
        try {
            String urlString = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + webApiKey;
            java.net.URL url = new java.net.URL(urlString);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInput = "{\"email\": \"" + email + "\", \"password\": \"" + password + "\", \"returnSecureToken\": true}";

            try(java.io.OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                return "SUCCESS";
            } else {
  
                java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getErrorStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();
                String errorBody = response.toString();

                // 🌟 ADDED HERE: Prints the secret error during Sign Up
                System.out.println("FIREBASE SECRET ERROR (SIGN UP): " + errorBody);

                if (errorBody.contains("EMAIL_EXISTS")) {
                    return "Email is already registered!";
                } else if (errorBody.contains("WEAK_PASSWORD")) {
                    return "Password must be at least 6 characters!";
                } else {
                    return "Sign up failed. Please try again.";
                }
            }

        } catch (Exception e) {
            System.out.println("Sign Up Error: " + e.getMessage());
            return "Connection Error. Try Again!";
        }
    }
    
    public static String loginUser(String email, String password, String webApiKey) {
        try {
            String urlString = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + webApiKey;
            java.net.URL url = new java.net.URL(urlString);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInput = "{\"email\": \"" + email + "\", \"password\": \"" + password + "\", \"returnSecureToken\": true}";

            try(java.io.OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            
            if (responseCode == 200) {
                return "SUCCESS";
            } else {
                java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getErrorStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();
                String errorBody = response.toString();

                System.out.println("FIREBASE SECRET ERROR (LOGIN): " + errorBody);

                if (errorBody.contains("EMAIL_NOT_FOUND")) {
                    return "Account does not exist. Please Sign Up!";
                } else if (errorBody.contains("INVALID_PASSWORD")) {
                    return "Incorrect password!";
                } else if (errorBody.contains("INVALID_LOGIN_CREDENTIALS")) {
                    return "Invalid Email or Password!"; 
                } else if (errorBody.contains("TOO_MANY_ATTEMPTS_TRY_LATER")) {
                return "Too many failed attempts. Try again later!"; 
                }
                else {
                    return "Login failed. Please try again."; 
                }
            }

        } catch (Exception e) {
            System.out.println("Login Error: " + e.getMessage());
            return "Connection Error. Try Again!";
        }
    }
}
