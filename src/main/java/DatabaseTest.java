import java.io.FileInputStream;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;

public class DatabaseTest {

    public static void main(String[] args) {
        try {

            FileInputStream serviceAccount = 
                new FileInputStream("C:\\Users\\MY PC\\Downloads\\ai-cookbook-f347b-firebase-adminsdk-fbsvc-526f99b3f7.json");

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

            // 3. Initialize the Firebase App
            FirebaseApp.initializeApp(options);
            
            // 4. Try to access Firestore
            Firestore db = FirestoreClient.getFirestore();
            
            System.out.println("----------------------------------------------");
            System.out.println("🔥 SUCCESS! AI-CookBook is connected to Firebase!");
            System.out.println("Project ID: " + FirebaseApp.getInstance().getOptions().getProjectId());
            System.out.println("----------------------------------------------");

        } catch (Exception e) {
            System.out.println("❌ Connection Failed, bro!");
            e.printStackTrace();
        }
    }
}