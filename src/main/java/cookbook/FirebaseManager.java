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
        try {
            FileInputStream serviceAccount = 
                new FileInputStream("Y:\\Database AI_CookBook\\ai-cookbook-f347b-firebase-adminsdk-fbsvc-526f99b3f7.json");

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

    // 🌟 Updated: Now takes username and saves it to Firestore
    public static void signUpUser(String userEmail, String userPassword, String username) {
        try {
            // 1. Create the account in Firebase Authentication
            CreateRequest request = new CreateRequest()
                .setEmail(userEmail)
                .setPassword(userPassword);

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
            
            // 2. Save the username to Firestore Database
            Map<String, Object> userData = new HashMap<>();
            userData.put("username", username);
            userData.put("email", userEmail);

            FirestoreClient.getFirestore()
                .collection("users")
                .document(userRecord.getUid()) // Store it using their unique ID
                .set(userData)
                .get(); // .get() ensures it waits for the upload to finish

            System.out.println("✅ User created and username saved to Firestore! ID: " + userRecord.getUid());

        } catch (Exception e) {
            System.out.println("❌ Failed to create user!");
            e.printStackTrace();
        }
    }
    
    public static boolean isUsernameTaken(String username) {
        try {
            // This query only works if you've saved usernames to the "users" collection!
            return !FirestoreClient.getFirestore()
                    .collection("users")
                    .whereEqualTo("username", username)
                    .get().get().isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}