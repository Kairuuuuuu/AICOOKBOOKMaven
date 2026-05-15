package cookbook.backend;

import java.io.FileInputStream;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;

public class FirebaseManager {

    public static void connect() {
        try {
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
                } else {
                    return "Login failed. Please try again."; 
                }
            }

        } catch (Exception e) {
            System.out.println("Login Error: " + e.getMessage());
            return "Connection Error. Try Again!";
        }
    }

    // 🌟 NEW METHOD: Uses Admin SDK to force a password update!
    public static String changePassword(String email, String newPassword) {
        try {
            // 1. Find the user's hidden UID using their email
            UserRecord user = FirebaseAuth.getInstance().getUserByEmail(email);
            
            // 2. Update the password for that specific account
            UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(user.getUid())
                .setPassword(newPassword);
                
            FirebaseAuth.getInstance().updateUser(request);
            
            return "SUCCESS";
        } catch (Exception e) {
            System.out.println("Change Password Error: " + e.getMessage());
            if (e.getMessage() != null && e.getMessage().contains("WEAK_PASSWORD")) {
                return "Password must be at least 6 characters!";
            }
            return "Failed to update password. Try again.";
        }
    }
 // 🌟 THE BULLETPROOF REST API PASSWORD UPDATE
    public static String updatePasswordREST(String email, String oldPassword, String newPassword, String webApiKey) {
        try {
            // Step 1: Verify the old password and get a secure session Token
            String loginUrl = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + webApiKey;
            java.net.URL url = new java.net.URL(loginUrl);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String loginPayload = "{\"email\": \"" + email + "\", \"password\": \"" + oldPassword + "\", \"returnSecureToken\": true}";
            try(java.io.OutputStream os = conn.getOutputStream()) {
                os.write(loginPayload.getBytes("utf-8"));
            }

            if (conn.getResponseCode() != 200) {
                return "Incorrect Current Password!";
            }

            // Extract the secure idToken from the response
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) response.append(line);
            br.close();
            
            String jsonResponse = response.toString();
            String idToken = "";
            String[] parts = jsonResponse.split("\"idToken\"\\s*:\\s*\"");
            if (parts.length > 1) {
                idToken = parts[1].split("\"")[0];
            }

            if (idToken.isEmpty()) return "Authentication failed.";

            // Step 2: Use the Token to authorize the New Password
            String updateUrl = "https://identitytoolkit.googleapis.com/v1/accounts:update?key=" + webApiKey;
            java.net.URL url2 = new java.net.URL(updateUrl);
            java.net.HttpURLConnection conn2 = (java.net.HttpURLConnection) url2.openConnection();
            conn2.setRequestMethod("POST");
            conn2.setRequestProperty("Content-Type", "application/json");
            conn2.setDoOutput(true);

            String updatePayload = "{\"idToken\": \"" + idToken + "\", \"password\": \"" + newPassword + "\", \"returnSecureToken\": true}";
            try(java.io.OutputStream os = conn2.getOutputStream()) {
                os.write(updatePayload.getBytes("utf-8"));
            }

            if (conn2.getResponseCode() == 200) {
                return "SUCCESS";
            } else {
                return "Password must be at least 6 characters!";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Connection Error. Try Again!";
        }
    }
}