package cookbook.backend;

public class AuthenticationService {
    
    public interface LoginCallback {
        void onSuccess(String firstName, String userEmail);
        void onError(String errorMessage);
    }

    public static void attemptLogin(String email, String password, LoginCallback callback) {

        if (email.isEmpty() || password.isEmpty() || email.equals("Email") || password.equals("Password")) {
            callback.onError("Please enter email and password!");
            return;
        }
        else{
            new Thread(() -> {
            String apiKey = System.getenv("FIREBASE_API_KEY"); 
            String resultMessage = FirebaseManager.loginUser(email, password, apiKey);

            if (resultMessage.equals("SUCCESS")) {
                String generatedName = email.split("@")[0];
                if (generatedName.length() > 0) {
                    generatedName = generatedName.substring(0, 1).toUpperCase() + generatedName.substring(1).toLowerCase();
                }
                
                callback.onSuccess(generatedName, email);
            } else {
                callback.onError(resultMessage);
            }
        }).start();

        }
    }
}