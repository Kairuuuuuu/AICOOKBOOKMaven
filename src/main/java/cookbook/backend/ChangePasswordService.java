package cookbook.backend;

public class ChangePasswordService {

    // Callback interface to communicate back to the UI
    public interface PasswordUpdateCallback {
        void onSuccess();
        void onError(String errorMessage);
    }

    public static void updatePassword(String email, String currentPass, String newPass, String confirmPass, PasswordUpdateCallback callback) {
        
        // 1. Initial Validation
        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty() || currentPass.equals("Current Password")) {
            callback.onError("Please fill in all fields!");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            callback.onError("New passwords do not match!");
            return;
        }

        // 2. Background task for API Call
        new Thread(() -> {
            String apiKey = System.getenv("FIREBASE_API_KEY");
            String resultMessage = FirebaseManager.updatePasswordREST(email, currentPass, newPass, apiKey);

            if (resultMessage.equals("SUCCESS")) {
                callback.onSuccess();
            } else {
                callback.onError(resultMessage);
            }
        }).start();
    }
}