package cookbook.backend;

public class SignUpBackend {

    // Callback interface to communicate back to the UI
    public interface SignUpListener {
        void onSuccess();
        void onError(String errorMessage);
    }

    /**
     * Initializes any backend connections needed for sign up.
     */
    public static void initConnection() {
        FirebaseManager.connect();
    }

    /**
     * Validates inputs and attempts to sign the user up via Firebase on a background thread.
     */
    public static void attemptSignUp(String email, String password, String confirmPassword, SignUpListener listener) {
        // 1. Basic Validation (No internet needed)
        if (password == null || password.isEmpty() || password.equals("Password")) {
            listener.onError("Please enter a password!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            listener.onError("Passwords do not match!");
            return;
        }

        // 2. Network Request (Run on a background thread)
        new Thread(() -> {
            try {
                String apiKey = System.getenv("FIREBASE_API_KEY"); 
                String resultMessage = FirebaseManager.signUpUser(email, password, apiKey);

                if ("SUCCESS".equals(resultMessage)) {
                    listener.onSuccess();
                } else {
                    listener.onError(resultMessage);
                }
            } catch (Exception e) {
                listener.onError("An unexpected error occurred.");
            }
        }).start();
    }
}