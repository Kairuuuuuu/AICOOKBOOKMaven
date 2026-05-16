package cookbook.backend;

import java.awt.Image;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;

import cookbook.frontend.MainMenu;

public class ShoppingListBackend {

    private static final HashMap<String, Image> imageCache = new HashMap<>();


    public interface ImageLoadListener {
        void onImageLoaded(Image image);
    }

    public static void fetchRecipeImageAsync(String searchKeyword, ImageLoadListener listener) {
        if (searchKeyword.equals("No meal selected") || searchKeyword.equals("AI Suggested Recipe")) {
            return;
        }

        if (imageCache.containsKey(searchKeyword)) {
            listener.onImageLoaded(imageCache.get(searchKeyword));
            return; 
        }

        new Thread(() -> {
            try {
                String safeName = searchKeyword.replace(" ", "%20");
                String prompt = "Authentic%20" + safeName + "%20dish,%20realistic%20food%20photography,%20restaurant%20plating";
                String urlStr = "https://image.pollinations.ai/prompt/" + prompt + "?width=320&height=240&nologo=true&model=flux";
                
                HttpURLConnection connection = (HttpURLConnection) new URL(urlStr).openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
                connection.setConnectTimeout(5000); 
                connection.setReadTimeout(5000);
                
                Image webImage = ImageIO.read(connection.getInputStream());
                
                if (webImage != null) {
                    imageCache.put(searchKeyword, webImage);
                    listener.onImageLoaded(webImage);
                }
            } catch (Exception e) {
                System.out.println("Could not generate image online, keeping default.");
            }
        }).start();
    }

    public static void updateCheckedState(List<Boolean> checkedState, int index, boolean isChecked) {
        if (checkedState != null && index < checkedState.size()) {
            checkedState.set(index, isChecked);
        }
    }

    public static void completeShoppingList(List<String> ingredients, List<Boolean> checkedState) {
        int missing = 0;
        if (ingredients != null && !ingredients.isEmpty()) {
            if (checkedState != null && checkedState.size() == ingredients.size()) {
                for (int i = 0; i < checkedState.size(); i++) {
                    if (!checkedState.get(i)) missing++;
                }
            } else {
                // fallback: count all as missing
                missing = ingredients.size();
            }
        }

        CookbookState.savedMissingIngredients = (missing == 0) ? "Missing: None" : "Missing: " + missing + " items";
        if (MainMenu.missingLabel != null) {
            MainMenu.missingLabel.setText(CookbookState.savedMissingIngredients);
        }
    }
}
